package com.longx.intelligent.android.imessage.notification;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;

import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.imessage.activity.AuthActivity;
import com.longx.intelligent.android.imessage.activity.BroadcastInteractionsActivity;
import com.longx.intelligent.android.imessage.activity.ChannelAdditionsActivity;
import com.longx.intelligent.android.imessage.activity.ChatActivity;
import com.longx.intelligent.android.imessage.activity.ExtraKeys;
import com.longx.intelligent.android.imessage.activity.GroupChannelAdditionsActivity;
import com.longx.intelligent.android.imessage.da.database.manager.ChannelDatabaseManager;
import com.longx.intelligent.android.imessage.data.Channel;
import com.longx.intelligent.android.imessage.data.ChatMessage;
import com.longx.intelligent.android.imessage.data.OfflineDetail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by LONG on 2024/4/6 at 5:51 PM.
 */
public class Notifications {
    public enum PendingNotificationId {CHAT_MESSAGE}
    private static final Map<PendingNotificationId, List<Runnable>> pendingNotificationMap = new HashMap<>();

    public synchronized static void notifyPendingNotifications(PendingNotificationId pendingNotificationId){
        List<Runnable> runnables = pendingNotificationMap.get(pendingNotificationId);
        Set<Runnable> runnableSet = new HashSet<>();
        if(runnables != null){
            runnables.forEach(runnable -> {
                runnable.run();
                runnableSet.add(runnable);
            });
        }
        if(runnables != null) {
            runnableSet.forEach(runnables::remove);
        }
    }

    public static void notifyServerMessageServiceNotRunning(Context context){
        new Notification.Builder(context,
                NotificationChannels.ServerMessageServiceNotRunning.ID_SERVER_MESSAGE_SERVICE_NOT_RUNNING,
                NotificationChannels.ServerMessageServiceNotRunning.NAME_SERVER_MESSAGE_SERVICE_NOT_RUNNING)
                .title(context.getString(R.string.notification_title_server_message_service_not_running))
                .text(context.getString(R.string.notification_message_server_message_service_not_running))
                .smallIcon(R.drawable.error_fill_24px)
                .importance(NotificationManager.IMPORTANCE_HIGH)
                .autoCancel(false)
                .build()
                .show();
    }

    public static void notifyChatMessage(Context context, ChatMessage chatMessage){
        String text = null;
        switch (chatMessage.getType()){
            case ChatMessage.TYPE_TEXT:
                text = chatMessage.getText();
                break;
            case ChatMessage.TYPE_IMAGE:
                text = "[图片]";
                break;
            case ChatMessage.TYPE_FILE:
                text = "[文件]";
                break;
            case ChatMessage.TYPE_VIDEO:
                text = "[视频]";
                break;
            case ChatMessage.TYPE_VOICE:
                text = "[语音]";
                break;
            case ChatMessage.TYPE_UNSEND:
                text = "[撤回]";
                break;
            case ChatMessage.TYPE_MESSAGE_EXPIRED:
                text = "[消息过期]";
                break;
        }
        Channel channel = ChannelDatabaseManager.getInstance().findOneChannel(chatMessage.getOther(context));
        if(channel == null){
            pendingNotificationMap.computeIfAbsent(PendingNotificationId.CHAT_MESSAGE, k -> new ArrayList<>());
            String finalText = text;
            pendingNotificationMap.get(PendingNotificationId.CHAT_MESSAGE).add(() -> {
                    Channel channel1 = ChannelDatabaseManager.getInstance().findOneChannel(chatMessage.getOther(context));
                    if(channel1 == null) return;
                    Intent intent = new Intent(context, ChatActivity.class);
                    intent.putExtra(ExtraKeys.CHANNEL, channel1);
                    new Notification.Builder(context,
                            NotificationChannels.ChatMessage.ID_CHAT_MESSAGE,
                            NotificationChannels.ChatMessage.NAME_CHAT_MESSAGE)
                            .intent(intent)
                            .importance(NotificationManager.IMPORTANCE_HIGH)
                            .title(channel1.getUsername())
                            .text(finalText)
                            .smallIcon(R.drawable.chat_fill_24px)
                            .build()
                            .show();
            });
        }else {
            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra(ExtraKeys.CHANNEL, channel);
            new Notification.Builder(context,
                    NotificationChannels.ChatMessage.ID_CHAT_MESSAGE,
                    NotificationChannels.ChatMessage.NAME_CHAT_MESSAGE)
                    .intent(intent)
                    .importance(NotificationManager.IMPORTANCE_HIGH)
                    .title(channel.getUsername())
                    .text(text)
                    .smallIcon(R.drawable.chat_fill_24px)
                    .build()
                    .show();
        }
    }

