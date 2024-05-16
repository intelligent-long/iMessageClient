package com.longx.intelligent.android.ichat2.data;

/**
 * Created by LONG on 2024/5/16 at 6:44 PM.
 */
public class MessageViewed {
    private final int notViewedCount;
    private final String viewedUuid;

    public MessageViewed(int notViewedCount, String viewedUuid) {
        this.notViewedCount = notViewedCount;
        this.viewedUuid = viewedUuid;
    }

    public int getNotViewedCount() {
        return notViewedCount;
    }

    public String getViewedUuid() {
        return viewedUuid;
    }
}
