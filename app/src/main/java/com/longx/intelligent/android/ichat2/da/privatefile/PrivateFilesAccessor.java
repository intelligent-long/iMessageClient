package com.longx.intelligent.android.ichat2.da.privatefile;

import android.content.Context;

import com.longx.intelligent.android.ichat2.da.DataPaths;
import com.longx.intelligent.android.ichat2.da.FileAccessHelper;
import com.longx.intelligent.android.ichat2.data.ChatMessage;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by LONG on 2024/1/21 at 8:19 PM.
 */
public class PrivateFilesAccessor {
    public static class ChatImage{
        public static String save(Context context, ChatMessage chatMessage) throws IOException {
            String other = chatMessage.isSelfSender(context) ? chatMessage.getTo() : chatMessage.getFrom();
            String imageFilePath = DataPaths.PrivateFile.getChatImageFilePath(context, other, chatMessage.getUuid());
            byte[] imageBytes = chatMessage.getImageBytes();
            return FileAccessHelper.save(imageBytes, imageFilePath);
        }

        public static InputStream streamOf(String path){
            return FileAccessHelper.streamOf(path);
        }
    }

    public static class ChatFile{
        public static String save(Context context, ChatMessage chatMessage) throws IOException {
            String other = chatMessage.isSelfSender(context) ? chatMessage.getTo() : chatMessage.getFrom();
            String chatFileFilePath = DataPaths.PrivateFile.getChatFileFilePath(context, other, chatMessage.getUuid());
            byte[] fileBytes = chatMessage.getFileBytes();
            return FileAccessHelper.save(fileBytes, chatFileFilePath);
        }
    }
}
