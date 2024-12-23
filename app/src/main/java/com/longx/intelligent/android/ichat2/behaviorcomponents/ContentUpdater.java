package com.longx.intelligent.android.ichat2.behaviorcomponents;

import android.content.Context;

import com.fasterxml.jackson.core.type.TypeReference;
import com.longx.intelligent.android.ichat2.da.database.manager.ChannelDatabaseManager;
import com.longx.intelligent.android.ichat2.da.database.manager.ChatMessageDatabaseManager;
import com.longx.intelligent.android.ichat2.da.database.manager.OpenedChatDatabaseManager;
import com.longx.intelligent.android.ichat2.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.ichat2.data.Broadcast;
import com.longx.intelligent.android.ichat2.data.BroadcastMedia;
import com.longx.intelligent.android.ichat2.data.ChannelAdditionNotViewedCount;
import com.longx.intelligent.android.ichat2.data.ChannelAssociation;
import com.longx.intelligent.android.ichat2.data.ChannelTag;
import com.longx.intelligent.android.ichat2.data.ChatMessage;
import com.longx.intelligent.android.ichat2.data.OpenedChat;
import com.longx.intelligent.android.ichat2.data.RecentBroadcastMedia;
import com.longx.intelligent.android.ichat2.data.Self;
import com.longx.intelligent.android.ichat2.data.response.OperationData;
import com.longx.intelligent.android.ichat2.data.response.OperationStatus;
import com.longx.intelligent.android.ichat2.data.response.PaginatedOperationData;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.BroadcastApiCaller;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.ChannelApiCaller;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.ChatApiCaller;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.UserApiCaller;
import com.longx.intelligent.android.ichat2.util.ErrorLogger;
import com.longx.intelligent.android.ichat2.value.Constants;
import com.longx.intelligent.android.ichat2.yier.GlobalYiersHolder;
import com.longx.intelligent.android.ichat2.yier.ResultsYier;

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

        void onStartUpdate(String id, List<String> updatingIds);

        void onUpdateComplete(String id, List<String> updatingIds);
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
            ErrorLogger.log(t);
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
                    onServerContentUpdateYier.onStartUpdate(updateId, new ArrayList<>(updatingIds));
                }));
    }

    private static void onUpdateComplete(String updateId) {
        GlobalYiersHolder.getYiers(OnServerContentUpdateYier.class)
                .ifPresent(onServerContentUpdateYiers -> onServerContentUpdateYiers.forEach(onServerContentUpdateYier -> {
                    onServerContentUpdateYier.onUpdateComplete(updateId, new ArrayList<>(updatingIds));
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
        ChatApiCaller.fetchAllNewChatMessages(null, new ContentUpdateApiYier<OperationData>(OnServerContentUpdateYier.ID_CHAT_MESSAGES, context){
            @Override
            public void ok(OperationData data, Response<OperationData> raw, Call<OperationData> call) {
                super.ok(data, raw, call);
                data.commonHandleSuccessResult(() -> {
                    List<ChatMessage> chatMessages = data.getData(new TypeReference<List<ChatMessage>>() {
                    });
                    chatMessages.sort(Comparator.comparing(ChatMessage::getTime));
                    Map<String, List<ChatMessage>> chatMessageMap = new HashMap<>();
                    AtomicInteger doneCount = new AtomicInteger();
                    List<ChatMessage> toUnsendChatMessages = new ArrayList<>();
                    chatMessages.forEach(chatMessage -> {
                        doneCount.getAndIncrement();
                        String other = chatMessage.getOther(context);
                        ChatMessageDatabaseManager chatMessageDatabaseManager = ChatMessageDatabaseManager.getInstanceOrInitAndGet(context, other);
                        if (chatMessageDatabaseManager.existsByUuid(chatMessage.getUuid())) return;
                        chatMessage.setViewed(false);
                        if(chatMessage.getType() == ChatMessage.TYPE_UNSEND){
                            ChatMessage toUnsendMessage = ChatMessageDatabaseManager.getInstanceOrInitAndGet(context, chatMessage.getFrom()).findOne(chatMessage.getUnsendMessageUuid());
                            toUnsendChatMessages.add(toUnsendMessage);
                        }
                        ChatMessage.mainDoOnNewChatMessage(chatMessage, context, results -> {
                            String key = chatMessage.getOther(context);
                            List<ChatMessage> chatMessageList;
                            if (chatMessageMap.get(key) == null) {
                                chatMessageList = new ArrayList<>();
                                chatMessageMap.put(key, chatMessageList);
                            } else {
                                chatMessageList = chatMessageMap.get(key);
                            }
                            chatMessageList.add(chatMessage);
                            if (doneCount.get() == chatMessages.size()) {
                                chatMessageMap.forEach((s, list) -> {
                                    OpenedChatDatabaseManager.getInstance().insertOrUpdate(new OpenedChat(s, list.size(), true));
                                });
                                resultsYier.onResults(chatMessages, toUnsendChatMessages);
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

    public static void updateRecentBroadcastMedias(Context context, String ichatId, ResultsYier resultsYier){
        BroadcastApiCaller.fetchChannelBroadcastsLimit(null, ichatId, null, 50, true,
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
                                                broadcast.getIchatId(),
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
                            ChannelDatabaseManager.getInstance().updateRecentBroadcastMedias(recentBroadcastMedias, ichatId);
                            resultsYier.onResults();
                        }, new OperationStatus.HandleResult(-102, () -> {
                            ChannelDatabaseManager.getInstance().updateRecentBroadcastMedias(new ArrayList<>(), ichatId);
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
}
