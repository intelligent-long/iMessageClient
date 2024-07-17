package com.longx.intelligent.android.ichat2.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.Objects;

/**
 * Created by LONG on 2024/5/8 at 2:06 AM.
 */
public class ChannelAssociation {
    private String associationId;
    private String ichatId;
    private String channelIchatId;
    private boolean requester;
    private Date requestTime;
    private Date acceptTime;
    private boolean active;
    @JsonProperty("channel")
    private Channel channel;
    private ChatMessageAllow chatMessageAllowToThem;
    private ChatMessageAllow chatMessageAllowToMe;

    public ChannelAssociation() {
    }

    public ChannelAssociation(String associationId, String ichatId, String channelIchatId, boolean requester, Date requestTime, Date acceptTime, boolean active, ChatMessageAllow chatMessageAllowToThem, ChatMessageAllow chatMessageAllowToMe) {
        this.associationId = associationId;
        this.ichatId = ichatId;
        this.channelIchatId = channelIchatId;
        this.requester = requester;
        this.requestTime = requestTime;
        this.acceptTime = acceptTime;
        this.active = active;
        this.chatMessageAllowToThem = chatMessageAllowToThem;
        this.chatMessageAllowToMe = chatMessageAllowToMe;
    }

    public ChannelAssociation(String associationId, String ichatId, String channelIchatId, boolean isRequester, Date requestTime, Date acceptTime, boolean active, Channel channel, ChatMessageAllow chatMessageAllowToThem, ChatMessageAllow chatMessageAllowToMe) {
        this(associationId, ichatId, channelIchatId, isRequester, requestTime, acceptTime, active, chatMessageAllowToThem, chatMessageAllowToMe);
        this.channel = channel;
    }

    public String getAssociationId() {
        return associationId;
    }

    public String getIchatId() {
        return ichatId;
    }

    public String getChannelIchatId() {
        return channelIchatId;
    }

    public boolean isRequester() {
        return requester;
    }

    public Date getRequestTime() {
        return requestTime;
    }

    public Date getAcceptTime() {
        return acceptTime;
    }

    public boolean isActive() {
        return active;
    }

    public Channel getChannel() {
        return channel;
    }

    public ChatMessageAllow getChatMessageAllowToThem() {
        return chatMessageAllowToThem;
    }

    public ChatMessageAllow getChatMessageAllowToMe() {
        return chatMessageAllowToMe;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChannelAssociation that = (ChannelAssociation) o;
        return requester == that.requester && active == that.active && Objects.equals(ichatId, that.ichatId) && Objects.equals(channelIchatId, that.channelIchatId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ichatId, channelIchatId, requester, active);
    }
}
