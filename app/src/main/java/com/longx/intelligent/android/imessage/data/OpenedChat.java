package com.longx.intelligent.android.imessage.data;

/**
 * Created by LONG on 2024/5/16 at 7:04 PM.
 */
public class OpenedChat {
    private final String channelImessageId;
    private final int notViewedCount;
    private final boolean show;
    private ChatMessage newestChatMessage;
    private Channel channel;

    public OpenedChat(String channelImessageId, int notViewedCount, boolean show) {
        this.channelImessageId = channelImessageId;
        this.notViewedCount = notViewedCount;
        this.show = show;
    }

    public String getChannelImessageId() {
        return channelImessageId;
    }

    public int getNotViewedCount() {
        return notViewedCount;
    }

    public boolean isShow() {
        return show;
    }

    public ChatMessage getNewestChatMessage() {
        return newestChatMessage;
    }

    public void setNewestChatMessage(ChatMessage newestChatMessage) {
        this.newestChatMessage = newestChatMessage;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }
}
