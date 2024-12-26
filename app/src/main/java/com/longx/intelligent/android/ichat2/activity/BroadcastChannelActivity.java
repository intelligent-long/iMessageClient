package com.longx.intelligent.android.ichat2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.appbar.AppBarLayout;
import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.activity.helper.BaseActivity;
import com.longx.intelligent.android.ichat2.adapter.BroadcastsRecyclerAdapter;
import com.longx.intelligent.android.ichat2.behaviorcomponents.MessageDisplayer;
import com.longx.intelligent.android.ichat2.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.ichat2.data.Broadcast;
import com.longx.intelligent.android.ichat2.data.Channel;
import com.longx.intelligent.android.ichat2.data.response.OperationStatus;
import com.longx.intelligent.android.ichat2.data.response.PaginatedOperationData;
import com.longx.intelligent.android.ichat2.databinding.ActivityBroadcastChannelBinding;
import com.longx.intelligent.android.ichat2.databinding.RecyclerFooterBroadcastBinding;
import com.longx.intelligent.android.ichat2.databinding.RecyclerHeaderBroadcastBinding;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.BroadcastApiCaller;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.ichat2.ui.glide.GlideApp;
import com.longx.intelligent.android.ichat2.util.ErrorLogger;
import com.longx.intelligent.android.ichat2.util.TimeUtil;
import com.longx.intelligent.android.ichat2.util.UiUtil;
import com.longx.intelligent.android.ichat2.util.WindowAndSystemUiUtil;
import com.longx.intelligent.android.ichat2.value.Constants;
import com.longx.intelligent.android.ichat2.yier.BroadcastDeletedYier;
import com.longx.intelligent.android.ichat2.yier.BroadcastFetchNewsYier;
import com.longx.intelligent.android.ichat2.yier.BroadcastReloadYier;
import com.longx.intelligent.android.ichat2.yier.BroadcastUpdateYier;
import com.longx.intelligent.android.ichat2.yier.GlobalYiersHolder;
import com.longx.intelligent.android.ichat2.yier.OnSetChannelBroadcastExcludeYier;
import com.longx.intelligent.android.lib.recyclerview.RecyclerView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import retrofit2.Call;
import retrofit2.Response;

public class BroadcastChannelActivity extends BaseActivity implements BroadcastReloadYier, BroadcastDeletedYier, BroadcastFetchNewsYier, BroadcastUpdateYier, OnSetChannelBroadcastExcludeYier {
    private ActivityBroadcastChannelBinding binding;
    private RecyclerHeaderBroadcastBinding headerBinding;
    private RecyclerFooterBroadcastBinding footerBinding;
    private BroadcastsRecyclerAdapter adapter;
    private Channel channel;
    private boolean willToStart;
    private boolean stopFetchNextPage;
    private Call<PaginatedOperationData<Broadcast>> nextPageCall;
    private CountDownLatch NEXT_PAGE_LATCH;
    private int appBarExtendedState;
    private Date broadcastReloadedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBroadcastChannelBinding.inflate(getLayoutInflater());
        headerBinding = RecyclerHeaderBroadcastBinding.inflate(getLayoutInflater(), binding.getRoot(), false);
        footerBinding = RecyclerFooterBroadcastBinding.inflate(getLayoutInflater(), binding.getRoot(), false);
        setContentView(binding.getRoot());
        setupDefaultBackNavigation(binding.toolbar);
        intentData();
        setupToolbar();
        setupFab();
        setupRecyclerView();
        showContent();
        fetchAndRefreshBroadcasts();
        setupYiers();
        GlobalYiersHolder.holdYier(this, BroadcastReloadYier.class, this);
        GlobalYiersHolder.holdYier(this, BroadcastDeletedYier.class, this);
        GlobalYiersHolder.holdYier(this, BroadcastFetchNewsYier.class, this);
        GlobalYiersHolder.holdYier(this, BroadcastUpdateYier.class, this);
        GlobalYiersHolder.holdYier(this, OnSetChannelBroadcastExcludeYier.class, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GlobalYiersHolder.removeYier(this, BroadcastReloadYier.class, this);
        GlobalYiersHolder.removeYier(this, BroadcastDeletedYier.class, this);
        GlobalYiersHolder.removeYier(this, BroadcastFetchNewsYier.class, this);
        GlobalYiersHolder.removeYier(this, BroadcastUpdateYier.class, this);
        GlobalYiersHolder.removeYier(this, OnSetChannelBroadcastExcludeYier.class, this);
    }

