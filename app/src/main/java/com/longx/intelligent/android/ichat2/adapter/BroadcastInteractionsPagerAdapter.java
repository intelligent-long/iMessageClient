package com.longx.intelligent.android.ichat2.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.longx.intelligent.android.ichat2.fragment.broadcastinteraction.BroadcastCommentsFragment;
import com.longx.intelligent.android.ichat2.fragment.broadcastinteraction.BroadcastLikesFragment;
import com.longx.intelligent.android.ichat2.fragment.broadcastinteraction.BroadcastRepliesFragment;

/**
 * Created by LONG on 2024/9/14 at 上午3:04.
 */
public class BroadcastInteractionsPagerAdapter extends FragmentStateAdapter {
    private final BroadcastLikesFragment broadcastLikesFragment;
    private final BroadcastCommentsFragment broadcastCommentsFragment;
    private final BroadcastRepliesFragment broadcastRepliesFragment;

    public BroadcastInteractionsPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        broadcastLikesFragment = new BroadcastLikesFragment();
        broadcastCommentsFragment = new BroadcastCommentsFragment();
        broadcastRepliesFragment = new BroadcastRepliesFragment();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0: return broadcastLikesFragment;
            case 1: return broadcastCommentsFragment;
            case 2: return broadcastRepliesFragment;
        }
        return broadcastLikesFragment;
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    public BroadcastLikesFragment getBroadcastLikeFragment() {
        return broadcastLikesFragment;
    }

    public BroadcastCommentsFragment getBroadcastCommentFragment() {
        return broadcastCommentsFragment;
    }

    public BroadcastRepliesFragment getBroadcastReplyFragment() {
        return broadcastRepliesFragment;
    }
}
