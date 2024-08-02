package com.longx.intelligent.android.ichat2.fragment.main;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.appbar.AppBarLayout;
import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.activity.InstanceStateKeys;
import com.longx.intelligent.android.ichat2.activity.MainActivity;
import com.longx.intelligent.android.ichat2.activity.SendBroadcastActivity;
import com.longx.intelligent.android.ichat2.adapter.BroadcastsRecyclerAdapter;
import com.longx.intelligent.android.ichat2.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.ichat2.data.Broadcast;
import com.longx.intelligent.android.ichat2.data.response.PaginatedOperationData;
import com.longx.intelligent.android.ichat2.databinding.FragmentBroadcastsBinding;
import com.longx.intelligent.android.ichat2.databinding.LayoutBroadcastRecyclerFooterBinding;
import com.longx.intelligent.android.ichat2.databinding.LayoutBroadcastRecyclerHeaderBinding;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.BroadcastApiCaller;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.ichat2.util.ErrorLogger;
import com.longx.intelligent.android.ichat2.util.UiUtil;
import com.longx.intelligent.android.ichat2.util.Utils;
import com.longx.intelligent.android.ichat2.util.WindowAndSystemUiUtil;
import com.longx.intelligent.android.ichat2.value.Constants;
import com.longx.intelligent.android.ichat2.yier.BroadcastReloadYier;
import com.longx.intelligent.android.ichat2.yier.GlobalYiersHolder;
import com.longx.intelligent.android.lib.recyclerview.RecyclerView;
import com.longx.intelligent.android.lib.recyclerview.WrappableRecyclerViewAdapter;
import com.xcheng.retrofit.CompletableCall;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import retrofit2.Call;
import retrofit2.Response;

