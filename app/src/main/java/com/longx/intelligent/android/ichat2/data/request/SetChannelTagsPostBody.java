package com.longx.intelligent.android.ichat2.data.request;

import java.util.List;

/**
 * Created by LONG on 2024/6/6 at 1:38 AM.
 */
public class SetChannelTagsPostBody {
    private String channelIchatId;
    private List<String> newTagNames;
    private List<String> toAddTagIds;
    private List<String> toRemoveTagIds;

    public SetChannelTagsPostBody() {
    }

    public SetChannelTagsPostBody(String channelIchatId, List<String> newTagNames, List<String> toAddTagIds, List<String> toRemoveTagIds) {
        this.channelIchatId = channelIchatId;
        this.newTagNames = newTagNames;
        this.toAddTagIds = toAddTagIds;
        this.toRemoveTagIds = toRemoveTagIds;
    }

    public String getChannelIchatId() {
        return channelIchatId;
    }

    public List<String> getNewTagNames() {
        return newTagNames;
    }

    public List<String> getToAddTagIds() {
        return toAddTagIds;
    }

    public List<String> getToRemoveTagIds() {
        return toRemoveTagIds;
    }
}
