package com.longx.intelligent.android.ichat2.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.activity.helper.BaseActivity;
import com.longx.intelligent.android.ichat2.databinding.ActivityChatImageBinding;
import com.longx.intelligent.android.ichat2.util.ColorUtil;
import com.longx.intelligent.android.ichat2.util.WindowAndSystemUiUtil;

public class ChatImageActivity extends BaseActivity {
    private ActivityChatImageBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatImageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        WindowAndSystemUiUtil.checkAndExtendContentUnderSystemBars(this, null, null,
                ColorUtil.getAttrColor(this, com.google.android.material.R.attr.colorSurfaceContainer));
        setupDefaultBackNavigation(binding.toolbar);
        binding.appBar.bringToFront();
        showContent();
    }

    private void showContent() {

    }
}