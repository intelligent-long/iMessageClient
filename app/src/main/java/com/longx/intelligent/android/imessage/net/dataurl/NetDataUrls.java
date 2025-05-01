package com.longx.intelligent.android.imessage.net.dataurl;

import android.content.Context;

import com.longx.intelligent.android.imessage.net.BaseUrlProvider;

/**
 * Created by LONG on 2024/4/29 at 11:40 PM.
 */
public class NetDataUrls {
    public static String getAvatarUrl(Context context, String avatarHash){
        return BaseUrlProvider.getHttpBaseUrl(context, false) + "user/info/avatar/" + avatarHash;
    }

    public static String getBroadcastMediaDataUrl(Context context, String mediaId){
        return BaseUrlProvider.getHttpBaseUrl(context, false) + "broadcast/media/data/" + mediaId;
    }

    public static String getGroupAvatarUrl(Context context, String groupAvatarHash){
        return BaseUrlProvider.getHttpBaseUrl(context, false) + "group_channel/info/avatar/" + groupAvatarHash;
    }
}
