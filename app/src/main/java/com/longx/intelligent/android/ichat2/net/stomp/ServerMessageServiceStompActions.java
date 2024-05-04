package com.longx.intelligent.android.ichat2.net.stomp;

import android.content.Context;

import com.longx.intelligent.android.ichat2.behavior.ContentUpdater;
import com.longx.intelligent.android.ichat2.yier.GlobalYiersHolder;
import com.longx.intelligent.android.ichat2.yier.NewContentBadgeDisplayYier;

/**
 * Created by LONG on 2024/4/1 at 5:03 AM.
 */
public class ServerMessageServiceStompActions {

    public static void updateCurrentUserInfo(Context context){
        ContentUpdater.updateCurrentUserInfo(context);
    }

    public static void updateChannelAdditionActivities(Context context){
        ContentUpdater.updateChannelAdditionNotViewCount(context);
        GlobalYiersHolder.getYiers(NewContentBadgeDisplayYier.class).ifPresent(newContentBadgeDisplayYiers -> {
            newContentBadgeDisplayYiers.forEach(newContentBadgeDisplayYier -> {
                newContentBadgeDisplayYier.autoShowNewContentBadge(context, NewContentBadgeDisplayYier.ID.CHANNEL_ADDITION_ACTIVITIES);
            });
        });
    }

}
