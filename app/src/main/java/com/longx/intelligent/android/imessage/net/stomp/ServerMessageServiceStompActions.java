package com.longx.intelligent.android.imessage.net.stomp;

import android.content.Context;

import com.longx.intelligent.android.imessage.Application;
import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.imessage.activity.BroadcastInteractionsActivity;
import com.longx.intelligent.android.imessage.activity.ChatActivity;
import com.longx.intelligent.android.imessage.activity.MainActivity;
import com.longx.intelligent.android.imessage.activity.helper.ActivityOperator;
import com.longx.intelligent.android.imessage.activity.helper.HoldableActivity;
import com.longx.intelligent.android.imessage.behaviorcomponents.ContentUpdater;
import com.longx.intelligent.android.imessage.da.database.manager.ChannelDatabaseManager;
import com.longx.intelligent.android.imessage.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.imessage.data.ChannelAdditionNotViewedCount;
import com.longx.intelligent.android.imessage.data.ChatMessage;
import com.longx.intelligent.android.imessage.data.GroupChannelAdditionNotViewedCount;
import com.longx.intelligent.android.imessage.notification.Notifications;
import com.longx.intelligent.android.imessage.yier.BroadcastFetchNewsYier;
import com.longx.intelligent.android.imessage.yier.ChannelAdditionActivitiesUpdateYier;
import com.longx.intelligent.android.imessage.yier.GlobalYiersHolder;
import com.longx.intelligent.android.imessage.yier.GroupChannelAdditionActivitiesUpdateYier;
import com.longx.intelligent.android.imessage.yier.NewContentBadgeDisplayYier;
import com.longx.intelligent.android.imessage.yier.OpenedChatsUpdateYier;
import com.longx.intelligent.android.imessage.yier.RecentBroadcastMediasUpdateYier;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LONG on 2024/4/1 at 5:03 AM.
 */
//处理数据更新，以及附带其他操作
public class ServerMessageServiceStompActions {

    public static void updateCurrentUserProfile(Context context){
        ContentUpdater.updateCurrentUserProfile(context);
    }

    public static void updateChannelAdditionActivities(Context context){
        GlobalYiersHolder.getYiers(ChannelAdditionActivitiesUpdateYier.class).ifPresent(channelAdditionActivitiesUpdateYiers -> {
            channelAdditionActivitiesUpdateYiers.forEach(ChannelAdditionActivitiesUpdateYier::onChannelAdditionActivitiesUpdate);
        });
    }

