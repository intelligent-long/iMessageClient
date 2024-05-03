package com.longx.intelligent.android.ichat2.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Parcelable;

import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.activity.helper.BaseActivity;
import com.longx.intelligent.android.ichat2.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.ichat2.data.ChannelInfo;
import com.longx.intelligent.android.ichat2.data.SelfInfo;
import com.longx.intelligent.android.ichat2.databinding.ActivityRequestAddChannelBinding;

public class RequestAddChannelActivity extends BaseActivity {
    private ActivityRequestAddChannelBinding binding;
    private ChannelInfo channelInfo;
    private  SelfInfo currentUserInfo;
    private static final String MESSAGE = "你好，我是{NAME}。";
    private static String getMessage(String name){
        return MESSAGE.replace("{NAME}", name);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRequestAddChannelBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupDefaultBackNavigation(binding.toolbar);
        channelInfo = getIntent().getParcelableExtra(ExtraKeys.CHANNEL_INFO);
        currentUserInfo = SharedPreferencesAccessor.UserInfoPref.getCurrentUserInfo(this);
        showContent();
    }

    private void showContent() {
        binding.messageInput.setText(getMessage(currentUserInfo.getUsername()));
    }
}