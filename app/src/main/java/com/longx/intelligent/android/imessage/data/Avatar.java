package com.longx.intelligent.android.imessage.data;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Date;

/**
 * Created by LONG on 2024/4/29 at 9:28 PM.
 */
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
    public String toString() {
        return "Avatar{" +
                "hash='" + hash + '\'' +
                ", imessageId='" + imessageId + '\'' +
                ", extension='" + extension + '\'' +
                ", time=" + time +
                '}';
    }
}
