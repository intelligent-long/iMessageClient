package com.longx.intelligent.android.ichat2.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.longx.intelligent.android.ichat2.fragment.channeladdition.PendingFragment;
import com.longx.intelligent.android.ichat2.fragment.channeladdition.ReceiveFragment;
import com.longx.intelligent.android.ichat2.fragment.channeladdition.SendFragment;

/**
 * Created by LONG on 2024/5/2 at 7:10 PM.
 */
public class ChannelAdditionActivitiesViewPagerAdapter extends FragmentStateAdapter {
    private final PendingFragment pendingFragment;
    private final SendFragment sendFragment;
    private final ReceiveFragment receiveFragment;

    public ChannelAdditionActivitiesViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        pendingFragment = new PendingFragment();
        sendFragment = new SendFragment();
        receiveFragment = new ReceiveFragment();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0: return pendingFragment;
            case 1: return sendFragment;
            case 2: return receiveFragment;
        }
        return new PendingFragment();
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    public PendingFragment getPendingFragment() {
        return pendingFragment;
    }

    public SendFragment getSendFragment() {
        return sendFragment;
    }

    public ReceiveFragment getReceiveFragment() {
        return receiveFragment;
    }
}
