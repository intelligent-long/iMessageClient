package com.longx.intelligent.android.imessage.data.request;

/**
 * Created by LONG on 2024/3/30 at 5:26 PM.
 */
public class ImessageIdUserLoginPostBody {
    private final String imessageIdUser;

    private final String password;

    public ImessageIdUserLoginPostBody() {
        this(null, null);
    }

    public ImessageIdUserLoginPostBody(String imessageIdUser, String password) {
        this.imessageIdUser = imessageIdUser;
        this.password = password;
    }

    public String getImessageIdUser() {
        return imessageIdUser;
    }

    public String getPassword() {
        return password;
    }
}
