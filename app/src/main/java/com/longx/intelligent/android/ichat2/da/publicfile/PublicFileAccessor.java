package com.longx.intelligent.android.ichat2.da.publicfile;

import com.longx.intelligent.android.ichat2.da.DataPaths;
import com.longx.intelligent.android.ichat2.da.FileHelper;
import com.longx.intelligent.android.ichat2.data.ChatMessage;

import java.io.File;
import java.io.IOException;

/**
 * Created by LONG on 2024/5/29 at 3:19 PM.
 */
public class PublicFileAccessor {

    public static class ChatImage{
        public static String save(ChatMessage chatMessage) throws IOException {
            String savePath = DataPaths.PublicFile.getChatImageFilePath(chatMessage);
            return FileHelper.save(FileHelper.streamOf(chatMessage.getImageFilePath()), savePath);
        }
    }

    public static class ChatVideo{
        public static String save(ChatMessage chatMessage) throws IOException {
            String savePath = DataPaths.PublicFile.getChatVideoFilePath(chatMessage);
            return FileHelper.save(FileHelper.streamOf(chatMessage.getVideoFilePath()), savePath);
        }
    }

    public static class CapturedMedia{
        public static File createPhotoFile() throws IOException {
            String filePath = DataPaths.PublicFile.getCapturedChatPhotoFilePath();
            return FileHelper.createFile(filePath);
        }

        public static File createVideoFile() throws IOException {
            String filePath = DataPaths.PublicFile.getCapturedChatVideoFilePath();
            return FileHelper.createFile(filePath);
        }
    }
}
