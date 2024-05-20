package com.longx.intelligent.android.ichat2.notification;

/**
 * Created by LONG on 2024/4/6 at 5:53 PM.
 */
public class NotificationChannels {

    public static class ServerMessageServiceNotRunning {
        static final String ID_SERVER_MESSAGE_SERVICE_NOT_RUNNING = "SERVER_MESSAGE_SERVICE_NOT_RUNNING";
        static final String NAME_SERVER_MESSAGE_SERVICE_NOT_RUNNING = "服务长时间未运行";
    }

    public static class ChatMessage{
        static final String ID_CHAT_MESSAGE = "CHAT_MESSAGE";
        static final String NAME_CHAT_MESSAGE = "聊天消息";
    }

    public static class ChannelAdditionActivity{
        static final String ID_CHANNEL_ADDITION_ACTIVITY = "CHANNEL_ADDITION_ACTIVITY";
        static final String NAME_CHANNEL_ADDITION_ACTIVITY = "新的频道消息";
    }
}
