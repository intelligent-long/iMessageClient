package com.longx.intelligent.android.ichat2.permission;

/**
 * Created by LONG on 2024/4/19 at 5:03 PM.
 */
public class ToRequestPermissions {
    private final int requestCode;
    private final String[] permissions;

    public ToRequestPermissions(int requestCode, String[] permissions) {
        this.requestCode = requestCode;
        this.permissions = permissions;
    }

    public int getRequestCode() {
        return requestCode;
    }

    public String[] getPermissions() {
        return permissions;
    }
}
