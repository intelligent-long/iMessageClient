package com.longx.intelligent.android.ichat2.activity;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.longx.intelligent.android.ichat2.activity.helper.BaseActivity;
import com.longx.intelligent.android.ichat2.adapter.BroadcastLikesRecyclerAdapter;
import com.longx.intelligent.android.ichat2.data.Broadcast;
import com.longx.intelligent.android.ichat2.databinding.ActivityBroadcastLikesBinding;
import com.longx.intelligent.android.ichat2.databinding.RecyclerFooterAllLikesOfBroadcastBinding;

public class BroadcastLikesActivity extends BaseActivity {
    private ActivityBroadcastLikesBinding binding;
    private RecyclerFooterAllLikesOfBroadcastBinding footerBinding;
    private Broadcast broadcast;
    private BroadcastLikesRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBroadcastLikesBinding.inflate(getLayoutInflater());
        footerBinding = RecyclerFooterAllLikesOfBroadcastBinding.inflate(getLayoutInflater(), binding.getRoot(), false);
        setContentView(binding.getRoot());
        setupDefaultBackNavigation(binding.toolbar);
        intentData();
        init();
        fetchAndShowContent();
    }

    private void intentData() {
        broadcast = getIntent().getParcelableExtra(ExtraKeys.BROADCAST);
    }

    private void init() {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BroadcastLikesRecyclerAdapter(this);
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setFooterView(footerBinding.getRoot());
    }

    private void fetchAndShowContent() {
        nextPage();
    }

    private void nextPage() {

    }
}