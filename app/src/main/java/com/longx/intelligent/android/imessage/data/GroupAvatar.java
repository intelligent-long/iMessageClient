package com.longx.intelligent.android.imessage.data;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;
import java.util.Objects;

/**
 * Created by LONG on 2025/4/22 at 5:00 PM.
 */
@JsonIgnoreProperties({"stability"})
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupAvatar that = (GroupAvatar) o;
        return Objects.equals(hash, that.hash) && Objects.equals(groupChannelId, that.groupChannelId) && Objects.equals(extension, that.extension) && Objects.equals(time, that.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hash, groupChannelId, extension, time);
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
