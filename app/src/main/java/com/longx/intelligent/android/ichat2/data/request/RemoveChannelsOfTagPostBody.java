package com.longx.intelligent.android.ichat2.data.request;

import java.util.List;

/**
 * Created by LONG on 2024/6/4 at 5:22 PM.
 */
public class RemoveChannelsOfTagPostBody {
    private String tagId;

    private List<String> channelIchatIdList;

    public RemoveChannelsOfTagPostBody() {
    }

    public RemoveChannelsOfTagPostBody(String tagId, List<String> channelIchatIdList) {
        this.tagId = tagId;
        this.channelIchatIdList = channelIchatIdList;
    }

    public String getTagId() {
        return tagId;
    }

    public List<String> getChannelIchatIdList() {
        return channelIchatIdList;
    }
}
