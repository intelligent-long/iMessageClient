package com.longx.intelligent.android.ichat2.da;

import android.content.Context;
import android.os.Environment;

import com.longx.intelligent.android.ichat2.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.ichat2.data.Broadcast;
import com.longx.intelligent.android.ichat2.data.BroadcastMedia;
import com.longx.intelligent.android.ichat2.data.ChatMessage;
import com.longx.intelligent.android.ichat2.net.ServerConfig;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by LONG on 2024/3/28 at 6:05 PM.
 */
public class DataPaths {
    public static String getServerFolder(Context context){
        if(SharedPreferencesAccessor.ServerPref.isUseCentral(context)){
            ServerConfig centralServerConfig = SharedPreferencesAccessor.ServerPref.getCentralServerConfig(context);
            return centralServerConfig == null ? null : centralServerConfig.getDataFolder();
        }else {
            return SharedPreferencesAccessor.ServerPref.getCustomServerConfig(context).getDataFolder();
        }
    }

    public static class Cache {
        public static String getCacheFileRootPath(Context context) {
            return context.getCacheDir().getAbsolutePath() +
                    java.io.File.separator + getServerFolder(context);
        }

        public static String getChatVoiceTempFilePath(Context context, String channelId) {
            return getCacheFileRootPath(context) +
                    File.separator + channelId +
                    File.separator + "voice_temp" +
                    File.separator + "voice_temp.aac";
        }

        public static String getAppUpdateCacheFilePath(Context context){
            return getCacheFileRootPath(context) +
                    File.separator + "app-update" +
                    File.separator + "app-release.apk";
        }
    }

    public static class PrivateFile {
        public static String getPrivateFileRootPath(Context context){
            return context.getFilesDir().getAbsolutePath() +
                    java.io.File.separator + getServerFolder(context);
        }

        public static String getDatabaseFilePath(Context context, String ichatId, String databaseFileName) {
            return PrivateFile.getPrivateFileRootPath(context) +
                    java.io.File.separator + "database" +
                    java.io.File.separator + ichatId +
                    java.io.File.separator + databaseFileName;
        }

        public static String getChatImageFilePath(Context context, String ichatId, String imageFileName){
            return PrivateFile.getPrivateFileRootPath(context) +
                    java.io.File.separator + "chat_image" +
                    java.io.File.separator + ichatId +
                    java.io.File.separator + imageFileName;
        }

        public static String getChatFileFilePath(Context context, String ichatId, String fileName){
            return PrivateFile.getPrivateFileRootPath(context) +
                    java.io.File.separator + "chat_file" +
                    java.io.File.separator + ichatId +
                    java.io.File.separator + fileName;
        }

        public static String getChatVideoFilePath(Context context, String ichatId, String fileName){
            return PrivateFile.getPrivateFileRootPath(context) +
                    File.separator + "chat_video" +
                    File.separator + ichatId +
                    File.separator + fileName;
        }

        public static String getChatVoiceFilePath(Context context, String ichatId, String fileName){
            return PrivateFile.getPrivateFileRootPath(context) +
                    File.separator + "chat_voice" +
                    File.separator + ichatId +
                    File.separator + fileName;
        }
    }

    public static class PublicFile{
        public static String getPublicFileRootPath(){
            return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "iChat";
        }

        public static String getChatFilePath(ChatMessage chatMessage){
            SimpleDateFormat yyyyMMddHHmmss = new SimpleDateFormat("yyyyMMddHHmmss");
            String fileName = yyyyMMddHHmmss.format(chatMessage.getTime().getTime()) + "_" + chatMessage.getUuid() + "_" + chatMessage.getFileName();
            return getPublicFileRootPath() + File.separator + "Chat" + File.separator + fileName;
        }

        public static String getBroadcastFilePath(Broadcast broadcast, int mediaIndex){
            BroadcastMedia broadcastMedia = broadcast.getBroadcastMedias().get(mediaIndex);
            SimpleDateFormat yyyyMMddHHmmss = new SimpleDateFormat("yyyyMMddHHmmss");
            String fileName = yyyyMMddHHmmss.format(broadcast.getTime().getTime()) + "_" + broadcast.getBroadcastId() + "_" + broadcastMedia.getMediaId() + "." + broadcastMedia.getExtension();
            return getPublicFileRootPath() + File.separator + "Broadcast" + File.separator + fileName;
        }

        public static String getCapturedMediaFilePath(){
            SimpleDateFormat yyyyMMddHHmmss = new SimpleDateFormat("yyyyMMddHHmmss");
            String fileName = yyyyMMddHHmmss.format(new Date());
            return getPublicFileRootPath() + File.separator + "Captured" + File.separator + fileName;
        }
    }
}
