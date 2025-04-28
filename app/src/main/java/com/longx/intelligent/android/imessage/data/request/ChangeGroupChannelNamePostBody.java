package com.longx.intelligent.android.imessage.data.request;

/**
 * Created by LONG on 2024/4/7 at 11:52 PM.
 */
public class ChangeGroupChannelNamePostBody {

    private final String groupId;
    private final String newGroupName;

    public ChangeGroupChannelNamePostBody() {
        this(null, null);
    }

    public ChangeGroupChannelNamePostBody(String groupId, String newGroupName) {
        this.groupId = groupId;
        this.newGroupName = newGroupName;
    }

    public String getNewGroupName() {
        return newGroupName;
    }

    public String getGroupId() {
        return groupId;
    }
}
