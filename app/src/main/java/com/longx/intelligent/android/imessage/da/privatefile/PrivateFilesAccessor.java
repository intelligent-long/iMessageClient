package com.longx.intelligent.android.imessage.da.privatefile;

import android.content.Context;

import com.longx.intelligent.android.imessage.da.DataPaths;
import com.longx.intelligent.android.imessage.da.FileHelper;
import com.longx.intelligent.android.imessage.data.ChatMessage;
import com.longx.intelligent.android.imessage.util.FileUtil;

import java.io.IOException;

/**
 * Created by LONG on 2024/1/21 at 8:19 PM.
 */
public class PrivateFilesAccessor {
    public static class ChatImage{
        public static String save(Context context, ChatMessage chatMessage, byte[] imageBytes) throws IOException {
            String other = chatMessage.isSelfSender(context) ? chatMessage.getTo() : chatMessage.getFrom();
            String imageFilePath = DataPaths.PrivateFile.chatImageFilePath(context, other, chatMessage.getUuid());
            return FileHelper.save(imageBytes, imageFilePath);
        }

        public static boolean delete(ChatMessage chatMessage){
            return delete(chatMessage.getImageFilePath());
        }

        public static boolean delete(String imageFilePath){
            return FileUtil.deleteFile(imageFilePath);
        }
    }

    public static class ChatFile{
        public static String save(Context context, ChatMessage chatMessage, byte[] fileBytes) throws IOException {
            String other = chatMessage.isSelfSender(context) ? chatMessage.getTo() : chatMessage.getFrom();
            String chatFileFilePath = DataPaths.PrivateFile.chatFileFilePath(context, other, chatMessage.getFileName());
            return FileHelper.save(fileBytes, chatFileFilePath);
        }

        public static boolean delete(ChatMessage chatMessage){
            return delete(chatMessage.getFileFilePath());
        }

        public static boolean delete(String fileFilePath){
            return FileUtil.deleteFile(fileFilePath);
        }
    }

    public static class ChatVideo{
        public static String save(Context context, ChatMessage chatMessage, byte[] videoBytes) throws IOException {
            String other = chatMessage.isSelfSender(context) ? chatMessage.getTo() : chatMessage.getFrom();
            String chatVideoFilePath = DataPaths.PrivateFile.chatVideoFilePath(context, other, chatMessage.getFileName());
            return FileHelper.save(videoBytes, chatVideoFilePath);
        }

        public static boolean delete(ChatMessage chatMessage){
            return delete(chatMessage.getVideoFilePath());
        }

        public static boolean delete(String videoFilePath){
            return FileUtil.deleteFile(videoFilePath);
        }
    }

    public static class ChatVoice{
        public static String save(Context context, ChatMessage chatMessage, byte[] voiceBytes) throws IOException {
            String other = chatMessage.isSelfSender(context) ? chatMessage.getTo() : chatMessage.getFrom();
            String chatVoiceFilePath = DataPaths.PrivateFile.chatVoiceFilePath(context, other, chatMessage.getUuid());
            return FileHelper.save(voiceBytes, chatVoiceFilePath);
        }

        public static boolean delete(ChatMessage chatMessage){
            return delete(chatMessage.getVoiceFilePath());
        }

        public static boolean delete(String voiceFilePath){
            return FileUtil.deleteFile(voiceFilePath);
        }
    }
}
