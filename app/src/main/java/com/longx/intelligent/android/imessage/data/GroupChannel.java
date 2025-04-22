package com.longx.intelligent.android.imessage.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Created by LONG on 2025/4/15 at 10:49 PM.
 */
public class GroupChannel {
    private GroupAvatar groupAvatar;
    private String groupChannelId;
    private String owner;
    private String name;
    private String note;
    private Date createTime;
    private List<GroupChannelAssociation> groupChannelAssociations;

    public GroupChannel() {
    }

    public GroupChannel(GroupAvatar groupAvatar, String groupChannelId, String owner, String name, String note, Date createTime, List<GroupChannelAssociation> groupChannelAssociations) {
        this.groupAvatar = groupAvatar;
        this.groupChannelId = groupChannelId;
        this.owner = owner;
        this.name = name;
        this.note = note;
        this.createTime = createTime;
        this.groupChannelAssociations = groupChannelAssociations;
    }

    public GroupChannel(GroupAvatar groupAvatar, String groupChannelId, String owner, String name, String note, Date createTime) {
        this.groupAvatar = groupAvatar;
        this.groupChannelId = groupChannelId;
        this.owner = owner;
        this.name = name;
        this.note = note;
        this.createTime = createTime;
        this.groupChannelAssociations = new ArrayList<>();
    }

    public GroupAvatar getGroupAvatar() {
        return groupAvatar;
    }

    public String getGroupChannelId() {
        return groupChannelId;
    }

    public String getOwner() {
        return owner;
    }

    public String getName() {
        return name;
    }

    public String getNote() {
        return note;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public List<GroupChannelAssociation> getGroupChannelAssociations() {
        return groupChannelAssociations;
    }

    public void addGroupChannelAssociation(GroupChannelAssociation groupChannelAssociation){
        groupChannelAssociations.add(groupChannelAssociation);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupChannel that = (GroupChannel) o;
        return Objects.equals(groupChannelId, that.groupChannelId) && Objects.equals(owner, that.owner) && Objects.equals(name, that.name) && Objects.equals(note, that.note) && Objects.equals(createTime, that.createTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupChannelId, owner, name, note, createTime);
    }
}
