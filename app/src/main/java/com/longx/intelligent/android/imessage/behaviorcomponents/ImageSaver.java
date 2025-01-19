package com.longx.intelligent.android.imessage.behaviorcomponents;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.core.content.FileProvider;

import com.longx.intelligent.android.imessage.activity.helper.BaseActivity;
import com.longx.intelligent.android.imessage.permission.PermissionOperator;
import com.longx.intelligent.android.imessage.permission.PermissionRequirementChecker;
import com.longx.intelligent.android.imessage.permission.ToRequestPermissions;
import com.longx.intelligent.android.imessage.permission.ToRequestPermissionsItems;
import com.longx.intelligent.android.imessage.util.ErrorLogger;
import com.longx.intelligent.android.imessage.yier.ResultsYier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by LONG on 2024/4/19 at 2:51 PM.
 */
public class ImageSaver {

    public static void saveImageToDcim(Context context, byte[] sourceImageData, String saveFileName, String dcimRelativePath, ResultsYier yier) {
        File tempFile = createTemporaryFile(context, sourceImageData);
        if (tempFile != null) {
            saveImageToDcim(context, tempFile, saveFileName, dcimRelativePath, yier);
            tempFile.delete();
        }else {
            yier.onResults((Object) null);
        }
    }

    public static void saveImageToDcim(Context context, File sourceImageFile, String saveFileName, String dcimRelativePath, ResultsYier yier){
        if(context instanceof BaseActivity && PermissionRequirementChecker.needExternalStoragePermission()) {
            BaseActivity baseActivity = (BaseActivity) context;
            if(PermissionOperator.hasPermissions(baseActivity, ToRequestPermissionsItems.writeAndReadExternalStorage)){
                Uri uri = doSaveImageToDcim(context, sourceImageFile, saveFileName, dcimRelativePath);
                yier.onResults(uri);
            }else {
                List<ToRequestPermissions> toRequestPermissionsList = new ArrayList<>();
                toRequestPermissionsList.add(ToRequestPermissionsItems.writeAndReadExternalStorage);
                new PermissionOperator(baseActivity, toRequestPermissionsList,
                        new PermissionOperator.ShowCommonMessagePermissionResultCallback(baseActivity) {
                            @Override
                            public void onPermissionGranted(int requestCode) {
                                Uri uri = doSaveImageToDcim(baseActivity, sourceImageFile, saveFileName, dcimRelativePath);
                                yier.onResults(uri);
                            }
                        }).startRequestPermissions(baseActivity);
            }
        }else {
            Uri uri = doSaveImageToDcim(context, sourceImageFile, saveFileName, dcimRelativePath);
            yier.onResults(uri);
        }
    }

    private static Uri doSaveImageToDcim(Context context, File sourceImageFile, String saveFileName, String dcimRelativePath) {
        if (sourceImageFile == null || !sourceImageFile.exists()) {
            return null;
        }

        if (!PermissionRequirementChecker.needExternalStoragePermission()) {
            return saveImageForApiQAndAbove(context, sourceImageFile, saveFileName, dcimRelativePath);
        } else {
            return saveImageForBelowApiQ(context, sourceImageFile, saveFileName, dcimRelativePath);
        }
    }

    private static File createTemporaryFile(Context context, byte[] data) {
        File tempFile = null;
        try {
            tempFile = File.createTempFile("temp", null, context.getCacheDir());
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                fos.write(data);
            }
        } catch (Exception e) {
            ErrorLogger.log(ImageSaver.class, e);
        }
        return tempFile;
    }

    private static Uri saveImageForApiQAndAbove(Context context, File sourceImageFile, String saveFileName, String dcimRelativePath) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, saveFileName);
        String mimeType = URLConnection.guessContentTypeFromName(saveFileName);
        if (mimeType == null) {
            mimeType = "image/jpeg";
        }
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, mimeType);
        contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_DCIM + File.separator + dcimRelativePath);
        contentValues.put(MediaStore.MediaColumns.IS_PENDING, 1);
        Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        if (uri != null) {
            try (OutputStream outputStream = context.getContentResolver().openOutputStream(uri);
                 InputStream inputStream = Files.newInputStream(sourceImageFile.toPath())) {
                Objects.requireNonNull(outputStream);
                byte[] buf = new byte[10240];
                int len;
                while ((len = inputStream.read(buf)) > 0) {
                    outputStream.write(buf, 0, len);
                }
                contentValues.clear();
                contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0);
                context.getContentResolver().update(uri, contentValues, null, null);
                return uri;
            } catch (Exception e) {
                ErrorLogger.log(ImageSaver.class, e);
                MessageDisplayer.autoShow(context, "错误", MessageDisplayer.Duration.SHORT);
                return null;
            }
        }
        return null;
    }

    private static Uri saveImageForBelowApiQ(Context context, File sourceImageFile, String saveFileName, String dcimRelativePath) {
        File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), dcimRelativePath);
        if (!directory.exists() && !directory.mkdirs()) {
            return null;
        }
        File saveFile = new File(directory, saveFileName);
        synchronized (ImageSaver.class) {
            String imageFileAbsolutePath = saveFile.getAbsolutePath();
            String pathPart1 = imageFileAbsolutePath.substring(0, imageFileAbsolutePath.lastIndexOf("."));
            String pathPart2 = imageFileAbsolutePath.substring(imageFileAbsolutePath.lastIndexOf("."));
            for (int i = 1; saveFile.exists(); i++) {
                saveFile = new File(pathPart1 + " (" + i + ")" + pathPart2);
            }
            try {
                saveFile.createNewFile();
            } catch (Exception e) {
                ErrorLogger.log(ImageSaver.class, e);
                return null;
            }
            if(!saveFile.exists()){
                return null;
            }
        }
        try (InputStream inputStream = Files.newInputStream(sourceImageFile.toPath());
             OutputStream outputStream = Files.newOutputStream(saveFile.toPath())) {
            byte[] buf = new byte[10240];
            int len;
            while ((len = inputStream.read(buf)) > 0) {
                outputStream.write(buf, 0, len);
            }
            Uri uri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", saveFile);
            context.getContentResolver().notifyChange(uri, null);
            return uri;
        } catch (Exception e) {
            ErrorLogger.log(ImageSaver.class, e);
            return null;
        }
    }
}