    public static void updateChannelAdditionsNotViewCount(Context context) {
        ContentUpdater.updateChannelAdditionNotViewCount(context, results -> {
            ChannelAdditionNotViewedCount notViewedCount = (ChannelAdditionNotViewedCount) results[0];
            if(notViewedCount != null) {
                if (!Application.foreground) {
                    Notifications.notifyChannelAdditionActivity(context, notViewedCount.getNotificationRequest(), notViewedCount.getNotificationRespond());
                } else {
                    if (!(ActivityOperator.getTopActivity() instanceof MainActivity)) {
                        Notifications.notifyChannelAdditionActivity(context, notViewedCount.getNotificationRequest(), notViewedCount.getNotificationRespond());
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
            Notifications.notifyPendingNotifications(Notifications.PendingNotificationId.CHAT_MESSAGE);
        });
    }

    public static void updateChatMessages(Context context){
        ContentUpdater.updateChatMessages(context, results -> {
            List<ChatMessage> messages = (List<ChatMessage>) results[0];
            messages.forEach(message -> {
                if (!Application.foreground) {
                    Notifications.notifyChatMessage(context, message);
                } else {
                    HoldableActivity topActivity = ActivityOperator.getTopActivity();
                    if (!(topActivity instanceof MainActivity)) {
                        if (!(topActivity instanceof ChatActivity && ((ChatActivity) topActivity).getChannel().getImessageId().equals(message.getOther(context)))) {
                            Notifications.notifyChatMessage(context, message);
                        }
                    }
                }
            });
            GlobalYiersHolder.getYiers(OpenedChatsUpdateYier.class).ifPresent(openedChatsUpdateYiers -> {
                openedChatsUpdateYiers.forEach(OpenedChatsUpdateYier::onOpenedChatsUpdate);
            });
            GlobalYiersHolder.getYiers(NewContentBadgeDisplayYier.class).ifPresent(newContentBadgeDisplayYiers -> {
                newContentBadgeDisplayYiers.forEach(newContentBadgeDisplayYier -> {
                    newContentBadgeDisplayYier.autoShowNewContentBadge(context, NewContentBadgeDisplayYier.ID.MESSAGES);
                });
            });
        });
    }

    public static void updateChannelTags(Context context){
        ContentUpdater.updateChannelTags(context, results -> {
        });
    }

    public static void updateBroadcastsNews(Context context, String imessageId){
        GlobalYiersHolder.getYiers(BroadcastFetchNewsYier.class).ifPresent(broadcastFetchNewsYiers -> {
            broadcastFetchNewsYiers.forEach(broadcastFetchNewsYier -> broadcastFetchNewsYier.fetchNews(imessageId));
        });
    }

    public static void updateRecentBroadcastMedias(Context context, String imessageId){
        if(imessageId == null || imessageId.isEmpty()){
            List<String> channelIds = new ArrayList<>();
            channelIds.add(SharedPreferencesAccessor.UserProfilePref.getCurrentUserProfile(context).getImessageId());
            ChannelDatabaseManager.getInstance().findAllAssociations().forEach(channelAssociation -> {
                channelIds.add(channelAssociation.getChannelImessageId());
            });
            channelIds.forEach(channelId -> {
                ContentUpdater.updateRecentBroadcastMedias(context, channelId, results -> {
                    GlobalYiersHolder.getYiers(RecentBroadcastMediasUpdateYier.class).ifPresent(recentBroadcastMediasUpdateYiers -> {
                        recentBroadcastMediasUpdateYiers.forEach(recentBroadcastMediasUpdateYier -> {
                            recentBroadcastMediasUpdateYier.onRecentBroadcastMediasUpdate(channelId);
                        });
                    });
                });
            });
        }else {
            ContentUpdater.updateRecentBroadcastMedias(context, imessageId, results -> {
                GlobalYiersHolder.getYiers(RecentBroadcastMediasUpdateYier.class).ifPresent(recentBroadcastMediasUpdateYiers -> {
                    recentBroadcastMediasUpdateYiers.forEach(recentBroadcastMediasUpdateYier -> {
                        recentBroadcastMediasUpdateYier.onRecentBroadcastMediasUpdate(imessageId);
                    });
                });
            });
        }
    }

    public static void updateNewBroadcastLikesCount(Context context){
        ContentUpdater.updateNewBroadcastLikesCount(context, results -> {
            int newsCount = (int) results[0];
            if(newsCount > 0 && ! (ActivityOperator.getTopActivity() instanceof BroadcastInteractionsActivity)){
                Notifications.notifyBroadcastInteractionNewsContent(context, "广播互动", newsCount + " 个新的广播喜欢", R.drawable.favorite_fill_24px);
            }
            GlobalYiersHolder.getYiers(NewContentBadgeDisplayYier.class).ifPresent(newContentBadgeDisplayYiers -> {
                newContentBadgeDisplayYiers.forEach(newContentBadgeDisplayYier -> {
                    newContentBadgeDisplayYier.autoShowNewContentBadge(context, NewContentBadgeDisplayYier.ID.BROADCAST_LIKES);
                });
            });
        });
    }

    public static void updateNewBroadcastCommentsCount(Context context){
        ContentUpdater.updateNewBroadcastCommentsCount(context, results -> {
            int newsCount = (int) results[0];
            if(newsCount > 0 && ! (ActivityOperator.getTopActivity() instanceof BroadcastInteractionsActivity)){
                Notifications.notifyBroadcastInteractionNewsContent(context, "广播互动", newsCount + " 个新的广播评论", R.drawable.mode_comment_fill_24px);
            }
            GlobalYiersHolder.getYiers(NewContentBadgeDisplayYier.class).ifPresent(newContentBadgeDisplayYiers -> {
                newContentBadgeDisplayYiers.forEach(newContentBadgeDisplayYier -> {
                    newContentBadgeDisplayYier.autoShowNewContentBadge(context, NewContentBadgeDisplayYier.ID.BROADCAST_COMMENTS);
                });
            });
        });
    }

    public static void updateNewBroadcastRepliesCount(Context context){
        ContentUpdater.updateNewBroadcastRepliesCount(context, results -> {
            int newsCount = (int) results[0];
            if(newsCount > 0 && ! (ActivityOperator.getTopActivity() instanceof BroadcastInteractionsActivity)){
                Notifications.notifyBroadcastInteractionNewsContent(context, "广播互动", newsCount + " 个新的广播回复", R.drawable.mode_comment_fill_24px);
            }
            GlobalYiersHolder.getYiers(NewContentBadgeDisplayYier.class).ifPresent(newContentBadgeDisplayYiers -> {
                newContentBadgeDisplayYiers.forEach(newContentBadgeDisplayYier -> {
                    newContentBadgeDisplayYier.autoShowNewContentBadge(context, NewContentBadgeDisplayYier.ID.BROADCAST_REPLIES);
                });
            });
        });
    }

    public static void updateAllGroupChannels(Context context){
        ContentUpdater.updateAllGroupChannels(context, results -> {
            GlobalYiersHolder.getYiers(OpenedChatsUpdateYier.class).ifPresent(openedChatUpdateYiers -> {
                openedChatUpdateYiers.forEach(OpenedChatsUpdateYier::onOpenedChatsUpdate);
            });
        });
    }

    public static void updateOneGroupChannel(Context context, String groupChannelId){
        ContentUpdater.updateOneGroupChannel(context, groupChannelId, results -> {
            GlobalYiersHolder.getYiers(OpenedChatsUpdateYier.class).ifPresent(openedChatUpdateYiers -> {
                openedChatUpdateYiers.forEach(OpenedChatsUpdateYier::onOpenedChatsUpdate);
            });
        });
    }

    public static void updateGroupChannelTags(Context context){
        ContentUpdater.updateGroupChannelTags(context, results -> {
        });
    }

    public static void updateGroupChannelAdditionActivities(Context context){
        GlobalYiersHolder.getYiers(GroupChannelAdditionActivitiesUpdateYier.class).ifPresent(groupChannelAdditionActivitiesUpdateYiers -> {
            groupChannelAdditionActivitiesUpdateYiers.forEach(GroupChannelAdditionActivitiesUpdateYier::onGroupChannelAdditionActivitiesUpdate);
        });
    }

    public static void updateGroupChannelAdditionsNotViewCount(Context context) {
        ContentUpdater.updateGroupChannelAdditionNotViewCount(context, results -> {
            GroupChannelAdditionNotViewedCount notViewedCount = (GroupChannelAdditionNotViewedCount) results[0];
            if(notViewedCount != null) {
                if (!Application.foreground || !(ActivityOperator.getTopActivity() instanceof MainActivity)) {
                    Notifications.notifyGroupChannelAdditionActivity(context, notViewedCount.getSelfNotificationRequest(),
                            notViewedCount.getSelfNotificationRespond(), notViewedCount.getOtherNotificationRequest(),
                            notViewedCount.getOtherNotificationRespond(), notViewedCount.getNotificationInviter(), notViewedCount.getNotificationInvitee());
                }
            }
            GlobalYiersHolder.getYiers(NewContentBadgeDisplayYier.class).ifPresent(newContentBadgeDisplayYiers -> {
                newContentBadgeDisplayYiers.forEach(newContentBadgeDisplayYier -> {
                    newContentBadgeDisplayYier.autoShowNewContentBadge(context, NewContentBadgeDisplayYier.ID.GROUP_CHANNEL_ADDITION_ACTIVITIES);
                });
            });
        });
    }

    public static void updateGroupChannelDisconnections(Context context){
        ContentUpdater.updateGroupChannelDisconnections(context, results -> {
            GlobalYiersHolder.getYiers(NewContentBadgeDisplayYier.class).ifPresent(newContentBadgeDisplayYiers -> {
                newContentBadgeDisplayYiers.forEach(newContentBadgeDisplayYier -> {
                    newContentBadgeDisplayYier.autoShowNewContentBadge(context, NewContentBadgeDisplayYier.ID.GROUP_CHANNEL_NOTIFICATIONS);
                });
            });
        });
    }

}
