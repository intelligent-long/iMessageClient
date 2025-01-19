package com.longx.intelligent.android.imessage.service;

import android.content.Context;

import com.longx.intelligent.android.imessage.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.imessage.notification.Notifications;
import com.longx.intelligent.android.imessage.util.DeviceUtil;

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
