package com.longx.intelligent.android.imessage.data.request;

/**
 * Created by LONG on 2024/4/7 at 11:52 PM.
 */
public class ChangeGroupChannelJoinVerificationPostBody {

    private final String groupId;
    private final Boolean groupJoinVerification;

    public ChangeGroupChannelJoinVerificationPostBody() {
        this(null, null);
    }

    public ChangeGroupChannelJoinVerificationPostBody(String groupId, Boolean groupJoinVerification) {
        this.groupId = groupId;
        this.groupJoinVerification = groupJoinVerification;
    }

    public Boolean getGroupJoinVerification() {
        return groupJoinVerification;
    }

    public String getGroupId() {
        return groupId;
    }
}
