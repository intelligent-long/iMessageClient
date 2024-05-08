package com.longx.intelligent.android.ichat2.net.stomp;

import android.content.Context;

import com.longx.intelligent.android.ichat2.behavior.ContentUpdater;
import com.longx.intelligent.android.ichat2.util.ErrorLogger;
import com.longx.intelligent.android.ichat2.yier.ChannelAdditionActivitiesUpdateYier;
import com.longx.intelligent.android.ichat2.yier.GlobalYiersHolder;
import com.longx.intelligent.android.ichat2.yier.NewContentBadgeDisplayYier;
import com.longx.intelligent.android.ichat2.yier.ResultsYier;

/**
 * Created by LONG on 2024/4/1 at 5:03 AM.
 */
public class ServerMessageServiceStompActions {

    public static void updateCurrentUserInfo(Context context){
        ContentUpdater.updateCurrentUserInfo(context);
    }

    public static void updateChannelAdditionActivities(Context context){
        GlobalYiersHolder.getYiers(ChannelAdditionActivitiesUpdateYier.class).ifPresent(channelAdditionActivitiesUpdateYiers -> {
            channelAdditionActivitiesUpdateYiers.forEach(ChannelAdditionActivitiesUpdateYier::onChannelAdditionActivitiesUpdate);
        });
    }

    public static void updateChannelAdditionsNotViewCount(Context context){
        ContentUpdater.updateChannelAdditionNotViewCount(context, results -> GlobalYiersHolder.getYiers(NewContentBadgeDisplayYier.class).ifPresent(newContentBadgeDisplayYiers -> {
            newContentBadgeDisplayYiers.forEach(newContentBadgeDisplayYier -> {
                newContentBadgeDisplayYier.autoShowNewContentBadge(context, NewContentBadgeDisplayYier.ID.CHANNEL_ADDITION_ACTIVITIES);
            });
        }));
    }

}
