package com.longx.intelligent.android.ichat2.data.request;

import java.util.Set;

/**
 * Created by LONG on 2024/10/16 at 6:31 AM.
 */
public class ChangeExcludeBroadcastChannelPostBody {
    private Set<String> excludeBroadcastChannelIds;

    public ChangeExcludeBroadcastChannelPostBody() {
    }

    public ChangeExcludeBroadcastChannelPostBody(Set<String> excludeBroadcastChannelIds) {
        this.excludeBroadcastChannelIds = excludeBroadcastChannelIds;
    }

    public Set<String> getExcludeBroadcastChannelIds() {
        return excludeBroadcastChannelIds;
    }
}
