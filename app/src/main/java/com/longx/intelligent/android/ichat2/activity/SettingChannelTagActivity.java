package com.longx.intelligent.android.ichat2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.activity.helper.BaseActivity;
import com.longx.intelligent.android.ichat2.adapter.SettingTagChannelTagsRecyclerAdapter;
import com.longx.intelligent.android.ichat2.adapter.SettingTagNewChannelTagsRecyclerAdapter;
import com.longx.intelligent.android.ichat2.procedure.ContentUpdater;
import com.longx.intelligent.android.ichat2.procedure.MessageDisplayer;
import com.longx.intelligent.android.ichat2.bottomsheet.AddSettingChannelTagBottomSheet;
import com.longx.intelligent.android.ichat2.da.database.manager.ChannelDatabaseManager;
import com.longx.intelligent.android.ichat2.data.Channel;
import com.longx.intelligent.android.ichat2.data.ChannelTag;
import com.longx.intelligent.android.ichat2.data.request.SetChannelTagsPostBody;
import com.longx.intelligent.android.ichat2.data.response.OperationStatus;
import com.longx.intelligent.android.ichat2.databinding.ActivitySettingChannelTagBinding;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.ChannelApiCaller;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.ichat2.yier.GlobalYiersHolder;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class SettingChannelTagActivity extends BaseActivity implements ContentUpdater.OnServerContentUpdateYier {
    private ActivitySettingChannelTagBinding binding;
    private Channel channel;
    private SettingTagNewChannelTagsRecyclerAdapter newChannelTagsAdapter;
    private SettingTagChannelTagsRecyclerAdapter channelTagsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingChannelTagBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupDefaultBackNavigation(binding.toolbar);
        String channelIchatId = getIntent().getStringExtra(ExtraKeys.ICHAT_ID);
        channel = ChannelDatabaseManager.getInstance().findOneChannel(channelIchatId);
        showContent();
        setupYiers();
        GlobalYiersHolder.holdYier(this, ContentUpdater.OnServerContentUpdateYier.class, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GlobalYiersHolder.removeYier(this, ContentUpdater.OnServerContentUpdateYier.class, this);
    }

    private void showContent() {
        binding.recyclerViewNewTags.setLayoutManager(new LinearLayoutManager(this));
        newChannelTagsAdapter = new SettingTagNewChannelTagsRecyclerAdapter(this);
        binding.recyclerViewNewTags.setAdapter(newChannelTagsAdapter);
        binding.recyclerViewTags.setLayoutManager(new LinearLayoutManager(this));
        List<ChannelTag> allChannelTags = ChannelDatabaseManager.getInstance().findAllChannelTags();
        channelTagsAdapter = new SettingTagChannelTagsRecyclerAdapter(this, allChannelTags, channel);
        binding.recyclerViewTags.setAdapter(channelTagsAdapter);
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
                }).show();
            }
            return false;
        });
        newChannelTagsAdapter.setOnDeleteClickYier((position, view) -> {
            newChannelTagsAdapter.removeAndShow(position);
            if(newChannelTagsAdapter.getItemCount() == 0){
                binding.layoutNewTags.setVisibility(View.GONE);
            }
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
            SetChannelTagsPostBody postBody = new SetChannelTagsPostBody(channel.getIchatId(), newTagNames, toAddTagIds, toRemoveTagIds);
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
    public void onStartUpdate(String id, List<String> updatingIds) {

    }

    @Override
    public void onUpdateComplete(String id, List<String> updatingIds) {
        if(id.equals(ContentUpdater.OnServerContentUpdateYier.ID_CHANNEL_TAGS)){
            showContent();
            setupYiers();
        }
    }
}