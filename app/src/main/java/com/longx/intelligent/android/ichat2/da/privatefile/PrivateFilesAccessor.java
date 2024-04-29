package com.longx.intelligent.android.ichat2.da.privatefile;

import android.content.Context;

import com.longx.intelligent.android.ichat2.da.FileAccessor;
import com.longx.intelligent.android.ichat2.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.ichat2.data.SelfInfo;

import java.io.File;
import java.io.InputStream;

/**
 * Created by LONG on 2024/1/21 at 8:19 PM.
 */
public class PrivateFilesAccessor {
    public static void saveAvatar(Context context, InputStream contentStream, String ichatId, String extension){
        String path = PrivateFilePaths.getAvatar(context, ichatId, extension);
        FileAccessor.save(contentStream, path);
    }

    public static InputStream getCurrentUserAvatar(Context context){
        SelfInfo selfInfo = SharedPreferencesAccessor.UserInfoPref.getCurrentUserInfo(context);
        return getAvatar(context, selfInfo.getIchatId(), selfInfo.getAvatarExtension());
    }

    public static File getCurrentUserAvatarFile(Context context) {
        SelfInfo selfInfo = SharedPreferencesAccessor.UserInfoPref.getCurrentUserInfo(context);
        return getAvatarFile(context, selfInfo.getIchatId(), selfInfo.getAvatarExtension());
    }

    public static InputStream getAvatar(Context context, String ichatId, String extension){
        String path = PrivateFilePaths.getAvatar(context, ichatId, extension);
        return FileAccessor.streamOf(path);
    }

    public static File getAvatarFile(Context context, String ichatId, String extension) {
        String path = PrivateFilePaths.getAvatar(context, ichatId, extension);
        return new File(path);
    }

}
