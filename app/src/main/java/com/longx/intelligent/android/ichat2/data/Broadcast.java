package com.longx.intelligent.android.ichat2.data;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by LONG on 2024/7/28 at 2:13 PM.
 */
public class Broadcast implements Parcelable {
    private String broadcastId;
    private String ichatId;
    private Date time;
    private String text;
    private List<BroadcastMedia> broadcastMedias = new ArrayList<>();

    public Broadcast() {
    }

    public Broadcast(String broadcastId, String ichatId, Date time, String text, List<BroadcastMedia> broadcastMedias) {
        this.broadcastId = broadcastId;
        this.ichatId = ichatId;
        this.time = time;
        this.text = text;
        this.broadcastMedias = broadcastMedias;
    }

    public String getBroadcastId() {
        return broadcastId;
    }

    public String getIchatId() {
        return ichatId;
    }

    public Date getTime() {
        return time;
    }

    public String getText() {
        return text;
    }

    public List<BroadcastMedia> getBroadcastMedias() {
        return broadcastMedias;
    }

    public static final Creator<Broadcast> CREATOR = new Creator<Broadcast>() {
        @Override
        public Broadcast createFromParcel(Parcel in) {
            return new Broadcast(in);
        }

        @Override
        public Broadcast[] newArray(int size) {
            return new Broadcast[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    protected Broadcast(Parcel in) {
        broadcastId = in.readString();
        ichatId = in.readString();
        time = new Date(in.readLong());
        text = in.readString();
        in.readTypedList(broadcastMedias, BroadcastMedia.CREATOR);
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(broadcastId);
        dest.writeString(ichatId);
        dest.writeLong(time.getTime());
        dest.writeString(text);
        dest.writeTypedList(broadcastMedias);
    }
}
