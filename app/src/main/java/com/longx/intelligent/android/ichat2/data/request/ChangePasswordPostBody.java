package com.longx.intelligent.android.ichat2.data.request;

/**
 * Created by LONG on 2024/4/2 at 5:04 PM.
 */
public class ChangePasswordPostBody {
    private final String password;

    private final String verifyCode;

    public ChangePasswordPostBody() {
        this(null, null);
    }

    public ChangePasswordPostBody(String password, String verifyCode) {
        this.password = password;
        this.verifyCode = verifyCode;
    }

    public String getPassword() {
        return password;
    }

    public String getVerifyCode() {
        return verifyCode;
    }
}
