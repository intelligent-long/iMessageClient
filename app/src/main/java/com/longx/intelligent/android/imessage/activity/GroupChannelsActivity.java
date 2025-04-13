package com.longx.intelligent.android.imessage.activity;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.appbar.AppBarLayout;
import com.longx.intelligent.android.imessage.activity.helper.BaseActivity;
import com.longx.intelligent.android.imessage.adapter.GroupChannelRecyclerAdapter;
import com.longx.intelligent.android.imessage.databinding.ActivityGroupChannelsBinding;
import com.longx.intelligent.android.imessage.databinding.RecyclerHeaderChannelBinding;
import com.longx.intelligent.android.imessage.databinding.RecyclerHeaderGroupChannelBinding;
import com.longx.intelligent.android.lib.recyclerview.RecyclerView;

import java.util.ArrayList;

public class GroupChannelsActivity extends BaseActivity {
    private ActivityGroupChannelsBinding binding;
    private RecyclerHeaderGroupChannelBinding headerViewBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGroupChannelsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupDefaultBackNavigation(binding.toolbar);
        showContent();
        setUpYiers();
    }

    private void showContent() {
        setupRecyclerView();
        if(true){
            toNoContent();
        }else {
            toContent();
        }
    }

    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.recyclerView.setLayoutManager(layoutManager);
        headerViewBinding = RecyclerHeaderGroupChannelBinding.inflate(getLayoutInflater(), binding.recyclerView, false);
        GroupChannelRecyclerAdapter adapter = new GroupChannelRecyclerAdapter(this, new ArrayList<>());
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setHeaderView(headerViewBinding.getRoot());
    }

    private void toNoContent(){
        ((AppBarLayout.LayoutParams)binding.collapsingToolbarLayout.getLayoutParams())
                .setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_NO_SCROLL);
        binding.noContentLayout.setVisibility(View.VISIBLE);
    }

    private void toContent(){
        ((AppBarLayout.LayoutParams)binding.collapsingToolbarLayout.getLayoutParams())
                .setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
                        | AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED
                        | AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP);
        binding.noContentLayout.setVisibility(View.GONE);
    }

    private void setUpYiers() {
        binding.recyclerView.addOnThresholdScrollUpDownYier(new RecyclerView.OnThresholdScrollUpDownYier(50) {
            @Override
            public void onScrollUp() {
                if(binding.fab.isExtended()) binding.fab.shrink();
            }

            @Override
            public void onScrollDown() {
                if(!binding.fab.isExtended()) binding.fab.extend();
            }
        });

    }

    public ActivityGroupChannelsBinding getBinding(){
        return binding;
    }
}