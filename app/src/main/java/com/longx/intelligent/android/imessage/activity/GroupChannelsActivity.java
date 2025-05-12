package com.longx.intelligent.android.imessage.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.appbar.AppBarLayout;
import com.longx.intelligent.android.imessage.activity.helper.BaseActivity;
import com.longx.intelligent.android.imessage.adapter.GroupChannelsRecyclerAdapter;
import com.longx.intelligent.android.imessage.behaviorcomponents.ContentUpdater;
import com.longx.intelligent.android.imessage.bottomsheet.AddGroupChannelBottomSheet;
import com.longx.intelligent.android.imessage.da.database.manager.GroupChannelDatabaseManager;
import com.longx.intelligent.android.imessage.data.GroupChannel;
import com.longx.intelligent.android.imessage.databinding.ActivityGroupChannelsBinding;
import com.longx.intelligent.android.imessage.databinding.RecyclerHeaderGroupChannelBinding;
import com.longx.intelligent.android.imessage.ui.BadgeDisplayer;
import com.longx.intelligent.android.imessage.yier.GlobalYiersHolder;
import com.longx.intelligent.android.imessage.yier.NewContentBadgeDisplayYier;
import com.longx.intelligent.android.imessage.yier.UpdateGroupChannelYier;
import com.longx.intelligent.android.lib.recyclerview.RecyclerView;
import com.longx.intelligent.android.lib.recyclerview.WrappableRecyclerViewAdapter;

import java.util.List;

import q.rorbin.badgeview.Badge;

public class GroupChannelsActivity extends BaseActivity implements ContentUpdater.OnServerContentUpdateYier, WrappableRecyclerViewAdapter.OnItemClickYier<GroupChannelsRecyclerAdapter.ItemData>, NewContentBadgeDisplayYier {
    private ActivityGroupChannelsBinding binding;
    private RecyclerHeaderGroupChannelBinding headerViewBinding;
    private GroupChannelsRecyclerAdapter adapter;
    private int groupChannelAdditionActivitiesNewContentCount;
    private Badge newGroupChannelBadge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGroupChannelsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupDefaultBackNavigation(binding.toolbar);
        showContent();
        setUpYiers();
        GlobalYiersHolder.holdYier(this, ContentUpdater.OnServerContentUpdateYier.class, this);
        GlobalYiersHolder.holdYier(this, NewContentBadgeDisplayYier.class, this, ID.GROUP_CHANNEL_ADDITION_ACTIVITIES);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GlobalYiersHolder.removeYier(this, ContentUpdater.OnServerContentUpdateYier.class, this);
        GlobalYiersHolder.removeYier(this, NewContentBadgeDisplayYier.class, this, ID.GROUP_CHANNEL_ADDITION_ACTIVITIES);
    }

    private void showContent() {
        List<GroupChannel> allAssociations = GroupChannelDatabaseManager.getInstance().findAllAssociations();
        setupRecyclerView(allAssociations);
        if(allAssociations.isEmpty()){
            toNoContent();
        }else {
            toContent();
        }
    }

    private void setupRecyclerView(List<GroupChannel> allAssociations) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.recyclerView.setLayoutManager(layoutManager);
        headerViewBinding = RecyclerHeaderGroupChannelBinding.inflate(getLayoutInflater(), binding.recyclerView, false);
        adapter = new GroupChannelsRecyclerAdapter(this, allAssociations);
        adapter.setOnItemClickYier(this);
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setHeaderView(headerViewBinding.getRoot());
        binding.recyclerView.setItemAnimator(null);
        newGroupChannelBadge = BadgeDisplayer.initBadge(this, headerViewBinding.newGroupChannelBadgeHost, groupChannelAdditionActivitiesNewContentCount, Gravity.CENTER);
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
        headerViewBinding.layoutNewGroupChannel.setOnClickListener(v -> {
            startActivity(new Intent(this, GroupChannelAdditionsActivity.class));
        });
        headerViewBinding.layoutGroupTag.setOnClickListener(v -> {
            startActivity(new Intent(this, GroupTagActivity.class));
        });
        binding.fab.setOnClickListener(v -> {
            AddGroupChannelBottomSheet bottomSheet = new AddGroupChannelBottomSheet(this);
            bottomSheet.setExploreChannelClickYier(v1 -> {
                startActivity(new Intent(this, ExploreGroupChannelActivity.class));
            });
            bottomSheet.setCreateClickYier(v1 -> {
                startActivity(new Intent(this, CreateGroupChannelActivity.class));
            });
            bottomSheet.setInviteClickYier(v1 -> {
                startActivity(new Intent(this, InviteJoinGroupChannelActivity.class));
            });
            bottomSheet.show();
        });
    }

    public ActivityGroupChannelsBinding getBinding(){
        return binding;
    }

    @Override
    public void onStartUpdate(String id, List<String> updatingIds, Object... objects) {

    }

    @Override
    public void onUpdateComplete(String id, List<String> updatingIds, Object... objects) {
        if(id.equals(ContentUpdater.OnServerContentUpdateYier.ID_GROUP_CHANNELS) || id.equals(ContentUpdater.OnServerContentUpdateYier.ID_GROUP_CHANNEL)){
            List<GroupChannel> allAssociations = GroupChannelDatabaseManager.getInstance().findAllAssociations();
            adapter.updateAll(allAssociations);
        }
    }

    @Override
    public void onItemClick(int position, GroupChannelsRecyclerAdapter.ItemData data) {
        Intent intent = new Intent(this, GroupChannelActivity.class);
        intent.putExtra(ExtraKeys.GROUP_CHANNEL, data.getGroupChannel());
        startActivity(intent);
    }

    @Override
    public void showNewContentBadge(ID id, int newContentCount) {
        if(id.equals(ID.GROUP_CHANNEL_ADDITION_ACTIVITIES)){
            groupChannelAdditionActivitiesNewContentCount = newContentCount;
            if(newGroupChannelBadge != null){
                newGroupChannelBadge.setBadgeNumber(newContentCount);
            }
        }
    }
}