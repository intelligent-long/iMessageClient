package com.longx.intelligent.android.imessage.data;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Date;

/**
 * Created by LONG on 2025/4/20 at 2:02 AM.
 */
public class GroupChannelAssociation implements Parcelable {
    private String associationId;
    private String groupChannelId;
    private String owner;
    private String inviter;
    private String inviteMessage;
    private Date inviteTime;
    private Date acceptTime;

    public GroupChannelAssociation() {
    }

    public GroupChannelAssociation(String associationId, String groupChannelId, String owner, String inviter, String inviteMessage, Date inviteTime, Date acceptTime) {
        this.associationId = associationId;
        this.groupChannelId = groupChannelId;
        this.owner = owner;
        this.inviter = inviter;
        this.inviteMessage = inviteMessage;
        this.inviteTime = inviteTime;
        this.acceptTime = acceptTime;
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

    public String getInviter() {
        return inviter;
    }

    public String getInviteMessage() {
        return inviteMessage;
    }

    public Date getInviteTime() {
        return inviteTime;
    }

    public Date getAcceptTime() {
        return acceptTime;
    }

    public GroupChannelAssociation(Parcel in) {
        associationId = in.readString();
        groupChannelId = in.readString();
        owner = in.readString();
        inviter = in.readString();
        inviteMessage = in.readString();
        inviteTime = (Date) in.readValue(getClass().getClassLoader());
        acceptTime = (Date) in.readValue(getClass().getClassLoader());
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
        dest.writeString(inviter);
        dest.writeString(inviteMessage);
        dest.writeValue(inviteTime);
        dest.writeValue(acceptTime);
    }
}
