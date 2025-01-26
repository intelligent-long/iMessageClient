package com.longx.intelligent.android.imessage.yier;

import com.longx.intelligent.android.imessage.data.ChatMessage;

import java.util.List;

/**
 * Created by LONG on 2024/5/19 at 1:13 PM.
 */
public interface ChatMessagesUpdateYier {
    void onNewChatMessages(List<ChatMessage> newChatMessages);
    void onChatMessagesUpdated(List<ChatMessage> updatedChatMessages);
    void onUnsendChatMessages(List<ChatMessage> unsendChatMessages, List<ChatMessage> toUnsendChatMessages);
}
