package com.longx.intelligent.android.imessage.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.appbar.AppBarLayout;
import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.imessage.activity.helper.BaseActivity;
import com.longx.intelligent.android.imessage.adapter.SettingTagGroupChannelTagsRecyclerAdapter;
import com.longx.intelligent.android.imessage.adapter.SettingTagNewGroupChannelTagsRecyclerAdapter;
import com.longx.intelligent.android.imessage.behaviorcomponents.ContentUpdater;
import com.longx.intelligent.android.imessage.behaviorcomponents.MessageDisplayer;
import com.longx.intelligent.android.imessage.bottomsheet.AddSettingChannelTagBottomSheet;
import com.longx.intelligent.android.imessage.da.database.manager.GroupChannelDatabaseManager;
import com.longx.intelligent.android.imessage.data.GroupChannel;
import com.longx.intelligent.android.imessage.data.GroupChannelTag;
import com.longx.intelligent.android.imessage.data.request.SetGroupChannelTagsPostBody;
import com.longx.intelligent.android.imessage.data.response.OperationStatus;
import com.longx.intelligent.android.imessage.databinding.ActivitySetGroupChannelTagBinding;
import com.longx.intelligent.android.imessage.net.retrofit.caller.GroupChannelApiCaller;
import com.longx.intelligent.android.imessage.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.imessage.yier.GlobalYiersHolder;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class SetGroupChannelTagActivity extends BaseActivity implements ContentUpdater.OnServerContentUpdateYier {
    private ActivitySetGroupChannelTagBinding binding;
    private SettingTagNewGroupChannelTagsRecyclerAdapter newGroupChannelTagsAdapter;
    private SettingTagGroupChannelTagsRecyclerAdapter groupChannelTagsAdapter;
    private GroupChannel groupChannel;
    private List<GroupChannelTag> allGroupChannelTags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySetGroupChannelTagBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupDefaultBackNavigation(binding.toolbar);
        intentData();
        newGroupChannelTagsAdapter = new SettingTagNewGroupChannelTagsRecyclerAdapter(this);
        binding.recyclerViewNewTags.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewNewTags.setAdapter(newGroupChannelTagsAdapter);
        binding.recyclerViewTags.setLayoutManager(new LinearLayoutManager(this));
        updateContent();
        setupYiers();
        GlobalYiersHolder.holdYier(this, ContentUpdater.OnServerContentUpdateYier.class, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GlobalYiersHolder.removeYier(this, ContentUpdater.OnServerContentUpdateYier.class, this);
    }

    private void intentData() {
        groupChannel = getIntent().getParcelableExtra(ExtraKeys.GROUP_CHANNEL);
    }

    private void updateContent() {
        allGroupChannelTags = GroupChannelDatabaseManager.getInstance().findAllGroupChannelTags();
        groupChannelTagsAdapter = new SettingTagGroupChannelTagsRecyclerAdapter(this, allGroupChannelTags, groupChannel);
        binding.recyclerViewTags.setAdapter(groupChannelTagsAdapter);
        checkAndShowContent();
    }

    private void checkAndShowContent() {
        if(allGroupChannelTags.isEmpty() && newGroupChannelTagsAdapter.getNewTagNames().isEmpty()){
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
                startActivity(new Intent(this, GroupTagActivity.class));
            }else if(item.getItemId() == R.id.add_tag){
                new AddSettingChannelTagBottomSheet(this, results -> {
                    String inputtedTagName = (String) results[0];
                    newGroupChannelTagsAdapter.addAndShow(inputtedTagName);
                    binding.layoutNewTags.setVisibility(View.VISIBLE);
                    checkAndShowContent();
                }).show();
            }
            return false;
        });
        newGroupChannelTagsAdapter.setOnDeleteClickYier((position, view) -> {
            newGroupChannelTagsAdapter.removeAndShow(position);
            if(newGroupChannelTagsAdapter.getItemCount() == 0){
                binding.layoutNewTags.setVisibility(View.GONE);
            }
            checkAndShowContent();
        });
        binding.doneButton.setOnClickListener(v -> {
            List<String> newTagNames = newGroupChannelTagsAdapter.getNewTagNames();
            List<String> toAddTagIds = new ArrayList<>();
            groupChannelTagsAdapter.getToAddGroupChannelTags().forEach(channelTag -> {
                toAddTagIds.add(channelTag.getTagId());
            });
            List<String> toRemoveTagIds = new ArrayList<>();
            groupChannelTagsAdapter.getToRemoveGroupChannelTags().forEach(channelTag -> {
                toRemoveTagIds.add(channelTag.getTagId());
            });
            SetGroupChannelTagsPostBody postBody = new SetGroupChannelTagsPostBody(groupChannel.getGroupChannelId(), newTagNames, toAddTagIds, toRemoveTagIds);
            GroupChannelApiCaller.setGroupChannelTags(this, postBody, new RetrofitApiCaller.CommonYier<OperationStatus>(this){
                @Override
                public void ok(OperationStatus data, Response<OperationStatus> raw, Call<OperationStatus> call) {
                    super.ok(data, raw, call);
                    data.commonHandleResult(getActivity(), new int[]{-101, -102}, () -> {
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
        if(id.equals(ContentUpdater.OnServerContentUpdateYier.ID_GROUP_CHANNEL_TAGS) || (id.equals(ContentUpdater.OnServerContentUpdateYier.ID_GROUP_CHANNEL) && objects[0].equals(groupChannel.getGroupChannelId()))){
            updateContent();
            setupYiers();
        }
    }
}