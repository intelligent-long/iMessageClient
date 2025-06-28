package com.longx.intelligent.android.imessage.behaviorcomponents;

import android.content.Context;

import com.fasterxml.jackson.core.type.TypeReference;
import com.longx.intelligent.android.imessage.da.database.manager.ChannelDatabaseManager;
import com.longx.intelligent.android.imessage.da.database.manager.ChatMessageDatabaseManager;
import com.longx.intelligent.android.imessage.da.database.manager.GroupChannelDatabaseManager;
import com.longx.intelligent.android.imessage.da.database.manager.OpenedChatDatabaseManager;
import com.longx.intelligent.android.imessage.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.imessage.data.Broadcast;
import com.longx.intelligent.android.imessage.data.BroadcastMedia;
import com.longx.intelligent.android.imessage.data.ChannelAdditionNotViewedCount;
import com.longx.intelligent.android.imessage.data.ChannelAssociation;
import com.longx.intelligent.android.imessage.data.ChannelCollectionItem;
import com.longx.intelligent.android.imessage.data.ChannelTag;
import com.longx.intelligent.android.imessage.data.ChatMessage;
import com.longx.intelligent.android.imessage.data.GroupChannel;
import com.longx.intelligent.android.imessage.data.GroupChannelAdditionNotViewedCount;
import com.longx.intelligent.android.imessage.data.GroupChannelCollectionItem;
import com.longx.intelligent.android.imessage.data.GroupChannelNotification;
import com.longx.intelligent.android.imessage.data.GroupChannelTag;
import com.longx.intelligent.android.imessage.data.OpenedChat;
import com.longx.intelligent.android.imessage.data.RecentBroadcastMedia;
import com.longx.intelligent.android.imessage.data.Self;
import com.longx.intelligent.android.imessage.data.response.OperationData;
import com.longx.intelligent.android.imessage.data.response.OperationStatus;
import com.longx.intelligent.android.imessage.data.response.PaginatedOperationData;
import com.longx.intelligent.android.imessage.net.retrofit.caller.BroadcastApiCaller;
import com.longx.intelligent.android.imessage.net.retrofit.caller.ChannelApiCaller;
import com.longx.intelligent.android.imessage.net.retrofit.caller.ChatApiCaller;
import com.longx.intelligent.android.imessage.net.retrofit.caller.GroupChannelApiCaller;
import com.longx.intelligent.android.imessage.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.imessage.net.retrofit.caller.UserApiCaller;
import com.longx.intelligent.android.imessage.util.ErrorLogger;
import com.longx.intelligent.android.imessage.value.Constants;
import com.longx.intelligent.android.imessage.yier.GlobalYiersHolder;
import com.longx.intelligent.android.imessage.yier.ResultsYier;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by LONG on 2024/4/1 at 4:41 AM.
 */
//主要只操作数据获取和存储
public class ContentUpdater {
    private static final List<String> updatingIds = new ArrayList<>();

    public interface OnServerContentUpdateYier {
        String ID_CURRENT_USER_INFO = "current_user_info";
        String ID_CHANNEL_ADDITIONS_UNVIEWED_COUNT = "channel_additions_unviewed_count";
        String ID_CHANNELS = "channels";
        String ID_CHAT_MESSAGES = "chat_messages";
        String ID_CHANNEL_TAGS = "channel_tags";
        String ID_RECENT_BROADCAST_MEDIAS = "recent_broadcast_medias";
        String ID_BROADCAST_LIKE_NEWS_COUNT = "broadcast_like_news_count";
        String ID_BROADCAST_COMMENT_NEWS_COUNT = "broadcast_comment_news_count";
        String ID_BROADCAST_REPLY_NEWS_COUNT = "broadcast_reply_news_count";
        String ID_GROUP_CHANNELS = "group_channels";
        String ID_GROUP_CHANNEL = "group_channel";
        String ID_GROUP_CHANNEL_TAGS = "group_channel_tags";
        String ID_GROUP_CHANNEL_ADDITIONS_UNVIEWED_COUNT = "group_channel_additions_unviewed_count";
        String ID_GROUP_CHANNEL_NOTIFICATIONS = "group_channel_notifications";
        String ID_CHANNEL_COLLECTIONS = "channel_collections";
        String ID_GROUP_CHANNEL_COLLECTIONS = "group_channel_collections";

        void onStartUpdate(String id, List<String> updatingIds, Object... objects);

        void onUpdateComplete(String id, List<String> updatingIds, Object... objects);
    }

