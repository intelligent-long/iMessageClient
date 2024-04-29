package com.longx.intelligent.android.ichat2.data.request;

/**
 * Created by LONG on 2024/3/30 at 3:38 PM.
 */
public class SendVerifyCodePostBody {

    private final String email;

    public SendVerifyCodePostBody() {
        this(null);
    }

    public SendVerifyCodePostBody(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
