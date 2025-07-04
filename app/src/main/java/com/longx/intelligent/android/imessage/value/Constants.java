package com.longx.intelligent.android.imessage.value;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by LONG on 2024/4/20 at 6:27 PM.
 */
public class Constants {
    public static final String APP_NAME = "iMessage";
    public static final String BUILD_TIME = "2025 年 2 月 5 日";
    public static final String AUTHOR = "LONG";
    public static final SimpleDateFormat COMMON_SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
    public static final long CHAT_MESSAGE_SHOW_TIME_INTERVAL_MILLI_SEC = 5 * 60 * 1000;
    public static final double GRID_SPACE_DP = 2.9;
    public static final int GRID_COLUMN_COUNT = 3;
    public static final String TIME_FORMAT_PATTERN_Y_M_D = "yyyy 年 M 月 d 日";
    public static final String TIME_FORMAT_PATTERN_D = "d 日";
    public static final String TIME_FORMAT_PATTERN_M_D = "M 月 d 日";
    public static final int CHAT_IMAGE_VIEW_MAX_WIDTH_DP = 200;
    public static final int CHAT_IMAGE_VIEW_MAX_HEIGHT_DP = 200;
    public static final int MAX_ONCE_SEND_CHAT_MESSAGE_IMAGE_COUNT = 30;
    public static final int MAX_ONCE_SEND_CHAT_MESSAGE_FILE_COUNT = 10;
    public static final int MAX_ONCE_SEND_CHAT_MESSAGE_VIDEO_COUNT = 10;
    public static final long MAX_SEND_CHAT_MESSAGE_FILE_SIZE = 512 * 1024 * 1024;
    public static final long MAX_SEND_CHAT_MESSAGE_VIDEO_SIZE = 512 * 1024 * 1024;
    public static final long MAX_SEND_CHAT_MESSAGE_IMAGE_SIZE = 64 * 1024 * 1024;
    public static final int MAX_CHAT_VOICE_TIME_SEC = 120;
    public static final int FETCH_BROADCAST_PAGE_SIZE = 30;
    public static final int MAX_BROADCAST_IMAGE_COUNT = 30;
    public static final int MAX_BROADCAST_VIDEO_COUNT = 5;
    public static final long MAX_BROADCAST_IMAGE_SIZE = 64 * 1024 * 1024;
    public static final long MAX_BROADCAST_VIDEO_SIZE = 512 * 1024 * 1024;
    public static final double GRID_SPACE_SEND_BROADCAST_DP = 5;
    public static final int SINGLE_BROADCAST_IMAGE_VIEW_MAX_HEIGHT_DP = 230;
    public static final int SINGLE_BROADCAST_IMAGE_VIEW_MARGIN_END_DP = 50;
    public static final int MAX_BROADCAST_TEXT_LENGTH = 1000;
    public static final int EDIT_BROADCAST_MEDIA_COLUMN_COUNT = 3;
    public static final int RECENT_BROADCAST_MEDIAS_SHOW_ITEM_SIZE = 4;
    public static final int FETCH_BROADCAST_LIKES_INTERACTION_PAGE_SIZE = 30;
    public static final int FETCH_BROADCAST_LIKES_PAGE_SIZE = 30;
    public static final int FETCH_BROADCAST_COMMENTS_PAGE_SIZE = 30;
    public static final int FETCH_BROADCAST_COMMENTS_INTERACTION_PAGE_SIZE = 30;
    public static final int FETCH_BROADCAST_REPLY_COMMENTS_INTERACTION_PAGE_SIZE = 30;
    public static final int MAX_ALLOW_UNSEND_MINUTES = 3;
    public static final long MIN_CHECK_SOFTWARE_UPDATABLE_INTERVAL_MILLI_SEC = 5 * 60 * 60 * 1000;
    public static final String INVITE_JOIN_GROUP_CHANNEL_MESSAGE = "邀请添加群频道。";
    public static final String MY_IMESSAGE_URL = "imessage://channel/LONG";
    public static final String MY_IMESSAGE_ID = "LONG";
    public static final String MY_EMAIL = "tx.long@outlook.com";
    public static final String MY_QQ_URL = "mqq://im/chat?chat_type=wpa&uin=909691944&version=1&src_type=web";
    public static final String MY_QQ_ID = "909691944";
    public static final String MY_WECHAT_ID = "_909691944";
    public static final String MY_GITHUB_URL = "https://github.com/intelligent-long";
    public static final String[] CENTRAL_SERVER_IMESSAGE_IDS = {"LONG"};
}
