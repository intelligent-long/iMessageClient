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
    private Date lastEditTime;
    private String text;
    private List<BroadcastMedia> broadcastMedias = new ArrayList<>();
    private boolean liked;
    private int likeCount;
    private boolean commented;
    private int commentCount;
    private BroadcastPermission broadcastPermission;

    public Broadcast() {
    }

    public Broadcast(String broadcastId, String ichatId, Date time, Date lastEditTime, String text, List<BroadcastMedia> broadcastMedias, boolean liked, int likeCount, boolean commented, int commentCount, BroadcastPermission broadcastPermission) {
        this.broadcastId = broadcastId;
        this.ichatId = ichatId;
        this.time = time;
        this.lastEditTime = lastEditTime;
        this.text = text;
        this.broadcastMedias = broadcastMedias;
        this.liked = liked;
        this.likeCount = likeCount;
        this.commented = commented;
        this.commentCount = commentCount;
        this.broadcastPermission = broadcastPermission;
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

    public Date getLastEditTime() {
        return lastEditTime;
    }

    public String getText() {
        return text;
    }

    public List<BroadcastMedia> getBroadcastMedias() {
        return broadcastMedias;
    }

    public boolean isLiked() {
        return liked;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public boolean isCommented() {
        return commented;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public BroadcastPermission getBroadcastPermission() {
        return broadcastPermission;
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
        long l = in.readLong();
        lastEditTime = l == -1 ? null : new Date(l);
        text = in.readString();
        in.readTypedList(broadcastMedias, BroadcastMedia.CREATOR);
        liked = in.readInt() == 1;
        likeCount = in.readInt();
        commented = in.readInt() == 1;
        commentCount = in.readInt();
        broadcastPermission = in.readParcelable(getClass().getClassLoader());
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(broadcastId);
        dest.writeString(ichatId);
        dest.writeLong(time.getTime());
        dest.writeLong(lastEditTime == null ? -1 : lastEditTime.getTime());
        dest.writeString(text);
        dest.writeTypedList(broadcastMedias);
        dest.writeInt(liked ? 1 : 0);
        dest.writeInt(likeCount);
        dest.writeInt(commented ? 1 : 0);
        dest.writeInt(commentCount);
        dest.writeParcelable(broadcastPermission, 0);
    }

    @Override
    public String toString() {
        return "Broadcast{" +
                "broadcastId='" + broadcastId + '\'' +
                ", ichatId='" + ichatId + '\'' +
                ", time=" + time +
                ", lastEditTime=" + lastEditTime +
                ", text='" + text + '\'' +
                ", broadcastMedias=" + broadcastMedias +
                ", liked=" + liked +
                ", likeCount=" + likeCount +
                ", commented=" + commented +
                ", commentCount=" + commentCount +
                '}';
    }
}
