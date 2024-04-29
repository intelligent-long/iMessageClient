package com.longx.intelligent.android.ichat2.da;

import android.content.Context;

import com.longx.intelligent.android.ichat2.da.sharedpref.SharedPreferencesAccessor;

import java.io.File;

/**
 * Created by LONG on 2024/3/28 at 6:05 PM.
 */
public class DataPaths {
    public static String getDataFolderPath(Context context) {
        return context.getDataDir().getAbsolutePath() +
                File.separator + "data" +
                File.separator + SharedPreferencesAccessor.ServerSettingPref.getServerSetting(context).getDataFolder();
    }
}
