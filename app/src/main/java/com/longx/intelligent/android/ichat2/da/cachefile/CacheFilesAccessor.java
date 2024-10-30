package com.longx.intelligent.android.ichat2.da.cachefile;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;

import com.longx.intelligent.android.ichat2.da.DataPaths;
import com.longx.intelligent.android.ichat2.media.helper.MediaHelper;
import com.longx.intelligent.android.ichat2.util.ErrorLogger;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by LONG on 2024/4/30 at 12:34 AM.
 */
public class CacheFilesAccessor {

    public static class ChatMessage{
        public static String prepareChatVoiceTempFile(Context context, String ichatId){
            String voiceTempFilePath = DataPaths.Cache.getChatVoiceTempFilePath(context, ichatId);
            File file = new File(voiceTempFilePath);
            file.getParentFile().mkdirs();
            file.delete();
            return voiceTempFilePath;
        }
    }

    public static class App{
        public static String prepareAppUpdateCacheFile(Context context){
            String appUpdateCacheFilePath = DataPaths.Cache.getAppUpdateCacheFilePath(context);
            File file = new File(appUpdateCacheFilePath);
            file.getParentFile().mkdirs();
            file.delete();
            return appUpdateCacheFilePath;
        }
    }

}