    private static class ContentUpdateApiYier<T> extends RetrofitApiCaller.BaseYier<T>{
        private final String updateId;
        private final Context context;
        private final Object[] objects;

        public ContentUpdateApiYier(String updateId, Context context) {
            this.updateId = updateId;
            this.context = context;
            objects = null;
        }

        public ContentUpdateApiYier(String updateId, Context context, Object... objects) {
            this.updateId = updateId;
            this.context = context;
            this.objects = objects;
        }

        @Override
        public void start(Call<T> call) {
            synchronized (ContentUpdater.class) {
                updatingIds.add(updateId);
                super.start(call);
                onStartUpdate(updateId, objects);
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
            ErrorLogger.log(t);
            MessageDisplayer.autoShow(context, "数据更新出错 (" + updateId + ")  >  " + t.getClass().getName(), MessageDisplayer.Duration.LONG);
        }

        @Override
        public void complete(Call<T> call) {
            synchronized (ContentUpdater.class) {
                updatingIds.remove(updateId);
                super.complete(call);
                onUpdateComplete(updateId, objects);
            }
        }
    };

    public static boolean isUpdating(){
        return updatingIds.size() != 0;
    }

    public static List<String> getUpdatingIds() {
        return updatingIds;
    }

    private static void onStartUpdate(String updateId, Object... objects) {
        GlobalYiersHolder.getYiers(OnServerContentUpdateYier.class)
                .ifPresent(onServerContentUpdateYiers -> onServerContentUpdateYiers.forEach(onServerContentUpdateYier -> {
                    onServerContentUpdateYier.onStartUpdate(updateId, new ArrayList<>(updatingIds), objects);
                }));
    }

    private static void onUpdateComplete(String updateId, Object... objects) {
        GlobalYiersHolder.getYiers(OnServerContentUpdateYier.class)
                .ifPresent(onServerContentUpdateYiers -> onServerContentUpdateYiers.forEach(onServerContentUpdateYier -> {
                    onServerContentUpdateYier.onUpdateComplete(updateId, new ArrayList<>(updatingIds), objects);
                }));
    }

    public static void updateCurrentUserProfile(Context context){
        updateCurrentUserProfile(context, null);
    }

    public static void updateCurrentUserProfile(Context context, Self self) {
        if(self != null){
            SharedPreferencesAccessor.UserProfilePref.saveCurrentUserProfile(context, self);
        }else {
            UserApiCaller.whoAmI(null, new ContentUpdateApiYier<OperationData>(OnServerContentUpdateYier.ID_CURRENT_USER_INFO, context) {
                @Override
                public void ok(OperationData data, Response<OperationData> raw, Call<OperationData> call) {
                    super.ok(data, raw, call);
                    data.commonHandleSuccessResult(() -> {
                        Self self = data.getData(Self.class);
                        SharedPreferencesAccessor.UserProfilePref.saveCurrentUserProfile(context, self);
                    });
                }
            });
        }
    }

    public static void updateChannelAdditionNotViewCount(Context context, ResultsYier resultsYier){
        ChannelApiCaller.fetchChannelAdditionUnviewedCount(null, new ContentUpdateApiYier<OperationData>(OnServerContentUpdateYier.ID_CHANNEL_ADDITIONS_UNVIEWED_COUNT, context){
            @Override
            public void ok(OperationData data, Response<OperationData> raw, Call<OperationData> call) {
                super.ok(data, raw, call);
                data.commonHandleSuccessResult(() -> {
                    ChannelAdditionNotViewedCount notViewedCount = data.getData(ChannelAdditionNotViewedCount.class);
                    SharedPreferencesAccessor.NewContentCount.saveChannelAdditionActivities(context, notViewedCount);
                    resultsYier.onResults(notViewedCount);
                });
            }
        });
    }

    public static void updateChannels(Context context, ResultsYier resultsYier){
        ChannelApiCaller.fetchAllAssociations(null, new ContentUpdateApiYier<OperationData>(OnServerContentUpdateYier.ID_CHANNELS, context){
            @Override
            public void ok(OperationData data, Response<OperationData> raw, Call<OperationData> call) {
                super.ok(data, raw, call);
                data.commonHandleSuccessResult(() -> {
                    ChannelDatabaseManager.getInstance().clearChannels();
                    List<ChannelAssociation> channelAssociations = data.getData(new TypeReference<List<ChannelAssociation>>() {
                    });
                    ChannelDatabaseManager.getInstance().insertAssociationsOrIgnore(channelAssociations);
                    resultsYier.onResults();
                });
            }
        });
    }

