package com.longx.intelligent.android.ichat2.permission;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.PowerManager;
import android.provider.Settings;

import com.longx.intelligent.android.ichat2.util.ErrorLogger;

/**
 * Created by LONG on 2024/4/21 at 10:05 PM.
 */
public class BatteryRestrictionOperator {
    public static final int REQUEST_CODE = 100;

    public static boolean isIgnoringBatteryOptimizations(Context context) {
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (powerManager != null) {
            return powerManager.isIgnoringBatteryOptimizations(context.getPackageName());
        }
        return true;
    }

    @SuppressLint("BatteryLife")
    public static boolean requestIgnoreBatteryOptimizations(Activity activity){
        try {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + activity.getPackageName()));
            activity.startActivityForResult(intent, REQUEST_CODE);
            return true;
        }catch (Exception e){
            ErrorLogger.log(BatteryRestrictionOperator.class, e);
            return false;
        }
    }
}
