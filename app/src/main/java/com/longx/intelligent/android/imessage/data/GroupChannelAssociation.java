package com.longx.intelligent.android.imessage.data;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Date;
import java.util.List;

/**
 * Created by LONG on 2025/4/20 at 2:02 AM.
 */
public class GroupChannelAssociation implements Parcelable {
    private String associationId;
    private String groupChannelId;
    private String channelImessageId;
    private String inviteChannelImessageId;
    private String inviteMessage;
    private Date inviteTime;
    private Date acceptTime;

    public GroupChannelAssociation() {
    }

    public GroupChannelAssociation(String associationId, String groupChannelId, String channelImessageId, String inviteChannelImessageId, String inviteMessage, Date inviteTime, Date acceptTime) {
        this.associationId = associationId;
        this.groupChannelId = groupChannelId;
        this.channelImessageId = channelImessageId;
        this.inviteChannelImessageId = inviteChannelImessageId;
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

    public String getChannelImessageId() {
        return channelImessageId;
    }

    public String getInviteChannelImessageId() {
        return inviteChannelImessageId;
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
        channelImessageId = in.readString();
        inviteChannelImessageId = in.readString();
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
        dest.writeString(channelImessageId);
        dest.writeString(inviteChannelImessageId);
        dest.writeString(inviteMessage);
        dest.writeValue(inviteTime);
        dest.writeValue(acceptTime);
    }
}
