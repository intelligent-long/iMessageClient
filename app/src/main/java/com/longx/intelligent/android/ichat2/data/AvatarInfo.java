package com.longx.intelligent.android.ichat2.data;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Date;

/**
 * Created by LONG on 2024/4/29 at 9:28 PM.
 */
public class AvatarInfo implements Parcelable {
    private String hash;
    private String ichatId;
    private String extension;
    private Date time;

    public AvatarInfo() {
    }

    public AvatarInfo(String hash, String ichatId, String extension, Date time) {
        this.hash = hash;
        this.ichatId = ichatId;
        this.extension = extension;
        this.time = time;
    }

    public String getIchatId() {
        return ichatId;
    }

    public String getExtension() {
        return extension;
    }

    public Date getTime() {
        return time;
    }

    public String getHash() {
        return hash;
    }

    public static final Creator<AvatarInfo> CREATOR = new Creator<AvatarInfo>() {
        @Override
        public AvatarInfo createFromParcel(Parcel in) {
            return new AvatarInfo(in);
        }

        @Override
        public AvatarInfo[] newArray(int size) {
            return new AvatarInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    protected AvatarInfo(Parcel in) {
        hash = in.readString();
        ichatId = in.readString();
        extension = in.readString();
        time = (Date) in.readValue(getClass().getClassLoader());
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(hash);
        dest.writeString(ichatId);
        dest.writeString(extension);
        dest.writeValue(time);
    }
}
