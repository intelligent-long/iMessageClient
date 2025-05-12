package com.longx.intelligent.android.imessage.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.longx.intelligent.android.imessage.fragment.channeladdition.ChannelAdditionPendingFragment;
import com.longx.intelligent.android.imessage.fragment.channeladdition.ChannelAdditionReceiveFragment;
import com.longx.intelligent.android.imessage.fragment.channeladdition.ChannelAdditionSendFragment;
import com.longx.intelligent.android.imessage.fragment.groupchanneladdition.GroupChannelAdditionPendingFragment;
import com.longx.intelligent.android.imessage.fragment.groupchanneladdition.GroupChannelAdditionReceiveFragment;
import com.longx.intelligent.android.imessage.fragment.groupchanneladdition.GroupChannelAdditionSendFragment;

/**
 * Created by LONG on 2024/5/2 at 7:10 PM.
 */
public class GroupChannelAdditionsActivityPagerAdapter extends FragmentStateAdapter {
    private final GroupChannelAdditionReceiveFragment groupChannelAdditionReceiveFragment;
    private final GroupChannelAdditionSendFragment groupChannelAdditionSendFragment;
    private final GroupChannelAdditionPendingFragment groupChannelAdditionPendingFragment;

    public GroupChannelAdditionsActivityPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        groupChannelAdditionReceiveFragment = new GroupChannelAdditionReceiveFragment();
        groupChannelAdditionSendFragment = new GroupChannelAdditionSendFragment();
        groupChannelAdditionPendingFragment = new GroupChannelAdditionPendingFragment();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0: return groupChannelAdditionPendingFragment;
            case 1: return groupChannelAdditionSendFragment;
            case 2: return groupChannelAdditionReceiveFragment;
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    public GroupChannelAdditionReceiveFragment getReceiveFragment() {
        return groupChannelAdditionReceiveFragment;
    }

    public GroupChannelAdditionSendFragment getSendFragment() {
        return groupChannelAdditionSendFragment;
    }

    public GroupChannelAdditionPendingFragment getPendingFragment() {
        return groupChannelAdditionPendingFragment;
    }
}
