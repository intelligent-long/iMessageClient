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
    public ChannelAdditionActivitiesViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0: return new PendingFragment();
            case 1: return new SendFragment();
            case 2: return new ReceiveFragment();
        }
        return new PendingFragment();
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
