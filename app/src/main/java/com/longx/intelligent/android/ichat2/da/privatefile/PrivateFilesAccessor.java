package com.longx.intelligent.android.ichat2.da.privatefile;

import android.content.Context;

import com.longx.intelligent.android.ichat2.da.DataPaths;
import com.longx.intelligent.android.ichat2.da.FileAccessHelper;
import com.longx.intelligent.android.ichat2.data.ChatMessage;
import com.longx.intelligent.android.ichat2.util.ErrorLogger;
import com.longx.intelligent.android.ichat2.util.FileUtil;

import org.apache.commons.codec.binary.Base64;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * Created by LONG on 2024/1/21 at 8:19 PM.
 */
public class PrivateFilesAccessor {
    public static class ChatImage{
        public static String save(Context context, ChatMessage chatMessage) {
            String other = chatMessage.isSelfSender(context) ? chatMessage.getTo() : chatMessage.getFrom();
            String imageFilePath = DataPaths.PrivateFile.getChatMessageImageFilePath(context, other, chatMessage.getUuid());
            byte[] imageBytes = chatMessage.getImageBytes();
            if(FileAccessHelper.save(imageBytes, imageFilePath)){
                return imageFilePath;
            }else {
                return null;
            }
        }

        public static InputStream streamOf(String path){
            return FileAccessHelper.streamOf(path);
        }
    }
}
