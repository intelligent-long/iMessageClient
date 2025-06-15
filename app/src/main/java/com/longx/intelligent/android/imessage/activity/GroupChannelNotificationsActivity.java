package com.longx.intelligent.android.imessage.activity;

import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.longx.intelligent.android.imessage.activity.helper.BaseActivity;
import com.longx.intelligent.android.imessage.adapter.GroupChannelNotificationsRecyclerAdapter;
import com.longx.intelligent.android.imessage.da.database.manager.ChannelDatabaseManager;
import com.longx.intelligent.android.imessage.da.database.manager.GroupChannelDatabaseManager;
import com.longx.intelligent.android.imessage.data.GroupChannelNotification;
import com.longx.intelligent.android.imessage.databinding.ActivityGroupChannelNotificationsBinding;
import com.longx.intelligent.android.imessage.net.stomp.ServerMessageServiceStompActions;
import com.longx.intelligent.android.imessage.value.Constants;

import java.util.ArrayList;
import java.util.List;

public class GroupChannelNotificationsActivity extends BaseActivity {
    private ActivityGroupChannelNotificationsBinding binding;
    private GroupChannelNotificationsRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGroupChannelNotificationsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupDefaultBackNavigation(binding.toolbar);
        showContent();
    }

    private void showContent() {
        List<GroupChannelNotificationsRecyclerAdapter.ItemData> itemDataList = new ArrayList<>();
        List<GroupChannelNotification> allGroupChannelNotifications = GroupChannelDatabaseManager.getInstance().findAllGroupChannelNotifications();
        for (GroupChannelNotification groupChannelNotification : allGroupChannelNotifications) {
            itemDataList.add(new GroupChannelNotificationsRecyclerAdapter.ItemData(groupChannelNotification));
        }
        if(!itemDataList.isEmpty()) {
            adapter = new GroupChannelNotificationsRecyclerAdapter(this, itemDataList);
            binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
            binding.recyclerView.setAdapter(adapter);
            binding.recyclerView.setVisibility(View.VISIBLE);
            binding.noContentLayout.setVisibility(View.GONE);
        }else {
            binding.recyclerView.setVisibility(View.GONE);
            binding.noContentLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ServerMessageServiceStompActions.updateGroupChannelNotifications(this);
    }
}