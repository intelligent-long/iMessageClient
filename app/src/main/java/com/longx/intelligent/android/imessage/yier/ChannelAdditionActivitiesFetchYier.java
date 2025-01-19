package com.longx.intelligent.android.imessage.yier;

import com.longx.intelligent.android.imessage.data.ChannelAddition;

import java.util.List;

/**
 * Created by LONG on 2024/5/4 at 6:28 PM.
 */
public interface ChannelAdditionActivitiesFetchYier {
    void onStartFetch();
    void onFetched(List<ChannelAddition> channelAdditions);
    void onFailure(String failureMessage);
}
