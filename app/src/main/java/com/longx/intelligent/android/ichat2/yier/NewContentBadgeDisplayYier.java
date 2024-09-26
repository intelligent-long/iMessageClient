package com.longx.intelligent.android.ichat2.yier;

import android.content.Context;

import com.longx.intelligent.android.ichat2.da.sharedpref.SharedPreferencesAccessor;

/**
 * Created by LONG on 2024/5/3 at 3:58 PM.
 */
public interface NewContentBadgeDisplayYier {
    enum ID{CHANNEL_ADDITION_ACTIVITIES, MESSAGES, BROADCAST_LIKES, BROADCAST_COMMENTS}

    static int getChannelAdditionActivitiesNewContentCount(Context context){
        return SharedPreferencesAccessor.NewContentCount.getChannelAdditionActivitiesRequester(context)
                + SharedPreferencesAccessor.NewContentCount.getChannelAdditionActivitiesResponder(context);
    }

    static int getBroadcastLikesNewsCount(Context context){
        return SharedPreferencesAccessor.NewContentCount.getBroadcastLikeNewsCount(context);
    }

    static int getBroadcastCommentsNewsCount(Context context){
        return SharedPreferencesAccessor.NewContentCount.getBroadcastCommentNewsCount(context);
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
            case BROADCAST_COMMENTS:
                showNewContentBadge(id, getBroadcastLikesNewsCount(context) + getBroadcastCommentsNewsCount(context));
                break;

        }
    }

    void showNewContentBadge(ID id, int newContentCount);
}
