package com.longx.intelligent.android.imessage.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;

import com.longx.intelligent.android.imessage.da.ProgressCallback;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.Objects;

/**
 * Created by LONG on 2024/3/28 at 6:57 PM.
 */
public class FileUtil {

    public static void transfer(InputStream is, OutputStream os) throws IOException {
        transfer(is, os, null);
    }

    public static void transfer(InputStream is, OutputStream os, boolean[] cancel) throws IOException {
        transfer(is, os, null, -1, cancel);
    }

    public static void transfer(InputStream is, OutputStream os, ProgressCallback progressCallback, long total, boolean[] cancel) throws IOException {
        int bytesWritten = 0;
        try (InputStream inputStream = new BufferedInputStream(is);
             OutputStream outputStream = new BufferedOutputStream(os)) {
            byte[] buffer = new byte[10240];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                if(cancel != null && cancel[0]) {
                    return;
                }
                outputStream.write(buffer, 0, bytesRead);
                if(progressCallback != null) {
                    bytesWritten += bytesRead;
                    progressCallback.onProgressUpdate(bytesWritten, total);
                }
            }
        }
    }

    public static boolean dirContainsFile(File dir, String fileName){
        boolean contains = false;
        for (String fileNameInDir : Objects.requireNonNull(dir.list())) {
            if (fileNameInDir.equals(fileName)) {
                contains = true;
                break;
            }
        }
        return contains;
    }

    public static String extractFileNameInHttpHeader(String contentDisposition) {
        if (contentDisposition != null && !contentDisposition.isEmpty()) {
            String[] elements = contentDisposition.split(";");
            for (String element : elements) {
                element = element.trim();
                if (element.startsWith("filename=") || element.startsWith("filename*=")) {
                    int startIndex = element.indexOf("=");
                    if (startIndex != -1) {
                        String fileName = element.substring(startIndex + 1).trim();
                        if (fileName.startsWith("\"") || fileName.startsWith("'")) {
                            fileName = fileName.substring(1);
                        }
                        if (fileName.endsWith("\"") || fileName.endsWith("'")) {
                            fileName = fileName.substring(0, fileName.length() - 1);
                        }
                        return fileName;
                    }
                }
            }
        }
        return null;
    }

    public static String getFileExtension(File file) {
        return getFileExtension(file.getName());
    }

    public static String getFileExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }
        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex != -1) {
            return fileName.substring(lastDotIndex + 1).toLowerCase();
        } else {
            return "";
        }
    }

    public static String getFileBaseName(File file){
        return getFileBaseName(file.getName());
    }

    public static String getFileBaseName(String fileName){
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }
        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex != -1) {
            return fileName.substring(0, lastDotIndex);
        } else {
            return fileName;
        }
    }

    public static long getFileSize(String filePath) {
        File file = new File(filePath);
        if (file.exists() && file.isFile()) {
            return file.length();
        } else {
            return -1;
        }
    }

    public static String formatFileSize(long size) {
        if (size <= 0) return "0 B";
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    public static long getFileSize(Context context, Uri uri) {
        String scheme = uri.getScheme();
        long fileSize = 0;

        if (scheme == null) {
            return fileSize;
        }

        if (scheme.equals("file")) {
            File file = new File(uri.getPath());
            fileSize = file.length();
        } else if (scheme.equals("content")) {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                try {
                    int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                    if (sizeIndex != -1 && cursor.moveToFirst()) {
                        fileSize = cursor.getLong(sizeIndex);
                    }
                } finally {
                    cursor.close();
                }
            }

            if (fileSize == 0) {
                try (InputStream inputStream = context.getContentResolver().openInputStream(uri)) {
                    if (inputStream != null) {
                        fileSize = inputStream.available();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return fileSize;
    }

    public static boolean deleteFile(String filePath) {
        Path path = Paths.get(filePath);
        try {
            return Files.deleteIfExists(path);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

}
