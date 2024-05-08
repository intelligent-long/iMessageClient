package com.longx.intelligent.android.ichat2.data;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.longx.intelligent.android.ichat2.da.sharedpref.SharedPreferencesAccessor;

import java.util.Date;

/**
 * Created by LONG on 2024/5/2 at 1:11 AM.
 */
public class ChannelAdditionInfo implements Parcelable {
    private final String uuid;
    private final ChannelInfo requesterChannelInfo;
    private final ChannelInfo responderChannelInfo;
    private final String message;
    private final Date requestTime;
    private final Date respondTime;
    @JsonProperty("isAccepted")
    private final boolean isAccepted;
    @JsonProperty("isViewed")
    private final boolean isViewed;
    @JsonProperty("isExpired")
    private final boolean isExpired;

    public ChannelAdditionInfo() {
        this(null, null, null, null, null, null, false, false, false);
    }

    public ChannelAdditionInfo(String uuid, ChannelInfo requesterChannelInfo, ChannelInfo responderChannelInfo, String message, Date requestTime, Date respondTime, boolean isAccepted, boolean isViewed, boolean isExpired) {
        this.uuid = uuid;
        this.requesterChannelInfo = requesterChannelInfo;
        this.responderChannelInfo = responderChannelInfo;
        this.message = message;
        this.requestTime = requestTime;
        this.respondTime = respondTime;
        this.isAccepted = isAccepted;
        this.isViewed = isViewed;
        this.isExpired = isExpired;
    }

    public String getUuid() {
        return uuid;
    }

    public ChannelInfo getRequesterChannelInfo() {
        return requesterChannelInfo;
    }

    public ChannelInfo getResponderChannelInfo() {
        return responderChannelInfo;
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

    public boolean isRequester(Context context){
        return requesterChannelInfo.getIchatId().equals(SharedPreferencesAccessor.UserInfoPref.getCurrentUserInfo(context).getIchatId());
    }

    public static final Creator<ChannelAdditionInfo> CREATOR = new Creator<ChannelAdditionInfo>() {
        @Override
        public ChannelAdditionInfo createFromParcel(Parcel in) {
            return new ChannelAdditionInfo(in);
        }

        @Override
        public ChannelAdditionInfo[] newArray(int size) {
            return new ChannelAdditionInfo[size];
        }
    };

    protected ChannelAdditionInfo(Parcel in){
        uuid = in.readString();
        requesterChannelInfo = in.readParcelable(getClass().getClassLoader());
        responderChannelInfo = in.readParcelable(getClass().getClassLoader());
        message = in.readString();
        requestTime = (Date) in.readValue(getClass().getClassLoader());
        respondTime = (Date) in.readValue(getClass().getClassLoader());
        isAccepted = in.readInt() == 1;
        isViewed = in.readInt() == 1;
        isExpired = in.readInt() == 1;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(uuid);
        dest.writeParcelable(requesterChannelInfo, flags);
        dest.writeParcelable(responderChannelInfo, flags);
        dest.writeString(message);
        dest.writeValue(requestTime);
        dest.writeValue(respondTime);
        dest.writeInt(isAccepted ? 1 : 0);
        dest.writeInt(isViewed ? 1 : 0);
        dest.writeInt(isExpired ? 1 : 0);
    }

    @Override
    public String toString() {
        return "ChannelAdditionInfo{" +
                "uuid='" + uuid + '\'' +
                ", requesterChannelInfo=" + requesterChannelInfo +
                ", responderChannelInfo=" + responderChannelInfo +
                ", message='" + message + '\'' +
                ", requestTime=" + requestTime +
                ", respondTime=" + respondTime +
                ", isAccepted=" + isAccepted +
                ", isViewed=" + isViewed +
                ", isExpired=" + isExpired +
                '}';
    }
}
