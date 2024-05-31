package com.longx.intelligent.android.ichat2.data.request;

/**
 * Created by LONG on 2024/5/31 at 3:50 PM.
 */
public class DeleteChannelAssociationPostBody {
    private String channelIchatId;

    public DeleteChannelAssociationPostBody() {
    }

    public DeleteChannelAssociationPostBody(String channelIchatId) {
        this.channelIchatId = channelIchatId;
    }

    public String getChannelIchatId() {
        return channelIchatId;
    }
}
