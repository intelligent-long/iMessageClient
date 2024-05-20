package com.longx.intelligent.android.ichat2.net.stomp;

import android.content.Context;

import com.longx.intelligent.android.ichat2.activity.ChatActivity;
import com.longx.intelligent.android.ichat2.activity.MainActivity;
import com.longx.intelligent.android.ichat2.activity.helper.ActivityOperator;
import com.longx.intelligent.android.ichat2.activity.helper.HoldableActivity;
import com.longx.intelligent.android.ichat2.behavior.ContentUpdater;
import com.longx.intelligent.android.ichat2.data.ChatMessage;
import com.longx.intelligent.android.ichat2.notification.Notifications;
import com.longx.intelligent.android.ichat2.util.ErrorLogger;
import com.longx.intelligent.android.ichat2.yier.ChannelAdditionActivitiesUpdateYier;
import com.longx.intelligent.android.ichat2.yier.ChannelsUpdateYier;
import com.longx.intelligent.android.ichat2.yier.ChatMessageUpdateYier;
import com.longx.intelligent.android.ichat2.yier.GlobalYiersHolder;
import com.longx.intelligent.android.ichat2.yier.NewContentBadgeDisplayYier;
import com.longx.intelligent.android.ichat2.yier.OpenedChatsUpdateYier;
import com.longx.intelligent.android.ichat2.yier.ResultsYier;

import java.util.List;

/**
 * Created by LONG on 2024/4/1 at 5:03 AM.
 */
public class ServerMessageServiceStompActions {

    public static void updateCurrentUserInfo(Context context){
        ContentUpdater.updateCurrentUserInfo(context);
    }

    public static void updateChannelAdditionActivities(Context context){
        GlobalYiersHolder.getYiers(ChannelAdditionActivitiesUpdateYier.class).ifPresent(channelAdditionActivitiesUpdateYiers -> {
            channelAdditionActivitiesUpdateYiers.forEach(ChannelAdditionActivitiesUpdateYier::onChannelAdditionActivitiesUpdate);
        });
    }

    public static void updateChannelAdditionsNotViewCount(Context context) {
        ContentUpdater.updateChannelAdditionNotViewCount(context, results -> {
            Integer notViewedCount = (Integer) results[0];
            if(notViewedCount != null && notViewedCount != 0) {
                if (ActivityOperator.getActivityList().size() == 0) {
                    Notifications.notifyChannelAdditionActivity(context, notViewedCount);
                } else {
                    HoldableActivity topActivity = ActivityOperator.getActivityList().get(ActivityOperator.getActivityList().size() - 1);
                    if (!(topActivity instanceof MainActivity)) {
                        Notifications.notifyChannelAdditionActivity(context, notViewedCount);
                    }
                }
            }
            GlobalYiersHolder.getYiers(NewContentBadgeDisplayYier.class).ifPresent(newContentBadgeDisplayYiers -> {
                newContentBadgeDisplayYiers.forEach(newContentBadgeDisplayYier -> {
                    newContentBadgeDisplayYier.autoShowNewContentBadge(context, NewContentBadgeDisplayYier.ID.CHANNEL_ADDITION_ACTIVITIES);
                });
            });
        });
    }

    public static void updateChannels(Context context){
        ContentUpdater.updateChannels(context, results -> {
            GlobalYiersHolder.getYiers(OpenedChatsUpdateYier.class).ifPresent(openedChatUpdateYiers -> {
                openedChatUpdateYiers.forEach(OpenedChatsUpdateYier::onOpenedChatsUpdate);
            });
            GlobalYiersHolder.getYiers(NewContentBadgeDisplayYier.class).ifPresent(newContentBadgeDisplayYiers -> {
                newContentBadgeDisplayYiers.forEach(newContentBadgeDisplayYier -> {
                    newContentBadgeDisplayYier.autoShowNewContentBadge(context, NewContentBadgeDisplayYier.ID.MESSAGES);
                });
            });
            Notifications.notifyPendingNotifications(Notifications.NotificationId.CHAT_MESSAGE);
        });
    }

    public static void updateChatMessages(Context context){
        ContentUpdater.updateChatMessages(context, results -> {
            List<ChatMessage> chatMessages = (List<ChatMessage>) results[0];
            chatMessages.forEach(chatMessage -> {
                if (ActivityOperator.getActivityList().size() == 0) {
                    Notifications.notifyChatMessage(context, chatMessage);
                } else {
                    HoldableActivity topActivity = ActivityOperator.getActivityList().get(ActivityOperator.getActivityList().size() - 1);
                    if (!(topActivity instanceof MainActivity)) {
                        if (!(topActivity instanceof ChatActivity && ((ChatActivity) topActivity).getChannel().getIchatId().equals(chatMessage.getOther(context)))) {
                            Notifications.notifyChatMessage(context, chatMessage);
                        }
                    }
                }
            });
            GlobalYiersHolder.getYiers(OpenedChatsUpdateYier.class).ifPresent(openedChatUpdateYiers -> {
                openedChatUpdateYiers.forEach(OpenedChatsUpdateYier::onOpenedChatsUpdate);
            });
            GlobalYiersHolder.getYiers(NewContentBadgeDisplayYier.class).ifPresent(newContentBadgeDisplayYiers -> {
                newContentBadgeDisplayYiers.forEach(newContentBadgeDisplayYier -> {
                    newContentBadgeDisplayYier.autoShowNewContentBadge(context, NewContentBadgeDisplayYier.ID.MESSAGES);
                });
            });
            GlobalYiersHolder.getYiers(ChatMessageUpdateYier.class).ifPresent(chatMessageUpdateYiers -> {
                chatMessageUpdateYiers.forEach(chatMessageUpdateYier -> {
                    chatMessageUpdateYier.onNewChatMessage(chatMessages);
                });
            });
        });
    }

}
