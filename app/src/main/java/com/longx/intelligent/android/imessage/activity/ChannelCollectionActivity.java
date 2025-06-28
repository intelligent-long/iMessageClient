package com.longx.intelligent.android.imessage.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.MenuCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.appbar.AppBarLayout;
import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.imessage.activity.helper.BaseActivity;
import com.longx.intelligent.android.imessage.adapter.ChannelCollectionAdapter;
import com.longx.intelligent.android.imessage.behaviorcomponents.ContentUpdater;
import com.longx.intelligent.android.imessage.bottomsheet.AddChannelCollectionBottomSheet;
import com.longx.intelligent.android.imessage.da.database.manager.ChannelDatabaseManager;
import com.longx.intelligent.android.imessage.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.imessage.data.Channel;
import com.longx.intelligent.android.imessage.data.ChannelAssociation;
import com.longx.intelligent.android.imessage.data.ChannelCollectionItem;
import com.longx.intelligent.android.imessage.databinding.ActivityChannelCollectionBinding;
import com.longx.intelligent.android.imessage.util.ErrorLogger;
import com.longx.intelligent.android.imessage.yier.GlobalYiersHolder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChannelCollectionActivity extends BaseActivity implements ContentUpdater.OnServerContentUpdateYier {
    private ActivityChannelCollectionBinding binding;
    private ChannelCollectionAdapter adapter;
    private int lastScrollPosition = -1;
    private int lastScrollOffset = 0;
    private List<Channel> canAddChannels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChannelCollectionBinding.inflate(getLayoutInflater());
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
        SharedPreferencesAccessor.SortPref.ChannelCollectionSortBy channelCollectionSortBy = SharedPreferencesAccessor.SortPref.getChannelCollectionSortBy(this);
        switch (channelCollectionSortBy){
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

    public ActivityChannelCollectionBinding getBinding() {
        return binding;
    }

    public void showContent() {
        List<ChannelCollectionItem> allChannelCollections = ChannelDatabaseManager.getInstance().findAllChannelCollections();
        if (allChannelCollections.isEmpty()) {
            toNoContent();
        } else {
            toContent();
        }
        List<ChannelCollectionAdapter.ItemData> itemDataList = new ArrayList<>();
        for (ChannelCollectionItem channelCollectionItem : allChannelCollections) {
            Channel channel = ChannelDatabaseManager.getInstance().findOneChannel(channelCollectionItem.getChannelId());
            itemDataList.add(new ChannelCollectionAdapter.ItemData(channel, channelCollectionItem.getAddTime()));
        }
        adapter = new ChannelCollectionAdapter(this, itemDataList);
        updateSort();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);
        List<ChannelAssociation> allAssociations = ChannelDatabaseManager.getInstance().findAllAssociations();
        canAddChannels = new ArrayList<>();
        Set<String> collectedChannelIds = new HashSet<>();
        for (ChannelCollectionItem item : allChannelCollections) {
            collectedChannelIds.add(item.getChannelId());
        }
        for (ChannelAssociation association : allAssociations) {
            Channel channel = association.getChannel();
            if (!collectedChannelIds.contains(channel.getImessageId())) {
                canAddChannels.add(channel);
            }
        }
        binding.toolbar.getMenu().findItem(R.id.add).setEnabled(!canAddChannels.isEmpty());
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
        if(id.equals(ContentUpdater.OnServerContentUpdateYier.ID_CHANNEL_COLLECTIONS)){
            showContent();
        }
    }

    private void setupYiers() {
        binding.toolbar.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.sort_by_custom) {
                SharedPreferencesAccessor.SortPref.saveChannelCollectionSortBy(this, SharedPreferencesAccessor.SortPref.ChannelCollectionSortBy.CUSTOM);
                updateMenuChecked();
                updateSort();
            } else if (id == R.id.custom_sort) {

            }else if(id == R.id.new_to_old){
                SharedPreferencesAccessor.SortPref.saveChannelCollectionSortBy(this, SharedPreferencesAccessor.SortPref.ChannelCollectionSortBy.NEW_TO_OLD);
                updateMenuChecked();
                updateSort();
            }else if(id == R.id.old_to_new){
                SharedPreferencesAccessor.SortPref.saveChannelCollectionSortBy(this, SharedPreferencesAccessor.SortPref.ChannelCollectionSortBy.OLD_TO_NEW);
                updateMenuChecked();
                updateSort();
            }else if(id == R.id.a_to_z){
                SharedPreferencesAccessor.SortPref.saveChannelCollectionSortBy(this, SharedPreferencesAccessor.SortPref.ChannelCollectionSortBy.A_TO_Z);
                updateMenuChecked();
                updateSort();
            }else if(id == R.id.z_to_a){
                SharedPreferencesAccessor.SortPref.saveChannelCollectionSortBy(this, SharedPreferencesAccessor.SortPref.ChannelCollectionSortBy.Z_TO_A);
                updateMenuChecked();
                updateSort();
            }else if(id == R.id.add){
                new AddChannelCollectionBottomSheet(this, canAddChannels).show();
            }
            return true;
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
        SharedPreferencesAccessor.SortPref.ChannelCollectionSortBy channelCollectionSortBy = SharedPreferencesAccessor.SortPref.getChannelCollectionSortBy(this);
        adapter.sort(channelCollectionSortBy);
        binding.recyclerView.post(() -> {
            LinearLayoutManager lm = (LinearLayoutManager) binding.recyclerView.getLayoutManager();
            if (lm != null && lastScrollPosition >= 0) {
                lm.scrollToPositionWithOffset(lastScrollPosition, lastScrollOffset);
            }
        });
    }
}