package com.longx.intelligent.android.imessage.yier;

import com.longx.intelligent.android.imessage.data.ChannelAddition;
import com.longx.intelligent.android.imessage.data.GroupChannelActivity;
import com.longx.intelligent.android.imessage.data.GroupChannelAddition;

import java.util.List;

public interface GroupChannelAdditionActivitiesFetchYier {
    void onStartFetch();
    void onFetched(List<GroupChannelActivity> groupChannelActivities);
    void onFailure(String failureMessage);
}
