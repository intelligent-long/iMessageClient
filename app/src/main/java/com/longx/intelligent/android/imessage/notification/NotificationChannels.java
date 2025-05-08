package com.longx.intelligent.android.imessage.notification;

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

    public static class OtherOnline{
        static final String ID_OTHER_ONLINE = "OTHER_ONLINE";
        static final String NAME_OTHER_ONLINE = "登陆会话失效";
    }

    public static class VersionCompatibilityOffline{
        static final String ID_VERSION_COMPATIBILITY_OFFLINE = "VERSION_COMPATIBILITY_OFFLINE";
        static final String NAME_VERSION_COMPATIBILITY_OFFLINE = "版本不兼容下线";
    }

    public static class BroadcastInteraction{
        static final String ID_BROADCAST_INTERACTION = "BROADCAST_INTERACTION";
        static final String NAME_BROADCAST_INTERACTION = "广播互动";
    }

    public static class GroupChannelAdditionActivity{
        static final String ID_GROUP_CHANNEL_ADDITION_ACTIVITY = "GROUP_CHANNEL_ADDITION_ACTIVITY";
        static final String NAME_GROUP_CHANNEL_ADDITION_ACTIVITY = "新的群频道消息";
    }
}
