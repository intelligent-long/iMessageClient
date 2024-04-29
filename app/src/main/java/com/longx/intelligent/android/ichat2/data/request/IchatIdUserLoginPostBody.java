package com.longx.intelligent.android.ichat2.data.request;

/**
 * Created by LONG on 2024/3/30 at 5:26 PM.
 */
public class IchatIdUserLoginPostBody {
    private final String ichatIdUser;

    private final String password;

    public IchatIdUserLoginPostBody() {
        this(null, null);
    }

    public IchatIdUserLoginPostBody(String ichatIdUser, String password) {
        this.ichatIdUser = ichatIdUser;
        this.password = password;
    }

    public String getIchatIdUser() {
        return ichatIdUser;
    }

    public String getPassword() {
        return password;
    }
}
