package com.longx.intelligent.android.imessage.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by LONG on 2025/5/18 at 上午4:53.
 */
public enum AvatarType implements Parcelable {
    CHANNEL,
    GROUP_CHANNEL;

    public static final Creator<AvatarType> CREATOR = new Creator<AvatarType>() {
        @Override
        public AvatarType createFromParcel(Parcel in) {
            return AvatarType.valueOf(in.readString());
        }

        @Override
        public AvatarType[] newArray(int size) {
            return new AvatarType[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name());
    }
}
