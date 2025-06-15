package com.longx.intelligent.android.imessage.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.longx.intelligent.android.imessage.da.database.manager.ChannelDatabaseManager;
import com.longx.intelligent.android.imessage.da.database.manager.GroupChannelDatabaseManager;

import java.util.Date;

/**
 * Created by LONG on 2025/6/14 at 7:09 AM.
 */
public class GroupChannelNotification {
    public enum Type{ACTIVE_DISCONNECT, PASSIVE_DISCONNECT}
    private String uuid;
    private Type type;
    private String groupChannelId;
    private String channelId;
    private boolean passive;
    private String byWhom;
    private Date time;
    @JsonProperty("viewed")
    private boolean isViewed;

    private GroupChannel groupChannel;
    private Channel channel;
    private Channel byChannel;

    public GroupChannelNotification() {
    }

    public GroupChannelNotification(String uuid, Type type, String groupChannelId, String channelId, boolean passive, String byWhom, Date time, boolean isViewed) {
        this.uuid = uuid;
        this.type = type;
        this.groupChannelId = groupChannelId;
        this.channelId = channelId;
        this.passive = passive;
        this.byWhom = byWhom;
        this.time = time;
        this.isViewed = isViewed;
    }

    public String getUuid() {
        return uuid;
    }

    public Type getType() {
        return type;
    }

    public String getGroupChannelId() {
        return groupChannelId;
    }

    public String getChannelId() {
        return channelId;
    }

    public boolean isPassive() {
        return passive;
    }

    public String getByWhom() {
        return byWhom;
    }

    public Date getTime() {
        return time;
    }

    public boolean isViewed() {
        return isViewed;
    }

    public GroupChannel getGroupChannel() {
        if(groupChannel == null){
            groupChannel = GroupChannelDatabaseManager.getInstance().findOneAssociation(groupChannelId);
        }
        return groupChannel;
    }

    public Channel getChannel() {
        if(channel == null){
            channel = ChannelDatabaseManager.getInstance().findOneChannel(channelId);
        }
        return channel;
    }

    public Channel getByChannel() {
        if(byChannel == null){
            byChannel = ChannelDatabaseManager.getInstance().findOneChannel(byWhom);
        }
        return byChannel;
    }
}
