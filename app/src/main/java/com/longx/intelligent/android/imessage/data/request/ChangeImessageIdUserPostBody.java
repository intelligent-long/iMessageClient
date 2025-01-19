package com.longx.intelligent.android.imessage.data.request;

/**
 * Created by LONG on 2024/4/3 at 9:43 PM.
 */
public class ChangeImessageIdUserPostBody {
    private final String imessageIdUser;

    public ChangeImessageIdUserPostBody() {
        this(null);
    }

    public ChangeImessageIdUserPostBody(String imessageIdUser) {
        this.imessageIdUser = imessageIdUser;
    }

    public String getImessageIdUser() {
        return imessageIdUser;
    }
}