public class BroadcastsFragment extends BaseMainFragment implements BroadcastReloadYier {
    private FragmentBroadcastsBinding binding;
    private BroadcastsRecyclerAdapter adapter;
    private LayoutBroadcastRecyclerHeaderBinding headerBinding;
    private LayoutBroadcastRecyclerFooterBinding footerBinding;
    private MainActivity mainActivity;
    private int pn;
    private boolean stopFetchNextPage;
    private CountDownLatch NEXT_PAGE_LATCH;
    private Call<PaginatedOperationData<Broadcast>> nextPageCall;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainActivity = getMainActivity();
        binding = FragmentBroadcastsBinding.inflate(inflater, container, false);
        headerBinding = LayoutBroadcastRecyclerHeaderBinding.inflate(inflater, container, false);
        footerBinding = LayoutBroadcastRecyclerFooterBinding.inflate(inflater, container, false);
        setupFab();
        setupRecyclerView();
        restoreState(savedInstanceState);
        GlobalYiersHolder.holdYier(requireContext(), BroadcastReloadYier.class, this);
        if(mainActivity != null && mainActivity.isNeedInitFetchBroadcast()) fetchAndRefreshBroadcasts();
        return binding.getRoot();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        GlobalYiersHolder.removeYier(requireContext(), BroadcastReloadYier.class, this);
    }

    private void restoreState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            int appBarVerticalOffset = savedInstanceState.getInt(InstanceStateKeys.BroadcastFragment.APP_BAR_LAYOUT_STATE, 0);
            binding.appbar.post(() -> binding.appbar.setExpanded(appBarVerticalOffset == 0, false));
            boolean isSendBroadcastFabExpanded = savedInstanceState.getBoolean(InstanceStateKeys.BroadcastFragment.SEND_BROADCAST_FAB_EXPANDED_STATE, true);
            if (isSendBroadcastFabExpanded) {
                binding.sendBroadcastFab.extend();
            } else {
                binding.sendBroadcastFab.shrink();
            }
            boolean isToStartFabShown = savedInstanceState.getBoolean(InstanceStateKeys.BroadcastFragment.TO_START_FAB_VISIBILITY_STATE, false);
            if(isToStartFabShown){
                binding.toStartFab.show();
            }else {
                binding.toStartFab.hide();
            }
            pn = savedInstanceState.getInt(InstanceStateKeys.BroadcastFragment.CURRENT_PN);
            stopFetchNextPage = savedInstanceState.getBoolean(InstanceStateKeys.BroadcastFragment.STOP_FETCH_NEXT_PAGE, false);
            ArrayList<Parcelable> parcelableArrayList = savedInstanceState.getParcelableArrayList(InstanceStateKeys.BroadcastFragment.HISTORY_BROADCASTS_DATA);
            if(parcelableArrayList != null) {
                ArrayList<Broadcast> broadcasts = Utils.parseParcelableArray(parcelableArrayList);
                List<BroadcastsRecyclerAdapter.ItemData> itemDataList = new ArrayList<>();
                broadcasts.forEach(broadcast -> {
                    itemDataList.add(new BroadcastsRecyclerAdapter.ItemData(broadcast));
                });
                adapter.addItemsAndShow(itemDataList);
            }
        }else {
            loadHistoryBroadcastsData();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupToolbarNavIcon(binding.toolbar);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) binding.appbar.getLayoutParams();
        AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();
        if (behavior != null) {
            int appBarVerticalOffset = behavior.getTopAndBottomOffset();
            outState.putInt(InstanceStateKeys.BroadcastFragment.APP_BAR_LAYOUT_STATE, appBarVerticalOffset);
        }
        outState.putBoolean(InstanceStateKeys.BroadcastFragment.SEND_BROADCAST_FAB_EXPANDED_STATE, binding.sendBroadcastFab.isExtended());
        outState.putBoolean(InstanceStateKeys.BroadcastFragment.TO_START_FAB_VISIBILITY_STATE, binding.toStartFab.isShown());
        outState.putInt(InstanceStateKeys.BroadcastFragment.CURRENT_PN, pn);
        outState.putBoolean(InstanceStateKeys.BroadcastFragment.STOP_FETCH_NEXT_PAGE, stopFetchNextPage);
        if(adapter != null){
            List<BroadcastsRecyclerAdapter.ItemData> itemDataList = adapter.getItemDataList();
            ArrayList<Broadcast> broadcasts = new ArrayList<>();
            itemDataList.forEach(itemData -> {
                broadcasts.add(itemData.getBroadcast());
            });
            outState.putParcelableArrayList(InstanceStateKeys.BroadcastFragment.HISTORY_BROADCASTS_DATA, broadcasts);
        }
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
        binding.recyclerView.addOnApproachEdgeYier(5, new RecyclerView.OnApproachEdgeYier() {
            @Override
            public void onApproachStart() {

            }

            @Override
            public void onApproachEnd() {
                if(!stopFetchNextPage) {
                    if(NEXT_PAGE_LATCH == null || NEXT_PAGE_LATCH.getCount() == 0) {
                        nextPage();
                    }
                }
            }
        });
        binding.recyclerView.addOnThresholdScrollUpDownYier(new RecyclerView.OnThresholdScrollUpDownYier(50){
            @Override
            public void onScrollUp() {
                if(!binding.sendBroadcastFab.isExtended()) binding.sendBroadcastFab.extend();
            }

            @Override
            public void onScrollDown() {
                if(binding.sendBroadcastFab.isExtended()) binding.sendBroadcastFab.shrink();
                if(!binding.recyclerView.isApproachEnd(5)) binding.appbar.setExpanded(false);
            }
        });
        binding.recyclerView.addOnScrollListener(new androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull androidx.recyclerview.widget.RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (binding.recyclerView.isApproachStart(5)) {
                    binding.toStartFab.hide();
                } else {
                    binding.toStartFab.show();
                }
            }
        });
        binding.sendBroadcastFab.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), SendBroadcastActivity.class));
        });
        binding.toStartFab.setOnClickListener(v -> {
            binding.appbar.setExpanded(true);
            binding.recyclerView.scrollToStart(true);
        });
        headerBinding.load.setOnClickListener(v -> {
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
        UiUtil.setViewHeight(headerBinding.load, UiUtil.dpToPx(requireContext(), 172) - WindowAndSystemUiUtil.getActionBarSize(requireContext()));
        binding.recyclerView.setHeaderView(headerBinding.getRoot());
        binding.recyclerView.setFooterView(footerBinding.getRoot());
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
        pn = 1;
        stopFetchNextPage = true;
        if(nextPageCall != null) {
            breakFetchNextPage(nextPageCall);
        }
        BroadcastApiCaller.fetchBroadcastsLimit(this, null, Constants.FETCH_BROADCAST_PAGE_SIZE, new RetrofitApiCaller.BaseCommonYier<PaginatedOperationData<Broadcast>>(){

            @Override
            public void start(Call<PaginatedOperationData<Broadcast>> call) {
                super.start(call);
                headerBinding.loadFailedView.setVisibility(View.GONE);
                headerBinding.loadFailedText.setText(null);
                headerBinding.loadIndicator.setVisibility(View.VISIBLE);
            }

            @Override
            public void complete(Call<PaginatedOperationData<Broadcast>> call) {
                super.complete(call);
                headerBinding.loadIndicator.setVisibility(View.GONE);
            }

            @Override
            public void notOk(int code, String message, Response<PaginatedOperationData<Broadcast>> row, Call<PaginatedOperationData<Broadcast>> call) {
                super.notOk(code, message, row, call);
                headerBinding.loadFailedView.setVisibility(View.VISIBLE);
                headerBinding.loadFailedText.setText("HTTP 状态码异常 > " + code);
                binding.recyclerView.scrollToStart(false);
            }

            @Override
            public void failure(Throwable t, Call<PaginatedOperationData<Broadcast>> call) {
                super.failure(t, call);
                headerBinding.loadFailedView.setVisibility(View.VISIBLE);
                headerBinding.loadFailedText.setText("出错了 > " + t.getClass().getName());
                binding.recyclerView.scrollToStart(false);
            }

            @Override
            public void ok(PaginatedOperationData<Broadcast> data, Response<PaginatedOperationData<Broadcast>> row, Call<PaginatedOperationData<Broadcast>> call) {
                super.ok(data, row, call);
                stopFetchNextPage = !row.body().hasMore();
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

    private synchronized void nextPage() {
        NEXT_PAGE_LATCH = new CountDownLatch(1);
        pn ++;
        String lastBroadcastId = adapter.getItemDataList().get(adapter.getItemCount() - 1).getBroadcast().getBroadcastId();
        BroadcastApiCaller.fetchBroadcastsLimit(this, lastBroadcastId, Constants.FETCH_BROADCAST_PAGE_SIZE, new RetrofitApiCaller.BaseCommonYier<PaginatedOperationData<Broadcast>>() {

            @Override
            public void start(Call<PaginatedOperationData<Broadcast>> call) {
                super.start(call);
                nextPageCall = call;
                if (breakFetchNextPage(call)) return;
                footerBinding.loadFailedView.setVisibility(View.GONE);
                footerBinding.loadFailedText.setText(null);
                footerBinding.loadIndicator.setVisibility(View.VISIBLE);
            }

            @Override
            public void complete(Call<PaginatedOperationData<Broadcast>> call) {
                super.complete(call);
                if (breakFetchNextPage(call)) return;
                footerBinding.loadIndicator.setVisibility(View.GONE);
                NEXT_PAGE_LATCH.countDown();
            }

            @Override
            public void notOk(int code, String message, Response<PaginatedOperationData<Broadcast>> row, Call<PaginatedOperationData<Broadcast>> call) {
                super.notOk(code, message, row, call);
                if (breakFetchNextPage(call)) return;
                footerBinding.loadFailedView.setVisibility(View.VISIBLE);
                footerBinding.loadFailedText.setText("HTTP 状态码异常 > " + code);
                binding.recyclerView.scrollToEnd(false);
                stopFetchNextPage = true;
            }

            @Override
            public void failure(Throwable t, Call<PaginatedOperationData<Broadcast>> call) {
                super.failure(t, call);
                if (breakFetchNextPage(call)) return;
                footerBinding.loadFailedView.setVisibility(View.VISIBLE);
                footerBinding.loadFailedText.setText("出错了 > " + t.getClass().getName());
                binding.recyclerView.scrollToEnd(false);
                stopFetchNextPage = true;
            }

            @Override
            public void ok(PaginatedOperationData<Broadcast> data, Response<PaginatedOperationData<Broadcast>> row, Call<PaginatedOperationData<Broadcast>> call) {
                super.ok(data, row, call);
                if (breakFetchNextPage(call)) return;
                stopFetchNextPage = !row.body().hasMore();
                List<Broadcast> broadcastList = data.getData();
                saveHistoryBroadcastsData(broadcastList, false);
                List<BroadcastsRecyclerAdapter.ItemData> itemDataList = new ArrayList<>();
                broadcastList.forEach(broadcast -> {
                    itemDataList.add(new BroadcastsRecyclerAdapter.ItemData(broadcast));
                });
                adapter.addItemsAndShow(itemDataList);
            }
        });
    }

    private boolean breakFetchNextPage(Call<PaginatedOperationData<Broadcast>> call) {
        if(stopFetchNextPage) {
            call.cancel();
            footerBinding.loadIndicator.setVisibility(View.GONE);
            if(NEXT_PAGE_LATCH != null && NEXT_PAGE_LATCH.getCount() == 1) NEXT_PAGE_LATCH.countDown();
            return true;
        }
        return false;
    }

    @Override
    public void onBroadcastReload() {
        fetchAndRefreshBroadcasts();
    }
}