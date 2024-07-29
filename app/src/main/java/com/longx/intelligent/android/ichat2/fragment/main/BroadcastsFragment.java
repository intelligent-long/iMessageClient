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
import com.longx.intelligent.android.ichat2.activity.SearchChannelActivity;
import com.longx.intelligent.android.ichat2.activity.SendBroadcastActivity;
import com.longx.intelligent.android.ichat2.adapter.BroadcastsRecyclerAdapter;
import com.longx.intelligent.android.ichat2.adapter.ChannelsRecyclerAdapter;
import com.longx.intelligent.android.ichat2.data.Broadcast;
import com.longx.intelligent.android.ichat2.data.response.PaginatedOperationData;
import com.longx.intelligent.android.ichat2.databinding.FragmentBroadcastsBinding;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.BroadcastApiCaller;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.ichat2.util.UiUtil;
import com.longx.intelligent.android.ichat2.util.WindowAndSystemUiUtil;
import com.longx.intelligent.android.lib.recyclerview.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class BroadcastsFragment extends BaseMainFragment {
    private FragmentBroadcastsBinding binding;
    private BroadcastsRecyclerAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentBroadcastsBinding.inflate(inflater, container, false);
        setupFab();
        setupRecyclerView();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupToolbarNavIcon(binding.toolbar);
        fetchAndRefreshBroadcast();
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

    private void setupFab() {
        float fabMarginTop = WindowAndSystemUiUtil.getStatusBarHeight(requireContext()) + WindowAndSystemUiUtil.getActionBarSize(requireContext()) + requireContext().getResources().getDimension(R.dimen.fab_margin_bottom);
        float fabMarginEnd = requireContext().getResources().getDimension(R.dimen.fab_margin_end);
        UiUtil.setViewMargin(binding.sendBroadcastFab, 0, (int) fabMarginTop, (int) fabMarginEnd, 0);
    }

    private void setupRecyclerView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        binding.recyclerView.setLayoutManager(layoutManager);
        ArrayList<BroadcastsRecyclerAdapter.ItemData> itemDataList = new ArrayList<>();
        adapter = new BroadcastsRecyclerAdapter(requireActivity(), itemDataList);
        binding.recyclerView.setAdapter(adapter);
    }

    private void fetchAndRefreshBroadcast(){
        BroadcastApiCaller.fetchBroadcastsLimit(requireActivity(), 1, 50, new RetrofitApiCaller.BaseCommonYier<PaginatedOperationData<Broadcast>>(){
            @Override
            public void ok(PaginatedOperationData<Broadcast> data, Response<PaginatedOperationData<Broadcast>> row, Call<PaginatedOperationData<Broadcast>> call) {
                super.ok(data, row, call);
                List<Broadcast> broadcastList = data.getData();
                List<BroadcastsRecyclerAdapter.ItemData> itemDataList = new ArrayList<>();
                broadcastList.forEach(broadcast -> {
                    itemDataList.add(new BroadcastsRecyclerAdapter.ItemData(broadcast));
                });
                adapter.addItemsAndShow(itemDataList);
            }
        });
    }
}