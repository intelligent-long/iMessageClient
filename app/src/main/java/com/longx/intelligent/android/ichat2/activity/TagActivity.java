package com.longx.intelligent.android.ichat2.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.activity.helper.BaseActivity;
import com.longx.intelligent.android.ichat2.adapter.ChannelTagsRecyclerAdapter;
import com.longx.intelligent.android.ichat2.behavior.ContentUpdater;
import com.longx.intelligent.android.ichat2.bottomsheet.AddChannelTagBottomSheet;
import com.longx.intelligent.android.ichat2.da.database.manager.ChannelDatabaseManager;
import com.longx.intelligent.android.ichat2.data.ChannelTag;
import com.longx.intelligent.android.ichat2.databinding.ActivityTagBinding;
import com.longx.intelligent.android.ichat2.util.ColorUtil;
import com.longx.intelligent.android.ichat2.util.ErrorLogger;
import com.longx.intelligent.android.ichat2.yier.GlobalYiersHolder;
import com.longx.intelligent.android.lib.recyclerview.DragSortRecycler;

import java.util.List;

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
        if(allChannelTags.size() == 0){
            binding.noContentLayout.setVisibility(View.VISIBLE);
            binding.recyclerView.setVisibility(View.GONE);
        }else {
            binding.noContentLayout.setVisibility(View.GONE);
            binding.recyclerView.setVisibility(View.VISIBLE);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            binding.recyclerView.setLayoutManager(layoutManager);
            adapter = new ChannelTagsRecyclerAdapter(this, allChannelTags);
            binding.recyclerView.setAdapter(adapter);
        }
    }

    private void setUpYiers() {
        dragSortRecycler = new DragSortRecycler();
        dragSortRecycler.setViewHandleId(R.id.content);
        dragSortRecycler.setFloatingBgColor(ColorUtil.getAttrColor(this, com.google.android.material.R.attr.colorSurfaceContainer));
        dragSortRecycler.setFloatingAlpha(1F);
        dragSortRecycler.setOnItemMovedListener((from, to) -> {
            adapter.moveAndShow(from, to);
        });
        binding.toolbar.setOnMenuItemClickListener(item -> {
            if(item.getItemId() == R.id.add){
                new AddChannelTagBottomSheet(this).show();
            }else if(item.getItemId() == R.id.sort){
                switchDragSortState();
                doSortTags();
            }else if(item.getItemId() == R.id.cancel_sort){
                switchDragSortState();
                adapter.cancelMoveAndShow();
            }
            return true;
        });
    }

    private void switchDragSortState(){
        MenuItem sort = binding.toolbar.getMenu().findItem(R.id.sort);
        MenuItem cancelSort = binding.toolbar.getMenu().findItem(R.id.cancel_sort);
        MenuItem doneSort = binding.toolbar.getMenu().findItem(R.id.done_sort);
        MenuItem add = binding.toolbar.getMenu().findItem(R.id.add);
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