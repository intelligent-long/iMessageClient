package com.longx.intelligent.android.imessage.data.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.longx.intelligent.android.imessage.data.Channel;
import com.longx.intelligent.android.imessage.data.GroupChannel;

/**
 * Created by LONG on 2025/5/14 at 1:59 AM.
 */
public class InviteJoinGroupChannelPostBody {
    private Channel invitee;
    private GroupChannel groupChannelInvitedTo;

    public InviteJoinGroupChannelPostBody() {
    }

    public InviteJoinGroupChannelPostBody(Channel invitee, GroupChannel groupChannelInvitedTo) {
        this.invitee = invitee;
        this.groupChannelInvitedTo = groupChannelInvitedTo;
    }

    public Channel getInvitee() {
        return invitee;
    }

    public GroupChannel getGroupChannelInvitedTo() {
        return groupChannelInvitedTo;
    }
}
