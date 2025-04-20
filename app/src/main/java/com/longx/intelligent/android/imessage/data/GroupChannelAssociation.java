package com.longx.intelligent.android.imessage.data;

import java.util.Date;
import java.util.List;

/**
 * Created by LONG on 2025/4/20 at 2:02 AM.
 */
public class GroupChannelAssociation {
    private String associationId;
    private String groupChannelId;
    private String channelImessageId;
    private String inviteChannelImessageId;
    private String inviteMessage;
    private Date inviteTime;
    private Date acceptTime;

    public GroupChannelAssociation() {
    }

    public GroupChannelAssociation(String associationId, String groupChannelId, String channelImessageId, String inviteChannelImessageId, String inviteMessage, Date inviteTime, Date acceptTime) {
        this.associationId = associationId;
        this.groupChannelId = groupChannelId;
        this.channelImessageId = channelImessageId;
        this.inviteChannelImessageId = inviteChannelImessageId;
        this.inviteMessage = inviteMessage;
        this.inviteTime = inviteTime;
        this.acceptTime = acceptTime;
    }

    public String getAssociationId() {
        return associationId;
    }

    public String getGroupChannelId() {
        return groupChannelId;
    }

    public String getChannelImessageId() {
        return channelImessageId;
    }

    public String getInviteChannelImessageId() {
        return inviteChannelImessageId;
    }

    public String getInviteMessage() {
        return inviteMessage;
    }

    public Date getInviteTime() {
        return inviteTime;
    }

    public Date getAcceptTime() {
        return acceptTime;
    }
}
