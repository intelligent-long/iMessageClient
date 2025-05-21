package com.longx.intelligent.android.imessage.activity;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.imessage.activity.helper.BaseActivity;
import com.longx.intelligent.android.imessage.adapter.TransferGroupChannelAdminRecyclerAdapter;
import com.longx.intelligent.android.imessage.da.database.manager.GroupChannelDatabaseManager;
import com.longx.intelligent.android.imessage.data.Channel;
import com.longx.intelligent.android.imessage.data.GroupChannel;
import com.longx.intelligent.android.imessage.databinding.ActivityTransferGroupChannelAdminBinding;

import java.util.ArrayList;
import java.util.List;

public class TransferGroupChannelAdminActivity extends BaseActivity {
    private ActivityTransferGroupChannelAdminBinding binding;
    private GroupChannel groupChannel;
    private TransferGroupChannelAdminRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTransferGroupChannelAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupDefaultBackNavigation(binding.toolbar);
        intentData();
        showContent();
    }

    private void intentData() {
        groupChannel = getIntent().getParcelableExtra(ExtraKeys.GROUP_CHANNEL);
    }

    private void showContent() {
        List<Channel> associatedChannels = new ArrayList<>();
        groupChannel.getGroupChannelAssociations().forEach(groupChannelAssociation -> {
            associatedChannels.add(groupChannelAssociation.getRequester());
        });
        adapter = new TransferGroupChannelAdminRecyclerAdapter(this, associatedChannels);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);
    }

    public ActivityTransferGroupChannelAdminBinding getBinding() {
        return binding;
    }
}