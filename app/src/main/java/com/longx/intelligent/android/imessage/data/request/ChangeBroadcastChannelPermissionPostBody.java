package com.longx.intelligent.android.imessage.data.request;

import java.util.Set;

/**
 * Created by LONG on 2024/10/15 at 4:43 PM.
 */
public class ChangeBroadcastChannelPermissionPostBody {
    private int permission;
    private Set<String> excludeConnectedChannels;

    public ChangeBroadcastChannelPermissionPostBody() {
    }

    public ChangeBroadcastChannelPermissionPostBody(int permission, Set<String> excludeConnectedChannels) {
        this.permission = permission;
        this.excludeConnectedChannels = excludeConnectedChannels;
    }

    public int getPermission() {
        return permission;
    }

    public Set<String> getExcludeConnectedChannels() {
        return excludeConnectedChannels;
    }
}
