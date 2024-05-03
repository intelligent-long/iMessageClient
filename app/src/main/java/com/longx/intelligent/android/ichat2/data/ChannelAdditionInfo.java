package com.longx.intelligent.android.ichat2.data;

import java.util.Date;

/**
 * Created by LONG on 2024/5/2 at 1:11 AM.
 */
public class ChannelAdditionInfo {
    private final String uuid;
    private final String requesterIchatId;
    private final String responderIchatId;
    private final String message;
    private final Date requestTime;
    private final Date respondTime;
    private final boolean isAccepted;
    private final boolean isViewed;

    public ChannelAdditionInfo(String uuid, String requesterIchatId, String responderIchatId, String message, Date requestTime, Date respondTime, boolean isAccepted, boolean isViewed) {
        this.uuid = uuid;
        this.requesterIchatId = requesterIchatId;
        this.responderIchatId = responderIchatId;
        this.message = message;
        this.requestTime = requestTime;
        this.respondTime = respondTime;
        this.isAccepted = isAccepted;
        this.isViewed = isViewed;
    }

    public String getUuid() {
        return uuid;
    }

    public String getRequesterIchatId() {
        return requesterIchatId;
    }

    public String getResponderIchatId() {
        return responderIchatId;
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
}
