package com.longx.intelligent.android.ichat2.da.publicfile;

import com.longx.intelligent.android.ichat2.da.DataPaths;
import com.longx.intelligent.android.ichat2.da.FileAccessHelper;
import com.longx.intelligent.android.ichat2.data.ChatMessage;

import java.io.File;

/**
 * Created by LONG on 2024/5/29 at 3:19 PM.
 */
public class PublicFileAccessor {

    public static class ChatImage{
        public static String save(String filePath, ChatMessage chatMessage) {
            String savePath = DataPaths.PublicFile.getChatImageFilePath(chatMessage);
            return FileAccessHelper.save(FileAccessHelper.streamOf(filePath), savePath);
        }
    }
}
