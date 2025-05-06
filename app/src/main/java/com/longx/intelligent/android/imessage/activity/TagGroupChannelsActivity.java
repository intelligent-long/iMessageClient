package com.longx.intelligent.android.imessage.activity;

import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.appbar.AppBarLayout;
import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.imessage.activity.helper.BaseActivity;
import com.longx.intelligent.android.imessage.adapter.TagGroupChannelsRecyclerAdapter;
import com.longx.intelligent.android.imessage.behaviorcomponents.ContentUpdater;
import com.longx.intelligent.android.imessage.bottomsheet.AddGroupChannelsToTagBottomSheet;
import com.longx.intelligent.android.imessage.da.database.manager.GroupChannelDatabaseManager;
import com.longx.intelligent.android.imessage.data.GroupChannel;
import com.longx.intelligent.android.imessage.data.GroupChannelTag;
import com.longx.intelligent.android.imessage.databinding.ActivityTagGroupChannelsBinding;
import com.longx.intelligent.android.imessage.util.ErrorLogger;
import com.longx.intelligent.android.imessage.yier.GlobalYiersHolder;

import java.util.ArrayList;
import java.util.List;

public class TagGroupChannelsActivity extends BaseActivity implements ContentUpdater.OnServerContentUpdateYier{
    private ActivityTagGroupChannelsBinding binding;
    private GroupChannelTag groupChannelTag;
    private TagGroupChannelsRecyclerAdapter adapter;
    private List<GroupChannel> canAddChannels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTagGroupChannelsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupDefaultBackNavigation(binding.toolbar);
        findChannelTag();
        showContent();
        setupYiers();
        GlobalYiersHolder.holdYier(this, ContentUpdater.OnServerContentUpdateYier.class, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GlobalYiersHolder.removeYier(this, ContentUpdater.OnServerContentUpdateYier.class, this);
    }

    private void findChannelTag() {
        String groupChannelTagId = getIntent().getStringExtra(ExtraKeys.GROUP_CHANNEL_TAG_ID);
        groupChannelTag = GroupChannelDatabaseManager.getInstance().findOneGroupChannelTag(groupChannelTagId);
    }

    private void showContent() {
        binding.toolbar.setTitle(groupChannelTag.getName());
        if(groupChannelTag.getGroupChannelIdList().isEmpty()){
            toNoContent();
        }else {
            toContent();
            binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
            List<String> groupChannelIds = groupChannelTag.getGroupChannelIdList();
            List<GroupChannel> groupChannels = new ArrayList<>();
            groupChannelIds.forEach(groupChannelId -> {
                GroupChannel groupChannel = GroupChannelDatabaseManager.getInstance().findOneAssociation(groupChannelId);
                groupChannels.add(groupChannel);
            });
            adapter = new TagGroupChannelsRecyclerAdapter(this, groupChannelTag, groupChannels);
            binding.recyclerView.setAdapter(adapter);
        }
        List<GroupChannel> groupChannels = GroupChannelDatabaseManager.getInstance().findAllAssociations();
        canAddChannels = new ArrayList<>();
        groupChannels.forEach(groupChannel -> {
            if(!groupChannelTag.getGroupChannelIdList().contains(groupChannel.getGroupChannelId())) {
                canAddChannels.add(groupChannel);
            }
        });
        binding.toolbar.getMenu().findItem(R.id.add_channel).setVisible(!canAddChannels.isEmpty());
    }

    private void setupYiers() {
        binding.toolbar.setOnMenuItemClickListener(item -> {
            if(item.getItemId() == R.id.add_channel){
                new AddGroupChannelsToTagBottomSheet(this, groupChannelTag.getTagId(), canAddChannels).show();
            }
            return true;
        });
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

    public ActivityTagGroupChannelsBinding getBinding() {
        return binding;
    }

    @Override
    public void onStartUpdate(String id, List<String> updatingIds, Object... objects) {

    }

    @Override
    public void onUpdateComplete(String id, List<String> updatingIds, Object... objects) {
        if(id.equals(ContentUpdater.OnServerContentUpdateYier.ID_GROUP_CHANNEL_TAGS)){
            findChannelTag();
            showContent();
        }
    }
}