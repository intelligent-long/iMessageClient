package com.longx.intelligent.android.ichat2.permission;

import android.Manifest;
import android.os.Build;

import androidx.annotation.RequiresApi;

/**
 * Created by LONG on 2024/4/19 at 5:19 PM.
 */
public class ToRequestPermissionsItems {

    public static ToRequestPermissions writeAndReadExternalStorage = new ToRequestPermissions(100,
            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE});

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public static ToRequestPermissions showNotification = new ToRequestPermissions(101,
            new String[]{Manifest.permission.POST_NOTIFICATIONS});

}
