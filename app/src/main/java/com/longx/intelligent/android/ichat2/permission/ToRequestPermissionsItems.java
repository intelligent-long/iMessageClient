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

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public static ToRequestPermissions readMediaImagesAndVideos = new ToRequestPermissions(102,
            new String[]{Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO});

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static ToRequestPermissions accessMediaLocation = new ToRequestPermissions(103,
            new String[]{Manifest.permission.ACCESS_MEDIA_LOCATION});

    @RequiresApi(api = Build.VERSION_CODES.R)
    public static ToRequestPermissions manageExternalStorage = new ToRequestPermissions(104,
            new String[]{Manifest.permission.MANAGE_EXTERNAL_STORAGE});

}
