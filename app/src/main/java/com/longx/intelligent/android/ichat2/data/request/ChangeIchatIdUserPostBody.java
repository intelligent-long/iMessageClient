package com.longx.intelligent.android.ichat2.data.request;

/**
 * Created by LONG on 2024/4/3 at 9:43 PM.
 */
public class ChangeIchatIdUserPostBody {
    private final String ichatIdUser;

    public ChangeIchatIdUserPostBody() {
        this(null);
    }

    public ChangeIchatIdUserPostBody(String ichatIdUser) {
        this.ichatIdUser = ichatIdUser;
    }

    public String getIchatIdUser() {
        return ichatIdUser;
    }
}
