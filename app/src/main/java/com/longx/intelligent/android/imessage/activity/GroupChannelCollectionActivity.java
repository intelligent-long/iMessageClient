package com.longx.intelligent.android.imessage.activity;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
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
import com.longx.intelligent.android.imessage.adapter.GroupChannelCollectionAdapter;
import com.longx.intelligent.android.imessage.behaviorcomponents.ContentUpdater;
import com.longx.intelligent.android.imessage.da.database.manager.ChannelDatabaseManager;
import com.longx.intelligent.android.imessage.da.database.manager.GroupChannelDatabaseManager;
import com.longx.intelligent.android.imessage.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.imessage.data.Channel;
import com.longx.intelligent.android.imessage.data.ChannelCollectionItem;
import com.longx.intelligent.android.imessage.data.GroupChannel;
import com.longx.intelligent.android.imessage.data.GroupChannelCollectionItem;
import com.longx.intelligent.android.imessage.databinding.ActivityChannelCollectionBinding;
import com.longx.intelligent.android.imessage.databinding.ActivityGroupChannelCollectionBinding;

import java.util.ArrayList;
import java.util.List;

public class GroupChannelCollectionActivity extends BaseActivity implements ContentUpdater.OnServerContentUpdateYier{
    private ActivityGroupChannelCollectionBinding binding;
    private GroupChannelCollectionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGroupChannelCollectionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupDefaultBackNavigation(binding.toolbar);
        init();
        showContent();
        setupYiers();
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
            itemDataList.add(new GroupChannelCollectionAdapter.ItemData(groupChannel, groupChannelCollectionItem.getAddTime()));
        }
        adapter = new GroupChannelCollectionAdapter(this, itemDataList);
        updateSort();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);
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
        binding.toolbar.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.sort_by_custom) {
                SharedPreferencesAccessor.SortPref.saveGroupChannelCollectionSortBy(this, SharedPreferencesAccessor.SortPref.GroupChannelCollectionSortBy.CUSTOM);
                updateMenuChecked();
                updateSort();
            } else if (id == R.id.custom_sort) {

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
            }
            return true;
        });
    }

    private void updateSort() {
        SharedPreferencesAccessor.SortPref.GroupChannelCollectionSortBy groupChannelCollectionSortBy = SharedPreferencesAccessor.SortPref.getGroupChannelCollectionSortBy(this);
        adapter.sort(groupChannelCollectionSortBy);
    }
}