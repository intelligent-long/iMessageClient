package com.longx.intelligent.android.ichat2.data.request;

import com.longx.intelligent.android.ichat2.data.ChatMessageAllow;

/**
 * Created by LONG on 2024/7/15 at 4:23 PM.
 */
public class ChangeAllowChatMessagePostBody {
    private String channelId;
    private ChatMessageAllow chatMessageAllow;

    public ChangeAllowChatMessagePostBody() {
    }

    public ChangeAllowChatMessagePostBody(String channelId, ChatMessageAllow chatMessageAllow) {
        this.channelId = channelId;
        this.chatMessageAllow = chatMessageAllow;
    }

    public String getChannelId() {
        return channelId;
    }

    public ChatMessageAllow getChatMessageAllow() {
        return chatMessageAllow;
    }
}
