package com.longx.intelligent.android.ichat2.permission;

import android.app.Activity;
import android.content.pm.PackageManager;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.longx.intelligent.android.ichat2.procedure.MessageDisplayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by LONG on 2024/4/19 at 3:49 PM.
 */
public class PermissionOperator {
    private final Activity activity;
    private final List<ToRequestPermissions> toRequestPermissionsList;
    private final PermissionResultCallback callback;
    private int currentRequestIndex;

    public PermissionOperator(Activity activity, List<ToRequestPermissions> toRequestPermissionsList, PermissionResultCallback callback) {
        this.activity = activity;
        this.toRequestPermissionsList = toRequestPermissionsList;
        this.callback = callback;
    }

    public interface PermissionResultCallback {
        void onPermissionGranted(int requestCode);
        void onPermissionDenied(int requestCode, List<String> deniedPermissions);
        void onPermissionRationaleShouldBeShown(int requestCode);
    }

    public static class ShowCommonMessagePermissionResultCallback implements PermissionResultCallback{
        private final Activity activity;
        private boolean rationaleShowed;

        public ShowCommonMessagePermissionResultCallback(Activity activity) {
            this.activity = activity;
        }

        @Override
        public void onPermissionGranted(int requestCode) {

        }

        @Override
        public void onPermissionDenied(int requestCode, List<String> deniedPermissions) {
            if(!rationaleShowed) MessageDisplayer.autoShow(activity,
                    "请到系统设置中的此应用信息界面允许权限 > \n" + Arrays.toString(deniedPermissions.toArray(new String[0])),
                    MessageDisplayer.Duration.LONG);
        }

        @Override
        public void onPermissionRationaleShouldBeShown(int requestCode) {
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

    public void startRequestPermissions(LinkPermissionOperatorActivity linkPermissionOperatorActivity){
        linkPermissionOperatorActivity.linkPermissionOperator(this);
        requestPermissions();
    }

    private void requestPermissions() {
        if(currentRequestIndex == toRequestPermissionsList.size()) return;
        ToRequestPermissions toRequestPermissions = toRequestPermissionsList.get(currentRequestIndex);
        if (!hasPermissions(activity, toRequestPermissions)) {
            int requestCode = toRequestPermissions.getRequestCode();
            String[] permissions = toRequestPermissions.getPermissions();
            if (shouldShowRationale(permissions)) {
                callback.onPermissionRationaleShouldBeShown(requestCode);
                ActivityCompat.requestPermissions(activity, permissions, requestCode);
            } else {
                ActivityCompat.requestPermissions(activity, permissions, requestCode);
            }
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
        if(currentRequestIndex == toRequestPermissionsList.size()) return;
        ToRequestPermissions toRequestPermissions = toRequestPermissionsList.get(currentRequestIndex);
        if (toRequestPermissions.getRequestCode() == requestCode) {
            currentRequestIndex ++;
            List<String> deniedPermissions = new ArrayList<>();
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    deniedPermissions.add(permissions[i]);
                }
            }

            if (deniedPermissions.isEmpty()) {
                callback.onPermissionGranted(requestCode);
            } else {
                callback.onPermissionDenied(requestCode, deniedPermissions);
            }
            requestPermissions();
        }
    }
}
