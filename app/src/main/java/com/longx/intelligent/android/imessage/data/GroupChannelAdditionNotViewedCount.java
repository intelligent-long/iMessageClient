package com.longx.intelligent.android.imessage.data;

public class GroupChannelAdditionNotViewedCount {
    private int requester;
    private int responder;
    private int notificationRequest;
    private int notificationRespond;

    public GroupChannelAdditionNotViewedCount() {
    }

    public GroupChannelAdditionNotViewedCount(int requester, int responder, int notificationRequest, int notificationRespond) {
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
