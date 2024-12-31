package com.longx.intelligent.android.ichat2.activity;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.appbar.AppBarLayout;
import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.activity.helper.BaseActivity;
import com.longx.intelligent.android.ichat2.adapter.ChannelTagsRecyclerAdapter;
import com.longx.intelligent.android.ichat2.behaviorcomponents.ContentUpdater;
import com.longx.intelligent.android.ichat2.behaviorcomponents.MessageDisplayer;
import com.longx.intelligent.android.ichat2.bottomsheet.AddChannelTagBottomSheet;
import com.longx.intelligent.android.ichat2.da.database.manager.ChannelDatabaseManager;
import com.longx.intelligent.android.ichat2.data.ChannelTag;
import com.longx.intelligent.android.ichat2.data.request.SortTagsPostBody;
import com.longx.intelligent.android.ichat2.data.response.OperationStatus;
import com.longx.intelligent.android.ichat2.databinding.ActivityTagBinding;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.ChannelApiCaller;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.ichat2.ui.DisableExpandAppBarBehavior;
import com.longx.intelligent.android.ichat2.util.ColorUtil;
import com.longx.intelligent.android.ichat2.util.UiUtil;
import com.longx.intelligent.android.ichat2.yier.GlobalYiersHolder;
import com.longx.intelligent.android.lib.recyclerview.dragsort.DragSortRecycler;
import com.longx.intelligent.android.lib.recyclerview.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;

public class TagActivity extends BaseActivity implements ContentUpdater.OnServerContentUpdateYier {
    private ActivityTagBinding binding;
    private ChannelTagsRecyclerAdapter adapter;
    private DragSortRecycler dragSortRecycler;
    private boolean scrollToEnd;
    private boolean isAppBarExpanded = true;
    private boolean isAppBarExpandedBeforeToSort = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTagBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupDefaultBackNavigation(binding.toolbar);
        showContent();
        setUpYiers();
        GlobalYiersHolder.holdYier(this, ContentUpdater.OnServerContentUpdateYier.class, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GlobalYiersHolder.removeYier(this, ContentUpdater.OnServerContentUpdateYier.class, this);
    }

    private void showContent() {
        List<ChannelTag> allChannelTags = ChannelDatabaseManager.getInstance().findAllChannelTags();
        if(allChannelTags.isEmpty()){
            toNoContent();
        }else {
            toContent();
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.recyclerView.setLayoutManager(layoutManager);
        adapter = new ChannelTagsRecyclerAdapter(this, allChannelTags);
        binding.recyclerView.setAdapter(adapter);
    }

    private void toNoContent(){
        ((AppBarLayout.LayoutParams)binding.collapsingToolbarLayout.getLayoutParams())
                .setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_NO_SCROLL);
        binding.noContentLayout.setVisibility(View.VISIBLE);
        binding.recyclerView.setVisibility(View.GONE);
        UiUtil.setIconMenuEnabled(binding.toolbar.getMenu().findItem(R.id.sort), false);
    }

