package com.longx.intelligent.android.imessage.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.longx.intelligent.android.imessage.activity.helper.BaseActivity;
import com.longx.intelligent.android.imessage.adapter.GroupMembersRecyclerAdapter;
import com.longx.intelligent.android.imessage.da.database.manager.GroupChannelDatabaseManager;
import com.longx.intelligent.android.imessage.data.GroupChannelAssociation;
import com.longx.intelligent.android.imessage.databinding.ActivityGroupMembersBinding;
import com.longx.intelligent.android.imessage.util.ErrorLogger;
import com.longx.intelligent.android.lib.recyclerview.WrappableRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class GroupMembersActivity extends BaseActivity {
    private ActivityGroupMembersBinding binding;
    private String groupId;
    List<GroupChannelAssociation> groupChannelAssociations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGroupMembersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupDefaultBackNavigation(binding.toolbar);
        intentData();
        showContent();
    }

    private void intentData() {
        groupId = getIntent().getStringExtra(ExtraKeys.GROUP_CHANNEL_ID);
        groupChannelAssociations = GroupChannelDatabaseManager.getInstance().findOneAssociation(groupId).getGroupChannelAssociations();
    }

    public ActivityGroupMembersBinding getBinding() {
        return binding;
    }

    private void showContent() {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<GroupMembersRecyclerAdapter.ItemData> itemDataList = new ArrayList<>();
        groupChannelAssociations.forEach(groupChannelAssociation -> {
            itemDataList.add(new GroupMembersRecyclerAdapter.ItemData(groupChannelAssociation.getRequester()));
        });
        GroupMembersRecyclerAdapter adapter = new GroupMembersRecyclerAdapter(this, itemDataList);
        adapter.setOnItemClickYier((position, data) -> {
            Intent intent = new Intent(this, ChannelActivity.class);
            intent.putExtra(ExtraKeys.IMESSAGE_ID, data.getChannel().getImessageId());
            intent.putExtra(ExtraKeys.CHANNEL, data.getChannel());
            startActivity(intent);
        });
        binding.recyclerView.setAdapter(adapter);
    }
}