package com.longx.intelligent.android.imessage.da;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.provider.OpenableColumns;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.longx.intelligent.android.imessage.behaviorcomponents.MessageDisplayer;
import com.longx.intelligent.android.imessage.media.helper.MediaStoreHelper;
import com.longx.intelligent.android.imessage.ui.glide.GlideApp;
import com.longx.intelligent.android.imessage.util.ErrorLogger;
import com.longx.intelligent.android.imessage.util.FileUtil;

import org.apache.commons.codec.binary.Base64;
import org.apache.tika.Tika;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

/**
 * Created by LONG on 2024/1/21 at 9:23 PM.
 */
public class FileHelper {
    public static File createFile(String path) throws IOException{
        return createFile(path, false);
    }

    public static File createFile(String path, boolean ignore) throws IOException {
        File file = new File(path);
        if(!(file.exists() && ignore)) {
            int number = 1;
            String fileName = file.getName();
            int lastIndexOf = fileName.lastIndexOf('.');
            String dirPath = file.getParentFile().getAbsolutePath();
            while (file.exists()) {
                if (lastIndexOf != -1) {
                    String fileNameWithoutExtension = fileName.substring(0, lastIndexOf);
                    String extension = fileName.substring(lastIndexOf);
                    file = new File(dirPath + File.separator + fileNameWithoutExtension + " (" + number + ")" + extension);
                } else {
                    file = new File(dirPath + File.separator + fileName + " (" + number + ")");
                }
                number++;
            }
            Objects.requireNonNull(file.getParentFile()).mkdirs();
            file.createNewFile();
        }
        return file;
    }

    public static String save(InputStream contentStream, String path) throws IOException {
        return save(contentStream, path, -1, null, null);
    }

    public static String save(InputStream contentStream, String path, long total, ProgressCallback progressCallback, boolean[] cancel) throws IOException {
        File file = createFile(path);
        try (FileOutputStream outputStream = new FileOutputStream(file)){
            if(progressCallback != null) {
                FileUtil.transfer(contentStream, outputStream, progressCallback, total, cancel);
            }else {
                FileUtil.transfer(contentStream, outputStream);
            }
            return file.getAbsolutePath();
        }
    }

