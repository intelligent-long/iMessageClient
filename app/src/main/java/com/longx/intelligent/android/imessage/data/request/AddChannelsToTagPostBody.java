package com.longx.intelligent.android.imessage.data.request;

import java.util.List;

/**
 * Created by LONG on 2024/6/4 at 5:22 PM.
 */
public class AddChannelsToTagPostBody {
    private String tagId;
    private List<String> channelImessageIdList;

    public AddChannelsToTagPostBody() {
    }

    public AddChannelsToTagPostBody(String tagId, List<String> channelImessageIdList) {
        this.tagId = tagId;
        this.channelImessageIdList = channelImessageIdList;
    }

    public String getTagId() {
        return tagId;
    }

    public List<String> getChannelImessageIdList() {
        return channelImessageIdList;
    }
}
