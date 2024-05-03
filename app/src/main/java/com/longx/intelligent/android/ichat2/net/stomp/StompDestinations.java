package com.longx.intelligent.android.ichat2.net.stomp;

/**
 * Created by LONG on 2024/1/23 at 7:54 PM.
 */
public class StompDestinations {
    public static final String PREFIX_TOPIC = "/topic";
    public static final String PREFIX_QUEUE = "/queue";
    public static final String PREFIX_APP = "/app";
    public static final String PREFIX_USER = "/user";

    public static final String USER_INFO_UPDATE = "/user" +  PREFIX_QUEUE + "/user_info_update";
    public static final String CHANNEL_ADDITIONS_UPDATE = "/user" +  PREFIX_QUEUE + "/channel_additions_update";
}
