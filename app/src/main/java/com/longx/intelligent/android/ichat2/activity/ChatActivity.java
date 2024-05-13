package com.longx.intelligent.android.ichat2.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Parcelable;

import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.activity.helper.BaseActivity;
import com.longx.intelligent.android.ichat2.data.Channel;
import com.longx.intelligent.android.ichat2.databinding.ActivityChatBinding;
import com.longx.intelligent.android.ichat2.util.ColorUtil;

public class ChatActivity extends BaseActivity {
    private ActivityChatBinding binding;
    private Channel channel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupDefaultBackNavigation(binding.toolbar, ColorUtil.getColor(this, R.color.ichat));
        channel = getIntent().getParcelableExtra(ExtraKeys.CHANNEL);
        showContent();
    }

    private void showContent(){
        binding.toolbar.setTitle(channel.getUsername());
    }
}