package com.longx.intelligent.android.ichat2.activity;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.activity.helper.BaseActivity;
import com.longx.intelligent.android.ichat2.data.BroadcastChannelPermission;
import com.longx.intelligent.android.ichat2.data.BroadcastPermission;
import com.longx.intelligent.android.ichat2.databinding.ActivityBroadcastPermissionBinding;

public class BroadcastPermissionActivity extends BaseActivity {
    private ActivityBroadcastPermissionBinding binding;
    private BroadcastPermission broadcastPermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBroadcastPermissionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupCloseBackNavigation(binding.toolbar);
        intentData();
        showContent();
        setupYiers();
    }

    private void intentData() {
        broadcastPermission = getIntent().getParcelableExtra(ExtraKeys.BROADCAST_PERMISSION);
    }

    private void showContent() {
        showRadioButtonChecks(broadcastPermission);
    }

    private void setupYiers() {

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
}