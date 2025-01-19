package com.longx.intelligent.android.imessage.da.cachefile;

import android.content.Context;

import com.longx.intelligent.android.imessage.da.DataPaths;

import java.io.File;

/**
 * Created by LONG on 2024/4/30 at 12:34 AM.
 */
public class CacheFilesAccessor {

    public static class ChatMessage{
        public static String prepareChatVoiceTempFile(Context context, String ichatId){
            String voiceTempFilePath = DataPaths.Cache.chatVoiceTempFilePath(context, ichatId);
            File file = new File(voiceTempFilePath);
            file.getParentFile().mkdirs();
            file.delete();
            return voiceTempFilePath;
        }
    }

    public static class App{
        public static String prepareAppUpdateCacheFile(Context context){
            String appUpdateCacheFilePath = DataPaths.Cache.appUpdateCacheFilePath(context);
            File file = new File(appUpdateCacheFilePath);
            file.getParentFile().mkdirs();
            file.delete();
            return appUpdateCacheFilePath;
        }
    }

}
