package com.longx.intelligent.android.imessage.data.request;

/**
 * Created by LONG on 2024/4/7 at 11:52 PM.
 */
public class ChangeGroupChannelJoinVerificationPostBody {

    private final String groupId;
    private final Boolean groupJoinVerificationEnabled;

    public ChangeGroupChannelJoinVerificationPostBody() {
        this(null, null);
    }

    public ChangeGroupChannelJoinVerificationPostBody(String groupId, Boolean groupJoinVerificationEnabled) {
        this.groupId = groupId;
        this.groupJoinVerificationEnabled = groupJoinVerificationEnabled;
    }

    public Boolean getGroupJoinVerificationEnabled() {
        return groupJoinVerificationEnabled;
    }

    public String getGroupId() {
        return groupId;
    }
}
