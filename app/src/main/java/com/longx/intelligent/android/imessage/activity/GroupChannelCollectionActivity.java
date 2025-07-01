package com.longx.intelligent.android.imessage.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.MenuCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.appbar.AppBarLayout;
import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.imessage.activity.helper.BaseActivity;
import com.longx.intelligent.android.imessage.adapter.ChannelCollectionAdapter;
import com.longx.intelligent.android.imessage.adapter.GroupChannelCollectionAdapter;
import com.longx.intelligent.android.imessage.behaviorcomponents.ContentUpdater;
import com.longx.intelligent.android.imessage.behaviorcomponents.MessageDisplayer;
import com.longx.intelligent.android.imessage.bottomsheet.AddChannelCollectionBottomSheet;
import com.longx.intelligent.android.imessage.bottomsheet.AddGroupChannelCollectionBottomSheet;
import com.longx.intelligent.android.imessage.da.database.manager.ChannelDatabaseManager;
import com.longx.intelligent.android.imessage.da.database.manager.GroupChannelDatabaseManager;
import com.longx.intelligent.android.imessage.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.imessage.data.Channel;
import com.longx.intelligent.android.imessage.data.ChannelAssociation;
import com.longx.intelligent.android.imessage.data.ChannelCollectionItem;
import com.longx.intelligent.android.imessage.data.ChannelTag;
import com.longx.intelligent.android.imessage.data.GroupChannel;
import com.longx.intelligent.android.imessage.data.GroupChannelCollectionItem;
import com.longx.intelligent.android.imessage.data.request.SortGroupTagsPostBody;
import com.longx.intelligent.android.imessage.data.response.OperationStatus;
import com.longx.intelligent.android.imessage.databinding.ActivityChannelCollectionBinding;
import com.longx.intelligent.android.imessage.databinding.ActivityGroupChannelCollectionBinding;
import com.longx.intelligent.android.imessage.net.retrofit.caller.GroupChannelApiCaller;
import com.longx.intelligent.android.imessage.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.imessage.ui.DisableExpandAppBarBehavior;
import com.longx.intelligent.android.imessage.util.ColorUtil;
import com.longx.intelligent.android.imessage.yier.GlobalYiersHolder;
import com.longx.intelligent.android.lib.recyclerview.dragsort.DragSortRecycler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Response;

public class GroupChannelCollectionActivity extends BaseActivity implements ContentUpdater.OnServerContentUpdateYier{
    private ActivityGroupChannelCollectionBinding binding;
    private GroupChannelCollectionAdapter adapter;
    private int lastScrollPosition = -1;
    private int lastScrollOffset = 0;
    private List<GroupChannel> canAddGroupChannels;
    private DragSortRecycler dragSortRecycler;
    private boolean isAppBarExpanded = true;
    private boolean isAppBarExpandedBeforeToSort = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGroupChannelCollectionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupDefaultBackNavigation(binding.toolbar);
        init();
        showContent();
        setupYiers();
        GlobalYiersHolder.holdYier(this, ContentUpdater.OnServerContentUpdateYier.class, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GlobalYiersHolder.removeYier(this, ContentUpdater.OnServerContentUpdateYier.class, this);
    }

    private void init() {
        MenuCompat.setGroupDividerEnabled(binding.toolbar.getMenu(), true);
        binding.toolbar.getMenu().findItem(R.id.custom_sort).setCheckable(false);
        updateMenuChecked();
    }

    private void updateMenuChecked() {
        SharedPreferencesAccessor.SortPref.GroupChannelCollectionSortBy groupChannelCollectionSortBy = SharedPreferencesAccessor.SortPref.getGroupChannelCollectionSortBy(this);
        switch (groupChannelCollectionSortBy){
            case CUSTOM -> {
                binding.toolbar.getMenu().findItem(R.id.sort_by_custom).setChecked(true);
                binding.toolbar.getMenu().findItem(R.id.custom_sort).setEnabled(true);
            }
            case NEW_TO_OLD -> {
                binding.toolbar.getMenu().findItem(R.id.new_to_old).setChecked(true);
                binding.toolbar.getMenu().findItem(R.id.custom_sort).setEnabled(false);
            }
            case OLD_TO_NEW -> {
                binding.toolbar.getMenu().findItem(R.id.old_to_new).setChecked(true);
                binding.toolbar.getMenu().findItem(R.id.custom_sort).setEnabled(false);
            }
            case A_TO_Z -> {
                binding.toolbar.getMenu().findItem(R.id.a_to_z).setChecked(true);
                binding.toolbar.getMenu().findItem(R.id.custom_sort).setEnabled(false);
            }
            case Z_TO_A -> {
                binding.toolbar.getMenu().findItem(R.id.z_to_a).setChecked(true);
                binding.toolbar.getMenu().findItem(R.id.custom_sort).setEnabled(false);
            }
        }
    }

