package com.longx.intelligent.android.imessage.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

/**
 * Created by LONG on 2025/6/26 at 3:57 AM.
 */
public class GroupChannelCollectionItem {
    private String uuid;
    private String owner;
    private String groupChannelId;
    private Date addTime;
    private Integer order;
    @JsonProperty("active")
    private boolean isActive;

    public GroupChannelCollectionItem() {
    }

    public GroupChannelCollectionItem(String uuid, String owner, String groupChannelId, Date addTime, Integer order, boolean isActive) {
        this.uuid = uuid;
        this.owner = owner;
        this.groupChannelId = groupChannelId;
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

    public String getGroupChannelId() {
        return groupChannelId;
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

    public void setOrder(Integer order) {
        this.order = order;
    }
}
