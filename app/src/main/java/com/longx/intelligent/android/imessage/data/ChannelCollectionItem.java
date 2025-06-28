package com.longx.intelligent.android.imessage.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

/**
 * Created by LONG on 2025/6/26 at 3:57 AM.
 */
public class ChannelCollectionItem {
    private String uuid;
    private String owner;
    private String channelId;
    private Date addTime;
    private Integer order;
    @JsonProperty("active")
    private boolean isActive;

    public ChannelCollectionItem() {
    }

    public ChannelCollectionItem(String uuid, String owner, String channelId, Date addTime, Integer order, boolean isActive) {
        this.uuid = uuid;
        this.owner = owner;
        this.channelId = channelId;
        this.addTime = addTime;
        this.order = order;
        this.isActive = isActive;
    }

    public String getUuid() {
        return uuid;
    }

    public String getOwner() {
        return owner;
    }

    public String getChannelId() {
        return channelId;
    }

    public Date getAddTime() {
        return addTime;
    }

    public Integer getOrder() {
        return order;
    }

    public boolean isActive() {
        return isActive;
    }

    @Override
    public String toString() {
        return "ChannelCollectionItem{" +
                "uuid='" + uuid + '\'' +
                ", owner='" + owner + '\'' +
                ", channelId='" + channelId + '\'' +
                ", addTime=" + addTime +
                ", order=" + order +
                ", isActive=" + isActive +
                '}';
    }
}
