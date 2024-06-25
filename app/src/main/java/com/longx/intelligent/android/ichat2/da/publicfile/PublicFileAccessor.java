package com.longx.intelligent.android.ichat2.da.publicfile;

import android.content.Context;

import com.longx.intelligent.android.ichat2.da.DataPaths;
import com.longx.intelligent.android.ichat2.da.FileAccessHelper;
import com.longx.intelligent.android.ichat2.data.ChatMessage;
import com.longx.intelligent.android.ichat2.util.ErrorLogger;
import com.longx.intelligent.android.ichat2.util.FileUtil;

import java.io.File;
import java.io.IOException;

/**
 * Created by LONG on 2024/5/29 at 3:19 PM.
 */
public class PublicFileAccessor {

    public static class ChatImage{
        public static String save(ChatMessage chatMessage) throws IOException {
            String savePath = DataPaths.PublicFile.getChatImageFilePath(chatMessage);
            return FileAccessHelper.save(FileAccessHelper.streamOf(chatMessage.getImageFilePath()), savePath);
        }
    }

    public static class ChatVideo{
        public static String save(ChatMessage chatMessage) throws IOException {
            String savePath = DataPaths.PublicFile.getChatVideoFilePath(chatMessage);
            return FileAccessHelper.save(FileAccessHelper.streamOf(chatMessage.getVideoFilePath()), savePath);
        }
    }

    public static class CapturedMedia{
        public static File createPhotoFile() throws IOException {
            String filePath = DataPaths.PublicFile.getCapturedChatPhotoFilePath();
            return FileAccessHelper.createFile(filePath);
        }

        public static File detectAndRenamePhotoFile(File photoFile){
            String fileExtension = FileAccessHelper.detectFileExtension(photoFile);
            File parentFile = photoFile.getParentFile();
            String fileName;
            if(parentFile != null) {
                fileName = parentFile.getAbsolutePath() + File.separator
                        + FileUtil.getFileBaseName(photoFile) + "." + fileExtension;
            }else {
                fileName = FileUtil.getFileBaseName(photoFile) + "." + fileExtension;
            }
            File renamedFile = new File(fileName);
            boolean success = photoFile.renameTo(renamedFile);
            if(success) {
                return renamedFile;
            }else {
                return null;
            }
        }
    }
}
