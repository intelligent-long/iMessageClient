package com.longx.intelligent.android.imessage.da;

import android.content.Context;
import android.os.Environment;

import com.longx.intelligent.android.imessage.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.imessage.data.Broadcast;
import com.longx.intelligent.android.imessage.data.BroadcastMedia;
import com.longx.intelligent.android.imessage.data.ChatMessage;
import com.longx.intelligent.android.imessage.net.ServerConfig;
import com.longx.intelligent.android.imessage.value.Constants;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by LONG on 2024/3/28 at 6:05 PM.
 */
public class DataPaths {
    public static String serverFolder(Context context){
        if(SharedPreferencesAccessor.ServerPref.isUseCentral(context)){
            ServerConfig centralServerConfig = SharedPreferencesAccessor.ServerPref.getCentralServerConfig(context);
            return centralServerConfig == null ? null : centralServerConfig.getDataFolder();
        }else {
            return SharedPreferencesAccessor.ServerPref.getCustomServerConfig(context).getDataFolder();
        }
    }

    public static class Cache {
        public static String cacheFileRootPath(Context context) {
            return context.getCacheDir().getAbsolutePath() +
                    java.io.File.separator + serverFolder(context);
        }

        public static String chatVoiceTempFilePath(Context context, String channelId) {
            return cacheFileRootPath(context) +
                    File.separator + channelId +
                    File.separator + "voice_temp" +
                    File.separator + "voice_temp.aac";
        }

        public static String appUpdateCacheFilePath(Context context){
            return cacheFileRootPath(context) +
                    File.separator + "app-update" +
                    File.separator + "app-release.apk";
        }
    }

    public static class PrivateFile {
        public static String privateFileRootPath(Context context){
            return context.getFilesDir().getAbsolutePath() +
                    java.io.File.separator + serverFolder(context);
        }

        public static String databaseFilePath(Context context, String imessageId, String databaseFileName) {
            return PrivateFile.privateFileRootPath(context) +
                    java.io.File.separator + "database" +
                    java.io.File.separator + imessageId +
                    java.io.File.separator + databaseFileName;
        }

        public static String chatImageFilePath(Context context, String imessageId, String imageFileName){
            return PrivateFile.privateFileRootPath(context) +
                    java.io.File.separator + "chat_image" +
                    java.io.File.separator + imessageId +
                    java.io.File.separator + imageFileName;
        }

        public static String chatFileFilePath(Context context, String imessageId, String fileName){
            return PrivateFile.privateFileRootPath(context) +
                    java.io.File.separator + "chat_file" +
                    java.io.File.separator + imessageId +
                    java.io.File.separator + fileName;
        }

        public static String chatVideoFilePath(Context context, String imessageId, String fileName){
            return PrivateFile.privateFileRootPath(context) +
                    File.separator + "chat_video" +
                    File.separator + imessageId +
                    File.separator + fileName;
        }

        public static String chatVoiceFilePath(Context context, String imessageId, String fileName){
            return PrivateFile.privateFileRootPath(context) +
                    File.separator + "chat_voice" +
                    File.separator + imessageId +
                    File.separator + fileName;
        }
    }

    public static class PublicFile{
        public static String publicFileRootPath(){
            return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + Constants.APP_NAME;
        }

        public static String chatFilePath(ChatMessage chatMessage){
            SimpleDateFormat yyyyMMddHHmmss = new SimpleDateFormat("yyyyMMddHHmmss");
            String fileName = yyyyMMddHHmmss.format(chatMessage.getTime().getTime()) + "_" + chatMessage.getUuid() + "_" + chatMessage.getFileName();
            return publicFileRootPath() + File.separator + "Chat" + File.separator + fileName;
        }

        public static String broadcastFilePath(Broadcast broadcast, int mediaIndex){
            BroadcastMedia broadcastMedia = broadcast.getBroadcastMedias().get(mediaIndex);
            SimpleDateFormat yyyyMMddHHmmss = new SimpleDateFormat("yyyyMMddHHmmss");
            String fileName = yyyyMMddHHmmss.format(broadcast.getTime().getTime()) + "_" + broadcast.getBroadcastId() + "_" + broadcastMedia.getMediaId() + "." + broadcastMedia.getExtension();
            return publicFileRootPath() + File.separator + "Broadcast" + File.separator + fileName;
        }

        public static String capturedMediaFilePath(){
            SimpleDateFormat yyyyMMddHHmmss = new SimpleDateFormat("yyyyMMddHHmmss");
            String fileName = yyyyMMddHHmmss.format(new Date());
            return publicFileRootPath() + File.separator + "Captured" + File.separator + fileName;
        }

        public static String avatarFilePath(String avatarHash, String imessageId, String avatarExtension){
            SimpleDateFormat yyyyMMddHHmmss = new SimpleDateFormat("yyyyMMddHHmmss");
            String fileName = yyyyMMddHHmmss.format(new Date()) + "_" + imessageId + "_" + avatarHash + "." + avatarExtension;
            return publicFileRootPath() + File.separator + "Avatar" + File.separator + fileName;
        }
    }
}
