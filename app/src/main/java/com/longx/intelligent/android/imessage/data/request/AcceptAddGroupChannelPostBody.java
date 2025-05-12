package com.longx.intelligent.android.imessage.data.request;

/**
 * Created by LONG on 2024/5/8 at 1:19 AM.
 */
public class AcceptAddGroupChannelPostBody {

    private String uuid;

    public AcceptAddGroupChannelPostBody() {
    }

    public AcceptAddGroupChannelPostBody(String uuid) {
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }
}
