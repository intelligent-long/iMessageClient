package com.longx.intelligent.android.imessage.data;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;
import java.util.Objects;

/**
 * Created by LONG on 2025/4/20 at 2:02 AM.
 */
@JsonIgnoreProperties({"stability"})
public class GroupChannelAssociation implements Parcelable {
    private String associationId;
    private String groupChannelId;
    private String owner;
    private Channel requester;
    private String requestMessage;
    private Date requestTime;
    private Date acceptTime;
    private String inviteUuid;

    public GroupChannelAssociation() {
    }

    public GroupChannelAssociation(String associationId, String groupChannelId, String owner, Channel requester, String requestMessage, Date requestTime, Date acceptTime, String inviteUuid) {
        this.associationId = associationId;
        this.groupChannelId = groupChannelId;
        this.owner = owner;
        this.requester = requester;
        this.requestMessage = requestMessage;
        this.requestTime = requestTime;
        this.acceptTime = acceptTime;
        this.inviteUuid = inviteUuid;
    }

    public static final Creator<GroupChannelAssociation> CREATOR = new Creator<GroupChannelAssociation>() {
        @Override
        public GroupChannelAssociation createFromParcel(Parcel in) {
            return new GroupChannelAssociation(in);
        }

        @Override
        public GroupChannelAssociation[] newArray(int size) {
            return new GroupChannelAssociation[size];
        }
    };

    public String getAssociationId() {
        return associationId;
    }

    public String getGroupChannelId() {
        return groupChannelId;
    }

    public String getOwner() {
        return owner;
    }

    public Channel getRequester() {
        return requester;
    }

    public String getRequestMessage() {
        return requestMessage;
    }

    public Date getRequestTime() {
        return requestTime;
    }

    public Date getAcceptTime() {
        return acceptTime;
    }

    public String getInviteUuid() {
        return inviteUuid;
    }

    public GroupChannelAssociation(Parcel in) {
        associationId = in.readString();
        groupChannelId = in.readString();
        owner = in.readString();
        requester = in.readParcelable(getClass().getClassLoader());
        requestMessage = in.readString();
        requestTime = (Date) in.readValue(getClass().getClassLoader());
        acceptTime = (Date) in.readValue(getClass().getClassLoader());
        inviteUuid = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(associationId);
        dest.writeString(groupChannelId);
        dest.writeString(owner);
        dest.writeParcelable(requester, flags);
        dest.writeString(requestMessage);
        dest.writeValue(requestTime);
        dest.writeValue(acceptTime);
        dest.writeString(inviteUuid);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupChannelAssociation that = (GroupChannelAssociation) o;
        return Objects.equals(associationId, that.associationId) && Objects.equals(groupChannelId, that.groupChannelId) && Objects.equals(owner, that.owner) && Objects.equals(requester, that.requester) && Objects.equals(requestMessage, that.requestMessage) && Objects.equals(requestTime, that.requestTime) && Objects.equals(acceptTime, that.acceptTime) && Objects.equals(inviteUuid, that.inviteUuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(associationId, groupChannelId, owner, requester, requestMessage, requestTime, acceptTime, inviteUuid);
    }

    @Override
    public String toString() {
        return "GroupChannelAssociation{" +
                "associationId='" + associationId + '\'' +
                ", groupChannelId='" + groupChannelId + '\'' +
                ", owner='" + owner + '\'' +
                ", requester=" + requester +
                ", requestMessage='" + requestMessage + '\'' +
                ", requestTime=" + requestTime +
                ", acceptTime=" + acceptTime +
                ", inviteUuid='" + inviteUuid + '\'' +
                '}';
    }
}
