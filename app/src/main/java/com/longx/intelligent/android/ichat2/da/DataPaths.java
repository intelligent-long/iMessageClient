package com.longx.intelligent.android.ichat2.da;

import android.content.Context;
import android.os.Environment;

import com.longx.intelligent.android.ichat2.da.sharedpref.SharedPreferencesAccessor;

import java.io.File;

/**
 * Created by LONG on 2024/3/28 at 6:05 PM.
 */
public class DataPaths {
    public static String getServerFolder(Context context){
        return SharedPreferencesAccessor.ServerSettingPref.getServerSetting(context).getDataFolder();
    }

    public static class Cache{
        public static String getCacheFolderPath(Context context) {
            return context.getCacheDir().getAbsolutePath() +
                    java.io.File.separator + getServerFolder(context);
        }
    }

    public static class PrivateFile {
        public static String getPrivateFileFolderPath(Context context){
            return context.getFilesDir().getAbsolutePath() +
                    java.io.File.separator + getServerFolder(context);
        }

        public static String getDatabaseFilePath(Context context, String ichatId, String databaseFileName) {
            return PrivateFile.getPrivateFileFolderPath(context) +
                    java.io.File.separator + "database" +
                    java.io.File.separator + ichatId +
                    java.io.File.separator + databaseFileName;
        }

        public static String getChatImageFilePath(Context context, String ichatId, String imageFileName){
            return PrivateFile.getPrivateFileFolderPath(context) +
                    java.io.File.separator + "chat_image" +
                    java.io.File.separator + ichatId +
                    java.io.File.separator + imageFileName;
        }
    }

    public static class PublicFile{
        public static String getPublicFilePath(){
            return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "iChat";
        }

        public static String getChatImageFilePath(String fileName){
            return getPublicFilePath() + File.separator + "聊天图片" + File.separator + fileName;
        }
    }
}