    private void intentData() {
        channel = getIntent().getParcelableExtra(ExtraKeys.CHANNEL);
    }

    private void setupToolbar() {
        String currentUserIchatId = SharedPreferencesAccessor.UserProfilePref.getCurrentUserProfile(this).getIchatId();
        MenuItem menuItem = binding.toolbar.getMenu().findItem(R.id.send_broadcast);
        menuItem.setVisible(currentUserIchatId.equals(channel.getIchatId()));
    }

    private void setupFab() {
        float smallFabMarginTop = WindowAndSystemUiUtil.getStatusBarHeight(this) + WindowAndSystemUiUtil.getActionBarHeight(this);
        float fabMarginEnd = getResources().getDimension(R.dimen.fab_margin_end);
        UiUtil.setViewMargin(binding.toStartFab, 0, (int) smallFabMarginTop, (int) fabMarginEnd, 0);
        binding.toStartFab.setVisibility(View.GONE);
    }

    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
        binding.recyclerView.setLayoutManager(layoutManager);
        ArrayList<BroadcastsRecyclerAdapter.ItemData> itemDataList = new ArrayList<>();
        adapter = new BroadcastsRecyclerAdapter(this, binding.recyclerView, itemDataList, this);
        binding.recyclerView.setAdapter(adapter);
        int headerItemHeight = UiUtil.dpToPx(this, 172) - WindowAndSystemUiUtil.getActionBarHeight(this);
        UiUtil.setViewHeight(headerBinding.load, headerItemHeight);
        UiUtil.setViewHeight(headerBinding.loadIndicator, headerItemHeight);
        UiUtil.setViewHeight(headerBinding.loadFailedView, headerItemHeight);
        UiUtil.setViewHeight(headerBinding.noBroadcastView, headerItemHeight);
        binding.recyclerView.setHeaderView(headerBinding.getRoot());
        binding.recyclerView.setFooterView(footerBinding.getRoot());
    }

    private void showContent() {
        binding.toolbar.setTitle(channel.getName() + " 的广播");
    }

    private void setupYiers(){
        binding.recyclerView.addOnScrollListener(new androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull androidx.recyclerview.widget.RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE) {
                    GlideApp.with(BroadcastChannelActivity.this).resumeRequests();
                } else if (newState == androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_DRAGGING || newState == androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_SETTLING) {
                    GlideApp.with(BroadcastChannelActivity.this).pauseRequests();
                }
            }

            @Override
            public void onScrolled(@NonNull androidx.recyclerview.widget.RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (Math.abs(dy) < 190) {
                    GlideApp.with(BroadcastChannelActivity.this).resumeRequests();
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
            }

            @Override
            public void onScrollDown() {
                if(!binding.recyclerView.isApproachEnd(5)) binding.appbar.setExpanded(false);
            }
        });
        binding.appbar.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            if (Math.abs(verticalOffset) == appBarLayout.getTotalScrollRange()) {
                appBarExtendedState = 2;
                binding.toStartFab.show();
            } else if (verticalOffset == 0) {
                appBarExtendedState = 0;
                binding.toStartFab.hide();
            } else {
                appBarExtendedState = 1;
                binding.toStartFab.hide();
            }
        });
        binding.toStartFab.setOnClickListener(v -> {
            toStart();
        });
        headerBinding.load.setOnClickListener(v -> {
            fetchAndRefreshBroadcasts();
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
        binding.toolbar.getMenu().findItem(R.id.send_broadcast).setOnMenuItemClickListener(item -> {
            startActivity(new Intent(this, SendBroadcastActivity.class));
            return false;
        });
    }

    public void toStart() {
        if(binding.recyclerView.getScrollState() == RecyclerView.SCROLL_STATE_IDLE) {
            binding.appbar.setExpanded(true);
            binding.recyclerView.scrollToStart(true);
        }else {
            willToStart = true;
        }
    }

    private void fetchAndRefreshBroadcasts(){
        stopFetchNextPage = true;
        if(nextPageCall != null) {
            breakFetchNextPage(nextPageCall);
        }
        BroadcastApiCaller.fetchChannelBroadcastsLimit(this, channel.getIchatId(), null, Constants.FETCH_BROADCAST_PAGE_SIZE, true, new RetrofitApiCaller.BaseCommonYier<PaginatedOperationData<Broadcast>>(){

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
                binding.appbar.setExpanded(true);
                binding.recyclerView.scrollToStart(false);
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
                data.commonHandleResult(BroadcastChannelActivity.this, new int[]{-101}, () -> {
                    stopFetchNextPage = !raw.body().hasMore();
                    List<Broadcast> broadcastList = data.getData();
                    List<BroadcastsRecyclerAdapter.ItemData> itemDataList = new ArrayList<>();
                    broadcastList.forEach(broadcast -> {
                        itemDataList.add(new BroadcastsRecyclerAdapter.ItemData(broadcast));
                    });
                    adapter.clearAndShow();
                    adapter.addItemsAndShow(itemDataList);
                    broadcastReloadedTime = new Date();
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

    private synchronized void nextPage() {
        NEXT_PAGE_LATCH = new CountDownLatch(1);
        String lastBroadcastId = adapter.getItemDataList().get(adapter.getItemCount() - 1).getBroadcast().getBroadcastId();
        BroadcastApiCaller.fetchChannelBroadcastsLimit(this, channel.getIchatId(), lastBroadcastId, Constants.FETCH_BROADCAST_PAGE_SIZE, true, new RetrofitApiCaller.BaseCommonYier<PaginatedOperationData<Broadcast>>() {

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
                data.commonHandleResult(BroadcastChannelActivity.this, new int[]{-101}, () -> {
                    if (breakFetchNextPage(call)) return;
                    stopFetchNextPage = !raw.body().hasMore();
                    List<Broadcast> broadcastList = data.getData();
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
        fetchAndRefreshBroadcasts();
    }

    @Override
    public void onBroadcastDeleted(String broadcastId) {
        adapter.removeItemAndShow(broadcastId);
        if(adapter.getItemCount() == 0) {
            headerBinding.loadFailedView.setVisibility(View.GONE);
            headerBinding.loadFailedText.setText(null);
            headerBinding.loadIndicator.setVisibility(View.GONE);
            headerBinding.noBroadcastView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void fetchNews(String ichatId) {
        if(!ichatId.equals(channel.getIchatId())) return;
        String firstBroadcastId = adapter.getItemDataList().get(0).getBroadcast().getBroadcastId();
        BroadcastApiCaller.fetchChannelBroadcastsLimit(this, channel.getIchatId(), firstBroadcastId, Constants.FETCH_BROADCAST_PAGE_SIZE, false, new RetrofitApiCaller.BaseCommonYier<PaginatedOperationData<Broadcast>>(){

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
                data.commonHandleResult(BroadcastChannelActivity.this, new int[]{-101}, () -> {
                    List<Broadcast> broadcastList = data.getData();
                    List<BroadcastsRecyclerAdapter.ItemData> itemDataList = new ArrayList<>();
                    broadcastList.forEach(broadcast -> {
                        itemDataList.add(new BroadcastsRecyclerAdapter.ItemData(broadcast));
                    });
                    adapter.addItemsToStartAndShow(itemDataList);
                    broadcastReloadedTime = new Date();
                    showOrHideBroadcastReloadedTime();
                    if(raw.body().hasMore()){
                        fetchNews(ichatId);
                    }
                }, new OperationStatus.HandleResult(-102, () -> {
                    ErrorLogger.log("没有获取到新广播");
                    MessageDisplayer.showToast(getContext(), "没有获取到新广播", Toast.LENGTH_LONG);
                }));
            }
        });
    }

    private void showOrHideBroadcastReloadedTime() {
        if(broadcastReloadedTime == null){
            headerBinding.reloadTime.setVisibility(View.GONE);
        }else {
            headerBinding.reloadTime.setVisibility(View.VISIBLE);
            headerBinding.reloadTime.setText("更新时间 " + TimeUtil.formatRelativeTime(broadcastReloadedTime));
        }
    }

    @Override
    public void updateOneBroadcast(Broadcast newBroadcast) {
        if(adapter != null) adapter.updateOneBroadcast(newBroadcast, true);
    }

    @Override
    public void onSetChannelBroadcastExclude(int selectedPosition, String excludeChannelIchatId) {
        if(excludeChannelIchatId.equals(channel.getIchatId())){
            adapter.refreshAll();
        }
    }
}