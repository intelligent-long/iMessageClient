package com.longx.intelligent.android.ichat2.da;

import com.longx.intelligent.android.ichat2.util.ErrorLogger;
import com.longx.intelligent.android.ichat2.util.FileUtil;

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
public class FileAccessor {
    public static boolean save(InputStream contentStream, String path) {
        File file = new File(path);
        Objects.requireNonNull(file.getParentFile()).mkdirs();
        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            return FileUtil.transfer(contentStream, outputStream);
        }catch (IOException e){
            ErrorLogger.log(FileAccessor.class, e);
            return false;
        }
    }

    public static InputStream streamOf(String path){
        try {
            return new FileInputStream(path);
        } catch (FileNotFoundException e) {
            ErrorLogger.log(FileAccessor.class, e);
            return null;
        }
    }
}
