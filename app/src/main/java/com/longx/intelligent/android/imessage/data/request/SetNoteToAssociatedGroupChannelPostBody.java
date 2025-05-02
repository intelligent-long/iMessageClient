package com.longx.intelligent.android.imessage.data.request;

/**
 * Created by LONG on 2024/6/2 at 1:50 AM.
 */
public class SetNoteToAssociatedGroupChannelPostBody {
    private String groupChannelId;
    private String note;

    public SetNoteToAssociatedGroupChannelPostBody() {
    }

    public SetNoteToAssociatedGroupChannelPostBody(String groupChannelId, String note) {
        this.groupChannelId = groupChannelId;
        this.note = note;
    }

    public String getGroupChannelId() {
        return groupChannelId;
    }

    public String getNote() {
        return note;
    }
}
