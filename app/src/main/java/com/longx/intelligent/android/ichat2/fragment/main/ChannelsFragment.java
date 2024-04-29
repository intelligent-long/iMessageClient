package com.longx.intelligent.android.ichat2.fragment.main;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.activity.ChannelActivity;
import com.longx.intelligent.android.ichat2.activity.ExtraKeys;
import com.longx.intelligent.android.ichat2.activity.SearchChannelActivity;
import com.longx.intelligent.android.ichat2.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.ichat2.data.ChannelInfo;
import com.longx.intelligent.android.ichat2.data.SelfInfo;
import com.longx.intelligent.android.ichat2.adapter.ChannelListRecyclerAdapter;
import com.longx.intelligent.android.ichat2.databinding.FragmentChannelsBinding;
import com.longx.intelligent.android.lib.recyclerview.RecyclerView;
import com.longx.intelligent.android.lib.recyclerview.WrappableRecyclerViewAdapter;

import java.util.ArrayList;

public class ChannelsFragment extends BaseMainFragment implements WrappableRecyclerViewAdapter.OnItemClickYier<ChannelListRecyclerAdapter.ItemData> {
    private FragmentChannelsBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentChannelsBinding.inflate(inflater, container, false);
        setupRecyclerView(inflater);
        setupFab();
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

    private void setupRecyclerView(LayoutInflater inflater) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        binding.recyclerView.setLayoutManager(layoutManager);
        ArrayList<ChannelListRecyclerAdapter.ItemData> itemDataList = new ArrayList<>();
        SelfInfo selfInfo = SharedPreferencesAccessor.UserInfoPref.getCurrentUserInfo(requireContext());
        ChannelInfo selfChannelInfo = new ChannelInfo(
                selfInfo.getIchatId(),
                selfInfo.getIchatIdUser(),
                selfInfo.getEmail(),
                selfInfo.getUsername(),
                selfInfo.getAvatarHash(),
                selfInfo.getSex(),
                selfInfo.getFirstRegion(),
                selfInfo.getSecondRegion(),
                selfInfo.getThirdRegion(),
                true,
                selfInfo.getAvatarExtension());
        itemDataList.add(new ChannelListRecyclerAdapter.ItemData(selfChannelInfo));
        ChannelListRecyclerAdapter channelListRecyclerAdapter = new ChannelListRecyclerAdapter(requireActivity(), itemDataList);
        channelListRecyclerAdapter.setOnItemClickYier(this);
        binding.recyclerView.setAdapter(channelListRecyclerAdapter);
        View headerView = inflater.inflate(R.layout.layout_channel_recycler_view_header, null, false);
        binding.recyclerView.setHeaderView(headerView);
        binding.recyclerView.addOnScrollUpDownYier(new RecyclerView.OnScrollUpDownYier() {
            @Override
            public void onScrollUp() {
                binding.fab.shrink();
            }

            @Override
            public void onScrollDown() {
                binding.fab.extend();
            }
        });
    }

    @Override
    public void onItemClick(int position, ChannelListRecyclerAdapter.ItemData data) {
        Intent intent = new Intent(requireContext(), ChannelActivity.class);
        intent.putExtra(ExtraKeys.ICHAT_ID, data.getChannelInfo().getIchatId());
        startActivity(intent);
    }

    private void setupFab() {
        binding.fab.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), SearchChannelActivity.class));
        });
    }
}