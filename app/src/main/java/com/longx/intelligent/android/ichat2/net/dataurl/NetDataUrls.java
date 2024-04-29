package com.longx.intelligent.android.ichat2.net.dataurl;

import android.content.Context;

import com.longx.intelligent.android.ichat2.net.ServerProperties;

/**
 * Created by LONG on 2024/4/29 at 11:40 PM.
 */
public class NetDataUrls {
    public static String getAvatarUrl(Context context, String avatarHash){
        return ServerProperties.getBaseUrl(context) + "user/info/avatar/get/" + avatarHash;
    }
}
