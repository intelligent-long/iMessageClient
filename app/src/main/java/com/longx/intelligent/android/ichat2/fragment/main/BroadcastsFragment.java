package com.longx.intelligent.android.ichat2.fragment.main;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.activity.MainActivity;
import com.longx.intelligent.android.ichat2.activity.SendBroadcastActivity;
import com.longx.intelligent.android.ichat2.adapter.BroadcastsRecyclerAdapter;
import com.longx.intelligent.android.ichat2.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.ichat2.data.Broadcast;
import com.longx.intelligent.android.ichat2.data.response.PaginatedOperationData;
import com.longx.intelligent.android.ichat2.databinding.FragmentBroadcastsBinding;
import com.longx.intelligent.android.ichat2.databinding.LayoutBroadcastRecyclerHeaderBinding;
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
    private LayoutBroadcastRecyclerHeaderBinding headerBinding;
    private MainActivity mainActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainActivity = getMainActivity();
        binding = FragmentBroadcastsBinding.inflate(inflater, container, false);
        headerBinding = LayoutBroadcastRecyclerHeaderBinding.inflate(inflater, container, false);
        setupFab();
        setupRecyclerView();
        loadHistoryBroadcastsData();
        if(mainActivity != null && mainActivity.isNeedInitFetchBroadcast()) fetchAndRefreshBroadcasts();
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
                if(!binding.sendBroadcastFab.isExtended()) binding.sendBroadcastFab.extend();
            }

            @Override
            public void onScrollDown() {
                if(binding.sendBroadcastFab.isExtended()) binding.sendBroadcastFab.shrink();
                binding.appbar.setExpanded(false);
            }
        });
        binding.sendBroadcastFab.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), SendBroadcastActivity.class));
        });
        binding.toStartFab.setOnClickListener(v -> {
            binding.appbar.setExpanded(true);
            binding.recyclerView.scrollToStart(true);
        });
        headerBinding.reload.setOnClickListener(v -> {
            fetchAndRefreshBroadcasts();
        });
    }

    private void setupFab() {
        float fabMarginTop = WindowAndSystemUiUtil.getStatusBarHeight(requireContext()) + WindowAndSystemUiUtil.getActionBarSize(requireContext()) + requireContext().getResources().getDimension(R.dimen.fab_margin_bottom);
        float smallFabMarginTop = fabMarginTop + UiUtil.dpToPx(requireContext(), 70);
        float fabMarginEnd = requireContext().getResources().getDimension(R.dimen.fab_margin_end);
        UiUtil.setViewMargin(binding.sendBroadcastFab, 0, (int) fabMarginTop, (int) fabMarginEnd, 0);
        UiUtil.setViewMargin(binding.toStartFab, 0, (int) smallFabMarginTop, (int) fabMarginEnd, 0);
    }

    private void setupRecyclerView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, true);
        binding.recyclerView.setLayoutManager(layoutManager);
        ArrayList<BroadcastsRecyclerAdapter.ItemData> itemDataList = new ArrayList<>();
        adapter = new BroadcastsRecyclerAdapter(requireActivity(), itemDataList);
        binding.recyclerView.setAdapter(adapter);
        UiUtil.setViewHeight(headerBinding.reload, UiUtil.dpToPx(requireContext(), 172) - WindowAndSystemUiUtil.getActionBarSize(requireContext()));
        binding.recyclerView.setHeaderView(headerBinding.getRoot());
        binding.recyclerView.addOnScrollListener(new androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull androidx.recyclerview.widget.RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (layoutManager.findLastVisibleItemPosition() >= adapter.getItemCount() - 5) {
                    binding.toStartFab.show();
                } else {
                    binding.toStartFab.hide();
                }
            }
        });
    }

    private void loadHistoryBroadcastsData() {
        List<Broadcast> broadcasts = SharedPreferencesAccessor.ApiJson.Broadcasts.getAllRecords(requireContext());
        List<BroadcastsRecyclerAdapter.ItemData> itemDataList = new ArrayList<>();
        broadcasts.forEach(broadcast -> {
            itemDataList.add(new BroadcastsRecyclerAdapter.ItemData(broadcast));
        });
        adapter.addItemsAndShow(itemDataList);
    }

    private void saveHistoryBroadcastsData(List<Broadcast> broadcasts, boolean clearHistory){
        if(clearHistory){
            SharedPreferencesAccessor.ApiJson.Broadcasts.clearRecords(requireContext());
        }
        SharedPreferencesAccessor.ApiJson.Broadcasts.addRecords(requireContext(), broadcasts);
    }

    private void fetchAndRefreshBroadcasts(){
        BroadcastApiCaller.fetchBroadcastsLimit(requireActivity(), 1, 50, new RetrofitApiCaller.BaseCommonYier<PaginatedOperationData<Broadcast>>(requireActivity()){

            @Override
            public void start(Call<PaginatedOperationData<Broadcast>> call) {
                super.start(call);
                headerBinding.reloadIndicator.setVisibility(View.VISIBLE);
            }

            @Override
            public void complete(Call<PaginatedOperationData<Broadcast>> call) {
                super.complete(call);
                headerBinding.reloadIndicator.setVisibility(View.GONE);
            }

            @Override
            public void ok(PaginatedOperationData<Broadcast> data, Response<PaginatedOperationData<Broadcast>> row, Call<PaginatedOperationData<Broadcast>> call) {
                super.ok(data, row, call);
                List<Broadcast> broadcastList = data.getData();
                saveHistoryBroadcastsData(broadcastList, true);
                List<BroadcastsRecyclerAdapter.ItemData> itemDataList = new ArrayList<>();
                broadcastList.forEach(broadcast -> {
                    itemDataList.add(new BroadcastsRecyclerAdapter.ItemData(broadcast));
                });
                adapter.clearAndShow();
                adapter.addItemsAndShow(itemDataList);
                binding.recyclerView.scrollToStart(false);
                if(mainActivity != null) mainActivity.setNeedInitFetchBroadcast(false);
            }
        });
    }
}