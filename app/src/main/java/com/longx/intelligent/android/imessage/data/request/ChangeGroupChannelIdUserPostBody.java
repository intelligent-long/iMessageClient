package com.longx.intelligent.android.imessage.data.request;

/**
 * Created by LONG on 2024/4/3 at 9:43 PM.
 */
public class ChangeGroupChannelIdUserPostBody {
    private final String groupChannelId;
    private final String newGroupChannelIdUser;

    public ChangeGroupChannelIdUserPostBody() {
        this(null, null);
    }

    public ChangeGroupChannelIdUserPostBody(String groupChannelId, String newGroupChannelIdUser) {
        this.groupChannelId = groupChannelId;
        this.newGroupChannelIdUser = newGroupChannelIdUser;
    }

    public String getGroupChannelId() {
        return groupChannelId;
    }

    public String getNewGroupChannelIdUser() {
        return newGroupChannelIdUser;
    }
}
