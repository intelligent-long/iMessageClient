package com.longx.intelligent.android.imessage.permission;

import android.os.Build;

/**
 * Created by LONG on 2024/4/21 at 11:03 PM.
 */
public class PermissionRequirementChecker {

    public static boolean needExternalStoragePermission(){
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.Q;
    }

    public static boolean needNotificationPermission(){
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU;
    }

    public static boolean needReadMediaImageAndVideoPermission(){
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU;
    }

    public static boolean needManageExternalStoragePermission(){
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.R;
    }

    public static boolean needBluetoothConnectPermission(){
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.S;
    }

}
