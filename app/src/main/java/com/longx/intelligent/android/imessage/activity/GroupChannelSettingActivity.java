package com.longx.intelligent.android.imessage.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.imessage.activity.helper.BaseActivity;
import com.longx.intelligent.android.imessage.databinding.ActivityGroupChannelSettingBinding;

public class GroupChannelSettingActivity extends BaseActivity {
    private ActivityGroupChannelSettingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGroupChannelSettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupDefaultBackNavigation(binding.toolbar);
        showContent();
        setupYiers();
    }

    private void showContent() {

    }

    private void setupYiers() {

    }
}