package com.longx.intelligent.android.ichat2.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.longx.intelligent.android.ichat2.fragment.channeladdition.ChannelAdditionPendingFragment;
import com.longx.intelligent.android.ichat2.fragment.channeladdition.ChannelAdditionReceiveFragment;
import com.longx.intelligent.android.ichat2.fragment.channeladdition.ChannelAdditionSendFragment;

/**
 * Created by LONG on 2024/5/2 at 7:10 PM.
 */
public class ChannelAdditionActivitiesPagerAdapter extends FragmentStateAdapter {
    private final ChannelAdditionPendingFragment channelAdditionPendingFragment;
    private final ChannelAdditionSendFragment channelAdditionSendFragment;
    private final ChannelAdditionReceiveFragment channelAdditionReceiveFragment;

    public ChannelAdditionActivitiesPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        channelAdditionPendingFragment = new ChannelAdditionPendingFragment();
        channelAdditionSendFragment = new ChannelAdditionSendFragment();
        channelAdditionReceiveFragment = new ChannelAdditionReceiveFragment();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0: return channelAdditionPendingFragment;
            case 1: return channelAdditionSendFragment;
            case 2: return channelAdditionReceiveFragment;
        }
        return new ChannelAdditionPendingFragment();
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    public ChannelAdditionPendingFragment getPendingFragment() {
        return channelAdditionPendingFragment;
    }

    public ChannelAdditionSendFragment getSendFragment() {
        return channelAdditionSendFragment;
    }

    public ChannelAdditionReceiveFragment getReceiveFragment() {
        return channelAdditionReceiveFragment;
    }
}
