package com.longx.intelligent.android.imessage.data.request;

/**
 * Created by LONG on 2024/6/8 at 1:04 AM.
 */
public class ChangeWaysToFindMePostBody {
    private boolean byImessageId;
    private boolean byEmail;

    public ChangeWaysToFindMePostBody() {
    }

    public ChangeWaysToFindMePostBody(boolean byImessageId, boolean byEmail) {
        this.byImessageId = byImessageId;
        this.byEmail = byEmail;
    }

    public boolean isByImessageId() {
        return byImessageId;
    }

    public boolean isByEmail() {
        return byEmail;
    }
}
