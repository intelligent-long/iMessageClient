package com.longx.intelligent.android.ichat2.value;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by LONG on 2024/4/20 at 6:27 PM.
 */
public class Constants {
    public static final String AUTHOR = "LONG";
    public static final SimpleDateFormat COMMON_SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
    public static final long CHAT_MESSAGE_SHOW_TIME_INTERVAL = 5 * 60 * 1000;
    public static final double GRID_SPACE_DP = 2.7;
    public static final int GRID_COLUMN = 4;
    public static final String TIME_FORMAT_PATTERN_Y_M_D = "yyyy 年 M 月 d 日";
    public static final String TIME_FORMAT_PATTERN_D = "d 日";
    public static final String TIME_FORMAT_PATTERN_M_D = "M 月 d 日";
    public static final int CHAT_IMAGE_VIEW_MAX_WIDTH_DP = 200;
    public static final int CHAT_IMAGE_VIEW_MAX_HEIGHT_DP = 200;
    public static final int MAX_ONCE_SEND_CHAT_MESSAGE_IMAGE_COUNT = 30;
    public static final int MAX_ONCE_SEND_CHAT_MESSAGE_FILE_COUNT = 10;
    public static final int MAX_ONCE_SEND_CHAT_MESSAGE_VIDEO_COUNT = 10;
    public static final int MAX_SEND_CHAT_MESSAGE_FILE_SIZE = 512 * 1024 * 1024;
    public static final int MAX_SEND_CHAT_MESSAGE_VIDEO_SIZE = 512 * 1024 * 1024;
    public static final int MAX_CHAT_VOICE_TIME_SEC = 120;
    public static final int FETCH_BROADCAST_PAGE_SIZE = 30;
    public static final int MAX_BROADCAST_IMAGE_COUNT = 30;
    public static final int MAX_BROADCAST_VIDEO_COUNT = 5;
    public static final double GRID_SPACE_SEND_BROADCAST_DP = 5;
    public static final int SINGLE_BROADCAST_IMAGE_VIEW_MAX_HEIGHT_DP = 230;
    public static final int SINGLE_BROADCAST_IMAGE_VIEW_MARGIN_END_DP = 50;
    public static final int MAX_BROADCAST_TEXT_LENGTH = 1000;
}
