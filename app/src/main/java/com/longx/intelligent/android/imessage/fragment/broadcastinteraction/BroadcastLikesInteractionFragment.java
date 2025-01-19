package com.longx.intelligent.android.imessage.fragment.broadcastinteraction;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.longx.intelligent.android.imessage.adapter.BroadcastLikesInteractionRecyclerAdapter;
import com.longx.intelligent.android.imessage.data.BroadcastLike;
import com.longx.intelligent.android.imessage.data.request.MakeBroadcastLikesToOldPostBody;
import com.longx.intelligent.android.imessage.data.response.OperationStatus;
import com.longx.intelligent.android.imessage.data.response.PaginatedOperationData;
import com.longx.intelligent.android.imessage.databinding.FragmentBroadcastLikesInteractionBinding;
import com.longx.intelligent.android.imessage.databinding.RecyclerFooterBroadcastLikesInteractionBinding;
import com.longx.intelligent.android.imessage.net.retrofit.caller.BroadcastApiCaller;
import com.longx.intelligent.android.imessage.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.imessage.util.ErrorLogger;
import com.longx.intelligent.android.imessage.value.Constants;
import com.longx.intelligent.android.lib.recyclerview.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import retrofit2.Call;
import retrofit2.Response;

public class BroadcastLikesInteractionFragment extends Fragment {
    private FragmentBroadcastLikesInteractionBinding binding;
    private RecyclerFooterBroadcastLikesInteractionBinding footerBinding;
    private BroadcastLikesInteractionRecyclerAdapter adapter;
    private CountDownLatch NEXT_PAGE_LATCH;
    private boolean stopFetchNextPage;
    private List<BroadcastLike> makedToOldBroadcastLikes = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentBroadcastLikesInteractionBinding.inflate(inflater, container, false);
        footerBinding = RecyclerFooterBroadcastLikesInteractionBinding.inflate(inflater, binding.getRoot(), false);
        init();
        fetchAndShowContent();
        setupYiers();
        return binding.getRoot();
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
        binding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull androidx.recyclerview.widget.RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                makeBroadcastLikesToOldOnServer();
            }
        });
    }

    private void init() {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new BroadcastLikesInteractionRecyclerAdapter(requireActivity());
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setFooterView(footerBinding.getRoot());
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
        BroadcastApiCaller.fetchLikesOfSelfBroadcasts(this, lastLikeId, Constants.FETCH_BROADCAST_LIKES_INTERACTION_PAGE_SIZE, new RetrofitApiCaller.BaseCommonYier<PaginatedOperationData<BroadcastLike>>(){

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
                data.commonHandleResult(requireActivity(), new int[]{-101}, () -> {
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

    private void makeBroadcastLikesToOldOnServer() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) binding.recyclerView.getLayoutManager();
        int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
        int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
        List<String> toMakeToOldBroadcastLikeIds = new ArrayList<>();
        for (int i = firstVisibleItemPosition; i <= lastVisibleItemPosition; i++) {
            try {
                if(!adapter.getItemDataList().isEmpty()) {
                    BroadcastLike broadcastLike = adapter.getItemDataList().get(i).getBroadcastLike();
                    if (broadcastLike.isNew()) {
                        if (!makedToOldBroadcastLikes.contains(broadcastLike)) {
                            toMakeToOldBroadcastLikeIds.add(broadcastLike.getLikeId());
                            makedToOldBroadcastLikes.add(broadcastLike);
                        }
                    }
                }
            } catch (IndexOutOfBoundsException e) {
                ErrorLogger.log(e);
            }
        }
        if (!toMakeToOldBroadcastLikeIds.isEmpty()) {
            MakeBroadcastLikesToOldPostBody postBody = new MakeBroadcastLikesToOldPostBody(toMakeToOldBroadcastLikeIds);
            BroadcastApiCaller.makeBroadcastLikesToOld(requireActivity(), postBody, new RetrofitApiCaller.BaseCommonYier<>(requireActivity()));
        }
    }
}