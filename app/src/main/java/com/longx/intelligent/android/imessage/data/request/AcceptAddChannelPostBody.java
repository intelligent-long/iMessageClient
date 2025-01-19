package com.longx.intelligent.android.imessage.data.request;

/**
 * Created by LONG on 2024/5/8 at 1:19 AM.
 */
public class AcceptAddChannelPostBody {

    private String uuid;

    public AcceptAddChannelPostBody() {
    }

    public AcceptAddChannelPostBody(String uuid) {
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }
}
