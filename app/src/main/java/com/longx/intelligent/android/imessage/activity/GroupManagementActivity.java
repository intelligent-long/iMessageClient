package com.longx.intelligent.android.imessage.activity;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.imessage.activity.helper.BaseActivity;
import com.longx.intelligent.android.imessage.databinding.ActivityGroupManagementBinding;

public class GroupManagementActivity extends BaseActivity {
    private ActivityGroupManagementBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGroupManagementBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupDefaultBackNavigation(binding.toolbar);
        setupYiers();
    }

    private void setupYiers() {
        binding.clickViewJoinVerification.setOnClickListener(v -> {

        });
        binding.clickViewTransferGroupChannelAdmin.setOnClickListener(v -> {

        });
        binding.disbandGroupChannel.setOnClickListener(v -> {

        });
    }
}