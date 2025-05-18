package com.longx.intelligent.android.imessage.data;

public class GroupChannelAdditionNotViewedCount {
    private int requester;
    private int responder;
    private int selfNotificationRequest;
    private int selfNotificationRespond;
    private int otherNotificationRequest;
    private int otherNotificationRespond;
    private int inviter;
    private int invitee;
    private int notificationInviter;
    private int notificationInvitee;

    public GroupChannelAdditionNotViewedCount() {
    }

    public GroupChannelAdditionNotViewedCount(int requester, int responder, int selfNotificationRequest, int selfNotificationRespond, int otherNotificationRequest, int otherNotificationRespond, int inviter, int invitee, int notificationInviter, int notificationInvitee) {
        this.requester = requester;
        this.responder = responder;
        this.selfNotificationRequest = selfNotificationRequest;
        this.selfNotificationRespond = selfNotificationRespond;
        this.otherNotificationRequest = otherNotificationRequest;
        this.otherNotificationRespond = otherNotificationRespond;
        this.inviter = inviter;
        this.invitee = invitee;
        this.notificationInviter = notificationInviter;
        this.notificationInvitee = notificationInvitee;
    }

    public int getRequester() {
        return requester;
    }

    public int getResponder() {
        return responder;
    }

    public int getSelfNotificationRequest() {
        return selfNotificationRequest;
    }

    public int getSelfNotificationRespond() {
        return selfNotificationRespond;
    }

    public int getOtherNotificationRequest() {
        return otherNotificationRequest;
    }

    public int getOtherNotificationRespond() {
        return otherNotificationRespond;
    }

    public int getInviter() {
        return inviter;
    }

    public int getInvitee() {
        return invitee;
    }

    public int getNotificationInviter() {
        return notificationInviter;
    }

    public int getNotificationInvitee() {
        return notificationInvitee;
    }
}
