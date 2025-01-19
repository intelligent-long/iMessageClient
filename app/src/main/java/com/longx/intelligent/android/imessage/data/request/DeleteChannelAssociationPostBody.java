package com.longx.intelligent.android.imessage.data.request;

/**
 * Created by LONG on 2024/5/31 at 3:50 PM.
 */
public class DeleteChannelAssociationPostBody {
    private String channelImessageId;

    public DeleteChannelAssociationPostBody() {
    }

    public DeleteChannelAssociationPostBody(String channelImessageId) {
        this.channelImessageId = channelImessageId;
    }

    public String getChannelImessageId() {
        return channelImessageId;
    }
}
