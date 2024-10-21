package com.longx.intelligent.android.ichat2.yier;

import com.longx.intelligent.android.ichat2.data.ChatMessage;

import java.util.List;

/**
 * Created by LONG on 2024/5/19 at 1:13 PM.
 */
public interface ChatMessageUpdateYier {
    void onNewChatMessage(List<ChatMessage> newChatMessages);
    void onUnsendChatMessage(List<ChatMessage> unsendChatMessages);
}