    public static String save(byte[] bytes, String path) throws IOException {
        File file = createFile(path);
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(bytes);
            return file.getAbsolutePath();
        }
    }

    public static InputStream streamOf(String path){
        try {
            return new FileInputStream(path);
        } catch (FileNotFoundException e) {
            ErrorLogger.log(FileHelper.class, e);
            return null;
        }
    }

    public static byte[] bytesOf(String path) {
        File file = new File(path);
        if (!file.exists() || !file.isFile()) {
            return null;
        }
        try (FileInputStream inputStream = new FileInputStream(file);
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
            return outputStream.toByteArray();
        } catch (IOException e) {
            ErrorLogger.log(FileHelper.class, e);
            return null;
        }
    }

    public static String detectFileExtension(byte[] bytes) {
        Tika tika = new Tika();
        String mimeType;
        mimeType = tika.detect(bytes);
        MimeTypes defaultMimeTypes = MimeTypes.getDefaultMimeTypes();
        MimeType mimeTypeObj;
        try {
            mimeTypeObj = defaultMimeTypes.forName(mimeType);
        } catch (MimeTypeException e) {
            ErrorLogger.log(e);
            return "";
        }
        String extension = mimeTypeObj.getExtension();
        if (extension.startsWith(".")) {
            extension = extension.substring(1);
        }
        return extension;
    }

    public static String detectFileExtension(File file) {
        Tika tika = new Tika();
        String mimeType;
        try {
            mimeType = tika.detect(file);
        } catch (IOException e) {
            ErrorLogger.log(e);
            return "";
        }
        MimeTypes defaultMimeTypes = MimeTypes.getDefaultMimeTypes();
        MimeType mimeTypeObj;
        try {
            mimeTypeObj = defaultMimeTypes.forName(mimeType);
        } catch (MimeTypeException e) {
            ErrorLogger.log(e);
            return "";
        }
        String extension = mimeTypeObj.getExtension();
        if (extension.startsWith(".")) {
            extension = extension.substring(1);
        }
        return extension;
    }

    @SuppressLint("Range")
    public static String getFileNameFromUri(Context context, Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    public static String getFileExtensionFromUri(Context context, Uri uri){
        String fileName = getFileNameFromUri(context, uri);
        return FileUtil.getFileExtension(fileName);
    }

    public static String getMimeType(Context context, Uri uri) {
        String extension = getFileExtensionFromUri(context, uri);
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.toLowerCase());
    }

    public static String getMimeType(String filePath) {
        String extension = MimeTypeMap.getFileExtensionFromUrl(filePath);
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.toLowerCase());
    }

    public static void openFile(Context context, Uri fileUri, String mimeType) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(fileUri, mimeType);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent = Intent.createChooser(intent, "选择打开此文件的应用");
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        } else {
            MessageDisplayer.autoShow(context, "没有找到能够打开该文件的应用", MessageDisplayer.Duration.LONG);
        }
    }

    public static void openFile(Context context, String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            Uri fileUri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);
            String mimeType = getMimeType(filePath);
            openFile(context, fileUri, mimeType);
        } else {
            MessageDisplayer.autoShow(context, "文件不存在", MessageDisplayer.Duration.LONG);
        }
    }

    public static File detectAndRenameFile(File photoFile){
        String fileExtension = detectFileExtension(photoFile);
        File parentFile = photoFile.getParentFile();
        String fileName;
        if(parentFile != null) {
            fileName = parentFile.getAbsolutePath() + File.separator
                    + FileUtil.getFileBaseName(photoFile) + "." + fileExtension;
        }else {
            fileName = FileUtil.getFileBaseName(photoFile) + "." + fileExtension;
        }
        File renamedFile = new File(fileName);
        boolean success = photoFile.renameTo(renamedFile);
        if(success) {
            return renamedFile;
        }else {
            return null;
        }
    }

    public static byte[] readUriToBytes(Uri uri, Context context){
        try (InputStream is = context.getContentResolver().openInputStream(uri);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int read;
            while (-1 != (read = is.read(buffer))) {
                baos.write(buffer, 0, read);
            }
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String readUriToBase64(Uri uri, Context context){
        return Base64.encodeBase64String(readUriToBytes(uri, context));
    }

    public static void appendToFile(String filePath, String textToAppend) {
        File file = new File(filePath);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.append(textToAppend);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String saveNetImage(Context context, String url, String savePath) throws IOException, InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        ErrorLogger.log(savePath);
        new File(savePath).getParentFile().mkdirs();
        final String[] savedPath = new String[1];
        final IOException[] ioException = new IOException[1];
        GlideApp
                .with(context)
                .downloadOnly()
                .override(Target.SIZE_ORIGINAL)
                .load(url)
                .into(new CustomTarget<File>() {
                    @Override
                    public void onResourceReady(@NonNull File resource, @Nullable Transition<? super File> transition) {
                        InputStream inputStream = null;
                        try {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                inputStream = Files.newInputStream(resource.toPath());
                            } else {
                                inputStream = new FileInputStream(resource);
                            }
                            savedPath[0] = FileHelper.save(inputStream, savePath);
                            countDownLatch.countDown();
                        } catch (IOException e) {
                            ErrorLogger.log(e);
                            ioException[0] = e;
                            countDownLatch.countDown();
                        } finally {
                            try {
                                if (inputStream != null) {
                                    inputStream.close();
                                }
                            } catch (IOException e) {
                                ErrorLogger.log(e);
                                ioException[0] = e;
                            }
                        }
                    }
                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }
                });
        countDownLatch.await();
        if (ioException[0] != null) throw ioException[0];
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStoreHelper.notifyMediaStore(context, savedPath[0]);
        } else {
            MediaScannerConnection.scanFile(context, new String[]{savedPath[0]}, null, null);
        }
        return savedPath[0];
    }
}
