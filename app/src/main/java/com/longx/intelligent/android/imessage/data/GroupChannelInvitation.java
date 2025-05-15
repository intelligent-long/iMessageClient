package com.longx.intelligent.android.imessage.data;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.Objects;

/**
 * Created by LONG on 2025/5/8 at 5:09 AM.
 */
public final class GroupChannelInvitation implements GroupChannelActivity, Parcelable {
    private String uuid;
    private Channel inviter;
    private Channel invitee;
    private GroupChannel groupChannelInvitedTo;
    private String message;
    private Date requestTime;
    private Date respondTime;
    @JsonProperty("isAccepted")
    private boolean isAccepted;
    @JsonProperty("isViewed")
    private boolean isViewed;
    @JsonProperty("isExpired")
    private boolean isExpired;
    private Type inviteType;

    public enum Type{INVITER, INVITEE}

    public GroupChannelInvitation() {
    }

    public GroupChannelInvitation(
            String uuid, Channel inviter, Channel invitee, GroupChannel groupChannelInvitedTo, String message,
            Date requestTime, Date respondTime, boolean isAccepted, boolean isViewed, boolean isExpired, Type inviteType) {
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
        this.inviteType = inviteType;
    }

    protected GroupChannelInvitation(Parcel in) {
        uuid = in.readString();
        inviter = in.readParcelable(Channel.class.getClassLoader());
        invitee = in.readParcelable(GroupChannel.class.getClassLoader());
        groupChannelInvitedTo = in.readParcelable(GroupChannel.class.getClassLoader());
        message = in.readString();
        long requestTimeLong = in.readLong();
        if (requestTimeLong != -1) {
            requestTime = new Date(requestTimeLong);
        }
        long respondTimeLong = in.readLong();
        if (respondTimeLong != -1) {
            respondTime = new Date(respondTimeLong);
        }
        isAccepted = in.readByte() != 0;
        isViewed = in.readByte() != 0;
        isExpired = in.readByte() != 0;
        if(in.readInt() == 0) {
            inviteType = Type.INVITER;
        }else if(in.readInt() == 1) {
            inviteType = Type.INVITEE;
        }
    }

    public String getUuid() {
        return uuid;
    }

    public Channel getInviter() {
        return inviter;
    }

    public Channel getInvitee() {
        return invitee;
    }

    public GroupChannel getGroupChannelInvitedTo() {
        return groupChannelInvitedTo;
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

    public Type getInviteType() {
        return inviteType;
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
                this.isExpired == that.isExpired &&
                this.inviteType == that.inviteType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, inviter, invitee, groupChannelInvitedTo, message, requestTime, respondTime, isAccepted, isViewed, isExpired, inviteType);
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
                "isExpired=" + isExpired +
                "inviteType=" + inviteType+ ']';
    }

    public static final Creator<GroupChannelInvitation> CREATOR = new Creator<GroupChannelInvitation>() {
        @Override
        public GroupChannelInvitation createFromParcel(Parcel in) {
            return new GroupChannelInvitation(in);
        }

        @Override
        public GroupChannelInvitation[] newArray(int size) {
            return new GroupChannelInvitation[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(uuid);
        dest.writeParcelable(inviter, flags);
        dest.writeParcelable(invitee, flags);
        dest.writeParcelable(groupChannelInvitedTo, flags);
        dest.writeString(message);
        if(requestTime != null) {
            dest.writeLong(requestTime.getTime());
        }else {
            dest.writeLong(-1);
        }
        if(respondTime != null) {
            dest.writeLong(respondTime.getTime());
        }else {
            dest.writeLong(-1);
        }
        dest.writeByte((byte) (isAccepted ? 1 : 0));
        dest.writeByte((byte) (isViewed ? 1 : 0));
        dest.writeByte((byte) (isExpired ? 1 : 0));
        if(inviteType == Type.INVITER){
            dest.writeInt(0);
        }else if(inviteType == Type.INVITEE){
            dest.writeInt(1);
        }
    }
}