package com.longx.intelligent.android.ichat2.net;

import android.content.Context;

import com.longx.intelligent.android.ichat2.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.ichat2.data.ServerSetting;

/**
 * Created by LONG on 2024/1/12 at 4:38 PM.
 */
public class ServerProperties {
    public static final boolean DEFAULT_USE_CENTRAL = true;
    public static final String CENTRAL_DATA_FOLDER = "CENTRAL";
    public static final String DEFAULT_HOST = "localhost";
    public static final int DEFAULT_PORT = 10000;

    public static String getBaseUrl(Context context){
        ServerSetting serverSetting = SharedPreferencesAccessor.ServerSettingPref.getServerSetting(context);
        return  "http://" + serverSetting.getHost() + ":" + serverSetting.getPort() + "/";
    }
}
