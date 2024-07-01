package com.longx.intelligent.android.ichat2.data.request;

/**
 * Created by LONG on 2024/5/1 at 11:38 PM.
 */
public class RequestAddChannelPostBody {
    private String ichatIdUser;

    private String message;

    private String note;

    private String tagId;

    public RequestAddChannelPostBody() {
    }

    public RequestAddChannelPostBody(String ichatIdUser, String message, String note, String tagId) {
        this.ichatIdUser = ichatIdUser;
        this.message = message;
        this.note = note;
        this.tagId = tagId;
    }

    public String getIchatIdUser() {
        return ichatIdUser;
    }

    public String getMessage() {
        return message;
    }

    public String getNote() {
        return note;
    }

    public String getTagId() {
        return tagId;
    }
}
