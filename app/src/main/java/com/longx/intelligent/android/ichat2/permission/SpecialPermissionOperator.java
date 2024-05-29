package com.longx.intelligent.android.ichat2.permission;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;
import android.provider.Settings;

import androidx.annotation.RequiresApi;

import com.longx.intelligent.android.ichat2.util.ErrorLogger;

/**
 * Created by LONG on 2024/4/21 at 10:05 PM.
 */
public class SpecialPermissionOperator {
    public static final int IGNORE_BATTERY_OPTIMIZATIONS_REQUEST_CODE = 100;
    public static final int MANAGE_EXTERNAL_STORAGE_REQUEST_CODE = 101;

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
            activity.startActivityForResult(intent, IGNORE_BATTERY_OPTIMIZATIONS_REQUEST_CODE);
            return true;
        }catch (Exception e){
            ErrorLogger.log(SpecialPermissionOperator.class, e);
            return false;
        }
    }

    public static boolean isExternalStorageManager(){
        if (PermissionUtil.needManageExternalStoragePermission()) {
            return Environment.isExternalStorageManager();
        }else {
            return false;
        }
    }

    public static boolean requestManageExternalStorage(Activity activity){
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                activity.startActivityForResult(intent, MANAGE_EXTERNAL_STORAGE_REQUEST_CODE);
                return true;
            }else {
                return false;
            }
        }catch (Exception e){
            ErrorLogger.log(SpecialPermissionOperator.class, e);
            return false;
        }
    }
}
