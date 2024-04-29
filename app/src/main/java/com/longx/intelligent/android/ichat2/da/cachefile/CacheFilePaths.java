package com.longx.intelligent.android.ichat2.da.cachefile;

import android.content.Context;

import java.io.File;

/**
 * Created by LONG on 2024/4/30 at 12:23 AM.
 */
public class CacheFilePaths {
    public static String getCacheFolderPath(Context context) {
        return context.getCacheDir().getAbsolutePath();
    }

    public static String getAvatarCachePath(Context context, String avatarHash, String extension){
        return getCacheFolderPath(context) + File.separator + "avatar" + File.separator + avatarHash + extension;
    }
}
