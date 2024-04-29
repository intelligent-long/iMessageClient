package com.longx.intelligent.android.ichat2.data.request;

/**
 * Created by LONG on 2024/3/30 at 12:25 AM.
 */
public class RegistrationPostBody {
    private final String email;
    private final String username;
    private final String password;
    private final String verifyCode;

    public RegistrationPostBody(String email, String username, String password, String verifyCode) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.verifyCode = verifyCode;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getVerifyCode() {
        return verifyCode;
    }
}
