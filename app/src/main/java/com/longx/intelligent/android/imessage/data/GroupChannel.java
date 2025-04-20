package com.longx.intelligent.android.imessage.data;

import java.util.Date;
import java.util.List;

/**
 * Created by LONG on 2025/4/15 at 10:49 PM.
 */
public class GroupChannel {
    private String groupChannelId;
    private String owner;
    private String name;
    private String note;
    private Date createTime;
    private List<GroupChannelAssociation> groupChannelAssociations;

    public GroupChannel() {
    }

    public GroupChannel(String groupChannelId, String owner, String name, String note, Date createTime, List<GroupChannelAssociation> groupChannelAssociations) {
        this.groupChannelId = groupChannelId;
        this.owner = owner;
        this.name = name;
        this.note = note;
        this.createTime = createTime;
        this.groupChannelAssociations = groupChannelAssociations;
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
}
