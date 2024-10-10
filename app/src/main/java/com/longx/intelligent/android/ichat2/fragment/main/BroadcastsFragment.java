package com.longx.intelligent.android.ichat2.fragment.main;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Parcelable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.activity.BroadcastInteractionsActivity;
import com.longx.intelligent.android.ichat2.activity.InstanceStateKeys;
import com.longx.intelligent.android.ichat2.activity.SendBroadcastActivity;
import com.longx.intelligent.android.ichat2.adapter.BroadcastsRecyclerAdapter;
import com.longx.intelligent.android.ichat2.behavior.MessageDisplayer;
import com.longx.intelligent.android.ichat2.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.ichat2.data.Broadcast;
import com.longx.intelligent.android.ichat2.data.response.OperationStatus;
import com.longx.intelligent.android.ichat2.data.response.PaginatedOperationData;
import com.longx.intelligent.android.ichat2.databinding.FragmentBroadcastsBinding;
import com.longx.intelligent.android.ichat2.databinding.RecyclerFooterBroadcastBinding;
import com.longx.intelligent.android.ichat2.databinding.RecyclerHeaderBroadcastBinding;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.BroadcastApiCaller;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.ichat2.ui.BadgeDisplayer;
import com.longx.intelligent.android.ichat2.ui.glide.GlideApp;
import com.longx.intelligent.android.ichat2.util.ErrorLogger;
import com.longx.intelligent.android.ichat2.util.TimeUtil;
import com.longx.intelligent.android.ichat2.util.UiUtil;
import com.longx.intelligent.android.ichat2.util.Utils;
import com.longx.intelligent.android.ichat2.util.WindowAndSystemUiUtil;
import com.longx.intelligent.android.ichat2.value.Constants;
import com.longx.intelligent.android.ichat2.yier.BroadcastDeletedYier;
import com.longx.intelligent.android.ichat2.yier.BroadcastReloadYier;
import com.longx.intelligent.android.ichat2.yier.BroadcastUpdateYier;
import com.longx.intelligent.android.ichat2.yier.GlobalYiersHolder;
import com.longx.intelligent.android.ichat2.yier.NewContentBadgeDisplayYier;
import com.longx.intelligent.android.lib.recyclerview.RecyclerView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import q.rorbin.badgeview.Badge;
import retrofit2.Call;
import retrofit2.Response;

public class BroadcastsFragment extends BaseMainFragment implements BroadcastReloadYier, BroadcastDeletedYier, BroadcastUpdateYier, NewContentBadgeDisplayYier {
    private FragmentBroadcastsBinding binding;
    private BroadcastsRecyclerAdapter adapter;
    private RecyclerHeaderBroadcastBinding headerBinding;
    private RecyclerFooterBroadcastBinding footerBinding;
    private boolean stopFetchNextPage;
    private CountDownLatch NEXT_PAGE_LATCH;
    private Call<PaginatedOperationData<Broadcast>> nextPageCall;
    private boolean willToStart;
    private Badge newInteractionsBadge;

