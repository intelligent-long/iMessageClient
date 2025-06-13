package com.longx.intelligent.android.imessage.activity;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.imessage.activity.helper.BaseActivity;
import com.longx.intelligent.android.imessage.adapter.GroupChannelNotificationsRecyclerAdapter;
import com.longx.intelligent.android.imessage.da.database.manager.ChannelDatabaseManager;
import com.longx.intelligent.android.imessage.da.database.manager.GroupChannelDatabaseManager;
import com.longx.intelligent.android.imessage.data.GroupChannelDisconnection;
import com.longx.intelligent.android.imessage.databinding.ActivityGroupChannelNotificationsBinding;
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
        List<GroupChannelDisconnection> allGroupChannelDisconnections = GroupChannelDatabaseManager.getInstance().findAllGroupChannelDisconnections();
        for (GroupChannelDisconnection groupChannelDisconnection : allGroupChannelDisconnections) {
            itemDataList.add(new GroupChannelNotificationsRecyclerAdapter.ItemData(
                    groupChannelDisconnection.isPassive() ? Constants.GroupChannelNotificationType.PASSIVE_DISCONNECT : Constants.GroupChannelNotificationType.ACTIVE_DISCONNECT,
                    GroupChannelDatabaseManager.getInstance().findOneAssociation(groupChannelDisconnection.getGroupChannelId()),
                    ChannelDatabaseManager.getInstance().findOneChannel(groupChannelDisconnection.getChannelId()),
                    ChannelDatabaseManager.getInstance().findOneChannel(groupChannelDisconnection.getByWhom()),
                    groupChannelDisconnection.getTime(),
                    groupChannelDisconnection.isViewed()));
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
}