    public static void updateChatMessages(Context context, ResultsYier resultsYier){
        ChatApiCaller.fetchAllUnviewedMessages(null, new ContentUpdateApiYier<OperationData>(OnServerContentUpdateYier.ID_CHAT_MESSAGES, context){
            @Override
            public void ok(OperationData data, Response<OperationData> raw, Call<OperationData> call) {
                super.ok(data, raw, call);
                data.commonHandleSuccessResult(() -> {
                    List<ChatMessage> allUnviewedMessages = data.getData(new TypeReference<List<ChatMessage>>() {
                    });
                    allUnviewedMessages.sort(Comparator.comparing(ChatMessage::getTime));
                    Map<String, List<ChatMessage>> messageMap = new HashMap<>();
                    AtomicInteger doneCount = new AtomicInteger();
                    allUnviewedMessages.forEach(unviewedMessage -> {
                        doneCount.getAndIncrement();
                        String other = unviewedMessage.getOther(context);
                        ChatMessageDatabaseManager chatMessageDatabaseManager = ChatMessageDatabaseManager.getInstanceOrInitAndGet(context, other);
                        if (chatMessageDatabaseManager.existsByUuid(unviewedMessage.getUuid())) return;
                        ChatMessage.mainDoOnNewMessage(unviewedMessage, context, results -> {
                            List<ChatMessage> messageList;
                            if (messageMap.get(other) == null) {
                                messageList = new ArrayList<>();
                                messageMap.put(other, messageList);
                            } else {
                                messageList = messageMap.get(other);
                            }
                            messageList.add(unviewedMessage);
                            if (doneCount.get() == allUnviewedMessages.size()) {
                                messageMap.forEach((channelImessageId, list) -> {
                                    OpenedChatDatabaseManager.getInstance().insertOrUpdate(new OpenedChat(channelImessageId, list.size(), true));
                                });
                                resultsYier.onResults(allUnviewedMessages);
                            }
                        });
                    });
                });
            }
        });
    }

    public static void updateChannelTags(Context context, ResultsYier resultsYier){
        ChannelApiCaller.fetchAllTags(null, new ContentUpdateApiYier<OperationData>(OnServerContentUpdateYier.ID_CHANNEL_TAGS, context){
            @Override
            public void ok(OperationData data, Response<OperationData> raw, Call<OperationData> call) {
                super.ok(data, raw, call);
                data.commonHandleSuccessResult(() -> {
                    List<ChannelTag> channelTags = data.getData(new TypeReference<List<ChannelTag>>() {
                    });
                    ChannelDatabaseManager.getInstance().clearChannelTags();
                    ChannelDatabaseManager.getInstance().insertTagsOrIgnore(channelTags);
                    resultsYier.onResults();
                });
            }
        });
    }

    public static void updateRecentBroadcastMedias(Context context, String imessageId, ResultsYier resultsYier){
        BroadcastApiCaller.fetchChannelBroadcastsLimit(null, imessageId, null, 50, true,
                new ContentUpdateApiYier<PaginatedOperationData<Broadcast>>(OnServerContentUpdateYier.ID_RECENT_BROADCAST_MEDIAS, context){
                    @Override
                    public void ok(PaginatedOperationData<Broadcast> data, Response<PaginatedOperationData<Broadcast>> raw, Call<PaginatedOperationData<Broadcast>> call) {
                        super.ok(data, raw, call);
                        data.commonHandleResult(null, new int[]{}, () -> {
                            List<Broadcast> broadcastList = data.getData();
                            List<RecentBroadcastMedia> recentBroadcastMedias = new ArrayList<>();
                            int index = 0;
                            OUTER: for (Broadcast broadcast : broadcastList) {
                                List<BroadcastMedia> broadcastMedias = broadcast.getBroadcastMedias();
                                if(broadcastMedias != null){
                                    broadcastMedias.sort(Comparator.comparingInt(BroadcastMedia::getIndex));
                                    for (BroadcastMedia broadcastMedia : broadcastMedias) {
                                        recentBroadcastMedias.add(new RecentBroadcastMedia(
                                                broadcast.getImessageId(),
                                                broadcastMedia.getBroadcastId(),
                                                broadcastMedia.getMediaId(),
                                                broadcastMedia.getType(),
                                                broadcastMedia.getExtension(),
                                                broadcastMedia.getType() == BroadcastMedia.TYPE_VIDEO ? broadcastMedia.getVideoDuration() : -2,
                                                index ++));
                                        if(index >= Constants.RECENT_BROADCAST_MEDIAS_SHOW_ITEM_SIZE) break OUTER;
                                    }
                                }
                            }
                            ChannelDatabaseManager.getInstance().updateRecentBroadcastMedias(recentBroadcastMedias, imessageId);
                            resultsYier.onResults();
                        }, new OperationStatus.HandleResult(-102, () -> {
                            ChannelDatabaseManager.getInstance().updateRecentBroadcastMedias(new ArrayList<>(), imessageId);
                            resultsYier.onResults();
                        }));
                    }
                });
    }

