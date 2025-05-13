package com.longx.intelligent.android.imessage.data;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;
import java.util.Objects;

/**
 * Created by LONG on 2024/4/29 at 9:28 PM.
 */
@JsonIgnoreProperties({"stability"})
public class Avatar implements Parcelable {
    private String hash;
    private String imessageId;
    private String extension;
    private Date time;

    public Avatar() {
    }

    public Avatar(String hash, String imessageId, String extension, Date time) {
        this.hash = hash;
        this.imessageId = imessageId;
        this.extension = extension;
        this.time = time;
    }

    public String getImessageId() {
        return imessageId;
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

    public static final Creator<Avatar> CREATOR = new Creator<Avatar>() {
        @Override
        public Avatar createFromParcel(Parcel in) {
            return new Avatar(in);
        }

        @Override
        public Avatar[] newArray(int size) {
            return new Avatar[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    protected Avatar(Parcel in) {
        hash = in.readString();
        imessageId = in.readString();
        extension = in.readString();
        time = (Date) in.readValue(getClass().getClassLoader());
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(hash);
        dest.writeString(imessageId);
        dest.writeString(extension);
        dest.writeValue(time);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Avatar avatar = (Avatar) o;
        return Objects.equals(hash, avatar.hash) && Objects.equals(imessageId, avatar.imessageId) && Objects.equals(extension, avatar.extension) && Objects.equals(time, avatar.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hash, imessageId, extension, time);
    }

    @Override
    public String toString() {
        return "Avatar{" +
                "hash='" + hash + '\'' +
                ", imessageId='" + imessageId + '\'' +
                ", extension='" + extension + '\'' +
                ", time=" + time +
                '}';
    }
}