    private void toNoContent(){
        ((AppBarLayout.LayoutParams)binding.collapsingToolbarLayout.getLayoutParams())
                .setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_NO_SCROLL);
        binding.noContentLayout.setVisibility(View.VISIBLE);
    }

    private void toContent(){
        ((AppBarLayout.LayoutParams)binding.collapsingToolbarLayout.getLayoutParams())
                .setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
                        | AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED
                        | AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP);
        binding.noContentLayout.setVisibility(View.GONE);
    }

    public ActivityGroupChannelCollectionBinding getBinding() {
        return binding;
    }

    private void showContent() {
        List<GroupChannelCollectionItem> allGroupChannelCollections = GroupChannelDatabaseManager.getInstance().findAllGroupChannelCollections();
        if (allGroupChannelCollections.isEmpty()) {
            toNoContent();
        } else {
            toContent();
        }
        List<GroupChannelCollectionAdapter.ItemData> itemDataList = new ArrayList<>();
        for (GroupChannelCollectionItem groupChannelCollectionItem : allGroupChannelCollections) {
            GroupChannel groupChannel = GroupChannelDatabaseManager.getInstance().findOneAssociation(groupChannelCollectionItem.getGroupChannelId());
            itemDataList.add(new GroupChannelCollectionAdapter.ItemData(groupChannel, groupChannelCollectionItem.getAddTime(), groupChannelCollectionItem.getOrder(), groupChannelCollectionItem.getUuid()));
        }
        adapter = new GroupChannelCollectionAdapter(this, itemDataList);
        updateSort();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);
        List<GroupChannel> groupChannels = GroupChannelDatabaseManager.getInstance().findAllAssociations();
        canAddGroupChannels = new ArrayList<>();
        Set<String> collectedGroupChannelIds = new HashSet<>();
        for (GroupChannelCollectionItem item : allGroupChannelCollections) {
            collectedGroupChannelIds.add(item.getGroupChannelId());
        }
        for (GroupChannel groupChannel : groupChannels) {
            if (!collectedGroupChannelIds.contains(groupChannel.getGroupChannelId())) {
                canAddGroupChannels.add(groupChannel);
            }
        }
        binding.toolbar.getMenu().findItem(R.id.add).setEnabled(!canAddGroupChannels.isEmpty());
    }

    @Override
    protected void onStart() {
        super.onStart();
        showContent();
    }

    @Override
    public void onStartUpdate(String id, List<String> updatingIds, Object... objects) {
    }

    @Override
    public void onUpdateComplete(String id, List<String> updatingIds, Object... objects) {
        if(id.equals(ContentUpdater.OnServerContentUpdateYier.ID_GROUP_CHANNEL_COLLECTIONS)){
            showContent();
        }
    }

    private void setupYiers() {
        dragSortRecycler = new DragSortRecycler();
        dragSortRecycler.setViewHandleId(R.id.drag_handle);
        dragSortRecycler.setFloatingBgColor(ColorUtil.getAttrColor(this, com.google.android.material.R.attr.colorSurfaceContainer));
        dragSortRecycler.setFloatingAlpha(1F);
        dragSortRecycler.setAutoScrollSpeed(0.17F);
        dragSortRecycler.setOnDragMovedYier((from, to) -> {
            adapter.moveAndShow(from, to);
        });
        binding.toolbar.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.sort_by_custom) {
                SharedPreferencesAccessor.SortPref.saveGroupChannelCollectionSortBy(this, SharedPreferencesAccessor.SortPref.GroupChannelCollectionSortBy.CUSTOM);
                updateMenuChecked();
                updateSort();
            }else if(id == R.id.new_to_old){
                SharedPreferencesAccessor.SortPref.saveGroupChannelCollectionSortBy(this, SharedPreferencesAccessor.SortPref.GroupChannelCollectionSortBy.NEW_TO_OLD);
                updateMenuChecked();
                updateSort();
            }else if(id == R.id.old_to_new){
                SharedPreferencesAccessor.SortPref.saveGroupChannelCollectionSortBy(this, SharedPreferencesAccessor.SortPref.GroupChannelCollectionSortBy.OLD_TO_NEW);
                updateMenuChecked();
                updateSort();
            }else if(id == R.id.a_to_z){
                SharedPreferencesAccessor.SortPref.saveGroupChannelCollectionSortBy(this, SharedPreferencesAccessor.SortPref.GroupChannelCollectionSortBy.A_TO_Z);
                updateMenuChecked();
                updateSort();
            }else if(id == R.id.z_to_a){
                SharedPreferencesAccessor.SortPref.saveGroupChannelCollectionSortBy(this, SharedPreferencesAccessor.SortPref.GroupChannelCollectionSortBy.Z_TO_A);
                updateMenuChecked();
                updateSort();
            }else if(id == R.id.add){
                new AddGroupChannelCollectionBottomSheet(this, canAddGroupChannels).show();
            }else if (id == R.id.custom_sort) {
                switchDragSortState();
            }else if(id == R.id.cancel_sort){
                switchDragSortState();
                adapter.cancelMoveAndShow();
            }else if(id == R.id.apply_sort){
                doCustomSort();
            }
            return true;
        });
    }

    private void switchDragSortState(){
        boolean dragSortState = adapter.isDragSortState();
        boolean nowDragSortState = !dragSortState;
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) binding.appbar.getLayoutParams();
        DisableExpandAppBarBehavior behavior = (DisableExpandAppBarBehavior) params.getBehavior();
        if (behavior != null) {
            behavior.setExpandEnabled(!nowDragSortState);
        }
        if(nowDragSortState){
            isAppBarExpandedBeforeToSort = isAppBarExpanded;
            binding.appbar.setExpanded(false);
        }else {
            if(isAppBarExpandedBeforeToSort && isRecyclerViewAtTop()){
                binding.appbar.setExpanded(true);
            }
        }
        adapter.switchDragSortState(nowDragSortState);
        binding.toolbar.getMenu().findItem(R.id.add).setVisible(!nowDragSortState);
        binding.toolbar.getMenu().findItem(R.id.sort).setVisible(!nowDragSortState);
        binding.toolbar.getMenu().findItem(R.id.cancel_sort).setVisible(nowDragSortState);
        binding.toolbar.getMenu().findItem(R.id.apply_sort).setVisible(nowDragSortState);
        if(nowDragSortState){
            binding.recyclerView.addItemDecoration(dragSortRecycler);
            binding.recyclerView.addOnItemTouchListener(dragSortRecycler);
            binding.recyclerView.addOnScrollListener(dragSortRecycler.getScrollListener());
        }else {
            binding.recyclerView.removeItemDecoration(dragSortRecycler);
            binding.recyclerView.removeOnItemTouchListener(dragSortRecycler);
            binding.recyclerView.removeOnScrollListener(dragSortRecycler.getScrollListener());
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

    private void doCustomSort() {
        Map<String, Integer> orderMap = new HashMap<>();
        adapter.getItemDataList().forEach(itemData -> {
            orderMap.put(itemData.getUuid(), itemData.getOrder());
        });
        SortGroupTagsPostBody postBody = new SortGroupTagsPostBody(orderMap);
        GroupChannelApiCaller.sortGroupCollections(this, postBody, new RetrofitApiCaller.CommonYier<>(this){
            @Override
            public void ok(OperationStatus data, Response<OperationStatus> raw, Call<OperationStatus> call) {
                super.ok(data, raw, call);
                data.commonHandleResult(GroupChannelCollectionActivity.this, new int[]{-101, -102, -103}, () -> {
                    switchDragSortState();
                    MessageDisplayer.autoShow(getActivity(), "排序成功", MessageDisplayer.Duration.SHORT);
                });
            }
        });
    }

    private void updateSort() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) binding.recyclerView.getLayoutManager();
        if (layoutManager != null) {
            View topView = layoutManager.getChildAt(0);
            if (topView != null) {
                lastScrollPosition = layoutManager.findFirstVisibleItemPosition();
                lastScrollOffset = topView.getTop() - binding.recyclerView.getPaddingTop();
            }
        }
        SharedPreferencesAccessor.SortPref.GroupChannelCollectionSortBy groupChannelCollectionSortBy = SharedPreferencesAccessor.SortPref.getGroupChannelCollectionSortBy(this);
        adapter.sort(groupChannelCollectionSortBy);
        binding.recyclerView.post(() -> {
            LinearLayoutManager lm = (LinearLayoutManager) binding.recyclerView.getLayoutManager();
            if (lm != null && lastScrollPosition >= 0) {
                lm.scrollToPositionWithOffset(lastScrollPosition, lastScrollOffset);
            }
        });
    }
}