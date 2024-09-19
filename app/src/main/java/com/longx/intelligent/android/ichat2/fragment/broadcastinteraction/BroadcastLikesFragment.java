package com.longx.intelligent.android.ichat2.fragment.broadcastinteraction;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.longx.intelligent.android.ichat2.adapter.BroadcastLikesRecyclerAdapter;
import com.longx.intelligent.android.ichat2.data.Broadcast;
import com.longx.intelligent.android.ichat2.data.BroadcastLike;
import com.longx.intelligent.android.ichat2.data.response.OperationStatus;
import com.longx.intelligent.android.ichat2.data.response.PaginatedOperationData;
import com.longx.intelligent.android.ichat2.databinding.FragmentBroadcastLikesBinding;
import com.longx.intelligent.android.ichat2.databinding.LayoutBroadcastLikesRecyclerFooterBinding;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.BroadcastApiCaller;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.ichat2.value.Constants;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import retrofit2.Call;
import retrofit2.Response;

public class BroadcastLikesFragment extends Fragment {
    private FragmentBroadcastLikesBinding binding;
    private LayoutBroadcastLikesRecyclerFooterBinding footerBinding;
    private BroadcastLikesRecyclerAdapter adapter;
    private CountDownLatch NEXT_PAGE_LATCH;
    private boolean stopFetchNextPage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentBroadcastLikesBinding.inflate(inflater, container, false);
        footerBinding = LayoutBroadcastLikesRecyclerFooterBinding.inflate(inflater, container, false);
        init();
        fetchAndShowContent();
        return binding.getRoot();
    }

    private void init() {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new BroadcastLikesRecyclerAdapter(requireActivity());
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
        BroadcastApiCaller.fetchLikesOfSelfBroadcasts(this, lastLikeId, Constants.FETCH_BROADCAST_LIKES_PAGE_SIZE, new RetrofitApiCaller.BaseCommonYier<PaginatedOperationData<BroadcastLike>>(){
            @Override
            public void ok(PaginatedOperationData<BroadcastLike> data, Response<PaginatedOperationData<BroadcastLike>> raw, Call<PaginatedOperationData<BroadcastLike>> call) {
                super.ok(data, raw, call);
                data.commonHandleResult(requireActivity(), new int[]{-101}, () -> {
                    if (breakFetchNextPage(call)) return;
                    stopFetchNextPage = !raw.body().hasMore();
                    List<BroadcastLike> broadcastLikeList = data.getData();
                    adapter.addItemsToEndAndShow(broadcastLikeList);
                }, new OperationStatus.HandleResult(-102, () -> {
//                    footerBinding.loadFailedView.setVisibility(View.GONE);
//                    footerBinding.loadFailedText.setText(null);
//                    footerBinding.loadIndicator.setVisibility(View.GONE);
//                    footerBinding.noBroadcastView.setVisibility(View.VISIBLE);
                }));
            }
        });
    }

    private boolean breakFetchNextPage(Call<PaginatedOperationData<BroadcastLike>> call) {
        if(stopFetchNextPage) {
            call.cancel();
//            footerBinding.loadIndicator.setVisibility(View.GONE);
            if(NEXT_PAGE_LATCH != null && NEXT_PAGE_LATCH.getCount() == 1) NEXT_PAGE_LATCH.countDown();
            return true;
        }
        return false;
    }
}