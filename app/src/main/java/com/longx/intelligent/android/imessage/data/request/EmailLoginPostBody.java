package com.longx.intelligent.android.imessage.data.request;

/**
 * Created by LONG on 2024/3/31 at 5:06 PM.
 */
public class EmailLoginPostBody {
    private final String email;
    private final String password;

    public EmailLoginPostBody() {
        this(null, null);
    }

    public EmailLoginPostBody(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
