package com.longx.intelligent.android.imessage.data.request;

import java.util.List;

/**
 * Created by LONG on 2025/4/14 at 1:46 PM.
 */
public class CreateGroupChannelPostBody {

    private String name;

    private String note;

    private List<String> newTagNames;

    private List<String> toAddTagIds;

    public CreateGroupChannelPostBody() {
    }

    public CreateGroupChannelPostBody(String name, String note, List<String> newTagNames, List<String> toAddTagIds) {
        this.name = name;
        this.note = note;
        this.newTagNames = newTagNames;
        this.toAddTagIds = toAddTagIds;
    }

    public String getName() {
        return name;
    }

    public String getNote() {
        return note;
    }

    public List<String> getNewTagNames() {
        return newTagNames;
    }

    public List<String> getToAddTagIds() {
        return toAddTagIds;
    }
}
