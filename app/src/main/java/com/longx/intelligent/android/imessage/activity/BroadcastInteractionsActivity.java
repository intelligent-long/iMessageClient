package com.longx.intelligent.android.imessage.activity;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayoutMediator;
import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.imessage.activity.helper.BaseActivity;
import com.longx.intelligent.android.imessage.adapter.BroadcastInteractionsPagerAdapter;
import com.longx.intelligent.android.imessage.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.imessage.databinding.ActivityBroadcastInteractionsBinding;

public class BroadcastInteractionsActivity extends BaseActivity {
    private ActivityBroadcastInteractionsBinding binding;
    private static String[] PAGER_TITLES;
    private BroadcastInteractionsPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        PAGER_TITLES = new String[]{getString(R.string.broadcast_interactions_activity_like), getString(R.string.broadcast_interactions_activity_comment), getString(R.string.broadcast_interactions_activity_reply)};
        super.onCreate(savedInstanceState);
        binding = ActivityBroadcastInteractionsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupDefaultBackNavigation(binding.toolbar);
        init();
    }

    private void init(){
        pagerAdapter = new BroadcastInteractionsPagerAdapter(this);
        binding.viewPager.setAdapter(pagerAdapter);
        new TabLayoutMediator(binding.tabs, binding.viewPager, (tab, position) -> tab.setText(PAGER_TITLES[position])).attach();
        int likeNewsCount = SharedPreferencesAccessor.NewContentCount.getBroadcastLikeNewsCount(this);
        int broadcastCommentNewsCount = SharedPreferencesAccessor.NewContentCount.getBroadcastCommentNewsCount(this);
        int broadcastReplyCommentNewsCount = SharedPreferencesAccessor.NewContentCount.getBroadcastReplyCommentNewsCount(this);
        int max = Math.max(Math.max(likeNewsCount, broadcastCommentNewsCount), broadcastReplyCommentNewsCount);
        if(likeNewsCount == max){
            binding.viewPager.setCurrentItem(0);
        }else if(broadcastCommentNewsCount == max){
            binding.viewPager.setCurrentItem(1);
        }else {
            binding.viewPager.setCurrentItem(2);
        }
    }
}