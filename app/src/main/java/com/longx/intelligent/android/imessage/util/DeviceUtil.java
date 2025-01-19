package com.longx.intelligent.android.imessage.util;

import android.os.SystemClock;

/**
 * Created by LONG on 2024/1/25 at 1:03 PM.
 */
public class DeviceUtil {

    public static long getBootTime(){
        return System.currentTimeMillis() - SystemClock.elapsedRealtime();
    }
}
