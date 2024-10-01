package com.longx.intelligent.android.ichat2.data;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Date;
import java.util.Objects;

/**
 * Created by LONG on 2024/9/23 at 6:41 AM.
 */
public class BroadcastComment implements Parcelable {
    private String commentId;
    private String broadcastId;
    private String fromId;
    private String text;
    private String toCommentId;
    private String toReplyCommentId;
    private Date commentTime;

    private BroadcastComment toComment;
    private BroadcastComment toReplyComment;

    private int replyCount;

    private String avatarHash;
    private String fromName;
    private String broadcastText;
    private Date broadcastTime;
    private String coverMediaId;
    private Boolean broadcastDeleted;
    private Boolean isNew;

    public BroadcastComment() {
    }

    public BroadcastComment(String commentId, String broadcastId, String fromId, String text, String toCommentId, String toReplyCommentId, Date commentTime, BroadcastComment toComment, BroadcastComment toReplyComment, int replyCount, String avatarHash, String fromName, String broadcastText, Date broadcastTime, String coverMediaId, Boolean broadcastDeleted, Boolean isNew) {
        this.commentId = commentId;
        this.broadcastId = broadcastId;
        this.fromId = fromId;
        this.text = text;
        this.toCommentId = toCommentId;
        this.toReplyCommentId = toReplyCommentId;
        this.commentTime = commentTime;
        this.toComment = toComment;
        this.toReplyComment = toReplyComment;
        this.replyCount = replyCount;
        this.avatarHash = avatarHash;
        this.fromName = fromName;
        this.broadcastText = broadcastText;
        this.broadcastTime = broadcastTime;
        this.coverMediaId = coverMediaId;
        this.broadcastDeleted = broadcastDeleted;
        this.isNew = isNew;
    }

    public static final Creator<BroadcastComment> CREATOR = new Creator<BroadcastComment>() {
        @Override
        public BroadcastComment createFromParcel(Parcel in) {
            return new BroadcastComment(in);
        }

        @Override
        public BroadcastComment[] newArray(int size) {
            return new BroadcastComment[size];
        }
    };

    public String getCommentId() {
        return commentId;
    }

    public String getBroadcastId() {
        return broadcastId;
    }

    public String getFromId() {
        return fromId;
    }

    public String getText() {
        return text;
    }

    public String getToCommentId() {
        return toCommentId;
    }

    public Date getCommentTime() {
        return commentTime;
    }

    public BroadcastComment getToComment() {
        return toComment;
    }

    public String getAvatarHash() {
        return avatarHash;
    }

    public String getFromName() {
        return fromName;
    }

    public String getBroadcastText() {
        return broadcastText;
    }

    public Date getBroadcastTime() {
        return broadcastTime;
    }

    public String getCoverMediaId() {
        return coverMediaId;
    }

    public Boolean getBroadcastDeleted() {
        return broadcastDeleted;
    }

    public Boolean isNew() {
        return isNew;
    }

    public void setNew(Boolean isNew) {
        this.isNew = isNew;
    }

    public void setBroadcastText(String broadcastText) {
        this.broadcastText = broadcastText;
    }

    public void setCoverMediaId(String coverMediaId) {
        this.coverMediaId = coverMediaId;
    }

    public String getToReplyCommentId() {
        return toReplyCommentId;
    }

    public BroadcastComment getToReplyComment() {
        return toReplyComment;
    }

    public int getReplyCount() {
        return replyCount;
    }

    public void setReplyCount(int replyCount) {
        this.replyCount = replyCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BroadcastComment that = (BroadcastComment) o;
        return Objects.equals(commentId, that.commentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(commentId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    protected BroadcastComment(Parcel in) {
        commentId = in.readString();
        broadcastId = in.readString();
        fromId = in.readString();
        text = in.readString();
        toCommentId = in.readString();
        toReplyCommentId = in.readString();
        commentTime = new Date(in.readLong());
        toComment = in.readParcelable(BroadcastComment.class.getClassLoader());
        toReplyComment = in.readParcelable(BroadcastComment.class.getClassLoader());
        replyCount = in.readInt();
        avatarHash = in.readString();
        fromName = in.readString();
        broadcastText = in.readString();
        broadcastTime = new Date(in.readLong());
        coverMediaId = in.readString();
        byte tmpBroadcastDeleted = in.readByte();
        broadcastDeleted = tmpBroadcastDeleted == 0 ? null : tmpBroadcastDeleted == 1;
        byte tmpIsNew = in.readByte();
        isNew = tmpIsNew == 0 ? null : tmpIsNew == 1;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(commentId);
        dest.writeString(broadcastId);
        dest.writeString(fromId);
        dest.writeString(text);
        dest.writeString(toCommentId);
        dest.writeString(toReplyCommentId);
        dest.writeLong(commentTime.getTime());
        dest.writeParcelable(toComment, flags);
        dest.writeParcelable(toReplyComment, flags);
        dest.writeInt(replyCount);
        dest.writeString(avatarHash);
        dest.writeString(fromName);
        dest.writeString(broadcastText);
        dest.writeLong(broadcastTime.getTime());
        dest.writeString(coverMediaId);
        dest.writeByte((byte) (broadcastDeleted == null ? 0 : broadcastDeleted ? 1 : 2));
        dest.writeByte((byte) (isNew == null ? 0 : isNew ? 1 : 2));
    }
}
