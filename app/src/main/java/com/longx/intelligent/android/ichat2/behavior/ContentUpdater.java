package com.longx.intelligent.android.ichat2.behavior;

import android.app.Activity;
import android.content.Context;

import com.fasterxml.jackson.core.type.TypeReference;
import com.longx.intelligent.android.ichat2.activity.ChatActivity;
import com.longx.intelligent.android.ichat2.activity.MainActivity;
import com.longx.intelligent.android.ichat2.activity.helper.ActivityHolder;
import com.longx.intelligent.android.ichat2.activity.helper.ActivityOperator;
import com.longx.intelligent.android.ichat2.activity.helper.HoldableActivity;
import com.longx.intelligent.android.ichat2.da.database.manager.ChannelDatabaseManager;
import com.longx.intelligent.android.ichat2.da.database.manager.OpenedChatDatabaseManager;
import com.longx.intelligent.android.ichat2.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.ichat2.data.Channel;
import com.longx.intelligent.android.ichat2.data.ChannelAssociation;
import com.longx.intelligent.android.ichat2.data.ChatMessage;
import com.longx.intelligent.android.ichat2.data.OpenedChat;
import com.longx.intelligent.android.ichat2.data.Self;
import com.longx.intelligent.android.ichat2.data.response.OperationData;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.ChannelApiCaller;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.ChatApiCaller;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.UserApiCaller;
import com.longx.intelligent.android.ichat2.notification.Notifications;
import com.longx.intelligent.android.ichat2.util.ErrorLogger;
import com.longx.intelligent.android.ichat2.yier.ChatMessageUpdateYier;
import com.longx.intelligent.android.ichat2.yier.GlobalYiersHolder;
import com.longx.intelligent.android.ichat2.yier.NewContentBadgeDisplayYier;
import com.longx.intelligent.android.ichat2.yier.OpenedChatsUpdateYier;
import com.longx.intelligent.android.ichat2.yier.ResultsYier;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by LONG on 2024/4/1 at 4:41 AM.
 */
public class ContentUpdater {
    private static final List<String> updatingIds = new ArrayList<>();

    public interface OnServerContentUpdateYier {
        String ID_CURRENT_USER_INFO = "current_user_info";
        String ID_CHANNEL_ADDITIONS_UNVIEWED_COUNT = "channel_additions_unviewed_count";
        String ID_CHANNELS = "channels";
        String ID_CHAT_MESSAGES = "chat_messages";

        void onStartUpdate(String id);

        void onUpdateComplete(String id);
    }

    private static class ContentUpdateApiYier<T> extends RetrofitApiCaller.BaseYier<T>{
        private final String updateId;
        private final Context context;

        public ContentUpdateApiYier(String updateId, Context context) {
            this.updateId = updateId;
            this.context = context;
        }

        @Override
        public void start(Call<T> call) {
            synchronized (ContentUpdater.class) {
                updatingIds.add(updateId);
                super.start(call);
                onStartUpdate(updateId);
            }
        }

        @Override
        public void notOk(int code, String message, Response<T> row, Call<T> call) {
            super.notOk(code, message, row, call);
            MessageDisplayer.autoShow(context, "数据更新 HTTP 状态码异常 (" + updateId + ")  >  " + code, MessageDisplayer.Duration.LONG);
        }

        @Override
        public void failure(Throwable t, Call<T> call) {
            super.failure(t, call);
            MessageDisplayer.autoShow(context, "数据更新出错 (" + updateId + ")  >  " + t.getClass().getName(), MessageDisplayer.Duration.LONG);
        }

        @Override
        public void complete(Call<T> call) {
            synchronized (ContentUpdater.class) {
                updatingIds.remove(updateId);
                super.complete(call);
                onUpdateComplete(updateId);
            }
        }
    };

    public static boolean isUpdating(){
        return updatingIds.size() != 0;
    }

    public static List<String> getUpdatingIds() {
        return updatingIds;
    }

    private static void onStartUpdate(String updateId) {
        GlobalYiersHolder.getYiers(OnServerContentUpdateYier.class)
                .ifPresent(onServerContentUpdateYiers -> onServerContentUpdateYiers.forEach(onServerContentUpdateYier -> {
                    onServerContentUpdateYier.onStartUpdate(updateId);
                }));
    }

    private static void onUpdateComplete(String updateId) {
        GlobalYiersHolder.getYiers(OnServerContentUpdateYier.class)
                .ifPresent(onServerContentUpdateYiers -> onServerContentUpdateYiers.forEach(onServerContentUpdateYier -> {
                    onServerContentUpdateYier.onUpdateComplete(updateId);
                }));
    }

