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
public class ChannelAddition implements Parcelable {
    private final String uuid;
    private final Channel requesterChannel;
    private final Channel responderChannel;
    private final String message;
    private final Date requestTime;
    private final Date respondTime;
    @JsonProperty("isAccepted")
    private final boolean isAccepted;
    @JsonProperty("isViewed")
    private final boolean isViewed;
    @JsonProperty("isExpired")
    private final boolean isExpired;

    public ChannelAddition() {
        this(null, null, null, null, null, null, false, false, false);
    }

    public ChannelAddition(String uuid, Channel requesterChannel, Channel responderChannel, String message, Date requestTime, Date respondTime, boolean isAccepted, boolean isViewed, boolean isExpired) {
        this.uuid = uuid;
        this.requesterChannel = requesterChannel;
        this.responderChannel = responderChannel;
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

    public Channel getRequesterChannel() {
        return requesterChannel;
    }

    public Channel getResponderChannel() {
        return responderChannel;
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
        return requesterChannel.getIchatId().equals(SharedPreferencesAccessor.UserProfilePref.getCurrentUserProfile(context).getIchatId());
    }

    public static final Creator<ChannelAddition> CREATOR = new Creator<ChannelAddition>() {
        @Override
        public ChannelAddition createFromParcel(Parcel in) {
            return new ChannelAddition(in);
        }

        @Override
        public ChannelAddition[] newArray(int size) {
            return new ChannelAddition[size];
        }
    };

    protected ChannelAddition(Parcel in){
        uuid = in.readString();
        requesterChannel = in.readParcelable(getClass().getClassLoader());
        responderChannel = in.readParcelable(getClass().getClassLoader());
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
        dest.writeParcelable(requesterChannel, flags);
        dest.writeParcelable(responderChannel, flags);
        dest.writeString(message);
        dest.writeValue(requestTime);
        dest.writeValue(respondTime);
        dest.writeInt(isAccepted ? 1 : 0);
        dest.writeInt(isViewed ? 1 : 0);
        dest.writeInt(isExpired ? 1 : 0);
    }
}
