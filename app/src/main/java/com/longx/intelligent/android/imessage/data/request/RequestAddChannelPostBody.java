package com.longx.intelligent.android.imessage.data.request;

import java.util.List;

/**
 * Created by LONG on 2024/5/1 at 11:38 PM.
 */
public class RequestAddChannelPostBody {
    private String imessageIdUser;
    private String message;
    private String note;
    private List<String> newTagNames;
    private List<String> toAddTagIds;

    public RequestAddChannelPostBody() {
    }

    public RequestAddChannelPostBody(String imessageIdUser, String message, String note, List<String> newTagNames, List<String> toAddTagIds) {
        this.imessageIdUser = imessageIdUser;
        this.message = message;
        this.note = note;
        this.newTagNames = newTagNames;
        this.toAddTagIds = toAddTagIds;
    }

    public String getImessageIdUser() {
        return imessageIdUser;
    }

    public String getMessage() {
        return message;
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
