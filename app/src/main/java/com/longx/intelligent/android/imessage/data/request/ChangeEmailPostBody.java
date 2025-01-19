package com.longx.intelligent.android.imessage.data.request;

/**
 * Created by LONG on 2024/4/8 at 12:13 AM.
 */
public class ChangeEmailPostBody {
    private final String email;

    private final String verifyCode;

    public ChangeEmailPostBody() {
        this(null, null);
    }

    public ChangeEmailPostBody(String email, String verifyCode) {
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