    public static void updateCurrentUserInfo(Context context){
        updateCurrentUserInfo(context, null);
    }

    public static void updateCurrentUserInfo(Context context, Self self) {
        if(self != null){
            SharedPreferencesAccessor.UserInfoPref.saveCurrentUserInfo(context, self);
        }else {
            UserApiCaller.whoAmI(null, new ContentUpdateApiYier<OperationData>(OnServerContentUpdateYier.ID_CURRENT_USER_INFO, context) {
                @Override
                public void ok(OperationData data, Response<OperationData> row, Call<OperationData> call) {
                    super.ok(data, row, call);
                    data.commonHandleSuccessResult(() -> {
                        Self self = data.getData(Self.class);
                        SharedPreferencesAccessor.UserInfoPref.saveCurrentUserInfo(context, self);
                    });
                }
            });
        }
    }

    public static void updateChannelAdditionNotViewCount(Context context, ResultsYier resultsYier){
        ChannelApiCaller.fetchChannelAdditionUnviewedCount(null, new ContentUpdateApiYier<OperationData>(OnServerContentUpdateYier.ID_CHANNEL_ADDITIONS_UNVIEWED_COUNT, context){
            @Override
            public void ok(OperationData data, Response<OperationData> row, Call<OperationData> call) {
                super.ok(data, row, call);
                Integer notViewCount = data.getData(Integer.class);
                SharedPreferencesAccessor.NewContentCount.saveChannelAdditionActivities(context, notViewCount);
                resultsYier.onResults();
            }
        });
    }

    public static void updateChannels(Context context){
        ChannelApiCaller.fetchAllAssociations(null, new ContentUpdateApiYier<OperationData>(OnServerContentUpdateYier.ID_CHANNELS, context){
            @Override
            public void ok(OperationData data, Response<OperationData> row, Call<OperationData> call) {
                super.ok(data, row, call);
                ChannelDatabaseManager.getInstance().clear();
                List<ChannelAssociation> channelAssociations = data.getData(new TypeReference<List<ChannelAssociation>>() {
                });
                ChannelDatabaseManager.getInstance().insertOrIgnore(channelAssociations);
                afterChannelFetched(context);
            }
        });
    }

    private static void afterChannelFetched(Context context){
        GlobalYiersHolder.getYiers(OpenedChatsUpdateYier.class).ifPresent(openedChatUpdateYiers -> {
            openedChatUpdateYiers.forEach(OpenedChatsUpdateYier::onOpenedChatsUpdate);
        });
        GlobalYiersHolder.getYiers(NewContentBadgeDisplayYier.class).ifPresent(newContentBadgeDisplayYiers -> {
            newContentBadgeDisplayYiers.forEach(newContentBadgeDisplayYier -> {
                newContentBadgeDisplayYier.autoShowNewContentBadge(context, NewContentBadgeDisplayYier.ID.MESSAGES);
            });
        });
        Notifications.notifyPendingNotifications(Notifications.NotificationId.CHAT_MESSAGE);
    }

    public static void updateChatMessages(Context context){
        ChatApiCaller.fetchAllNewChatMessages(null, new ContentUpdateApiYier<OperationData>(OnServerContentUpdateYier.ID_CHAT_MESSAGES, context){
            @Override
            public void ok(OperationData data, Response<OperationData> row, Call<OperationData> call) {
                super.ok(data, row, call);
                List<ChatMessage> chatMessages = data.getData(new TypeReference<List<ChatMessage>>() {
                });
                chatMessages.sort(Comparator.comparing(ChatMessage::getTime));
                Map<String, List<ChatMessage>> chatMessageMap = new HashMap<>();
                chatMessages.forEach(chatMessage -> {
                    chatMessage.setViewed(false);
                    ChatMessage.insertToDatabaseAndDetermineShowTime(chatMessage, context);
                    String key = chatMessage.getOther(context);
                    List<ChatMessage> chatMessageList;
                    if(chatMessageMap.get(key) == null){
                        chatMessageList = new ArrayList<>();
                        chatMessageMap.put(key, chatMessageList);
                    }else {
                        chatMessageList = chatMessageMap.get(key);
                    }
                    chatMessageList.add(chatMessage);
                });
                chatMessageMap.forEach((s, chatMessageList) -> {
                    OpenedChatDatabaseManager.getInstance().insertOrUpdate(new OpenedChat(s, chatMessageList.size(), true));
                });
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
            }
        });
    }

}
