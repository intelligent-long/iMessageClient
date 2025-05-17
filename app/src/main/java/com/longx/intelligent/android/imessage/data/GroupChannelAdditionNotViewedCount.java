package com.longx.intelligent.android.imessage.data;

public class GroupChannelAdditionNotViewedCount {
    private int requester;
    private int responder;
    private int notificationRequest;
    private int notificationRespond;
    private int inviter;
    private int invitee;
    private int notificationInviter;
    private int notificationInvitee;

    public GroupChannelAdditionNotViewedCount() {
    }

    public GroupChannelAdditionNotViewedCount(int requester, int responder, int notificationRequest, int notificationRespond, int inviter, int invitee, int notificationInviter, int notificationInvitee) {
        this.requester = requester;
        this.responder = responder;
        this.notificationRequest = notificationRequest;
        this.notificationRespond = notificationRespond;
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

    public int getNotificationRequest() {
        return notificationRequest;
    }

    public int getNotificationRespond() {
        return notificationRespond;
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
