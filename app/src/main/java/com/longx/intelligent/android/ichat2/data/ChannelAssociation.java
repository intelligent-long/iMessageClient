package com.longx.intelligent.android.ichat2.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

/**
 * Created by LONG on 2024/5/8 at 2:06 AM.
 */
public class ChannelAssociation {
    private String associationId;
    private String ichatId;
    private String channelIchatId;
    private boolean isRequester;
    private Date requestTime;
    private Date acceptTime;
    private boolean isActive;
    @JsonProperty("channel")
    private Channel channel;

    public ChannelAssociation() {
    }

    public ChannelAssociation(String associationId, String ichatId, String channelIchatId, boolean isRequester, Date requestTime, Date acceptTime, boolean isActive) {
        this.associationId = associationId;
        this.ichatId = ichatId;
        this.channelIchatId = channelIchatId;
        this.isRequester = isRequester;
        this.requestTime = requestTime;
        this.acceptTime = acceptTime;
        this.isActive = isActive;
    }

    public ChannelAssociation(String associationId, String ichatId, String channelIchatId, boolean isRequester, Date requestTime, Date acceptTime, boolean isActive, Channel channel) {
        this(associationId, ichatId, channelIchatId, isRequester, requestTime, acceptTime, isActive);
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
        return isRequester;
    }

    public Date getRequestTime() {
        return requestTime;
    }

    public Date getAcceptTime() {
        return acceptTime;
    }

    public boolean isActive() {
        return isActive;
    }

    public Channel getChannel() {
        return channel;
    }
}