    public static boolean needInitFetchBroadcast = true;
    public static boolean needFetchNewBroadcasts;
    public static boolean needReFetchBroadcast;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentBroadcastsBinding.inflate(inflater, container, false);
        headerBinding = RecyclerHeaderBroadcastBinding.inflate(inflater, container, false);
        footerBinding = RecyclerFooterBroadcastBinding.inflate(inflater, container, false);
        setupFab();
        setupRecyclerView();
        restoreState(savedInstanceState);
        showOrHideBroadcastReloadedTime();
        setupBadge();
        GlobalYiersHolder.holdYier(requireContext(), BroadcastReloadYier.class, this);
        GlobalYiersHolder.holdYier(requireContext(), BroadcastDeletedYier.class, this);
        GlobalYiersHolder.holdYier(requireContext(), BroadcastUpdateYier.class, this);
        GlobalYiersHolder.holdYier(requireContext(), NewContentBadgeDisplayYier.class, this, ID.BROADCAST_LIKES);
        GlobalYiersHolder.holdYier(requireContext(), NewContentBadgeDisplayYier.class, this, ID.BROADCAST_COMMENTS);
        if(needInitFetchBroadcast) {
            fetchAndRefreshBroadcasts(true);
        }else if(needReFetchBroadcast) {
            fetchAndRefreshBroadcasts(false);
        }else if(needFetchNewBroadcasts) {
            fetchNews();
        }
        return binding.getRoot();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        GlobalYiersHolder.removeYier(requireContext(), BroadcastReloadYier.class, this);
        GlobalYiersHolder.removeYier(requireContext(), BroadcastDeletedYier.class, this);
        GlobalYiersHolder.removeYier(requireContext(), BroadcastUpdateYier.class, this);
        GlobalYiersHolder.removeYier(requireContext(), NewContentBadgeDisplayYier.class, this, ID.BROADCAST_LIKES);
        GlobalYiersHolder.removeYier(requireContext(), NewContentBadgeDisplayYier.class, this, ID.BROADCAST_COMMENTS);
    }

    private void restoreState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            int appBarVerticalOffset = savedInstanceState.getInt(InstanceStateKeys.BroadcastFragment.APP_BAR_LAYOUT_STATE, 0);
            binding.appbar.post(() -> {
                binding.appbar.setExpanded(appBarVerticalOffset == 0, false);
                if(appBarVerticalOffset != 0){
                    binding.sendBroadcastFab.show();
                    ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) binding.toolbar.getLayoutParams();
                    layoutParams.setMargins(layoutParams.leftMargin, layoutParams.topMargin, UiUtil.dpToPx(requireContext(), 0), layoutParams.bottomMargin);
                    binding.toolbar.requestLayout();
                }else {
                    binding.sendBroadcastFab.hide();
                }
            });
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
            String headerErrorText = savedInstanceState.getString(InstanceStateKeys.BroadcastFragment.HEADER_ERROR_TEXT);
            if(headerErrorText != null){
                headerBinding.loadFailedView.setVisibility(View.VISIBLE);
                headerBinding.loadFailedText.setText(headerErrorText);
            }
            String footerErrorText = savedInstanceState.getString(InstanceStateKeys.BroadcastFragment.FOOTER_ERROR_TEXT);
            if(footerErrorText != null){
                footerBinding.loadFailedView.setVisibility(View.VISIBLE);
                footerBinding.loadFailedText.setText(footerErrorText);
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
        outState.putBoolean(InstanceStateKeys.BroadcastFragment.STOP_FETCH_NEXT_PAGE, stopFetchNextPage);
        if(adapter != null){
            List<BroadcastsRecyclerAdapter.ItemData> itemDataList = adapter.getItemDataList();
            ArrayList<Broadcast> broadcasts = new ArrayList<>();
            itemDataList.forEach(itemData -> {
                broadcasts.add(itemData.getBroadcast());
            });
            outState.putParcelableArrayList(InstanceStateKeys.BroadcastFragment.HISTORY_BROADCASTS_DATA, broadcasts);
        }
        if(headerBinding.loadFailedView.getVisibility() == View.VISIBLE){
            outState.putString(InstanceStateKeys.BroadcastFragment.HEADER_ERROR_TEXT, headerBinding.loadFailedText.getText().toString());
        }else {
            outState.putString(InstanceStateKeys.BroadcastFragment.HEADER_ERROR_TEXT, null);
        }
        if(footerBinding.loadFailedView.getVisibility() == View.VISIBLE){
            outState.putString(InstanceStateKeys.BroadcastFragment.FOOTER_ERROR_TEXT, footerBinding.loadFailedText.getText().toString());
        }else {
            outState.putString(InstanceStateKeys.BroadcastFragment.FOOTER_ERROR_TEXT, null);
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
        binding.recyclerView.addOnScrollListener(new androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull androidx.recyclerview.widget.RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE) {
                    GlideApp.with(requireContext()).resumeRequests();
                } else if (newState == androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_DRAGGING || newState == androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_SETTLING) {
                    GlideApp.with(requireContext()).pauseRequests();
                }
            }

            @Override
            public void onScrolled(@NonNull androidx.recyclerview.widget.RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (Math.abs(dy) < 190) {
                    GlideApp.with(requireContext()).resumeRequests();
                }
            }
        });
        binding.recyclerView.addOnApproachEdgeYier(7, new RecyclerView.OnApproachEdgeYier() {
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
        binding.sendBroadcastFab.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), SendBroadcastActivity.class));
        });
        binding.toStartFab.setOnClickListener(v -> {
            toStart();
        });
        headerBinding.load.setOnClickListener(v -> {
            fetchAndRefreshBroadcasts(false);
        });
        binding.recyclerView.addOnScrollListener(new androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull androidx.recyclerview.widget.RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (willToStart) {
                        toStart();
                        willToStart = false;
                    }
                }
            }
        });
        binding.appbar.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            if (Math.abs(verticalOffset) == appBarLayout.getTotalScrollRange()) {
                binding.sendBroadcastFab.show();
//                binding.toolbar.getMenu().findItem(R.id.send_broadcast).setVisible(false);
                binding.sendBroadcastButton.setVisibility(View.GONE);
                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) binding.toolbar.getLayoutParams();
                layoutParams.setMargins(layoutParams.leftMargin, layoutParams.topMargin, UiUtil.dpToPx(requireContext(), 0), layoutParams.bottomMargin);
                binding.toStartFab.show();
            } else if (verticalOffset == 0) {
                binding.sendBroadcastFab.hide();
//                binding.toolbar.getMenu().findItem(R.id.send_broadcast).setVisible(true);
                binding.sendBroadcastButton.setVisibility(View.VISIBLE);
                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) binding.toolbar.getLayoutParams();
                layoutParams.setMargins(layoutParams.leftMargin, layoutParams.topMargin, UiUtil.dpToPx(requireContext(), 148.5F), layoutParams.bottomMargin);
                binding.toStartFab.hide();
            } else {
                binding.sendBroadcastFab.hide();
//                binding.toolbar.getMenu().findItem(R.id.send_broadcast).setVisible(true);
                binding.sendBroadcastButton.setVisibility(View.VISIBLE);
                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) binding.toolbar.getLayoutParams();
                layoutParams.setMargins(layoutParams.leftMargin, layoutParams.topMargin, UiUtil.dpToPx(requireContext(), 148.5F), layoutParams.bottomMargin);
                binding.toStartFab.hide();
            }
        });
