package com.longx.intelligent.android.ichat2.yier;

import android.content.Context;

import com.longx.intelligent.android.ichat2.da.sharedpref.SharedPreferencesAccessor;

/**
 * Created by LONG on 2024/5/3 at 3:58 PM.
 */
public interface NewContentBadgeDisplayYier {
    enum ID{CHANNEL_ADDITION_ACTIVITIES, MESSAGES, BROADCAST_LIKES}

    static int getChannelAdditionActivitiesNewContentCount(Context context){
        return SharedPreferencesAccessor.NewContentCount.getChannelAdditionActivitiesRequester(context)
                + SharedPreferencesAccessor.NewContentCount.getChannelAdditionActivitiesResponder(context);
    }

    static int getBroadcastLikesNewsCount(Context context){
        int likeNewsCount = SharedPreferencesAccessor.NewContentCount.getBroadcastLikeNewsCount(context);
        return likeNewsCount;
    }

    default void autoShowNewContentBadge(Context context, ID id){
        switch (id){
            case CHANNEL_ADDITION_ACTIVITIES:
                showNewContentBadge(id, getChannelAdditionActivitiesNewContentCount(context));
                break;
            case MESSAGES:
                showNewContentBadge(id, -1);
                break;
            case BROADCAST_LIKES:
                showNewContentBadge(id, getBroadcastLikesNewsCount(context));
                break;
        }
    }

    void showNewContentBadge(ID id, int newContentCount);
}