    public static void updateNewBroadcastLikesCount(Context context, ResultsYier resultsYier){
        BroadcastApiCaller.fetchBroadcastLikeNewsCount(null, new ContentUpdateApiYier<OperationData>(OnServerContentUpdateYier.ID_BROADCAST_LIKE_NEWS_COUNT, context){
            @Override
            public void ok(OperationData data, Response<OperationData> raw, Call<OperationData> call) {
                super.ok(data, raw, call);
                data.commonHandleSuccessResult(() -> {
                    Integer newsCount = data.getData(Integer.class);
                    SharedPreferencesAccessor.NewContentCount.saveBroadcastLikeNewsCount(context, newsCount);
                    resultsYier.onResults(newsCount);
                });
            }
        });
    }

    public static void updateNewBroadcastCommentsCount(Context context, ResultsYier resultsYier){
        BroadcastApiCaller.fetchBroadcastCommentNewsCount(null, new ContentUpdateApiYier<OperationData>(OnServerContentUpdateYier.ID_BROADCAST_COMMENT_NEWS_COUNT, context){
            @Override
            public void ok(OperationData data, Response<OperationData> raw, Call<OperationData> call) {
                super.ok(data, raw, call);
                data.commonHandleSuccessResult(() -> {
                    Integer newsCount = data.getData(Integer.class);
                    SharedPreferencesAccessor.NewContentCount.saveBroadcastCommentNewsCount(context, newsCount);
                    resultsYier.onResults(newsCount);
                });
            }
        });
    }

    public static void updateNewBroadcastRepliesCount(Context context, ResultsYier resultsYier){
        BroadcastApiCaller.fetchBroadcastReplyCommentNewsCount(null, new ContentUpdateApiYier<OperationData>(OnServerContentUpdateYier.ID_BROADCAST_REPLY_NEWS_COUNT, context){
            @Override
            public void ok(OperationData data, Response<OperationData> raw, Call<OperationData> call) {
                super.ok(data, raw, call);
                data.commonHandleSuccessResult(() -> {
                    Integer newsCount = data.getData(Integer.class);
                    SharedPreferencesAccessor.NewContentCount.saveBroadcastReplyCommentNewsCount(context, newsCount);
                    resultsYier.onResults(newsCount);
                });
            }
        });
    }

    public static void updateAllGroupChannels(Context context, ResultsYier resultsYier){
        GroupChannelApiCaller.fetchAllGroupAssociations(null, new ContentUpdateApiYier<OperationData>(OnServerContentUpdateYier.ID_GROUP_CHANNELS, context){
            @Override
            public void ok(OperationData data, Response<OperationData> raw, Call<OperationData> call) {
                super.ok(data, raw, call);
                data.commonHandleSuccessResult(() -> {
                    List<GroupChannel> groupChannels = data.getData(new TypeReference<List<GroupChannel>>() {
                    });
                    GroupChannelDatabaseManager.getInstance().updateAssociationsOrIgnore(groupChannels);
                    resultsYier.onResults();
                });
            }
        });
    }

    public static void updateOneGroupChannel(Context context, String groupChannelId, ResultsYier resultsYier){
        GroupChannelApiCaller.findGroupChannelByGroupChannelId(null, groupChannelId, "id", false, new ContentUpdateApiYier<OperationData>(OnServerContentUpdateYier.ID_GROUP_CHANNEL, context, groupChannelId){
            @Override
            public void ok(OperationData data, Response<OperationData> raw, Call<OperationData> call) {
                super.ok(data, raw, call);
                data.commonHandleSuccessResult(() -> {
                    GroupChannel groupChannel = data.getData(GroupChannel.class);
                    GroupChannelDatabaseManager.getInstance().insertOrUpdate(groupChannel);
                    resultsYier.onResults();
                });
            }
        });
    }

