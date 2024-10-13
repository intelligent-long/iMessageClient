package com.longx.intelligent.android.ichat2.adapter;

import android.app.Activity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.core.widget.NestedScrollView;

import com.longx.intelligent.android.ichat2.data.Channel;
import com.longx.intelligent.android.ichat2.ui.LinearLayoutViews;

/**
 * Created by LONG on 2024/10/14 at 上午2:22.
 */
public class BroadcastChannelPermissionLinearLayoutViews extends LinearLayoutViews<Channel> {
    public BroadcastChannelPermissionLinearLayoutViews(Activity activity, LinearLayout linearLayout, NestedScrollView nestedScrollView) {
        super(activity, linearLayout, nestedScrollView);
    }

    public BroadcastChannelPermissionLinearLayoutViews(Activity activity, LinearLayout linearLayout, ScrollView scrollView) {
        super(activity, linearLayout, scrollView);
    }

    @Override
    public View getView(Channel item, Activity activity) {
        return null;
    }
}
