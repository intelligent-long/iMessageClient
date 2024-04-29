package com.longx.intelligent.android.ichat2.permission;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.longx.intelligent.android.ichat2.behavior.MessageDisplayer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LONG on 2024/4/19 at 3:49 PM.
 */
public class PermissionOperator {
    private final Activity activity;
    private final ToRequestPermissions toRequestPermissions;
    private final PermissionResultCallback callback;

    public PermissionOperator(Activity activity, ToRequestPermissions toRequestPermissions, PermissionResultCallback callback) {
        this.activity = activity;
        this.toRequestPermissions = toRequestPermissions;
        this.callback = callback;
    }

    public interface PermissionResultCallback {
        void onPermissionGranted();
        void onPermissionDenied(List<String> deniedPermissions);
        void onPermissionRationaleShouldBeShown();
    }

    public static class ShowCommonMessagePermissionResultCallback implements PermissionResultCallback{
        private final Activity activity;
        private boolean rationaleShowed;

        public ShowCommonMessagePermissionResultCallback(Activity activity) {
            this.activity = activity;
        }

        @Override
        public void onPermissionGranted() {

        }

        @Override
        public void onPermissionDenied(List<String> deniedPermissions) {
            if(!rationaleShowed) MessageDisplayer.autoShow(activity, "请到系统设置中的此应用信息界面允许权限", MessageDisplayer.Duration.LONG);
        }

        @Override
        public void onPermissionRationaleShouldBeShown() {
            rationaleShowed = true;
            MessageDisplayer.autoShow(activity, "请允许使用权限", MessageDisplayer.Duration.LONG);
        }
    }

    public static boolean hasPermissions(Activity activity, ToRequestPermissions toRequestPermissions){
        for (String permission : toRequestPermissions.getPermissions()) {
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public boolean requestPermissions(LinkPermissionOperatorActivity linkPermissionOperatorActivity) {
        if (!hasPermissions(activity, toRequestPermissions)) {
            linkPermissionOperatorActivity.linkPermissionOperator(this);
            int requestCode = toRequestPermissions.getRequestCode();
            String[] permissions = toRequestPermissions.getPermissions();
            if (shouldShowRationale(permissions)) {
                callback.onPermissionRationaleShouldBeShown();
                ActivityCompat.requestPermissions(activity, permissions, requestCode);
            } else {
                ActivityCompat.requestPermissions(activity, permissions, requestCode);
            }
            return true;
        } else {
            return false;
        }
    }

    private boolean shouldShowRationale(String[] permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                return true;
            }
        }
        return false;
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (this.toRequestPermissions.getRequestCode() == requestCode) {
            List<String> deniedPermissions = new ArrayList<>();
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    deniedPermissions.add(permissions[i]);
                }
            }

            if (deniedPermissions.isEmpty()) {
                callback.onPermissionGranted();
            } else {
                callback.onPermissionDenied(deniedPermissions);
            }
        }
    }
}