    public static void updateGroupChannelTags(Context context, ResultsYier resultsYier){
        GroupChannelApiCaller.fetchAllTags(null, new ContentUpdateApiYier<OperationData>(OnServerContentUpdateYier.ID_GROUP_CHANNEL_TAGS, context){
            @Override
            public void ok(OperationData data, Response<OperationData> raw, Call<OperationData> call) {
                super.ok(data, raw, call);
                data.commonHandleSuccessResult(() -> {
                    List<GroupChannelTag> channelTags = data.getData(new TypeReference<List<GroupChannelTag>>() {
                    });
                    GroupChannelDatabaseManager.getInstance().updateTagsOrIgnore(channelTags);
                    resultsYier.onResults();
                });
            }
        });
    }

    public static void updateGroupChannelAdditionNotViewCount(Context context, ResultsYier resultsYier){
        GroupChannelApiCaller.fetchGroupChannelAdditionUnviewedCount(null, new ContentUpdateApiYier<OperationData>(OnServerContentUpdateYier.ID_GROUP_CHANNEL_ADDITIONS_UNVIEWED_COUNT, context){
            @Override
            public void ok(OperationData data, Response<OperationData> raw, Call<OperationData> call) {
                super.ok(data, raw, call);
                data.commonHandleSuccessResult(() -> {
                    GroupChannelAdditionNotViewedCount notViewedCount = data.getData(GroupChannelAdditionNotViewedCount.class);
                    SharedPreferencesAccessor.NewContentCount.saveGroupChannelAdditionActivities(context, notViewedCount);
                    resultsYier.onResults(notViewedCount);
                });
            }
        });
    }

    public static void updateGroupChannelNotifications(Context context, ResultsYier resultsYier){
        GroupChannelApiCaller.fetchGroupChannelNotifications(null, new ContentUpdateApiYier<>(OnServerContentUpdateYier.ID_GROUP_CHANNEL_NOTIFICATIONS, context) {
            @Override
            public void ok(OperationData data, Response<OperationData> raw, Call<OperationData> call) {
                super.ok(data, raw, call);
                data.commonHandleSuccessResult(() -> {
                    List<GroupChannelNotification> groupChannelNotifications = data.getData(new TypeReference<>() {
                    });
                    int newsCount = 0;
                    for (GroupChannelNotification groupChannelNotification : groupChannelNotifications) {
                        if (!groupChannelNotification.isViewed()) newsCount++;
                    }
                    SharedPreferencesAccessor.NewContentCount.saveGroupChannelNotifications(context, newsCount);
                    GroupChannelDatabaseManager.getInstance().insertGroupChannelNotificationsOrUpdate(groupChannelNotifications);
                    resultsYier.onResults(newsCount);
                });
            }
        });
    }
    
    public static void updateChannelCollections(Context context, ResultsYier resultsYier){
        ChannelApiCaller.fetchAllCollections(null, new ContentUpdateApiYier<>(OnServerContentUpdateYier.ID_CHANNEL_COLLECTIONS, context){
            @Override
            public void ok(OperationData data, Response<OperationData> raw, Call<OperationData> call) {
                super.ok(data, raw, call);
                data.commonHandleSuccessResult(() -> {
                    List<ChannelCollectionItem> channelCollectionItems = data.getData(new TypeReference<>() {
                    });
                    ChannelDatabaseManager.getInstance().deleteAllChannelCollections();
                    ChannelDatabaseManager.getInstance().updateChannelCollections(channelCollectionItems);
                    resultsYier.onResults();
                });
            }
        });
    }

    public static void updateGroupChannelCollections(Context context, ResultsYier resultsYier){
        GroupChannelApiCaller.fetchAllGroupCollections(null, new ContentUpdateApiYier<>(OnServerContentUpdateYier.ID_GROUP_CHANNEL_COLLECTIONS, context){
            @Override
            public void ok(OperationData data, Response<OperationData> raw, Call<OperationData> call) {
                super.ok(data, raw, call);
                data.commonHandleSuccessResult(() -> {
                    List<GroupChannelCollectionItem> groupChannelCollectionItems = data.getData(new TypeReference<>() {
                    });
                    GroupChannelDatabaseManager.getInstance().deleteAllGroupChannelCollections();
                    GroupChannelDatabaseManager.getInstance().updateGroupChannelCollections(groupChannelCollectionItems);
                    resultsYier.onResults();
                });
            }
        });
    }
}
