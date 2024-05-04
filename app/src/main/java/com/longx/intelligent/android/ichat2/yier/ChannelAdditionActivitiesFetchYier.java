package com.longx.intelligent.android.ichat2.yier;

import com.longx.intelligent.android.ichat2.data.ChannelAdditionInfo;

import java.util.List;

/**
 * Created by LONG on 2024/5/4 at 6:28 PM.
 */
public interface ChannelAdditionActivitiesFetchYier {
    void onStartFetch();
    void onFetched(List<ChannelAdditionInfo> channelAdditionInfos);
    void onFailure(String failureMessage);
}
