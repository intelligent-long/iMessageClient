package com.longx.intelligent.android.ichat2.data;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

/**
 * Created by LONG on 2024/4/26 at 8:19 PM.
 */
public class Channel extends UserInfo implements Parcelable {
    private final String ichatId;
    private final String ichatIdUser;
    private final String email;
    private final String username;
    private final String note;
    private final Avatar avatar;
    private final Integer sex;
    private final Region firstRegion;
    private final Region secondRegion;
    private final Region thirdRegion;
    private final boolean associated;

    public Channel() {
        this(null, null, null, null, null, null, null, null, null, null, false);
    }

    public Channel(String ichatId, String ichatIdUser, String email, String username, String note, Avatar avatar, Integer sex, Region firstRegion, Region secondRegion, Region thirdRegion, boolean associated) {
        this.ichatId = ichatId;
        this.ichatIdUser = ichatIdUser;
        this.email = email;
        this.username = username;
        this.note = note;
        this.avatar = avatar;
        this.sex = sex;
        this.firstRegion = firstRegion;
        this.secondRegion = secondRegion;
        this.thirdRegion = thirdRegion;
        this.associated = associated;
    }

    public String getIchatId() {
        return ichatId;
    }

    public String getUsername() {
        return username;
    }

    public String getNote() {
        return note;
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

    public Avatar getAvatar() {
        return avatar;
    }

    public boolean isAssociated() {
        return associated;
    }

    public static final Creator<Channel> CREATOR = new Creator<Channel>() {
        @Override
        public Channel createFromParcel(Parcel in) {
            return new Channel(in);
        }

        @Override
        public Channel[] newArray(int size) {
            return new Channel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    protected Channel(Parcel in) {
        ichatId = in.readString();
        ichatIdUser = in.readString();
        email = in.readString();
        username = in.readString();
        note = in.readString();
        avatar = in.readParcelable(getClass().getClassLoader());
        sex = (Integer) in.readValue(getClass().getClassLoader());
        firstRegion = in.readParcelable(getClass().getClassLoader());
        secondRegion = in.readParcelable(getClass().getClassLoader());
        thirdRegion = in.readParcelable(getClass().getClassLoader());
        associated = in.readInt() == 1;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(ichatId);
        dest.writeString(ichatIdUser);
        dest.writeString(email);
        dest.writeString(username);
        dest.writeString(note);
        dest.writeParcelable(avatar, flags);
        dest.writeValue(sex);
        dest.writeParcelable(firstRegion, flags);
        dest.writeParcelable(secondRegion, flags);
        dest.writeParcelable(thirdRegion, flags);
        dest.writeInt(associated ? 1 : 0);
    }
}
