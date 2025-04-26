package com.longx.intelligent.android.imessage.data.request;

/**
 * Created by LONG on 2024/4/7 at 11:52 PM.
 */
public class ChangeGroupNamePostBody {

    private final String groupId;
    private final String newGroupName;

    public ChangeGroupNamePostBody() {
        this(null, null);
    }

    public ChangeGroupNamePostBody(String groupId, String newGroupName) {
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
