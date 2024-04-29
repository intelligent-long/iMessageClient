package com.longx.intelligent.android.ichat2.data.request;

/**
 * Created by LONG on 2024/3/31 at 6:14 PM.
 */
public class ResetPasswordPostBody {
    private final String email;
    private final String password;
    private final String verifyCode;

    public ResetPasswordPostBody() {
        this(null, null, null);
    }

    public ResetPasswordPostBody(String email, String password, String verifyCode) {
        this.email = email;
        this.password = password;
        this.verifyCode = verifyCode;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getVerifyCode() {
        return verifyCode;
    }
}