//        binding.toolbar.setOnMenuItemClickListener(item -> {
//            if(item.getItemId() == R.id.send_broadcast){
//                startActivity(new Intent(requireContext(), SendBroadcastActivity.class));
//            }
//            return false;
//        });
        binding.sendBroadcastButton.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), SendBroadcastActivity.class));
        });
        View actionView = binding.toolbar.getMenu().findItem(R.id.interaction_notification).getActionView();
        if(actionView != null){
            actionView.findViewById(R.id.image_button).setOnClickListener(v -> {
                startActivity(new Intent(requireContext(), BroadcastInteractionsActivity.class));
            });
        }
    }

    public void toStart() {
        if(binding.recyclerView.getScrollState() == RecyclerView.SCROLL_STATE_IDLE) {
            binding.appbar.setExpanded(true);
            binding.recyclerView.scrollToStart(true);
        }else {
            willToStart = true;
        }
    }

    private void setupFab() {
        float fabMarginTop = WindowAndSystemUiUtil.getStatusBarHeight(requireContext()) + WindowAndSystemUiUtil.getActionBarHeight(requireContext()) + requireContext().getResources().getDimension(R.dimen.fab_margin_bottom);
        float smallFabMarginTop = fabMarginTop + UiUtil.dpToPx(requireContext(), 70);
        float fabMarginEnd = requireContext().getResources().getDimension(R.dimen.fab_margin_end);
        UiUtil.setViewMargin(binding.sendBroadcastFab, 0, (int) fabMarginTop, (int) fabMarginEnd, 0);
        UiUtil.setViewMargin(binding.toStartFab, 0, (int) smallFabMarginTop, (int) fabMarginEnd, 0);
    }

    private void setupRecyclerView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, true);
        binding.recyclerView.setLayoutManager(layoutManager);
        ArrayList<BroadcastsRecyclerAdapter.ItemData> itemDataList = new ArrayList<>();
        adapter = new BroadcastsRecyclerAdapter((AppCompatActivity) requireActivity(), binding.recyclerView, itemDataList, this);
        binding.recyclerView.setAdapter(adapter);
        int headerItemHeight = UiUtil.dpToPx(requireContext(), 172) - WindowAndSystemUiUtil.getActionBarHeight(requireContext());
        UiUtil.setViewHeight(headerBinding.load, headerItemHeight);
        UiUtil.setViewHeight(headerBinding.loadIndicator, headerItemHeight);
        UiUtil.setViewHeight(headerBinding.loadFailedView, headerItemHeight);
        UiUtil.setViewHeight(headerBinding.noBroadcastView, headerItemHeight);
        binding.recyclerView.setHeaderView(headerBinding.getRoot());
        binding.recyclerView.setFooterView(footerBinding.getRoot());
    }

    private void setupBadge() {
        View actionView = binding.toolbar.getMenu().findItem(R.id.interaction_notification).getActionView();
        if(actionView != null){
            ImageButton imageButton = actionView.findViewById(R.id.image_button);
            newInteractionsBadge = BadgeDisplayer.initBadge(requireContext(), imageButton, 0, Gravity.END | Gravity.TOP);
        }
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

    private void fetchAndRefreshBroadcasts(boolean init){
        stopFetchNextPage = true;
        if(nextPageCall != null) {
            breakFetchNextPage(nextPageCall);
        }
        BroadcastApiCaller.fetchBroadcastsLimit(this, null, Constants.FETCH_BROADCAST_PAGE_SIZE, true, new RetrofitApiCaller.BaseCommonYier<PaginatedOperationData<Broadcast>>(){

            @Override
            public void start(Call<PaginatedOperationData<Broadcast>> call) {
                super.start(call);
                binding.recyclerView.scrollToStart(false);
                headerBinding.loadFailedView.setVisibility(View.GONE);
                headerBinding.loadFailedText.setText(null);
                headerBinding.loadIndicator.setVisibility(View.VISIBLE);
                headerBinding.noBroadcastView.setVisibility(View.GONE);
                if(!init)headerBinding.load.setVisibility(View.GONE);
                needReFetchBroadcast = true;
            }

            @Override
            public void complete(Call<PaginatedOperationData<Broadcast>> call) {
                super.complete(call);
                headerBinding.loadIndicator.setVisibility(View.GONE);
                headerBinding.load.setVisibility(View.VISIBLE);
                binding.appbar.setExpanded(true);
                binding.recyclerView.scrollToStart(false);
                needInitFetchBroadcast = false;
                needReFetchBroadcast = false;
            }

            @Override
            public void notOk(int code, String message, Response<PaginatedOperationData<Broadcast>> row, Call<PaginatedOperationData<Broadcast>> call) {
                super.notOk(code, message, row, call);
                headerBinding.loadFailedView.setVisibility(View.VISIBLE);
                headerBinding.loadFailedText.setText("HTTP 状态码异常 > " + code);
                headerBinding.noBroadcastView.setVisibility(View.GONE);
                binding.recyclerView.scrollToStart(false);
            }

            @Override
            public void failure(Throwable t, Call<PaginatedOperationData<Broadcast>> call) {
                super.failure(t, call);
                headerBinding.loadFailedView.setVisibility(View.VISIBLE);
                headerBinding.loadFailedText.setText("出错了 > " + t.getClass().getName());
                headerBinding.noBroadcastView.setVisibility(View.GONE);
                binding.recyclerView.scrollToStart(false);
            }

            @Override
            public void ok(PaginatedOperationData<Broadcast> data, Response<PaginatedOperationData<Broadcast>> raw, Call<PaginatedOperationData<Broadcast>> call) {
                super.ok(data, raw, call);
                data.commonHandleResult(requireActivity(), new int[]{-101}, () -> {
                    stopFetchNextPage = !raw.body().hasMore();
                    List<Broadcast> broadcastList = data.getData();
                    saveHistoryBroadcastsData(broadcastList, true);
                    List<BroadcastsRecyclerAdapter.ItemData> itemDataList = new ArrayList<>();
                    broadcastList.forEach(broadcast -> {
                        itemDataList.add(new BroadcastsRecyclerAdapter.ItemData(broadcast));
                    });
                    adapter.clearAndShow();
                    adapter.addItemsAndShow(itemDataList);
                    SharedPreferencesAccessor.DefaultPref.saveBroadcastReloadedTime(requireContext(), new Date());
                    showOrHideBroadcastReloadedTime();
                }, new OperationStatus.HandleResult(-102, () -> {
                    headerBinding.loadFailedView.setVisibility(View.GONE);
                    headerBinding.loadFailedText.setText(null);
                    headerBinding.loadIndicator.setVisibility(View.GONE);
                    headerBinding.noBroadcastView.setVisibility(View.VISIBLE);
                }));
            }
        });
    }

    private void showOrHideBroadcastReloadedTime() {
        Date broadcastReloadedTime = SharedPreferencesAccessor.DefaultPref.getBroadcastReloadedTime(requireContext());
        if(broadcastReloadedTime == null){
            headerBinding.reloadTime.setVisibility(View.GONE);
        }else {
            headerBinding.reloadTime.setVisibility(View.VISIBLE);
            headerBinding.reloadTime.setText("更新时间 " + TimeUtil.formatRelativeTime(broadcastReloadedTime));
        }
    }

    private synchronized void nextPage() {
        NEXT_PAGE_LATCH = new CountDownLatch(1);
        String lastBroadcastId = adapter.getItemDataList().get(adapter.getItemCount() - 1).getBroadcast().getBroadcastId();
        BroadcastApiCaller.fetchBroadcastsLimit(this, lastBroadcastId, Constants.FETCH_BROADCAST_PAGE_SIZE, true, new RetrofitApiCaller.BaseCommonYier<PaginatedOperationData<Broadcast>>() {

            @Override
            public void start(Call<PaginatedOperationData<Broadcast>> call) {
                super.start(call);
                nextPageCall = call;
                if (breakFetchNextPage(call)) return;
                footerBinding.loadFailedView.setVisibility(View.GONE);
                footerBinding.loadFailedText.setText(null);
                footerBinding.loadIndicator.setVisibility(View.VISIBLE);
                footerBinding.noBroadcastView.setVisibility(View.GONE);
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
                footerBinding.noBroadcastView.setVisibility(View.GONE);
                binding.recyclerView.scrollToEnd(false);
                stopFetchNextPage = true;
            }

            @Override
            public void failure(Throwable t, Call<PaginatedOperationData<Broadcast>> call) {
                super.failure(t, call);
                if (breakFetchNextPage(call)) return;
                footerBinding.loadFailedView.setVisibility(View.VISIBLE);
                footerBinding.loadFailedText.setText("出错了 > " + t.getClass().getName());
                footerBinding.noBroadcastView.setVisibility(View.GONE);
                binding.recyclerView.scrollToEnd(false);
                stopFetchNextPage = true;
            }

            @Override
            public void ok(PaginatedOperationData<Broadcast> data, Response<PaginatedOperationData<Broadcast>> raw, Call<PaginatedOperationData<Broadcast>> call) {
                super.ok(data, raw, call);
                data.commonHandleResult(requireActivity(), new int[]{-101, -102}, () -> {
                    if (breakFetchNextPage(call)) return;
                    stopFetchNextPage = !raw.body().hasMore();
                    List<Broadcast> broadcastList = data.getData();
                    saveHistoryBroadcastsData(broadcastList, false);
                    List<BroadcastsRecyclerAdapter.ItemData> itemDataList = new ArrayList<>();
                    broadcastList.forEach(broadcast -> {
                        itemDataList.add(new BroadcastsRecyclerAdapter.ItemData(broadcast));
                    });
                    adapter.addItemsAndShow(itemDataList);
                }, new OperationStatus.HandleResult(-102, () -> {
                    footerBinding.loadFailedView.setVisibility(View.GONE);
                    footerBinding.loadFailedText.setText(null);
                    footerBinding.loadIndicator.setVisibility(View.GONE);
                    footerBinding.noBroadcastView.setVisibility(View.VISIBLE);
                }));
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
    public void reloadBroadcast() {
        fetchAndRefreshBroadcasts(false);
    }

    @Override
    public void onBroadcastDeleted(String broadcastId) {
        adapter.removeItemAndShow(broadcastId);
        SharedPreferencesAccessor.ApiJson.Broadcasts.deleteRecord(requireContext(), broadcastId);
    }

    public void fetchNews() {
        String firstBroadcastId = adapter.getItemDataList().get(0).getBroadcast().getBroadcastId();
        BroadcastApiCaller.fetchBroadcastsLimit(this, firstBroadcastId, Constants.FETCH_BROADCAST_PAGE_SIZE, false, new RetrofitApiCaller.BaseCommonYier<PaginatedOperationData<Broadcast>>(){

            @Override
            public void start(Call<PaginatedOperationData<Broadcast>> call) {
                super.start(call);
                headerBinding.loadFailedView.setVisibility(View.GONE);
                headerBinding.loadFailedText.setText(null);
                headerBinding.loadIndicator.setVisibility(View.VISIBLE);
                headerBinding.noBroadcastView.setVisibility(View.GONE);
                headerBinding.load.setVisibility(View.GONE);
            }

            @Override
            public void complete(Call<PaginatedOperationData<Broadcast>> call) {
                super.complete(call);
                headerBinding.loadIndicator.setVisibility(View.GONE);
                headerBinding.load.setVisibility(View.VISIBLE);
                needFetchNewBroadcasts = false;
            }

            @Override
            public void notOk(int code, String message, Response<PaginatedOperationData<Broadcast>> row, Call<PaginatedOperationData<Broadcast>> call) {
                super.notOk(code, message, row, call);
                headerBinding.loadFailedView.setVisibility(View.VISIBLE);
                headerBinding.loadFailedText.setText("HTTP 状态码异常 > " + code);
                headerBinding.noBroadcastView.setVisibility(View.GONE);
            }

            @Override
            public void failure(Throwable t, Call<PaginatedOperationData<Broadcast>> call) {
                super.failure(t, call);
                headerBinding.loadFailedView.setVisibility(View.VISIBLE);
                headerBinding.loadFailedText.setText("出错了 > " + t.getClass().getName());
                headerBinding.noBroadcastView.setVisibility(View.GONE);
            }

            @Override
            public void ok(PaginatedOperationData<Broadcast> data, Response<PaginatedOperationData<Broadcast>> raw, Call<PaginatedOperationData<Broadcast>> call) {
                super.ok(data, raw, call);
                data.commonHandleResult(requireActivity(), new int[]{-101}, () -> {
                    List<Broadcast> broadcastList = data.getData();
                    saveHistoryBroadcastsData(broadcastList, false);
                    List<BroadcastsRecyclerAdapter.ItemData> itemDataList = new ArrayList<>();
                    broadcastList.forEach(broadcast -> {
                        itemDataList.add(new BroadcastsRecyclerAdapter.ItemData(broadcast));
                    });
                    adapter.addItemsToStartAndShow(itemDataList);
                    SharedPreferencesAccessor.DefaultPref.saveBroadcastReloadedTime(requireContext(), new Date());
                    showOrHideBroadcastReloadedTime();
                    if(raw.body().hasMore()){
                        fetchNews();
                    }
                }, new OperationStatus.HandleResult(-102, () -> {
                    ErrorLogger.log("没有获取到新广播");
                    MessageDisplayer.showToast(getContext(), "没有获取到新广播", Toast.LENGTH_LONG);
                }));
            }
        });
    }

    @Override
    public void updateOneBroadcast(Broadcast newBroadcast) {
        if(adapter != null) adapter.updateOneBroadcast(newBroadcast, true);
        SharedPreferencesAccessor.ApiJson.Broadcasts.updateRecord(requireContext(), newBroadcast);
    }

    @Override
    public void showNewContentBadge(ID id, int newContentCount) {
        if(id.equals(ID.BROADCAST_LIKES) || id.equals(ID.BROADCAST_COMMENTS) || id.equals((ID.BROADCAST_REPLIES))){
            newInteractionsBadge.setBadgeNumber(newContentCount);
        }
    }
}