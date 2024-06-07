package com.longx.intelligent.android.ichat2.data.request;

/**
 * Created by LONG on 2024/6/8 at 1:04 AM.
 */
public class ChangeWaysToFindMePostBody {
    private boolean byIchatId;
    private boolean byEmail;

    public ChangeWaysToFindMePostBody() {
    }

    public ChangeWaysToFindMePostBody(boolean byIchatId, boolean byEmail) {
        this.byIchatId = byIchatId;
        this.byEmail = byEmail;
    }

    public boolean isByIchatId() {
        return byIchatId;
    }

    public boolean isByEmail() {
        return byEmail;
    }
}
