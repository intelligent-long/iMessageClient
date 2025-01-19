package com.longx.intelligent.android.imessage.data;

/**
 * Created by LONG on 2024/5/21 at 5:39 AM.
 */
public class ChannelAdditionNotViewedCount {
    private int requester;
    private int responder;
    private int notificationRequest;
    private int notificationRespond;

    public ChannelAdditionNotViewedCount() {
    }

    public ChannelAdditionNotViewedCount(int requester, int responder, int notificationRequest, int notificationRespond) {
        this.requester = requester;
        this.responder = responder;
        this.notificationRequest = notificationRequest;
        this.notificationRespond = notificationRespond;
    }

    public int getRequester() {
        return requester;
    }

    public int getResponder() {
        return responder;
    }

    public int getNotificationRequest() {
        return notificationRequest;
    }

    public int getNotificationRespond() {
        return notificationRespond;
    }
}
