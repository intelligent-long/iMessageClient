package com.longx.intelligent.android.imessage.data.request;


import java.util.List;

/**
 * Created by LONG on 2025/6/11 at 4:49 AM.
 */
public class ManageGroupChannelDisconnectPostBody {
    private List<String> channelIds;

    public ManageGroupChannelDisconnectPostBody() {
    }

    public ManageGroupChannelDisconnectPostBody(List<String> channelIds) {
        this.channelIds = channelIds;
    }

    public List<String> getChannelIds() {
        return channelIds;
    }
}
