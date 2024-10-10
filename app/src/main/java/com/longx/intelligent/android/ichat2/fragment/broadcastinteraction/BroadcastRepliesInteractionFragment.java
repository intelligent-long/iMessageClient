package com.longx.intelligent.android.ichat2.fragment.broadcastinteraction;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.longx.intelligent.android.ichat2.adapter.BroadcastCommentsInteractionRecyclerAdapter;
import com.longx.intelligent.android.ichat2.adapter.BroadcastRepliesInteractionRecyclerAdapter;
import com.longx.intelligent.android.ichat2.data.BroadcastComment;
import com.longx.intelligent.android.ichat2.data.request.MakeBroadcastCommentsToOldPostBody;
import com.longx.intelligent.android.ichat2.data.request.MakeBroadcastReplyCommentsToOldPostBody;
import com.longx.intelligent.android.ichat2.data.response.OperationStatus;
import com.longx.intelligent.android.ichat2.data.response.PaginatedOperationData;
import com.longx.intelligent.android.ichat2.databinding.FragmentBroadcastRepliesInteractionBinding;
import com.longx.intelligent.android.ichat2.databinding.RecyclerFooterBroadcastRepliesInteractionBinding;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.BroadcastApiCaller;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.ichat2.util.ErrorLogger;
import com.longx.intelligent.android.ichat2.value.Constants;
import com.longx.intelligent.android.lib.recyclerview.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import retrofit2.Call;
import retrofit2.Response;

public class BroadcastRepliesInteractionFragment extends Fragment {
    private FragmentBroadcastRepliesInteractionBinding binding;
    private RecyclerFooterBroadcastRepliesInteractionBinding footerBinding;
    private BroadcastRepliesInteractionRecyclerAdapter adapter;
    private CountDownLatch NEXT_PAGE_LATCH;
    private boolean stopFetchNextPage;
    private List<BroadcastComment> makedToOldBroadcastReplies = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentBroadcastRepliesInteractionBinding.inflate(inflater, container, false);
        footerBinding = RecyclerFooterBroadcastRepliesInteractionBinding.inflate(inflater, binding.getRoot(), false);
        init();
        fetchAndShowContent();
        setupYiers();
        return binding.getRoot();
    }

    private void init() {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new BroadcastRepliesInteractionRecyclerAdapter(requireActivity());
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
        binding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull androidx.recyclerview.widget.RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                makeBroadcastReplyCommentsToOldOnServer();
            }
        });
    }

    private void fetchAndShowContent() {
        nextPage();
    }

    private synchronized void nextPage() {
        NEXT_PAGE_LATCH = new CountDownLatch(1);
        String lastReplyCommentId = null;
        if(adapter.getItemCount() > 0){
            lastReplyCommentId = adapter.getItemDataList().get(adapter.getItemCount() - 1).getBroadcastReplyComment().getCommentId();
        }
        BroadcastApiCaller.fetchReplyCommentsOfSelfBroadcasts(this, lastReplyCommentId, Constants.FETCH_BROADCAST_REPLY_COMMENTS_INTERACTION_PAGE_SIZE,  new RetrofitApiCaller.BaseCommonYier<PaginatedOperationData<BroadcastComment>>(){

            @Override
            public void start(Call<PaginatedOperationData<BroadcastComment>> call) {
                super.start(call);
                if (breakFetchNextPage(call)) return;
                footerBinding.loadFailedView.setVisibility(View.GONE);
                footerBinding.loadFailedText.setText(null);
                footerBinding.loadIndicator.setVisibility(View.VISIBLE);
                footerBinding.noContentView.setVisibility(View.GONE);
            }

            @Override
            public void complete(Call<PaginatedOperationData<BroadcastComment>> call) {
                super.complete(call);
                if (breakFetchNextPage(call)) return;
                footerBinding.loadIndicator.setVisibility(View.GONE);
                NEXT_PAGE_LATCH.countDown();
            }

            @Override
            public void notOk(int code, String message, Response<PaginatedOperationData<BroadcastComment>> row, Call<PaginatedOperationData<BroadcastComment>> call) {
                super.notOk(code, message, row, call);
                if (breakFetchNextPage(call)) return;
                footerBinding.loadFailedView.setVisibility(View.VISIBLE);
                footerBinding.loadFailedText.setText("HTTP 状态码异常 > " + code);
                footerBinding.noContentView.setVisibility(View.GONE);
                binding.recyclerView.scrollToEnd(false);
                stopFetchNextPage = true;
            }

            @Override
            public void failure(Throwable t, Call<PaginatedOperationData<BroadcastComment>> call) {
                super.failure(t, call);
                if (breakFetchNextPage(call)) return;
                footerBinding.loadFailedView.setVisibility(View.VISIBLE);
                footerBinding.loadFailedText.setText("出错了 > " + t.getClass().getName());
                footerBinding.noContentView.setVisibility(View.GONE);
                binding.recyclerView.scrollToEnd(false);
                stopFetchNextPage = true;
            }

            @Override
            public void ok(PaginatedOperationData<BroadcastComment> data, Response<PaginatedOperationData<BroadcastComment>> raw, Call<PaginatedOperationData<BroadcastComment>> call) {
                super.ok(data, raw, call);
                data.commonHandleResult(requireActivity(), new int[]{-101}, () -> {
                    if (breakFetchNextPage(call)) return;
                    stopFetchNextPage = !raw.body().hasMore();
                    List<BroadcastComment> broadcastReplyCommentList = data.getData();
                    adapter.addItemsToEndAndShow(broadcastReplyCommentList);
                }, new OperationStatus.HandleResult(-102, () -> {
                    footerBinding.loadFailedView.setVisibility(View.GONE);
                    footerBinding.loadFailedText.setText(null);
                    footerBinding.loadIndicator.setVisibility(View.GONE);
                    footerBinding.noContentView.setVisibility(View.VISIBLE);
                }));
            }
        });

    }

    private boolean breakFetchNextPage(Call<PaginatedOperationData<BroadcastComment>> call) {
        if(stopFetchNextPage) {
            call.cancel();
            footerBinding.loadIndicator.setVisibility(View.GONE);
            if(NEXT_PAGE_LATCH != null && NEXT_PAGE_LATCH.getCount() == 1) NEXT_PAGE_LATCH.countDown();
            return true;
        }
        return false;
    }

    private void makeBroadcastReplyCommentsToOldOnServer() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) binding.recyclerView.getLayoutManager();
        int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
        int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
        List<String> toMakeToOldBroadcastReplyCommentIds = new ArrayList<>();
        for (int i = firstVisibleItemPosition; i <= lastVisibleItemPosition; i++) {
            try {
                BroadcastComment broadcastReplyComment = adapter.getItemDataList().get(i).getBroadcastReplyComment();
                if (broadcastReplyComment.isNew()) {
                    if (!makedToOldBroadcastReplies.contains(broadcastReplyComment)) {
                        toMakeToOldBroadcastReplyCommentIds.add(broadcastReplyComment.getCommentId());
                        makedToOldBroadcastReplies.add(broadcastReplyComment);
                    }
                }
            } catch (IndexOutOfBoundsException e) {
                ErrorLogger.log(e);
            }
        }
        if (!toMakeToOldBroadcastReplyCommentIds.isEmpty()) {
            MakeBroadcastReplyCommentsToOldPostBody postBody = new MakeBroadcastReplyCommentsToOldPostBody(toMakeToOldBroadcastReplyCommentIds);
            BroadcastApiCaller.makeBroadcastReplyCommentsToOld(requireActivity(), postBody, new RetrofitApiCaller.BaseCommonYier<>(requireActivity()));
        }
    }
}