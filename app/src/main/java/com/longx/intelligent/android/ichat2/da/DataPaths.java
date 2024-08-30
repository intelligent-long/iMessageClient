package com.longx.intelligent.android.ichat2.da;

import android.content.Context;
import android.os.Environment;

import com.longx.intelligent.android.ichat2.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.ichat2.data.Broadcast;
import com.longx.intelligent.android.ichat2.data.BroadcastMedia;
import com.longx.intelligent.android.ichat2.data.ChatMessage;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by LONG on 2024/3/28 at 6:05 PM.
 */
public class DataPaths {
    public static String getServerFolder(Context context){
        return SharedPreferencesAccessor.ServerSettingPref.getServerSetting(context).getDataFolder();
    }

    public static class Cache {
        public static String getCacheFolderPath(Context context) {
            return context.getCacheDir().getAbsolutePath() +
                    java.io.File.separator + getServerFolder(context);
        }

        public static String getChatVoiceTempFilePath(Context context, String channelId) {
            return getCacheFolderPath(context) +
                    File.separator + channelId +
                    File.separator + "voice_temp" +
                    File.separator + "voice_temp.aac";
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

        public static String getChatFileFilePath(Context context, String ichatId, String fileName){
            return PrivateFile.getPrivateFileFolderPath(context) +
                    java.io.File.separator + "chat_file" +
                    java.io.File.separator + ichatId +
                    java.io.File.separator + fileName;
        }

        public static String getChatVideoFilePath(Context context, String ichatId, String fileName){
            return PrivateFile.getPrivateFileFolderPath(context) +
                    File.separator + "chat_video" +
                    File.separator + ichatId +
                    File.separator + fileName;
        }

        public static String getChatVoiceFilePath(Context context, String ichatId, String fileName){
            return PrivateFile.getPrivateFileFolderPath(context) +
                    File.separator + "chat_voice" +
                    File.separator + ichatId +
                    File.separator + fileName;
        }
    }

    public static class PublicFile{
        public static String getPublicFilePath(){
            return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "iChat";
        }

        public static String getChatFilePath(ChatMessage chatMessage){
            SimpleDateFormat yyyyMMddHHmmss = new SimpleDateFormat("yyyyMMddHHmmss");
            String fileName = yyyyMMddHHmmss.format(chatMessage.getTime().getTime()) + "_" + chatMessage.getUuid() + "_" + chatMessage.getFileName();
            return getPublicFilePath() + File.separator + "Chat" + File.separator + fileName;
        }

        public static String getBroadcastFilePath(Broadcast broadcast, int mediaIndex){
            BroadcastMedia broadcastMedia = broadcast.getBroadcastMedias().get(mediaIndex);
            SimpleDateFormat yyyyMMddHHmmss = new SimpleDateFormat("yyyyMMddHHmmss");
            String fileName = yyyyMMddHHmmss.format(broadcast.getTime().getTime()) + "_" + broadcast.getBroadcastId() + "_" + broadcastMedia.getMediaId() + "." + broadcastMedia.getExtension();
            return getPublicFilePath() + File.separator + "Broadcast" + File.separator + fileName;
        }

        public static String getCapturedMediaFilePath(){
            SimpleDateFormat yyyyMMddHHmmss = new SimpleDateFormat("yyyyMMddHHmmss");
            String fileName = yyyyMMddHHmmss.format(new Date());
            return getPublicFilePath() + File.separator + "Captured" + File.separator + fileName;
        }
    }
}
