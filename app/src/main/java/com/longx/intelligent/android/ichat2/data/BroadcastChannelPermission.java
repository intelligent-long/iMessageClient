package com.longx.intelligent.android.ichat2.data;

import java.util.Set;

/**
 * Created by LONG on 2024/10/14 at 2:48 AM.
 */
public class BroadcastChannelPermission {
    public static final int PUBLIC = 0;
    public static final int PRIVATE = 1;
    public static final int CONNECTED_CHANNEL_CIRCLE = 2;

    private int permission;
    private Set<String> excludeConnectedChannels;

    public BroadcastChannelPermission() {
    }

    public BroadcastChannelPermission(int permission, Set<String> excludeConnectedChannels) {
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
