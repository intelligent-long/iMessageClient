package com.longx.intelligent.android.ichat2.adapter;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.longx.intelligent.android.ichat2.fragment.forwardmessage.ForwardMessageChannelsFragment;
import com.longx.intelligent.android.ichat2.fragment.forwardmessage.ForwardMessageMessagesFragment;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by LONG on 2024/10/19 at 上午10:03.
 */
public class ForwardMessagePagerAdapter extends FragmentStateAdapter {
    private final ForwardMessageMessagesFragment forwardMessageMessagesFragment;
    private final ForwardMessageChannelsFragment forwardMessageChannelsFragment;

    public ForwardMessagePagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        forwardMessageMessagesFragment = new ForwardMessageMessagesFragment();
        forwardMessageChannelsFragment = new ForwardMessageChannelsFragment();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0: return forwardMessageMessagesFragment;
            case 1: return forwardMessageChannelsFragment;
        }
        return forwardMessageChannelsFragment;
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    public Set<String> getCheckedChannelIds() {
        Set<String> checkedChannelIds = new HashSet<>();
        checkedChannelIds.addAll(forwardMessageMessagesFragment.getCheckedChannelIds());
        checkedChannelIds.addAll(forwardMessageChannelsFragment.getCheckedChannelIds());
        return checkedChannelIds;
    }
}
