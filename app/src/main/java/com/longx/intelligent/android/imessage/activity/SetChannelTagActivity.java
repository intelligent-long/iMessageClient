package com.longx.intelligent.android.imessage.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.appbar.AppBarLayout;
import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.imessage.activity.helper.BaseActivity;
import com.longx.intelligent.android.imessage.adapter.SettingTagChannelTagsRecyclerAdapter;
import com.longx.intelligent.android.imessage.adapter.SettingTagNewChannelTagsRecyclerAdapter;
import com.longx.intelligent.android.imessage.behaviorcomponents.ContentUpdater;
import com.longx.intelligent.android.imessage.behaviorcomponents.MessageDisplayer;
import com.longx.intelligent.android.imessage.bottomsheet.AddSettingChannelTagBottomSheet;
import com.longx.intelligent.android.imessage.da.database.manager.ChannelDatabaseManager;
import com.longx.intelligent.android.imessage.data.Channel;
import com.longx.intelligent.android.imessage.data.ChannelTag;
import com.longx.intelligent.android.imessage.data.request.SetChannelTagsPostBody;
import com.longx.intelligent.android.imessage.data.response.OperationStatus;
import com.longx.intelligent.android.imessage.databinding.ActivitySetChannelTagBinding;
import com.longx.intelligent.android.imessage.net.retrofit.caller.ChannelApiCaller;
import com.longx.intelligent.android.imessage.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.imessage.yier.GlobalYiersHolder;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class SetChannelTagActivity extends BaseActivity implements ContentUpdater.OnServerContentUpdateYier {
    private ActivitySetChannelTagBinding binding;
    private Channel channel;
    private SettingTagNewChannelTagsRecyclerAdapter newChannelTagsAdapter;
    private SettingTagChannelTagsRecyclerAdapter channelTagsAdapter;
    private List<ChannelTag> allChannelTags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySetChannelTagBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupDefaultBackNavigation(binding.toolbar);
        String channelImessageId = getIntent().getStringExtra(ExtraKeys.IMESSAGE_ID);
        channel = ChannelDatabaseManager.getInstance().findOneChannel(channelImessageId);
        newChannelTagsAdapter = new SettingTagNewChannelTagsRecyclerAdapter(this);
        binding.recyclerViewNewTags.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewTags.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewNewTags.setAdapter(newChannelTagsAdapter);
        updateContent();
        setupYiers();
        GlobalYiersHolder.holdYier(this, ContentUpdater.OnServerContentUpdateYier.class, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GlobalYiersHolder.removeYier(this, ContentUpdater.OnServerContentUpdateYier.class, this);
    }

    private void updateContent() {
        allChannelTags = ChannelDatabaseManager.getInstance().findAllChannelTags();
        channelTagsAdapter = new SettingTagChannelTagsRecyclerAdapter(this, allChannelTags, channel);
        binding.recyclerViewTags.setAdapter(channelTagsAdapter);
        checkAndShowContent();
    }

    private void checkAndShowContent() {
        if(allChannelTags.isEmpty() && newChannelTagsAdapter.getNewTagNames().isEmpty()){
            toNoContent();
        }else {
            toContent();
        }
    }

    private void toNoContent(){
        ((AppBarLayout.LayoutParams)binding.collapsingToolbarLayout.getLayoutParams())
                .setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_NO_SCROLL);
        binding.noContentLayout.setVisibility(View.VISIBLE);
        binding.contentScrollView.setVisibility(View.GONE);
    }

    private void toContent(){
        ((AppBarLayout.LayoutParams)binding.collapsingToolbarLayout.getLayoutParams())
                .setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
                        | AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED
                        | AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP);
        binding.noContentLayout.setVisibility(View.GONE);
        binding.contentScrollView.setVisibility(View.VISIBLE);
    }

    private void setupYiers() {
        binding.toolbar.setOnMenuItemClickListener(item -> {
            if(item.getItemId() == R.id.tag){
                startActivity(new Intent(this, TagActivity.class));
            }else if(item.getItemId() == R.id.add_tag){
                new AddSettingChannelTagBottomSheet(this, results -> {
                    String inputtedTagName = (String) results[0];
                    newChannelTagsAdapter.addAndShow(inputtedTagName);
                    binding.layoutNewTags.setVisibility(View.VISIBLE);
                    checkAndShowContent();
                }).show();
            }
            return false;
        });
        newChannelTagsAdapter.setOnDeleteClickYier((position, view) -> {
            newChannelTagsAdapter.removeAndShow(position);
            if(newChannelTagsAdapter.getItemCount() == 0){
                binding.layoutNewTags.setVisibility(View.GONE);
            }
            checkAndShowContent();
        });
        binding.doneButton.setOnClickListener(v -> {
            List<String> newTagNames = newChannelTagsAdapter.getNewTagNames();
            List<String> toAddTagIds = new ArrayList<>();
            channelTagsAdapter.getToAddChannelTags().forEach(channelTag -> {
                toAddTagIds.add(channelTag.getTagId());
            });
            List<String> toRemoveTagIds = new ArrayList<>();
            channelTagsAdapter.getToRemoveChannelTags().forEach(channelTag -> {
                toRemoveTagIds.add(channelTag.getTagId());
            });
            SetChannelTagsPostBody postBody = new SetChannelTagsPostBody(channel.getImessageId(), newTagNames, toAddTagIds, toRemoveTagIds);
            ChannelApiCaller.setChannelTags(this, postBody, new RetrofitApiCaller.CommonYier<OperationStatus>(this){
                @Override
                public void ok(OperationStatus data, Response<OperationStatus> raw, Call<OperationStatus> call) {
                    super.ok(data, raw, call);
                    data.commonHandleResult(getActivity(), new int[]{-101}, () -> {
                        binding.layoutNewTags.setVisibility(View.GONE);
                        MessageDisplayer.autoShow(getActivity(), "设置成功", MessageDisplayer.Duration.SHORT);
                    });
                }
            });
        });
    }

    @Override
    public void onStartUpdate(String id, List<String> updatingIds, Object... objects) {

    }

    @Override
    public void onUpdateComplete(String id, List<String> updatingIds, Object... objects) {
        if(id.equals(ContentUpdater.OnServerContentUpdateYier.ID_CHANNEL_TAGS)){
            updateContent();
            setupYiers();
        }
    }
}