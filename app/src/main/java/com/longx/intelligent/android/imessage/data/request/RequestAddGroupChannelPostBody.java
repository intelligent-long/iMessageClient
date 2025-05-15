package com.longx.intelligent.android.imessage.data.request;

import java.util.List;

/**
 * Created by LONG on 2025/5/8 at 4:28 AM.
 */
public class RequestAddGroupChannelPostBody {
    private String groupChannelIdUser;
    private String message;
    private String note;
    private List<String> newTagNames;
    private List<String> toAddTagIds;
    private String inviteUuid;

    public RequestAddGroupChannelPostBody() {
    }

    public RequestAddGroupChannelPostBody(String groupChannelIdUser, String message, String note, List<String> newTagNames, List<String> toAddTagIds, String inviteUuid) {
        this.groupChannelIdUser = groupChannelIdUser;
        this.message = message;
        this.note = note;
        this.newTagNames = newTagNames;
        this.toAddTagIds = toAddTagIds;
        this.inviteUuid = inviteUuid;
    }

    public String getGroupChannelIdUser() {
        return groupChannelIdUser;
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

    public String getInviteUuid() {
        return inviteUuid;
    }
}
