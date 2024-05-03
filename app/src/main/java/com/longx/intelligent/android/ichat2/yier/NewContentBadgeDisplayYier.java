package com.longx.intelligent.android.ichat2.yier;

import android.content.Context;

import com.longx.intelligent.android.ichat2.da.sharedpref.SharedPreferencesAccessor;

/**
 * Created by LONG on 2024/5/3 at 3:58 PM.
 */
public interface NewContentBadgeDisplayYier {
    enum ID{CHANNEL_ADDITION_ACTIVITIES}

    static int getChannelAdditionActivitiesNewContentCount(Context context){
        return SharedPreferencesAccessor.NewContentCount.getChannelAdditionActivities(context);
    }

    default void autoShowNewContentBadge(Context context, ID id){
        if(id.equals(ID.CHANNEL_ADDITION_ACTIVITIES)){
            showNewContentBadge(id, getChannelAdditionActivitiesNewContentCount(context));
        }
    }

    void showNewContentBadge(ID id, int newContentCount);
}
