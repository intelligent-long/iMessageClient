package com.longx.intelligent.android.ichat2.data;

import java.util.Date;

/**
 * Created by LONG on 2024/5/2 at 1:11 AM.
 */
public class ChannelAdditionInfo {
    private final String uuid;
    private final ChannelInfo requesterChannelInfo;
    private final ChannelInfo responderChannelInfo;
    private final String message;
    private final Date requestTime;
    private final Date respondTime;
    private final boolean isAccepted;
    private final boolean isViewed;
    private final boolean isExpired;

    public ChannelAdditionInfo() {
        this(null, null, null, null, null, null, false, false, false);
    }

    public ChannelAdditionInfo(String uuid, ChannelInfo requesterChannelInfo, ChannelInfo responderChannelInfo, String message, Date requestTime, Date respondTime, boolean isAccepted, boolean isViewed, boolean isExpired) {
        this.uuid = uuid;
        this.requesterChannelInfo = requesterChannelInfo;
        this.responderChannelInfo = responderChannelInfo;
        this.message = message;
        this.requestTime = requestTime;
        this.respondTime = respondTime;
        this.isAccepted = isAccepted;
        this.isViewed = isViewed;
        this.isExpired = isExpired;
    }

    public String getUuid() {
        return uuid;
    }

    public ChannelInfo getRequesterChannelInfo() {
        return requesterChannelInfo;
    }

    public ChannelInfo getResponderChannelInfo() {
        return responderChannelInfo;
    }

    public String getMessage() {
        return message;
    }

    public Date getRequestTime() {
        return requestTime;
    }

    public Date getRespondTime() {
        return respondTime;
    }

    public boolean isAccepted() {
        return isAccepted;
    }

    public boolean isViewed() {
        return isViewed;
    }

    public boolean isExpired() {
        return isExpired;
    }
}
