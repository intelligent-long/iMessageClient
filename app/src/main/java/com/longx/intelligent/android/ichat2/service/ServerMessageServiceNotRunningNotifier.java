package com.longx.intelligent.android.ichat2.service;

import android.content.Context;
import android.os.Environment;

import com.longx.intelligent.android.ichat2.da.FileHelper;
import com.longx.intelligent.android.ichat2.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.ichat2.notification.Notifications;
import com.longx.intelligent.android.ichat2.util.DeviceUtil;
import com.longx.intelligent.android.ichat2.util.ErrorLogger;
import com.longx.intelligent.android.ichat2.util.FileUtil;

import java.io.File;
import java.io.IOException;

/**
 * Created by LONG on 2024/4/6 at 5:24 PM.
 */
public class ServerMessageServiceNotRunningNotifier {
    private static final long MAX_ALLOW_INTERVAL_MILLI_SEC = 60 * 60 * 1000;

    public static void recordAndNotify(Context context, long now){
        long last = SharedPreferencesAccessor.ServerMessageServicePref.getRunningTime(context);
        if(last != -1 && now - last > MAX_ALLOW_INTERVAL_MILLI_SEC && now - DeviceUtil.getBootTime() > MAX_ALLOW_INTERVAL_MILLI_SEC){
            Notifications.notifyServerMessageServiceNotRunning(context);
        }
        SharedPreferencesAccessor.ServerMessageServicePref.recordRunningTime(context, now);
    }
}
