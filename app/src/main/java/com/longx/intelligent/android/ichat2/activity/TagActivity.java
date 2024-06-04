package com.longx.intelligent.android.ichat2.activity;

import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.appbar.AppBarLayout;
import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.activity.helper.BaseActivity;
import com.longx.intelligent.android.ichat2.adapter.ChannelTagsRecyclerAdapter;
import com.longx.intelligent.android.ichat2.behavior.ContentUpdater;
import com.longx.intelligent.android.ichat2.behavior.MessageDisplayer;
import com.longx.intelligent.android.ichat2.bottomsheet.AddChannelTagBottomSheet;
import com.longx.intelligent.android.ichat2.da.database.manager.ChannelDatabaseManager;
import com.longx.intelligent.android.ichat2.data.ChannelTag;
import com.longx.intelligent.android.ichat2.data.request.SortTagsPostBody;
import com.longx.intelligent.android.ichat2.data.response.OperationStatus;
import com.longx.intelligent.android.ichat2.databinding.ActivityTagBinding;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.ChannelApiCaller;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.ichat2.util.ColorUtil;
import com.longx.intelligent.android.ichat2.yier.GlobalYiersHolder;
import com.longx.intelligent.android.lib.recyclerview.DragSortRecycler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;

public class TagActivity extends BaseActivity implements ContentUpdater.OnServerContentUpdateYier {
    private ActivityTagBinding binding;
    private ChannelTagsRecyclerAdapter adapter;
    private DragSortRecycler dragSortRecycler;

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
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            binding.recyclerView.setLayoutManager(layoutManager);
            adapter = new ChannelTagsRecyclerAdapter(this, allChannelTags);
            binding.recyclerView.setAdapter(adapter);
        }
    }

    private void toNoContent(){
        ((AppBarLayout.LayoutParams)binding.collapsingToolbarLayout.getLayoutParams())
                .setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_NO_SCROLL);
        binding.noContentLayout.setVisibility(View.VISIBLE);
        binding.recyclerView.setVisibility(View.GONE);
    }

    private void toContent(){
        ((AppBarLayout.LayoutParams)binding.collapsingToolbarLayout.getLayoutParams())
                .setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
                        | AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED
                        | AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP);
        binding.noContentLayout.setVisibility(View.GONE);
        binding.recyclerView.setVisibility(View.VISIBLE);
    }

    private void setUpYiers() {
        dragSortRecycler = new DragSortRecycler();
        dragSortRecycler.setViewHandleId(R.id.drag_handle);
        dragSortRecycler.setFloatingBgColor(ColorUtil.getAttrColor(this, com.google.android.material.R.attr.colorSurfaceContainer));
        dragSortRecycler.setFloatingAlpha(1F);
        dragSortRecycler.setOnItemMovedListener((from, to) -> {
            adapter.moveAndShow(from, to);
        });
        binding.toolbar.setOnMenuItemClickListener(item -> {
            if(item.getItemId() == R.id.add_tag){
                new AddChannelTagBottomSheet(this).show();
            }else if(item.getItemId() == R.id.sort){
                switchDragSortState();
            }else if(item.getItemId() == R.id.cancel_sort){
                switchDragSortState();
                adapter.cancelMoveAndShow();
            }else if(item.getItemId() == R.id.done_sort){
                doSortTags();
            }
            return true;
        });
    }

    private void switchDragSortState(){
        MenuItem sort = binding.toolbar.getMenu().findItem(R.id.sort);
        MenuItem cancelSort = binding.toolbar.getMenu().findItem(R.id.cancel_sort);
        MenuItem doneSort = binding.toolbar.getMenu().findItem(R.id.done_sort);
        MenuItem add = binding.toolbar.getMenu().findItem(R.id.add_tag);
        boolean dragSortState = adapter.isDragSortState();
        boolean now = !dragSortState;
        adapter.switchDragSortState(now);
        sort.setVisible(!now);
        cancelSort.setVisible(now);
        doneSort.setVisible(now);
        add.setVisible(!now);
        if(now){
            binding.recyclerView.addItemDecoration(dragSortRecycler);
            binding.recyclerView.addOnItemTouchListener(dragSortRecycler);
            binding.recyclerView.addOnScrollListener(dragSortRecycler.getScrollListener());
        }else {
            binding.recyclerView.removeItemDecoration(dragSortRecycler);
            binding.recyclerView.removeOnItemTouchListener(dragSortRecycler);
            binding.recyclerView.removeOnScrollListener(dragSortRecycler.getScrollListener());
        }
    }

    private void doSortTags() {
        List<ChannelTag> channelTags = adapter.getChannelTags();
        Map<String, Integer> orderMap = new HashMap<>();
        channelTags.forEach(channelTag -> {
            orderMap.put(channelTag.getId(), channelTag.getOrder());
        });
        SortTagsPostBody postBody = new SortTagsPostBody(orderMap);
        ChannelApiCaller.sortChannelTags(this, postBody, new RetrofitApiCaller.CommonYier<OperationStatus>(this){
            @Override
            public void ok(OperationStatus data, Response<OperationStatus> row, Call<OperationStatus> call) {
                super.ok(data, row, call);
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
            showContent();
        }
    }
}