    public static void notifyChannelAdditionActivity(Context context, int notificationRequest, int notificationRespond){
        Intent intent = new Intent(context, ChannelAdditionsActivity.class);
        String text;
        if(notificationRequest != 0 && notificationRespond != 0) {
            text = notificationRequest + " 个新的频道添加请求, " + notificationRespond  + " 个新的频道添加回应";
            intent.putExtra(ExtraKeys.INIT_TAB_INDEX, 0);
        }else if(notificationRequest != 0){
            text = notificationRequest + " 个新的频道添加请求";
            intent.putExtra(ExtraKeys.INIT_TAB_INDEX, 0);
        }else if(notificationRespond != 0){
            text = notificationRespond + " 个新的频道添加回应";
            intent.putExtra(ExtraKeys.INIT_TAB_INDEX, 1);
        }else {
            return;
        }
        new Notification.Builder(context,
                NotificationChannels.ChannelAdditionActivity.ID_CHANNEL_ADDITION_ACTIVITY,
                NotificationChannels.ChannelAdditionActivity.NAME_CHANNEL_ADDITION_ACTIVITY)
                .intent(intent)
                .importance(NotificationManager.IMPORTANCE_HIGH)
                .title("新的频道")
                .text(text)
                .smallIcon(R.drawable.person_add_fill_24px)
                .build()
                .show();
    }

    public static void notifyGoOfflineBecauseOfOtherOnline(Context context, OfflineDetail offlineDetail){
        Intent intent = new Intent(context, AuthActivity.class);
        new Notification.Builder(context,
                NotificationChannels.OtherOnline.ID_OTHER_ONLINE,
                NotificationChannels.OtherOnline.NAME_OTHER_ONLINE)
                .intent(intent)
                .importance(NotificationManager.IMPORTANCE_HIGH)
                .title("登陆会话已失效")
                .text(offlineDetail.getDesc())
                .smallIcon(R.drawable.no_accounts_fill_24px)
                .build()
                .show();
    }

    public static void notifyVersionCompatibilityOffline(Context context, String title, String message){
        Intent intent = new Intent(context, AuthActivity.class);
        intent.putExtra(ExtraKeys.MESSAGE, message);
        new Notification.Builder(context,
                NotificationChannels.VersionCompatibilityOffline.ID_VERSION_COMPATIBILITY_OFFLINE,
                NotificationChannels.VersionCompatibilityOffline.NAME_VERSION_COMPATIBILITY_OFFLINE)
                .intent(intent)
                .importance(NotificationManager.IMPORTANCE_HIGH)
                .title(title)
                .text(message)
                .smallIcon(R.drawable.hide_source_24px)
                .build()
                .show();
    }

    public static void notifyBroadcastInteractionNewsContent(Context context, String title, String message, int iconRes){
        Intent intent = new Intent(context, BroadcastInteractionsActivity.class);
        new Notification.Builder(context,
                NotificationChannels.BroadcastInteraction.ID_BROADCAST_INTERACTION,
                NotificationChannels.BroadcastInteraction.NAME_BROADCAST_INTERACTION)
                .intent(intent)
                .importance(NotificationManager.IMPORTANCE_HIGH)
                .title(title)
                .text(message)
                .smallIcon(iconRes)
                .build()
                .show();
    }

    public static void notifyGroupChannelAdditionActivity(Context context, int notificationRequest, int notificationRespond, int notificationInviter, int notificationInvitee){
        Intent intent = new Intent(context, GroupChannelAdditionsActivity.class);
        String text;
        boolean[] booleans = new boolean[4];
        StringBuilder stringBuilder = new StringBuilder();
        if(notificationRequest != 0){
            stringBuilder.append(notificationRequest).append(" 个新的群频道添加请求, ");
            booleans[0] = true;
        }
        if(notificationRespond != 0){
            stringBuilder.append(notificationRequest).append(" 个新的群频道添加回应, ");
            booleans[1] = true;
        }
        if(notificationInviter != 0){
            stringBuilder.append(notificationInviter).append(" 个新的邀请添加群频道请求, ");
            booleans[2] = true;
        }
        if(notificationInvitee != 0){
            stringBuilder.append(notificationInvitee).append(" 个新的邀请添加群频道回应, ");
            booleans[3] = true;
        }
        if(stringBuilder.length() != 0){
            text = stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length() - 1).toString();
        }else {
            return;
        }
        if((booleans[0] || booleans[2])){
            intent.putExtra(ExtraKeys.INIT_TAB_INDEX, 0);
        }else if(booleans[1] && booleans[3]){
            intent.putExtra(ExtraKeys.INIT_TAB_INDEX, 1);
        }else if(booleans[1]){
            intent.putExtra(ExtraKeys.INIT_TAB_INDEX, 1);
        }else if(booleans[3]){
            intent.putExtra(ExtraKeys.INIT_TAB_INDEX, 3);
        }
        new Notification.Builder(context,
                NotificationChannels.GroupChannelAdditionActivity.ID_GROUP_CHANNEL_ADDITION_ACTIVITY,
                NotificationChannels.GroupChannelAdditionActivity.NAME_GROUP_CHANNEL_ADDITION_ACTIVITY)
                .intent(intent)
                .importance(NotificationManager.IMPORTANCE_HIGH)
                .title("新的群频道")
                .text(text)
                .smallIcon(R.drawable.group_add_fill_24px)
                .build()
                .show();
    }
}
