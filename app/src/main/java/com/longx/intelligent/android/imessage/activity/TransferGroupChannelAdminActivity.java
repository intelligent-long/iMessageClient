package com.longx.intelligent.android.imessage.activity;

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
            if(!groupChannelAssociation.getRequester().getImessageId().equals(groupChannel.getOwner())) {
                associatedChannels.add(groupChannelAssociation.getRequester());
            }
        });
        if(associatedChannels.isEmpty()){
            ((AppBarLayout.LayoutParams)binding.collapsingToolbarLayout.getLayoutParams())
                    .setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_NO_SCROLL);
            binding.noContentLayout.setVisibility(View.VISIBLE);
            binding.recyclerView.setVisibility(View.GONE);
        }else {
            ((AppBarLayout.LayoutParams)binding.collapsingToolbarLayout.getLayoutParams())
                    .setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
                            | AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED
                            | AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP);
            binding.noContentLayout.setVisibility(View.GONE);
            binding.recyclerView.setVisibility(View.VISIBLE);
            adapter = new TransferGroupChannelAdminRecyclerAdapter(this, associatedChannels);
            binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
            binding.recyclerView.setAdapter(adapter);
        }
    }

    public ActivityTransferGroupChannelAdminBinding getBinding() {
        return binding;
    }
}