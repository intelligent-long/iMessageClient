package com.longx.intelligent.android.imessage.data;

import java.util.Date;

/**
 * Created by LONG on 2025/6/13 at 1:33 AM.
 */
public class GroupChannelDisconnection {
    private String groupChannelId;
    private String channelId;
    private boolean passive;
    private String byWhom;
    private Date time;
    private boolean isViewed;

    public GroupChannelDisconnection() {
    }

    public GroupChannelDisconnection(String groupChannelId, String channelId, boolean passive, String byWhom, Date time, boolean isViewed) {
        this.groupChannelId = groupChannelId;
        this.channelId = channelId;
        this.passive = passive;
        this.byWhom = byWhom;
        this.time = time;
        this.isViewed = isViewed;
    }

    public String getGroupChannelId() {
        return groupChannelId;
    }

    public String getChannelId() {
        return channelId;
    }

    public boolean isPassive() {
        return passive;
    }

    public String getByWhom() {
        return byWhom;
    }

    public Date getTime() {
        return time;
    }

    public boolean isViewed() {
        return isViewed;
    }
}
