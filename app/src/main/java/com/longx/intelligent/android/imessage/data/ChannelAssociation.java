package com.longx.intelligent.android.imessage.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.Objects;

/**
 * Created by LONG on 2024/5/8 at 2:06 AM.
 */
public class ChannelAssociation {
    private String associationId;
    private String imessageId;
    private String channelImessageId;
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

    public ChannelAssociation(String associationId, String imessageId, String channelImessageId, boolean requester, Date requestTime, Date acceptTime, boolean active, ChatMessageAllow chatMessageAllowToThem, ChatMessageAllow chatMessageAllowToMe) {
        this.associationId = associationId;
        this.imessageId = imessageId;
        this.channelImessageId = channelImessageId;
        this.requester = requester;
        this.requestTime = requestTime;
        this.acceptTime = acceptTime;
        this.active = active;
        this.chatMessageAllowToThem = chatMessageAllowToThem;
        this.chatMessageAllowToMe = chatMessageAllowToMe;
    }

    public ChannelAssociation(String associationId, String imessageId, String channelImessageId, boolean isRequester, Date requestTime, Date acceptTime, boolean active, Channel channel, ChatMessageAllow chatMessageAllowToThem, ChatMessageAllow chatMessageAllowToMe) {
        this(associationId, imessageId, channelImessageId, isRequester, requestTime, acceptTime, active, chatMessageAllowToThem, chatMessageAllowToMe);
        this.channel = channel;
    }

    public String getAssociationId() {
        return associationId;
    }

    public String getImessageId() {
        return imessageId;
    }

    public String getChannelImessageId() {
        return channelImessageId;
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
        return requester == that.requester && active == that.active && Objects.equals(imessageId, that.imessageId) && Objects.equals(channelImessageId, that.channelImessageId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(imessageId, channelImessageId, requester, active);
    }
}
