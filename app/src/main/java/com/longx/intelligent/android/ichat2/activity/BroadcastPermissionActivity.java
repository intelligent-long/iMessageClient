package com.longx.intelligent.android.ichat2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.activity.helper.BaseActivity;
import com.longx.intelligent.android.ichat2.adapter.BroadcastChannelPermissionLinearLayoutViews;
import com.longx.intelligent.android.ichat2.adapter.BroadcastPermissionLinearLayoutViews;
import com.longx.intelligent.android.ichat2.da.database.manager.ChannelDatabaseManager;
import com.longx.intelligent.android.ichat2.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.ichat2.data.BroadcastChannelPermission;
import com.longx.intelligent.android.ichat2.data.BroadcastPermission;
import com.longx.intelligent.android.ichat2.data.ChannelAssociation;
import com.longx.intelligent.android.ichat2.databinding.ActivityBroadcastPermissionBinding;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class BroadcastPermissionActivity extends BaseActivity {
    private ActivityBroadcastPermissionBinding binding;
    private BroadcastPermission broadcastPermission;
    private BroadcastPermissionLinearLayoutViews linearLayoutViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBroadcastPermissionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupCloseBackNavigation(binding.toolbar);
        intentData();
        init();
        showContent();
        setupYiers();
    }

    private void init() {
        linearLayoutViews = new BroadcastPermissionLinearLayoutViews(this, binding.linearLayoutViews, binding.scrollView, new HashSet<>(broadcastPermission.getExcludeConnectedChannels()));
    }

    private void intentData() {
        broadcastPermission = getIntent().getParcelableExtra(ExtraKeys.BROADCAST_PERMISSION);
    }

    private void showContent() {
        showRadioButtonChecks(broadcastPermission);
        List<ChannelAssociation> associations = ChannelDatabaseManager.getInstance().findAllAssociations();
        List<BroadcastPermissionLinearLayoutViews.ItemData> itemDataList = new ArrayList<>();
        associations.forEach(association -> {
            itemDataList.add(new BroadcastPermissionLinearLayoutViews.ItemData(association.getChannel()));
        });
        itemDataList.sort((o1, o2) -> {
            if (o1.getIndexChar() == '#') return 1;
            if (o2.getIndexChar() == '#') return -1;
            return Character.compare(o1.getIndexChar(), o2.getIndexChar());
        });
        linearLayoutViews.addItemsAndShow(itemDataList);
    }

    private void setupYiers() {
        View.OnClickListener yier = v -> {
            if(v.equals(binding.layoutPublic)){
                broadcastPermission.setPermission(BroadcastPermission.PUBLIC);
                binding.radioPublic.setChecked(true);
                binding.radioPrivate.setChecked(false);
                binding.radioConnectedChannelCircle.setChecked(false);
                hideConnectedChannels();
            }else if(v.equals(binding.layoutPrivate)){
                broadcastPermission.setPermission(BroadcastPermission.PRIVATE);
                binding.radioPublic.setChecked(false);
                binding.radioPrivate.setChecked(true);
                binding.radioConnectedChannelCircle.setChecked(false);
                hideConnectedChannels();
            }else if(v.equals(binding.layoutConnectedChannelCircle)){
                broadcastPermission.setPermission(BroadcastPermission.CONNECTED_CHANNEL_CIRCLE);
                binding.radioPublic.setChecked(false);
                binding.radioPrivate.setChecked(false);
                binding.radioConnectedChannelCircle.setChecked(true);
                showConnectedChannels();
            }
            setResult();
        };
        binding.layoutPublic.setOnClickListener(yier);
        binding.layoutPrivate.setOnClickListener(yier);
        binding.layoutConnectedChannelCircle.setOnClickListener(yier);
    }
    private void showRadioButtonChecks(BroadcastPermission broadcastPermission) {
        switch (broadcastPermission.getPermission()){
            case BroadcastPermission.PUBLIC:{
                binding.radioPublic.setChecked(true);
                hideConnectedChannels();
                break;
            }
            case BroadcastPermission.PRIVATE:{
                binding.radioPrivate.setChecked(true);
                hideConnectedChannels();
                break;
            }
            case BroadcastPermission.CONNECTED_CHANNEL_CIRCLE:{
                binding.radioConnectedChannelCircle.setChecked(true);
                showConnectedChannels();
                break;
            }
        }
    }

    private void showConnectedChannels(){
        binding.linearLayoutViews.setVisibility(View.VISIBLE);
    }

    private void hideConnectedChannels(){
        binding.linearLayoutViews.setVisibility(View.GONE);
    }

    public ActivityBroadcastPermissionBinding getBinding() {
        return binding;
    }

    public BroadcastPermission getBroadcastPermission() {
        return broadcastPermission;
    }

    public void setResult() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(ExtraKeys.BROADCAST_PERMISSION, broadcastPermission);
        setResult(RESULT_OK, resultIntent);
    }
}