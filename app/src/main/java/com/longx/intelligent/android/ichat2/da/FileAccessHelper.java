package com.longx.intelligent.android.ichat2.da;

import com.longx.intelligent.android.ichat2.util.ErrorLogger;
import com.longx.intelligent.android.ichat2.util.FileUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * Created by LONG on 2024/1/21 at 9:23 PM.
 */
public class FileAccessHelper {
    public static boolean save(InputStream contentStream, String path) {
        File file = new File(path);
        Objects.requireNonNull(file.getParentFile()).mkdirs();
        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            return FileUtil.transfer(contentStream, outputStream);
        }catch (IOException e){
            ErrorLogger.log(FileAccessHelper.class, e);
            return false;
        }
    }

    public static boolean save(byte[] bytes, String path) {
        File file = new File(path);
        Objects.requireNonNull(file.getParentFile()).mkdirs();
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(bytes);
            return true;
        } catch (IOException e) {
            ErrorLogger.log(FileAccessHelper.class, e);
            return false;
        }
    }

    public static InputStream streamOf(String path){
        try {
            return new FileInputStream(path);
        } catch (FileNotFoundException e) {
            ErrorLogger.log(FileAccessHelper.class, e);
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
            ErrorLogger.log(FileAccessHelper.class, e);
            return null;
        }
    }
}
