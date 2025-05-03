package com.longx.intelligent.android.imessage.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.appbar.AppBarLayout;
import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.imessage.activity.helper.BaseActivity;
import com.longx.intelligent.android.imessage.adapter.SettingTagChannelTagsRecyclerAdapter;
import com.longx.intelligent.android.imessage.adapter.SettingTagGroupChannelTagsRecyclerAdapter;
import com.longx.intelligent.android.imessage.adapter.SettingTagNewChannelTagsRecyclerAdapter;
import com.longx.intelligent.android.imessage.adapter.SettingTagNewGroupChannelTagsRecyclerAdapter;
import com.longx.intelligent.android.imessage.behaviorcomponents.ContentUpdater;
import com.longx.intelligent.android.imessage.bottomsheet.AddSettingChannelTagBottomSheet;
import com.longx.intelligent.android.imessage.da.database.manager.GroupChannelDatabaseManager;
import com.longx.intelligent.android.imessage.data.GroupChannel;
import com.longx.intelligent.android.imessage.data.GroupChannelTag;
import com.longx.intelligent.android.imessage.databinding.ActivitySetGroupChannelTagBinding;
import com.longx.intelligent.android.imessage.yier.GlobalYiersHolder;

import java.util.List;

public class SetGroupChannelTagActivity extends BaseActivity implements ContentUpdater.OnServerContentUpdateYier {
    private ActivitySetGroupChannelTagBinding binding;
    private SettingTagNewGroupChannelTagsRecyclerAdapter newGroupChannelTagsAdapter;
    private SettingTagGroupChannelTagsRecyclerAdapter groupChannelTagsAdapter;
    private GroupChannel groupChannel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySetGroupChannelTagBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        intentData();
        showContent();
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

    private void showContent() {
        binding.recyclerViewNewTags.setLayoutManager(new LinearLayoutManager(this));
        newGroupChannelTagsAdapter = new SettingTagNewGroupChannelTagsRecyclerAdapter(this);
        binding.recyclerViewNewTags.setAdapter(newGroupChannelTagsAdapter);
        List<GroupChannelTag> allGroupChannelTags = GroupChannelDatabaseManager.getInstance().findAllGroupChannelTags();
        binding.recyclerViewTags.setLayoutManager(new LinearLayoutManager(this));
        groupChannelTagsAdapter = new SettingTagGroupChannelTagsRecyclerAdapter(this, allGroupChannelTags, groupChannel);
        binding.recyclerViewTags.setAdapter(groupChannelTagsAdapter);
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
                new AddSettingChannelTagBottomSheet(this, results -> {
                    String inputtedTagName = (String) results[0];
                    newGroupChannelTagsAdapter.addAndShow(inputtedTagName);
                    binding.layoutNewTags.setVisibility(View.VISIBLE);
                }).show();
            }
            return false;
        });

    }

    @Override
    public void onStartUpdate(String id, List<String> updatingIds) {

    }

    @Override
    public void onUpdateComplete(String id, List<String> updatingIds) {
        if(id.equals(ContentUpdater.OnServerContentUpdateYier.ID_GROUP_CHANNEL_TAGS) || id.equals(ContentUpdater.OnServerContentUpdateYier.ID_GROUP_CHANNEL)){
            showContent();
            setupYiers();
        }
    }
}