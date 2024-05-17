package com.longx.intelligent.android.ichat2.data;

/**
 * Created by LONG on 2024/5/16 at 6:44 PM.
 */
public class MessageViewed {
    private final int notViewedCount;
    private final String viewedUuid;
    private final String other;

    public MessageViewed(){
        this(-1, null, null);
    }

    public MessageViewed(int notViewedCount, String viewedUuid, String other) {
        this.notViewedCount = notViewedCount;
        this.viewedUuid = viewedUuid;
        this.other = other;
    }

    public int getNotViewedCount() {
        return notViewedCount;
    }

    public String getViewedUuid() {
        return viewedUuid;
    }

    public String getOther() {
        return other;
    }
}
