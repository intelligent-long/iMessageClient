package com.longx.intelligent.android.imessage.data;

import com.longx.intelligent.android.imessage.data.Channel;
import com.longx.intelligent.android.imessage.data.GroupChannel;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Created by LONG on 2025/5/8 at 5:09 AM.
 */
public final class GroupChannelInvitation {
    private final String uuid;
    private final Channel inviter;
    private final Channel invitee;
    private final GroupChannel groupChannelInvitedTo;
    private final String message;
    private final Date requestTime;
    private final Date respondTime;
    private final boolean isAccepted;
    private final boolean isViewed;
    private final boolean isExpired;

    public GroupChannelInvitation(
            String uuid, Channel inviter, Channel invitee, GroupChannel groupChannelInvitedTo,
            String message, Date requestTime, Date respondTime, boolean isAccepted, boolean isViewed, boolean isExpired) {
        this.uuid = uuid;
        this.inviter = inviter;
        this.invitee = invitee;
        this.groupChannelInvitedTo = groupChannelInvitedTo;
        this.message = message;
        this.requestTime = requestTime;
        this.respondTime = respondTime;
        this.isAccepted = isAccepted;
        this.isViewed = isViewed;
        this.isExpired = isExpired;
    }

    public String uuid() {
        return uuid;
    }

    public Channel inviter() {
        return inviter;
    }

    public Channel invitee() {
        return invitee;
    }

    public GroupChannel groupChannelInvitedTo() {
        return groupChannelInvitedTo;
    }

    public String message() {
        return message;
    }

    public Date requestTime() {
        return requestTime;
    }

    public Date respondTime() {
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

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        GroupChannelInvitation that = (GroupChannelInvitation) obj;
        return Objects.equals(this.uuid, that.uuid) &&
                Objects.equals(this.inviter, that.inviter) &&
                Objects.equals(this.invitee, that.invitee) &&
                Objects.equals(this.groupChannelInvitedTo, that.groupChannelInvitedTo) &&
                Objects.equals(this.message, that.message) &&
                Objects.equals(this.requestTime, that.requestTime) &&
                Objects.equals(this.respondTime, that.respondTime) &&
                this.isAccepted == that.isAccepted &&
                this.isViewed == that.isViewed &&
                this.isExpired == that.isExpired;
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, inviter, invitee, groupChannelInvitedTo, message, requestTime, respondTime, isAccepted, isViewed, isExpired);
    }

    @Override
    public String toString() {
        return "GroupChannelInvitation[" +
                "uuid=" + uuid + ", " +
                "inviter=" + inviter + ", " +
                "invitee=" + invitee + ", " +
                "groupChannelInvitedTo=" + groupChannelInvitedTo + ", " +
                "message=" + message + ", " +
                "requestTime=" + requestTime + ", " +
                "respondTime=" + respondTime + ", " +
                "isAccepted=" + isAccepted + ", " +
                "isViewed=" + isViewed + ", " +
                "isExpired=" + isExpired + ']';
    }

}