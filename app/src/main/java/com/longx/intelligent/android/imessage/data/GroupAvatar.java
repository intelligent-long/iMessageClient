package com.longx.intelligent.android.imessage.data;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Date;

/**
 * Created by LONG on 2025/4/22 at 5:00 PM.
 */
public class GroupAvatar implements Parcelable {
    private String hash;
    private String groupChannelId;
    private String extension;
    private Date time;

    public GroupAvatar() {
    }

    public GroupAvatar(String hash, String groupChannelId, String extension, Date time) {
        this.hash = hash;
        this.groupChannelId = groupChannelId;
        this.extension = extension;
        this.time = time;
    }

    public String getGroupChannelId() {
        return groupChannelId;
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

    public static final Creator<GroupAvatar> CREATOR = new Creator<GroupAvatar>() {
        @Override
        public GroupAvatar createFromParcel(Parcel in) {
            return new GroupAvatar(in);
        }

        @Override
        public GroupAvatar[] newArray(int size) {
            return new GroupAvatar[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    protected GroupAvatar(Parcel in) {
        hash = in.readString();
        groupChannelId = in.readString();
        extension = in.readString();
        time = (Date) in.readValue(getClass().getClassLoader());
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(hash);
        dest.writeString(groupChannelId);
        dest.writeString(extension);
        dest.writeValue(time);
    }

    @Override
    public String toString() {
        return "GroupAvatar{" +
                "hash='" + hash + '\'' +
                ", groupChannelId='" + groupChannelId + '\'' +
                ", extension='" + extension + '\'' +
                ", time=" + time +
                '}';
    }
}
