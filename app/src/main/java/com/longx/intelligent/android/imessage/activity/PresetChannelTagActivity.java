package com.longx.intelligent.android.imessage.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.imessage.activity.helper.BaseActivity;
import com.longx.intelligent.android.imessage.adapter.PresettingTagChannelTagsRecyclerAdapter;
import com.longx.intelligent.android.imessage.adapter.PresettingTagNewChannelTagsRecyclerAdapter;
import com.longx.intelligent.android.imessage.behaviorcomponents.ContentUpdater;
import com.longx.intelligent.android.imessage.bottomsheet.AddSettingChannelTagBottomSheet;
import com.longx.intelligent.android.imessage.da.database.manager.ChannelDatabaseManager;
import com.longx.intelligent.android.imessage.data.ChannelTag;
import com.longx.intelligent.android.imessage.databinding.ActivityPresetChannelTagBinding;
import com.longx.intelligent.android.imessage.util.ErrorLogger;
import com.longx.intelligent.android.imessage.util.Utils;
import com.longx.intelligent.android.imessage.yier.GlobalYiersHolder;

import java.util.ArrayList;
import java.util.List;

public class PresetChannelTagActivity extends BaseActivity implements ContentUpdater.OnServerContentUpdateYier {
    private ActivityPresetChannelTagBinding binding;
    private PresettingTagNewChannelTagsRecyclerAdapter newChannelTagsAdapter;
    private PresettingTagChannelTagsRecyclerAdapter channelTagsAdapter;
    private ArrayList<ChannelTag> checkedChannelTags;
    private ArrayList<String> newChannelTagNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPresetChannelTagBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupDefaultBackNavigation(binding.toolbar);
        getIntentData();
        showContent();
        setupYiers();
        GlobalYiersHolder.holdYier(this, ContentUpdater.OnServerContentUpdateYier.class, this);
    }

    private void getIntentData() {
        ArrayList<Parcelable> checkedChannelTagsParcelable = getIntent().getParcelableArrayListExtra(ExtraKeys.CHANNEL_TAGS);
        checkedChannelTags = Utils.parseParcelableArray(checkedChannelTagsParcelable);
        newChannelTagNames = getIntent().getStringArrayListExtra(ExtraKeys.CHANNEL_TAG_NAMES);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GlobalYiersHolder.removeYier(this, ContentUpdater.OnServerContentUpdateYier.class, this);
    }

    private void showContent() {
        List<ChannelTag> allChannelTags = ChannelDatabaseManager.getInstance().findAllChannelTags();
        binding.recyclerViewNewTags.setLayoutManager(new LinearLayoutManager(this));
        newChannelTagsAdapter = new PresettingTagNewChannelTagsRecyclerAdapter(this, newChannelTagNames);
        binding.recyclerViewNewTags.setAdapter(newChannelTagsAdapter);
        if(!newChannelTagNames.isEmpty()) binding.layoutNewTags.setVisibility(View.VISIBLE);
        binding.recyclerViewTags.setLayoutManager(new LinearLayoutManager(this));
        channelTagsAdapter = new PresettingTagChannelTagsRecyclerAdapter(this, allChannelTags, checkedChannelTags);
        binding.recyclerViewTags.setAdapter(channelTagsAdapter);
        if(allChannelTags.isEmpty()){
            binding.noContentLayout.setVisibility(View.VISIBLE);
            binding.contentScrollView.setVisibility(View.GONE);
        }else {
            binding.noContentLayout.setVisibility(View.GONE);
            binding.contentScrollView.setVisibility(View.VISIBLE);
        }
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
            Intent resultIntent = new Intent();
            resultIntent.putStringArrayListExtra(ExtraKeys.CHANNEL_TAG_NAMES, newChannelTagsAdapter.getNewTagNames());
            resultIntent.putParcelableArrayListExtra(ExtraKeys.CHANNEL_TAGS, channelTagsAdapter.getCheckedChannelTags());
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
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