package com.longx.intelligent.android.imessage.yier;

import android.content.Context;

import com.longx.intelligent.android.imessage.da.sharedpref.SharedPreferencesAccessor;

/**
 * Created by LONG on 2024/5/3 at 3:58 PM.
 */
public interface NewContentBadgeDisplayYier {
    enum ID{CHANNEL_ADDITION_ACTIVITIES, MESSAGES, BROADCAST_LIKES, BROADCAST_COMMENTS, BROADCAST_REPLIES, GROUP_CHANNEL_ADDITION_ACTIVITIES}

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

    static int getBroadcastRepliesNewsCount(Context context){
        return SharedPreferencesAccessor.NewContentCount.getBroadcastReplyCommentNewsCount(context);
    }

    static int getGroupChannelAdditionActivitiesNewContentCount(Context context){
        return SharedPreferencesAccessor.NewContentCount.getGroupChannelAdditionActivitiesRequester(context)
                + SharedPreferencesAccessor.NewContentCount.getGroupChannelAdditionActivitiesResponder(context)
                + SharedPreferencesAccessor.NewContentCount.getGroupChannelAdditionActivitiesInviter(context)
                + SharedPreferencesAccessor.NewContentCount.getGroupChannelAdditionActivitiesInvitee(context);
    }

    default void autoShowNewContentBadge(Context context, ID id){
        switch (id){
            case CHANNEL_ADDITION_ACTIVITIES:
                showNewContentBadge(id, getChannelAdditionActivitiesNewContentCount(context));
                break;
            case MESSAGES:
                showNewContentBadge(id, -1); //TODO
                break;
            case BROADCAST_LIKES:
            case BROADCAST_COMMENTS:
            case BROADCAST_REPLIES:
                showNewContentBadge(id, getBroadcastLikesNewsCount(context) +
                        getBroadcastCommentsNewsCount(context) +
                        getBroadcastRepliesNewsCount(context));
                break;
            case GROUP_CHANNEL_ADDITION_ACTIVITIES:
                showNewContentBadge(id, getGroupChannelAdditionActivitiesNewContentCount(context));
                break;

        }
    }

    void showNewContentBadge(ID id, int newContentCount);
}
