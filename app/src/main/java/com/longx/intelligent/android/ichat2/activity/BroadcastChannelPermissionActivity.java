package com.longx.intelligent.android.ichat2.activity;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.activity.helper.BaseActivity;
import com.longx.intelligent.android.ichat2.adapter.BroadcastChannelPermissionLinearLayoutViews;
import com.longx.intelligent.android.ichat2.databinding.ActivityBroadcastChannelPermissionBinding;
import com.longx.intelligent.android.ichat2.databinding.LinearLayoutViewsFooterBroadcastChannelPermissionBinding;

public class BroadcastChannelPermissionActivity extends BaseActivity {
    private ActivityBroadcastChannelPermissionBinding binding;
    private BroadcastChannelPermissionLinearLayoutViews linearLayoutViews;
    private LinearLayoutViewsFooterBroadcastChannelPermissionBinding footerBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBroadcastChannelPermissionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupDefaultBackNavigation(binding.toolbar);
        init();
        showContent();
        setupYiers();
    }

    private void init() {
        linearLayoutViews = new BroadcastChannelPermissionLinearLayoutViews(this, binding.linearLayoutViews, binding.scrollView);
        footerBinding = LinearLayoutViewsFooterBroadcastChannelPermissionBinding.inflate(getLayoutInflater(), binding.getRoot(), false);
        linearLayoutViews.setFooter(footerBinding.getRoot());
    }

    private void showContent() {

    }

    private void setupYiers() {

    }
}