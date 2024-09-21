package com.longx.intelligent.android.ichat2.activity;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.longx.intelligent.android.ichat2.activity.helper.BaseActivity;
import com.longx.intelligent.android.ichat2.adapter.BroadcastLikesRecyclerAdapter;
import com.longx.intelligent.android.ichat2.data.Broadcast;
import com.longx.intelligent.android.ichat2.data.BroadcastLike;
import com.longx.intelligent.android.ichat2.data.response.OperationStatus;
import com.longx.intelligent.android.ichat2.data.response.PaginatedOperationData;
import com.longx.intelligent.android.ichat2.databinding.ActivityBroadcastLikesBinding;
import com.longx.intelligent.android.ichat2.databinding.RecyclerFooterAllLikesOfBroadcastBinding;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.BroadcastApiCaller;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.ichat2.value.Constants;
import com.longx.intelligent.android.lib.recyclerview.RecyclerView;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import retrofit2.Call;
import retrofit2.Response;

public class BroadcastLikesActivity extends BaseActivity {
    private ActivityBroadcastLikesBinding binding;
    private RecyclerFooterAllLikesOfBroadcastBinding footerBinding;
    private Broadcast broadcast;
    private BroadcastLikesRecyclerAdapter adapter;
    private CountDownLatch NEXT_PAGE_LATCH;
    private boolean stopFetchNextPage;

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
        setupYiers();
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

    private void setupYiers() {
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
    }

    private void fetchAndShowContent() {
        nextPage();
    }

    private synchronized void nextPage() {
        NEXT_PAGE_LATCH = new CountDownLatch(1);
        String lastLikeId = null;
        if(adapter.getItemCount() > 0){
            lastLikeId = adapter.getItemDataList().get(adapter.getItemCount() - 1).getBroadcastLike().getLikeId();
        }
        BroadcastApiCaller.fetchLikesOfBroadcast(this, broadcast.getBroadcastId(), lastLikeId, Constants.FETCH_BROADCAST_LIKES_PAGE_SIZE, new RetrofitApiCaller.BaseCommonYier<PaginatedOperationData<BroadcastLike>>(){

            @Override
            public void start(Call<PaginatedOperationData<BroadcastLike>> call) {
                super.start(call);
                if (breakFetchNextPage(call)) return;
                footerBinding.loadFailedView.setVisibility(View.GONE);
                footerBinding.loadFailedText.setText(null);
                footerBinding.loadIndicator.setVisibility(View.VISIBLE);
                footerBinding.noContentView.setVisibility(View.GONE);
            }

            @Override
            public void complete(Call<PaginatedOperationData<BroadcastLike>> call) {
                super.complete(call);
                if (breakFetchNextPage(call)) return;
                footerBinding.loadIndicator.setVisibility(View.GONE);
                NEXT_PAGE_LATCH.countDown();
            }

            @Override
            public void notOk(int code, String message, Response<PaginatedOperationData<BroadcastLike>> row, Call<PaginatedOperationData<BroadcastLike>> call) {
                super.notOk(code, message, row, call);
                if (breakFetchNextPage(call)) return;
                footerBinding.loadFailedView.setVisibility(View.VISIBLE);
                footerBinding.loadFailedText.setText("HTTP 状态码异常 > " + code);
                footerBinding.noContentView.setVisibility(View.GONE);
                binding.recyclerView.scrollToEnd(false);
                stopFetchNextPage = true;
            }

            @Override
            public void failure(Throwable t, Call<PaginatedOperationData<BroadcastLike>> call) {
                super.failure(t, call);
                if (breakFetchNextPage(call)) return;
                footerBinding.loadFailedView.setVisibility(View.VISIBLE);
                footerBinding.loadFailedText.setText("出错了 > " + t.getClass().getName());
                footerBinding.noContentView.setVisibility(View.GONE);
                binding.recyclerView.scrollToEnd(false);
                stopFetchNextPage = true;
            }

            @Override
            public void ok(PaginatedOperationData<BroadcastLike> data, Response<PaginatedOperationData<BroadcastLike>> raw, Call<PaginatedOperationData<BroadcastLike>> call) {
                super.ok(data, raw, call);
                data.commonHandleResult(BroadcastLikesActivity.this, new int[]{-101}, () -> {
                    if (breakFetchNextPage(call)) return;
                    stopFetchNextPage = !raw.body().hasMore();
                    List<BroadcastLike> broadcastLikeList = data.getData();
                    adapter.addItemsToEndAndShow(broadcastLikeList);
                }, new OperationStatus.HandleResult(-102, () -> {
                    footerBinding.loadFailedView.setVisibility(View.GONE);
                    footerBinding.loadFailedText.setText(null);
                    footerBinding.loadIndicator.setVisibility(View.GONE);
                    footerBinding.noContentView.setVisibility(View.VISIBLE);
                }));
            }
        });
    }

    private boolean breakFetchNextPage(Call<PaginatedOperationData<BroadcastLike>> call) {
        if(stopFetchNextPage) {
            call.cancel();
            footerBinding.loadIndicator.setVisibility(View.GONE);
            if(NEXT_PAGE_LATCH != null && NEXT_PAGE_LATCH.getCount() == 1) NEXT_PAGE_LATCH.countDown();
            return true;
        }
        return false;
    }
}