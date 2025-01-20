package com.longx.intelligent.android.imessage.data;

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
    private String imessageId;
    private String channelName;
    private String channelAvatarHash;
    private Date time;
    private Date lastEditTime;
    private String text;
    private List<BroadcastMedia> broadcastMedias = new ArrayList<>();
    private boolean liked;
    private int likeCount;
    private boolean commented;
    private int commentCount;
    private BroadcastPermission broadcastPermission;

    public enum BroadcastVisibility{ALL, PARTIAL, NONE}

    public static BroadcastVisibility determineBroadcastVisibility(BroadcastChannelPermission broadcastChannelPermission, BroadcastPermission broadcastPermission){
        switch (broadcastChannelPermission.getPermission()){
            case BroadcastChannelPermission.PRIVATE:{
                return BroadcastVisibility.NONE;
            }
            case BroadcastChannelPermission.PUBLIC:{
                switch (broadcastPermission.getPermission()){
                    case BroadcastPermission.PRIVATE:{
                        return BroadcastVisibility.NONE;
                    }
                    case BroadcastPermission.PUBLIC:{
                        return BroadcastVisibility.ALL;
                    }
                    case BroadcastPermission.CONNECTED_CHANNEL_CIRCLE:{
                        return BroadcastVisibility.PARTIAL;
                    }
                }
            }
            case BroadcastChannelPermission.CONNECTED_CHANNEL_CIRCLE:{
                switch (broadcastPermission.getPermission()){
                    case BroadcastPermission.PRIVATE:{
                        return BroadcastVisibility.NONE;
                    }
                    case BroadcastPermission.PUBLIC:
                    case BroadcastPermission.CONNECTED_CHANNEL_CIRCLE:{
                        return BroadcastVisibility.PARTIAL;
                    }
                }
            }
        }
        return null;
    }

    public Broadcast() {
    }

    public Broadcast(String broadcastId, String imessageId, String channelName, String channelAvatarHash, Date time, Date lastEditTime, String text, List<BroadcastMedia> broadcastMedias, boolean liked, int likeCount, boolean commented, int commentCount, BroadcastPermission broadcastPermission) {
        this.broadcastId = broadcastId;
        this.imessageId = imessageId;
        this.channelName = channelName;
        this.channelAvatarHash = channelAvatarHash;
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

    public String getImessageId() {
        return imessageId;
    }

    public String getChannelName() {
        return channelName;
    }

    public String getChannelAvatarHash() {
        return channelAvatarHash;
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
        imessageId = in.readString();
        channelName = in.readString();
        channelAvatarHash = in.readString();
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
        dest.writeString(imessageId);
        dest.writeString(channelName);
        dest.writeString(channelAvatarHash);
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
                ", imessageId='" + imessageId + '\'' +
                ", channelName='" + channelName + '\'' +
                ", channelAvatarHash='" + channelAvatarHash + '\'' +
                ", time=" + time +
                ", lastEditTime=" + lastEditTime +
                ", text='" + text + '\'' +
                ", broadcastMedias=" + broadcastMedias +
                ", liked=" + liked +
                ", likeCount=" + likeCount +
                ", commented=" + commented +
                ", commentCount=" + commentCount +
                ", broadcastPermission=" + broadcastPermission +
                '}';
    }
}
