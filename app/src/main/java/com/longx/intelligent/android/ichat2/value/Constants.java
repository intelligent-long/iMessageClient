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
}
