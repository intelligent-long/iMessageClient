package com.longx.intelligent.android.imessage.net.stomp;

/**
 * Created by LONG on 2024/1/23 at 7:54 PM.
 */
public class StompDestinations {
    public static final String PREFIX_TOPIC = "/topic";
    public static final String PREFIX_QUEUE = "/queue";
    public static final String PREFIX_APP = "/app";
    public static final String PREFIX_USER = "/user";

    public static final String USER_PROFILE_UPDATE = "/user" +  PREFIX_QUEUE + "/user_profile_update";
    public static final String CHANNEL_ADDITIONS_UPDATE = "/user" +  PREFIX_QUEUE + "/channel_additions_update";
    public static final String CHANNEL_ADDITIONS_NOT_VIEW_COUNT_UPDATE = "/user" + PREFIX_QUEUE + "/channel_additions_not_view_count_update";
    public static final String CHANNELS_UPDATE = "/user" + PREFIX_QUEUE + "/channels_update";
    public static final String CHAT_MESSAGES_UPDATE = "/user" + PREFIX_QUEUE + "/chat_messages_update";
    public static final String CHANNEL_TAGS_UPDATE = "/user" + PREFIX_QUEUE + "/channel_tags_update";
    public static final String BROADCASTS_NEWS_UPDATE = "/user" + PREFIX_QUEUE + "/broadcasts_news_update";
    public static final String RECENT_BROADCAST_MEDIAS_UPDATE = "/user" + PREFIX_QUEUE + "/recent_broadcast_medias_update";
    public static final String BROADCASTS_LIKES_UPDATE = "/user" + PREFIX_QUEUE + "/broadcasts_likes_update";
    public static final String BROADCASTS_COMMENTS_UPDATE = "/user" + PREFIX_QUEUE + "/broadcasts_comments_update";
    public static final String BROADCASTS_REPLIES_UPDATE = "/user" + PREFIX_QUEUE + "/broadcasts_replies_update";
    public static final String GROUP_CHANNELS_UPDATE = "/user" + PREFIX_QUEUE + "/group_channels_update";
}
