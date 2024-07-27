package com.longx.intelligent.android.ichat2.fragment.main;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.longx.intelligent.android.ichat2.activity.SearchChannelActivity;
import com.longx.intelligent.android.ichat2.activity.SendBroadcastActivity;
import com.longx.intelligent.android.ichat2.databinding.FragmentBroadcastsBinding;
import com.longx.intelligent.android.lib.recyclerview.RecyclerView;

public class BroadcastsFragment extends BaseMainFragment {
    private FragmentBroadcastsBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentBroadcastsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupToolbarNavIcon(binding.toolbar);
    }

    @Override
    public Toolbar getToolbar() {
        return binding == null ? null : binding.toolbar;
    }

    @Override
    public void onResume() {
        super.onResume();
        setupYiers();
    }

    private void setupYiers() {
        binding.recyclerView.addOnThresholdScrollUpDownYier(new RecyclerView.OnThresholdScrollUpDownYier(50){
            @Override
            public void onScrollUp() {
                if(binding.sendBroadcastFab.isExtended()) binding.sendBroadcastFab.shrink();
            }

            @Override
            public void onScrollDown() {
                if(!binding.sendBroadcastFab.isExtended()) binding.sendBroadcastFab.extend();
            }
        });
        binding.sendBroadcastFab.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), SendBroadcastActivity.class));
        });
    }
}