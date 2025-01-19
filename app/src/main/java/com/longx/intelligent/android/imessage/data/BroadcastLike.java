package com.longx.intelligent.android.imessage.data;

import java.util.Date;

/**
 * Created by LONG on 2024/9/13 at 8:54 PM.
 */
public class BroadcastLike {
    private String likeId;
    private String fromId;
    private String fromName;
    private String avatarHash;
    private Date likeTime;
    private String broadcastId;
    private String broadcastText;
    private Boolean broadcastDeleted;
    private Date broadcastTime;
    private String coverMediaId;
    private Boolean isNew;

    public BroadcastLike() {
    }

    public BroadcastLike(String likeId, String fromId, String fromName, String avatarHash, Date likeTime, String broadcastId, String broadcastText, Boolean broadcastDeleted, Date broadcastTime, String coverMediaId, Boolean isNew) {
        this.likeId = likeId;
        this.fromId = fromId;
        this.fromName = fromName;
        this.avatarHash = avatarHash;
        this.likeTime = likeTime;
        this.broadcastId = broadcastId;
        this.broadcastText = broadcastText;
        this.broadcastDeleted = broadcastDeleted;
        this.broadcastTime = broadcastTime;
        this.coverMediaId = coverMediaId;
        this.isNew = isNew;
    }

    public String getLikeId() {
        return likeId;
    }

    public String getFromId() {
        return fromId;
    }

    public String getFromName() {
        return fromName;
    }

    public String getAvatarHash() {
        return avatarHash;
    }

    public Date getLikeTime() {
        return likeTime;
    }

    public String getBroadcastId() {
        return broadcastId;
    }

    public String getBroadcastText() {
        return broadcastText;
    }

    public Boolean getBroadcastDeleted() {
        return broadcastDeleted;
    }

    public Date getBroadcastTime() {
        return broadcastTime;
    }

    public String getCoverMediaId() {
        return coverMediaId;
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
}
