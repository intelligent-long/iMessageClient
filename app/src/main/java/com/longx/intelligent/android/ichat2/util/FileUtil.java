package com.longx.intelligent.android.ichat2.util;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

/**
 * Created by LONG on 2024/3/28 at 6:57 PM.
 */
public class FileUtil {

    public static boolean transfer(InputStream is, OutputStream os){
        try (InputStream inputStream = new BufferedInputStream(is);
             OutputStream outputStream = new BufferedOutputStream(os)) {
            byte[] buffer = new byte[10240];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            return true;
        } catch (IOException e) {
            Log.e(FileUtil.class.getName(), "", e);
            return false;
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
        return getFileExtension(file.getAbsolutePath());
    }

    public static String getFileExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }
        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex != -1) {
            return fileName.substring(lastDotIndex).toLowerCase();
        } else {
            return "";
        }
    }

}
