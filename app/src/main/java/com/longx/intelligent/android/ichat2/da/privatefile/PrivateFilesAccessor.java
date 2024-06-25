package com.longx.intelligent.android.ichat2.da.privatefile;

import android.content.Context;

import com.longx.intelligent.android.ichat2.da.DataPaths;
import com.longx.intelligent.android.ichat2.da.FileAccessHelper;
import com.longx.intelligent.android.ichat2.data.ChatMessage;
import com.longx.intelligent.android.ichat2.util.FileUtil;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by LONG on 2024/1/21 at 8:19 PM.
 */
public class PrivateFilesAccessor {
    public static class ChatImage{
        public static String save(Context context, ChatMessage chatMessage, byte[] imageBytes) throws IOException {
            String other = chatMessage.isSelfSender(context) ? chatMessage.getTo() : chatMessage.getFrom();
            String imageFilePath = DataPaths.PrivateFile.getChatImageFilePath(context, other, chatMessage.getUuid());
            return FileAccessHelper.save(imageBytes, imageFilePath);
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
            String chatFileFilePath = DataPaths.PrivateFile.getChatFileFilePath(context, other, chatMessage.getFileName());
            return FileAccessHelper.save(fileBytes, chatFileFilePath);
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
            String chatVideoFilePath = DataPaths.PrivateFile.getChatVideoFilePath(context, other, chatMessage.getFileName());
            return FileAccessHelper.save(videoBytes, chatVideoFilePath);
        }

        public static boolean delete(ChatMessage chatMessage){
            return delete(chatMessage.getVideoFilePath());
        }

        public static boolean delete(String videoFilePath){
            return FileUtil.deleteFile(videoFilePath);
        }
    }
}
