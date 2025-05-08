package com.longx.intelligent.android.imessage.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.appbar.AppBarLayout;
import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.imessage.activity.helper.BaseActivity;
import com.longx.intelligent.android.imessage.adapter.PresettingTagGroupChannelTagsRecyclerAdapter;
import com.longx.intelligent.android.imessage.adapter.PresettingTagNewGroupChannelTagsRecyclerAdapter;
import com.longx.intelligent.android.imessage.behaviorcomponents.ContentUpdater;
import com.longx.intelligent.android.imessage.bottomsheet.AddSettingGroupChannelTagBottomSheet;
import com.longx.intelligent.android.imessage.da.database.manager.GroupChannelDatabaseManager;
import com.longx.intelligent.android.imessage.data.GroupChannelTag;
import com.longx.intelligent.android.imessage.databinding.ActivityPresetGroupChannelTagBinding;
import com.longx.intelligent.android.imessage.util.Utils;
import com.longx.intelligent.android.imessage.yier.GlobalYiersHolder;

import java.util.ArrayList;
import java.util.List;

public class PresetGroupChannelTagActivity extends BaseActivity implements ContentUpdater.OnServerContentUpdateYier {
    private ActivityPresetGroupChannelTagBinding binding;
    private PresettingTagNewGroupChannelTagsRecyclerAdapter newGroupChannelTagsAdapter;
    private PresettingTagGroupChannelTagsRecyclerAdapter groupChannelTagsAdapter;
    private ArrayList<GroupChannelTag> checkedGroupChannelTags;
    private ArrayList<String> newGroupChannelTagNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPresetGroupChannelTagBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupDefaultBackNavigation(binding.toolbar);
        getIntentData();
        showContent();
        setupYiers();
        GlobalYiersHolder.holdYier(this, ContentUpdater.OnServerContentUpdateYier.class, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GlobalYiersHolder.removeYier(this, ContentUpdater.OnServerContentUpdateYier.class, this);
    }

    private void getIntentData() {
        ArrayList<Parcelable> checkedGroupChannelTagsParcelable = getIntent().getParcelableArrayListExtra(ExtraKeys.GROUP_CHANNEL_TAGS);
        checkedGroupChannelTags = Utils.parseParcelableArray(checkedGroupChannelTagsParcelable);
        newGroupChannelTagNames = getIntent().getStringArrayListExtra(ExtraKeys.GROUP_CHANNEL_TAG_NAMES);
    }

    private void showContent() {
        binding.recyclerViewNewGroupTags.setLayoutManager(new LinearLayoutManager(this));
        newGroupChannelTagsAdapter = new PresettingTagNewGroupChannelTagsRecyclerAdapter(this, newGroupChannelTagNames);
        binding.recyclerViewNewGroupTags.setAdapter(newGroupChannelTagsAdapter);
        if(!newGroupChannelTagNames.isEmpty()) binding.layoutNewTags.setVisibility(View.VISIBLE);
        binding.recyclerViewGroupTags.setLayoutManager(new LinearLayoutManager(this));
        List<GroupChannelTag> allGroupChannelTags = GroupChannelDatabaseManager.getInstance().findAllGroupChannelTags();
        groupChannelTagsAdapter = new PresettingTagGroupChannelTagsRecyclerAdapter(this, allGroupChannelTags, checkedGroupChannelTags);
        binding.recyclerViewGroupTags.setAdapter(groupChannelTagsAdapter);
        if(allGroupChannelTags.isEmpty()){
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
                new AddSettingGroupChannelTagBottomSheet(this, results -> {
                    String inputtedTagName = (String) results[0];
                    newGroupChannelTagsAdapter.addAndShow(inputtedTagName);
                    binding.layoutNewTags.setVisibility(View.VISIBLE);
                }).show();
            }
            return false;
        });
        newGroupChannelTagsAdapter.setOnDeleteClickYier((position, view) -> {
            newGroupChannelTagsAdapter.removeAndShow(position);
            if(newGroupChannelTagsAdapter.getItemCount() == 0){
                binding.layoutNewTags.setVisibility(View.GONE);
            }
        });
        binding.doneButton.setOnClickListener(v -> {
            Intent resultIntent = new Intent();
            resultIntent.putStringArrayListExtra(ExtraKeys.GROUP_CHANNEL_TAG_NAMES, newGroupChannelTagsAdapter.getNewTagNames());
            resultIntent.putParcelableArrayListExtra(ExtraKeys.GROUP_CHANNEL_TAGS, groupChannelTagsAdapter.getCheckedGroupChannelTags());
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        });
    }

    @Override
    public void onStartUpdate(String id, List<String> updatingIds, Object... objects) {

    }

    @Override
    public void onUpdateComplete(String id, List<String> updatingIds, Object... objects) {
        if(id.equals(ContentUpdater.OnServerContentUpdateYier.ID_GROUP_CHANNEL_TAGS)){
            showContent();
            setupYiers();
        }
    }
}