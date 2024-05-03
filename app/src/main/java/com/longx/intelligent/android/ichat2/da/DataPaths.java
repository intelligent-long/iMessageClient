package com.longx.intelligent.android.ichat2.da;

import android.content.Context;

import com.longx.intelligent.android.ichat2.da.sharedpref.SharedPreferencesAccessor;

import java.io.File;

/**
 * Created by LONG on 2024/3/28 at 6:05 PM.
 */
public class DataPaths {

    public static class Data{
        public static String getDataFolderPath(Context context) {
            return context.getDataDir().getAbsolutePath() +
                    java.io.File.separator + "data" +
                    java.io.File.separator + SharedPreferencesAccessor.ServerSettingPref.getServerSetting(context).getDataFolder();
        }
    }

    public static class Cache{
        public static String getCacheFolderPath(Context context) {
            return context.getCacheDir().getAbsolutePath();
        }

        public static String getAvatarCachePath(Context context, String avatarHash, String extension){
            return getCacheFolderPath(context) + java.io.File.separator + "avatar" + java.io.File.separator + avatarHash + extension;
        }
    }

    public static class File{
        public static String getPrivateFileFolderPath(Context context){
            return context.getFilesDir().getAbsolutePath();
        }

        public static String getAvatar(Context context, String ichatId, String extension){
            return getPrivateFileFolderPath(context)  + java.io.File.separator + "avatar"  + java.io.File.separator + ichatId + extension;
        }
    }

    public static class Database{
        public static String getDatabaseFilePath(Context context, String ichatId, String databaseFileName) {
            return DataPaths.Data.getDataFolderPath(context) +
                    java.io.File.separator + "database" +
                    java.io.File.separator + ichatId +
                    java.io.File.separator + databaseFileName;
        }
    }
}
