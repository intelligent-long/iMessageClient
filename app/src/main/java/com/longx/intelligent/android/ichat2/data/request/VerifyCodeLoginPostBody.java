package com.longx.intelligent.android.ichat2.data.request;

/**
 * Created by LONG on 2024/3/31 at 5:30 PM.
 */
public class VerifyCodeLoginPostBody {
    private final String email;

    private final String verifyCode;

    public VerifyCodeLoginPostBody() {
        this(null, null);
    }

    public VerifyCodeLoginPostBody(String email, String verifyCode) {
        this.email = email;
        this.verifyCode = verifyCode;
    }

    public String getEmail() {
        return email;
    }

    public String getVerifyCode() {
        return verifyCode;
    }
}
