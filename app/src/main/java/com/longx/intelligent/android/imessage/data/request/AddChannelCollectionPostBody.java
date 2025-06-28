package com.longx.intelligent.android.imessage.data.request;

import com.longx.intelligent.android.imessage.data.ChannelCollectionItem;

import java.util.List;

/**
 * Created by LONG on 2025/6/26 at 4:04 AM.
 */
public class AddChannelCollectionPostBody {
    private List<String> channelIds;

    public AddChannelCollectionPostBody() {
    }

    public AddChannelCollectionPostBody(List<String> channelIds) {
        this.channelIds = channelIds;
    }

    public List<String> getChannelIds() {
        return channelIds;
    }
}
