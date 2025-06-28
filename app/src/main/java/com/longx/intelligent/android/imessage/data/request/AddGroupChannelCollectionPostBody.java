package com.longx.intelligent.android.imessage.data.request;

import java.util.List;

/**
 * Created by LONG on 2025/6/26 at 4:04 AM.
 */
public class AddGroupChannelCollectionPostBody {
    private List<String> groupChannelIds;

    public AddGroupChannelCollectionPostBody() {
    }

    public AddGroupChannelCollectionPostBody(List<String> groupChannelIds) {
        this.groupChannelIds = groupChannelIds;
    }

    public List<String> getGroupChannelIds() {
        return groupChannelIds;
    }
}
