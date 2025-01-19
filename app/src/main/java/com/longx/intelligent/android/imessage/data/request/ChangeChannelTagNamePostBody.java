package com.longx.intelligent.android.imessage.data.request;

/**
 * Created by LONG on 2024/6/4 at 12:34 AM.
 */
public class ChangeChannelTagNamePostBody {
    private String tagId;
    private String name;

    public ChangeChannelTagNamePostBody() {
    }

    public ChangeChannelTagNamePostBody(String tagId, String name) {
        this.tagId = tagId;
        this.name = name;
    }

    public String getTagId() {
        return tagId;
    }

    public String getName() {
        return name;
    }
}
