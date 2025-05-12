package com.longx.intelligent.android.imessage.data;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Created by LONG on 2024/5/2 at 1:11 AM.
 */
public class GroupChannelAddition implements Parcelable {
    private String uuid;
    private Channel requesterChannel;
    private GroupChannel responderGroupChannel;
    private String message;
    private Date requestTime;
    private Date respondTime;
    @JsonProperty("isAccepted")
    private boolean isAccepted;
    @JsonProperty("isViewed")
    private boolean isViewed;
    @JsonProperty("isExpired")
    private boolean isExpired;

    public GroupChannelAddition() {
    }

    public GroupChannelAddition(String uuid, Channel requesterChannel, GroupChannel responderGroupChannel,
                                String message, Date requestTime, Date respondTime, boolean isAccepted,
                                boolean isViewed, boolean isExpired) {
        this.uuid = uuid;
        this.requesterChannel = requesterChannel;
        this.responderGroupChannel = responderGroupChannel;
        this.message = message;
        this.requestTime = requestTime;
        this.respondTime = respondTime;
        this.isAccepted = isAccepted;
        this.isViewed = isViewed;
        this.isExpired = isExpired;
    }

    protected GroupChannelAddition(Parcel in) {
        uuid = in.readString();
        requesterChannel = in.readParcelable(Channel.class.getClassLoader());
        responderGroupChannel = in.readParcelable(GroupChannel.class.getClassLoader());
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
    }

    public static final Creator<GroupChannelAddition> CREATOR = new Creator<GroupChannelAddition>() {
        @Override
        public GroupChannelAddition createFromParcel(Parcel in) {
            return new GroupChannelAddition(in);
        }

        @Override
        public GroupChannelAddition[] newArray(int size) {
            return new GroupChannelAddition[size];
        }
    };

    public String getUuid() {
        return uuid;
    }

    public Channel getRequesterChannel() {
        return requesterChannel;
    }

    public GroupChannel getResponderGroupChannel() {
        return responderGroupChannel;
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

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        GroupChannelAddition that = (GroupChannelAddition) obj;
        return Objects.equals(this.uuid, that.uuid) &&
                Objects.equals(this.requesterChannel, that.requesterChannel) &&
                Objects.equals(this.responderGroupChannel, that.responderGroupChannel) &&
                Objects.equals(this.message, that.message) &&
                Objects.equals(this.requestTime, that.requestTime) &&
                Objects.equals(this.respondTime, that.respondTime) &&
                this.isAccepted == that.isAccepted &&
                this.isViewed == that.isViewed &&
                this.isExpired == that.isExpired;
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, requesterChannel, responderGroupChannel, message, requestTime, respondTime, isAccepted, isViewed, isExpired);
    }

    @Override
    public String toString() {
        return "GroupChannelAddition{" +
                "getUuid='" + uuid + '\'' +
                ", getRequesterChannel=" + requesterChannel +
                ", getResponderGroupChannel=" + responderGroupChannel +
                ", getMessage='" + message + '\'' +
                ", getRequestTime=" + requestTime +
                ", getRespondTime=" + respondTime +
                ", isAccepted=" + isAccepted +
                ", isViewed=" + isViewed +
                ", isExpired=" + isExpired +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(uuid);
        dest.writeParcelable(requesterChannel, flags);
        dest.writeParcelable(responderGroupChannel, flags);
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
    }
}