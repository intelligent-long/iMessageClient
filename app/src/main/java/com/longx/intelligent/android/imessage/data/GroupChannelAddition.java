package com.longx.intelligent.android.imessage.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Created by LONG on 2024/5/2 at 1:11 AM.
 */
public class GroupChannelAddition {
    private String uuid;
    private Channel requesterChannel;
    private GroupChannel responderGroupChannel;
    private String message;
    private String note;
    private List<String> newTagNames;
    private List<String> toAddTagIds;
    private Date requestTime;
    private Date respondTime;
    @JsonProperty("isAccepted")
    private boolean isAccepted;
    @JsonProperty("isViewed")
    private boolean isViewed;
    @JsonProperty("isExpired")
    private boolean isExpired;

    public GroupChannelAddition() {
    }

    public GroupChannelAddition(String uuid, Channel requesterChannel, GroupChannel responderGroupChannel,
                                String message, String note, List<String> newTagNames, List<String> toAddTagIds, Date requestTime,
                                Date respondTime, boolean isAccepted, boolean isViewed, boolean isExpired) {
        this.uuid = uuid;
        this.requesterChannel = requesterChannel;
        this.responderGroupChannel = responderGroupChannel;
        this.message = message;
        this.note = note;
        this.newTagNames = newTagNames;
        this.toAddTagIds = toAddTagIds;
        this.requestTime = requestTime;
        this.respondTime = respondTime;
        this.isAccepted = isAccepted;
        this.isViewed = isViewed;
        this.isExpired = isExpired;
    }

    public String getUuid() {
        return uuid;
    }

    public Channel getRequesterChannel() {
        return requesterChannel;
    }

    public GroupChannel getResponderGroupChannel() {
        return responderGroupChannel;
    }

    public String getMessage() {
        return message;
    }

    public String getNote() {
        return note;
    }

    public List<String> getNewTagNames() {
        return newTagNames;
    }

    public List<String> getToAddTagIds() {
        return toAddTagIds;
    }

    public Date getRequestTime() {
        return requestTime;
    }

    public Date getRespondTime() {
        return respondTime;
    }

    public boolean isAccepted() {
        return isAccepted;
    }

    public boolean isViewed() {
        return isViewed;
    }

    public boolean isExpired() {
        return isExpired;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        GroupChannelAddition that = (GroupChannelAddition) obj;
        return Objects.equals(this.uuid, that.uuid) &&
                Objects.equals(this.requesterChannel, that.requesterChannel) &&
                Objects.equals(this.responderGroupChannel, that.responderGroupChannel) &&
                Objects.equals(this.message, that.message) &&
                Objects.equals(this.note, that.note) &&
                Objects.equals(this.newTagNames, that.newTagNames) &&
                Objects.equals(this.toAddTagIds, that.toAddTagIds) &&
                Objects.equals(this.requestTime, that.requestTime) &&
                Objects.equals(this.respondTime, that.respondTime) &&
                this.isAccepted == that.isAccepted &&
                this.isViewed == that.isViewed &&
                this.isExpired == that.isExpired;
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, requesterChannel, responderGroupChannel, message, note, newTagNames, toAddTagIds, requestTime, respondTime, isAccepted, isViewed, isExpired);
    }

    @Override
    public String toString() {
        return "GroupChannelAddition{" +
                "getUuid='" + uuid + '\'' +
                ", getRequesterChannel=" + requesterChannel +
                ", getResponderGroupChannel=" + responderGroupChannel +
                ", getMessage='" + message + '\'' +
                ", getNote='" + note + '\'' +
                ", getNewTagNames=" + newTagNames +
                ", getToAddTagIds=" + toAddTagIds +
                ", getRequestTime=" + requestTime +
                ", getRespondTime=" + respondTime +
                ", isAccepted=" + isAccepted +
                ", isViewed=" + isViewed +
                ", isExpired=" + isExpired +
                '}';
    }

}