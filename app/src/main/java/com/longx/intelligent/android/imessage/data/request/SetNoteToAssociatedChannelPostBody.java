package com.longx.intelligent.android.imessage.data.request;

/**
 * Created by LONG on 2024/6/2 at 1:50 AM.
 */
public class SetNoteToAssociatedChannelPostBody {
    private String channelImessageId;
    private String note;

    public SetNoteToAssociatedChannelPostBody() {
    }

    public SetNoteToAssociatedChannelPostBody(String channelImessageId, String note) {
        this.channelImessageId = channelImessageId;
        this.note = note;
    }

    public String getChannelImessageId() {
        return channelImessageId;
    }

    public String getNote() {
        return note;
    }
}
