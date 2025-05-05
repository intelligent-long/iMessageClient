package com.longx.intelligent.android.imessage.data.request;

import java.util.List;

/**
 * Created by LONG on 2024/6/4 at 5:22 PM.
 */
public class AddGroupChannelsToTagPostBody {
    private String tagId;

    private List<String> groupChannelIdList;

    public AddGroupChannelsToTagPostBody() {
    }

    public AddGroupChannelsToTagPostBody(String tagId, List<String> groupChannelIdList) {
        this.tagId = tagId;
        this.groupChannelIdList = groupChannelIdList;
    }

    public String getTagId() {
        return tagId;
    }

    public List<String> getGroupChannelIdList() {
        return groupChannelIdList;
    }
}
