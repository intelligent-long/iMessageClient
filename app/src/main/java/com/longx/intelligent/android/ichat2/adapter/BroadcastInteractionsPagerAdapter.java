package com.longx.intelligent.android.ichat2.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.longx.intelligent.android.ichat2.fragment.broadcastinteraction.BroadcastCommentFragment;
import com.longx.intelligent.android.ichat2.fragment.broadcastinteraction.BroadcastLikeFragment;
import com.longx.intelligent.android.ichat2.fragment.broadcastinteraction.BroadcastReplyFragment;

/**
 * Created by LONG on 2024/9/14 at 上午3:04.
 */
public class BroadcastInteractionsPagerAdapter extends FragmentStateAdapter {
    private final BroadcastLikeFragment broadcastLikeFragment;
    private final BroadcastCommentFragment broadcastCommentFragment;
    private final BroadcastReplyFragment broadcastReplyFragment;

    public BroadcastInteractionsPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        broadcastLikeFragment = new BroadcastLikeFragment();
        broadcastCommentFragment = new BroadcastCommentFragment();
        broadcastReplyFragment = new BroadcastReplyFragment();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0: return broadcastLikeFragment;
            case 1: return broadcastCommentFragment;
            case 2: return broadcastReplyFragment;
        }
        return broadcastLikeFragment;
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    public BroadcastLikeFragment getBroadcastLikeFragment() {
        return broadcastLikeFragment;
    }

    public BroadcastCommentFragment getBroadcastCommentFragment() {
        return broadcastCommentFragment;
    }

    public BroadcastReplyFragment getBroadcastReplyFragment() {
        return broadcastReplyFragment;
    }
}
