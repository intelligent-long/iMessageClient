package com.longx.intelligent.android.ichat2.activity;

import android.os.Bundle;
import android.os.Parcelable;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.activity.helper.BaseActivity;
import com.longx.intelligent.android.ichat2.data.Broadcast;
import com.longx.intelligent.android.ichat2.databinding.ActivityBroadcastBinding;

public class BroadcastActivity extends BaseActivity {
    private ActivityBroadcastBinding binding;
    private Broadcast broadcast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBroadcastBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupDefaultBackNavigation(binding.toolbar);
        broadcast = getIntent().getParcelableExtra(ExtraKeys.BROADCAST);
        showContent();
    }

    private void showContent() {

    }

}