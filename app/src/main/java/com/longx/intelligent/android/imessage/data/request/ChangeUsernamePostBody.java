package com.longx.intelligent.android.imessage.data.request;

/**
 * Created by LONG on 2024/4/7 at 11:52 PM.
 */
public class ChangeUsernamePostBody {

    private final String username;

    public ChangeUsernamePostBody() {
        this(null);
    }

    public ChangeUsernamePostBody(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
