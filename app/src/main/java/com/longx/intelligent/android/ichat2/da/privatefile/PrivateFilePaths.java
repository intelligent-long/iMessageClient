package com.longx.intelligent.android.ichat2.da.privatefile;

import android.content.Context;

/**
 * Created by LONG on 2024/1/21 at 8:20 PM.
 */
public class PrivateFilePaths {
    public static String getPrivateFileFolderPath(Context context){
        return context.getFilesDir().getAbsolutePath();
    }

    public static String getAvatar(Context context, String ichatId, String extension){
        return getPrivateFileFolderPath(context) + "/avatar/" + ichatId + extension;
    }

}
