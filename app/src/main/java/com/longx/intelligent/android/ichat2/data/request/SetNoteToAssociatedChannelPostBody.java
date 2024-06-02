package com.longx.intelligent.android.ichat2.data.request;

/**
 * Created by LONG on 2024/6/2 at 1:50 AM.
 */
public class SetNoteToAssociatedChannelPostBody {
    private String channelIchatId;
    private String note;

    public SetNoteToAssociatedChannelPostBody() {
    }

    public SetNoteToAssociatedChannelPostBody(String channelIchatId, String note) {
        this.channelIchatId = channelIchatId;
        this.note = note;
    }

    public String getChannelIchatId() {
        return channelIchatId;
    }

    public String getNote() {
        return note;
    }
}
