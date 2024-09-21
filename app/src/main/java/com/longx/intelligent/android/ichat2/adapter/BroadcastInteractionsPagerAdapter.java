package com.longx.intelligent.android.ichat2.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.longx.intelligent.android.ichat2.fragment.broadcastinteraction.BroadcastCommentsInteractionFragment;
import com.longx.intelligent.android.ichat2.fragment.broadcastinteraction.BroadcastLikesInteractionFragment;
import com.longx.intelligent.android.ichat2.fragment.broadcastinteraction.BroadcastRepliesInteractionFragment;

/**
 * Created by LONG on 2024/9/14 at 上午3:04.
 */
public class BroadcastInteractionsPagerAdapter extends FragmentStateAdapter {
    private final BroadcastLikesInteractionFragment broadcastLikesInteractionFragment;
    private final BroadcastCommentsInteractionFragment broadcastCommentsInteractionFragment;
    private final BroadcastRepliesInteractionFragment broadcastRepliesInteractionFragment;

    public BroadcastInteractionsPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        broadcastLikesInteractionFragment = new BroadcastLikesInteractionFragment();
        broadcastCommentsInteractionFragment = new BroadcastCommentsInteractionFragment();
        broadcastRepliesInteractionFragment = new BroadcastRepliesInteractionFragment();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0: return broadcastLikesInteractionFragment;
            case 1: return broadcastCommentsInteractionFragment;
            case 2: return broadcastRepliesInteractionFragment;
        }
        return broadcastLikesInteractionFragment;
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    public BroadcastLikesInteractionFragment getBroadcastLikeFragment() {
        return broadcastLikesInteractionFragment;
    }

    public BroadcastCommentsInteractionFragment getBroadcastCommentFragment() {
        return broadcastCommentsInteractionFragment;
    }

    public BroadcastRepliesInteractionFragment getBroadcastReplyFragment() {
        return broadcastRepliesInteractionFragment;
    }
}
