package com.longx.intelligent.android.ichat2.data;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.longx.intelligent.android.ichat2.da.privatefile.PrivateFilesAccessor;

import java.io.File;

/**
 * Created by LONG on 2024/4/26 at 8:19 PM.
 */
public class ChannelInfo extends UserInfo implements Parcelable {
    private final String ichatId;
    private final String ichatIdUser;
    private final String email;
    private final String username;
    private final String avatarHash;
    private final Integer sex;
    private final Region firstRegion;
    private final Region secondRegion;
    private final Region thirdRegion;
    private final boolean connected;
    @JsonIgnore
    private final String avatarExtension;

    public ChannelInfo() {
        this(null, null, null, null, null, null, null, null, null, false , null);
    }

    public ChannelInfo(String ichatId, String ichatIdUser, String email, String username, String avatarHash, Integer sex, Region firstRegion, Region secondRegion, Region thirdRegion, boolean connected, String avatarExtension) {
        this.ichatId = ichatId;
        this.ichatIdUser = ichatIdUser;
        this.email = email;
        this.username = username;
        this.avatarHash = avatarHash;
        this.sex = sex;
        this.firstRegion = firstRegion;
        this.secondRegion = secondRegion;
        this.thirdRegion = thirdRegion;
        this.connected = connected;
        this.avatarExtension = avatarExtension;
    }

    public String getIchatId() {
        return ichatId;
    }

    public String getUsername() {
        return username;
    }

    public String getIchatIdUser() {
        return ichatIdUser;
    }

    public Integer getSex() {
        return sex;
    }

    public Region getFirstRegion() {
        return firstRegion;
    }

    public Region getSecondRegion() {
        return secondRegion;
    }

    public Region getThirdRegion() {
        return thirdRegion;
    }

    public String getEmail() {
        return email;
    }

    public String getAvatarHash() {
        return avatarHash;
    }

    public boolean isConnected() {
        return connected;
    }

    public ChannelInfo setAvatarExtension(String avatarExtension){
        return new ChannelInfo(ichatId, ichatIdUser, email, username, avatarHash, sex, firstRegion, secondRegion, thirdRegion, connected, avatarExtension);
    }

    public String getAvatarExtension() {
        return avatarExtension;
    }

    public File getAvatarFile(Context context) {
        if(avatarExtension == null) return null;
        return PrivateFilesAccessor.getAvatarFile(context, ichatId, avatarExtension);
    }

    public static final Creator<ChannelInfo> CREATOR = new Creator<ChannelInfo>() {
        @Override
        public ChannelInfo createFromParcel(Parcel in) {
            return new ChannelInfo(in);
        }

        @Override
        public ChannelInfo[] newArray(int size) {
            return new ChannelInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    protected ChannelInfo(Parcel in) {
        ichatId = in.readString();
        ichatIdUser = in.readString();
        email = in.readString();
        username = in.readString();
        avatarHash = in.readString();
        sex = (Integer) in.readValue(getClass().getClassLoader());
        firstRegion = in.readParcelable(getClass().getClassLoader());
        secondRegion = in.readParcelable(getClass().getClassLoader());
        thirdRegion = in.readParcelable(getClass().getClassLoader());
        connected = in.readInt() == 1;
        avatarExtension = in.readString();
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(ichatId);
        dest.writeString(ichatIdUser);
        dest.writeString(email);
        dest.writeString(username);
        dest.writeString(avatarHash);
        dest.writeValue(sex);
        dest.writeParcelable(firstRegion, flags);
        dest.writeParcelable(secondRegion, flags);
        dest.writeParcelable(thirdRegion, flags);
        dest.writeInt(connected ? 1 : 0);
        dest.writeString(avatarExtension);
    }
}
