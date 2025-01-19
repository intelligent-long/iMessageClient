package com.longx.intelligent.android.imessage.activity;

import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.appbar.AppBarLayout;
import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.imessage.activity.helper.BaseActivity;
import com.longx.intelligent.android.imessage.adapter.TagChannelsRecyclerAdapter;
import com.longx.intelligent.android.imessage.behaviorcomponents.ContentUpdater;
import com.longx.intelligent.android.imessage.bottomsheet.AddChannelToTagBottomSheet;
import com.longx.intelligent.android.imessage.da.database.manager.ChannelDatabaseManager;
import com.longx.intelligent.android.imessage.data.Channel;
import com.longx.intelligent.android.imessage.data.ChannelAssociation;
import com.longx.intelligent.android.imessage.data.ChannelTag;
import com.longx.intelligent.android.imessage.databinding.ActivityTagChannelBinding;
import com.longx.intelligent.android.imessage.yier.GlobalYiersHolder;

import java.util.ArrayList;
import java.util.List;

public class TagChannelActivity extends BaseActivity implements ContentUpdater.OnServerContentUpdateYier {
    private ActivityTagChannelBinding binding;
    private ChannelTag channelTag;
    private TagChannelsRecyclerAdapter adapter;
    private List<Channel> canAddChannels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTagChannelBinding.inflate(getLayoutInflater());
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
        String channelTagId = getIntent().getStringExtra(ExtraKeys.CHANNEL_TAG_ID);
        channelTag = ChannelDatabaseManager.getInstance().findOneChannelTags(channelTagId);
    }

    private void showContent() {
        binding.toolbar.setTitle(channelTag.getName());
        if(channelTag.getChannelIchatIdList().isEmpty()){
            toNoContent();
        }else {
            toContent();
            binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
            List<String> channelIchatIds = channelTag.getChannelIchatIdList();
            List<Channel> channels = new ArrayList<>();
            channelIchatIds.forEach(channelIchatId -> {
                Channel channel = ChannelDatabaseManager.getInstance().findOneChannel(channelIchatId);
                channels.add(channel);
            });
            adapter = new TagChannelsRecyclerAdapter(this, channelTag, channels);
            binding.recyclerView.setAdapter(adapter);
        }
        List<ChannelAssociation> allAssociations = ChannelDatabaseManager.getInstance().findAllAssociations();
        canAddChannels = new ArrayList<>();
        allAssociations.forEach(association -> {
            Channel channel = association.getChannel();
            if(!channelTag.getChannelIchatIdList().contains(channel.getIchatId())) {
                canAddChannels.add(channel);
            }
        });
        binding.toolbar.getMenu().findItem(R.id.add_channel).setVisible(!canAddChannels.isEmpty());
    }

    private void setupYiers() {
        binding.toolbar.setOnMenuItemClickListener(item -> {
            if(item.getItemId() == R.id.add_channel){
                new AddChannelToTagBottomSheet(this, channelTag.getTagId(), canAddChannels).show();
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

    @Override
    public void onStartUpdate(String id, List<String> updatingIds) {

    }

    @Override
    public void onUpdateComplete(String id, List<String> updatingIds) {
        if(id.equals(ContentUpdater.OnServerContentUpdateYier.ID_CHANNEL_TAGS)){
            findChannelTag();
            showContent();
            setupYiers();
        }
    }

    public ActivityTagChannelBinding getBinding() {
        return binding;
    }
}