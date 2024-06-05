package com.longx.intelligent.android.ichat2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.activity.helper.BaseActivity;
import com.longx.intelligent.android.ichat2.adapter.SettingTagChannelTagsRecyclerAdapter;
import com.longx.intelligent.android.ichat2.adapter.SettingTagNewChannelTagsRecyclerAdapter;
import com.longx.intelligent.android.ichat2.bottomsheet.AddSettingChannelTagBottomSheet;
import com.longx.intelligent.android.ichat2.da.database.manager.ChannelDatabaseManager;
import com.longx.intelligent.android.ichat2.data.Channel;
import com.longx.intelligent.android.ichat2.data.ChannelTag;
import com.longx.intelligent.android.ichat2.databinding.ActivitySettingChannelTagBinding;
import com.longx.intelligent.android.ichat2.yier.RecyclerItemYiers;
import com.longx.intelligent.android.ichat2.yier.ResultsYier;

import java.util.List;

public class SettingChannelTagActivity extends BaseActivity {
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
    }
}