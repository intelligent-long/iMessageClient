package com.longx.intelligent.android.imessage.data.request;

import java.util.List;

/**
 * Created by LONG on 2025/6/14 at 9:07 AM.
 */
public class ViewGroupChannelNotificationsPostBody {
    private List<String> uuids;

    public ViewGroupChannelNotificationsPostBody() {
    }

    public ViewGroupChannelNotificationsPostBody(List<String> uuids) {
        this.uuids = uuids;
    }

    public List<String> getUuids() {
        return uuids;
    }
}
