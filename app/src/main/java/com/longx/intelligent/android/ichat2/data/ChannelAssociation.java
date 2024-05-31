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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChannelAssociation that = (ChannelAssociation) o;
        return isRequester == that.isRequester && isActive == that.isActive && Objects.equals(ichatId, that.ichatId) && Objects.equals(channelIchatId, that.channelIchatId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ichatId, channelIchatId, isRequester, isActive);
    }
}