    private void toContent(){
        ((AppBarLayout.LayoutParams)binding.collapsingToolbarLayout.getLayoutParams())
                .setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
                        | AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED
                        | AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP);
        binding.noContentLayout.setVisibility(View.GONE);
        binding.recyclerView.setVisibility(View.VISIBLE);
        UiUtil.setIconMenuEnabled(binding.toolbar.getMenu().findItem(R.id.sort), true);
    }

    private void setUpYiers() {
        binding.recyclerView.addOnThresholdScrollUpDownYier(new RecyclerView.OnThresholdScrollUpDownYier(50) {
            @Override
            public void onScrollUp() {
                if(binding.fab.isExtended()) binding.fab.shrink();
            }

            @Override
            public void onScrollDown() {
                if(!binding.fab.isExtended()) binding.fab.extend();
            }
        });
        dragSortRecycler = new DragSortRecycler();
        dragSortRecycler.setViewHandleId(R.id.drag_handle);
        dragSortRecycler.setFloatingBgColor(ColorUtil.getAttrColor(this, com.google.android.material.R.attr.colorSurfaceContainer));
        dragSortRecycler.setFloatingAlpha(1F);
        dragSortRecycler.setAutoScrollSpeed(0.3F);
        dragSortRecycler.setOnDragMovedYier((from, to) -> {
            adapter.moveAndShow(from, to);
        });
        binding.fab.setOnClickListener(v -> {
            new AddChannelTagBottomSheet(this, results -> {
                scrollToEnd = true;
            }).show();
        });
        binding.toolbar.setOnMenuItemClickListener(item -> {
            if(item.getItemId() == R.id.sort){
                switchDragSortState();
            }else if(item.getItemId() == R.id.cancel_sort){
                switchDragSortState();
                adapter.cancelMoveAndShow();
            }else if(item.getItemId() == R.id.done_sort){
                doSortTags();
            }
            return true;
        });
        binding.appBar.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) {
                isAppBarExpanded = false;
            } else if (verticalOffset == 0) {
                isAppBarExpanded = true;
            }
        });
    }

    private void switchDragSortState(){
        MenuItem sort = binding.toolbar.getMenu().findItem(R.id.sort);
        MenuItem cancelSort = binding.toolbar.getMenu().findItem(R.id.cancel_sort);
        MenuItem doneSort = binding.toolbar.getMenu().findItem(R.id.done_sort);
        boolean dragSortState = adapter.isDragSortState();
        boolean nowDragSortState = !dragSortState;
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) binding.appBar.getLayoutParams();
        DisableExpandAppBarBehavior behavior = (DisableExpandAppBarBehavior) params.getBehavior();
        if (behavior != null) {
            behavior.setExpandEnabled(!nowDragSortState);
        }
        if(nowDragSortState){
            isAppBarExpandedBeforeToSort = isAppBarExpanded;
            binding.appBar.setExpanded(false);
        }else {
            if(isAppBarExpandedBeforeToSort && isRecyclerViewAtTop()){
                binding.appBar.setExpanded(true);
            }
        }
        adapter.switchDragSortState(nowDragSortState);
        sort.setVisible(!nowDragSortState);
        cancelSort.setVisible(nowDragSortState);
        doneSort.setVisible(nowDragSortState);
        if(nowDragSortState){
            binding.recyclerView.addItemDecoration(dragSortRecycler);
            binding.recyclerView.addOnItemTouchListener(dragSortRecycler);
            binding.recyclerView.addOnScrollListener(dragSortRecycler.getScrollListener());
            binding.fab.hide();
        }else {
            binding.recyclerView.removeItemDecoration(dragSortRecycler);
            binding.recyclerView.removeOnItemTouchListener(dragSortRecycler);
            binding.recyclerView.removeOnScrollListener(dragSortRecycler.getScrollListener());
            binding.fab.show();
        }
    }

    private boolean isRecyclerViewAtTop() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) binding.recyclerView.getLayoutManager();
        if (layoutManager != null) {
            int firstVisibleItemPosition = layoutManager.findFirstCompletelyVisibleItemPosition();
            return firstVisibleItemPosition == 0;
        }
        return false;
    }

    private void doSortTags() {
        List<ChannelTag> channelTags = adapter.getChannelTags();
        Map<String, Integer> orderMap = new HashMap<>();
        channelTags.forEach(channelTag -> {
            orderMap.put(channelTag.getTagId(), channelTag.getOrder());
        });
        SortTagsPostBody postBody = new SortTagsPostBody(orderMap);
        ChannelApiCaller.sortChannelTags(this, postBody, new RetrofitApiCaller.CommonYier<OperationStatus>(this){
            @Override
            public void ok(OperationStatus data, Response<OperationStatus> raw, Call<OperationStatus> call) {
                super.ok(data, raw, call);
                data.commonHandleResult(TagActivity.this, new int[]{-101, -102, -103}, () -> {
                    switchDragSortState();
                    MessageDisplayer.autoShow(getActivity(), "排序成功", MessageDisplayer.Duration.SHORT);
                });
            }
        });
    }

    @Override
    public void onStartUpdate(String id, List<String> updatingIds) {

    }

    @Override
    public void onUpdateComplete(String id, List<String> updatingIds) {
        if(id.equals(ContentUpdater.OnServerContentUpdateYier.ID_CHANNEL_TAGS)){
            boolean switched = false;
            if(adapter.isDragSortState()){
                switchDragSortState();
                switched = true;
            }
            showContent();
            if(switched){
                switchDragSortState();
            }
        }
    }
}