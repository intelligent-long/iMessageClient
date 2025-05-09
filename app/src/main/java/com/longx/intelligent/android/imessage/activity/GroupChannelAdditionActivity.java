package com.longx.intelligent.android.imessage.activity;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.imessage.activity.helper.BaseActivity;
import com.longx.intelligent.android.imessage.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.imessage.data.Channel;
import com.longx.intelligent.android.imessage.data.GroupChannel;
import com.longx.intelligent.android.imessage.data.GroupChannelAddition;
import com.longx.intelligent.android.imessage.data.Self;
import com.longx.intelligent.android.imessage.databinding.ActivityGroupChannelAdditionBinding;

public class GroupChannelAdditionActivity extends BaseActivity {
    private ActivityGroupChannelAdditionBinding binding;
    private GroupChannelAddition groupChannelAddition;
    private boolean isRequester;
    private Channel requesterChannel;
    private GroupChannel responderGroupChannel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGroupChannelAdditionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupDefaultBackNavigation(binding.toolbar);
        intentData();
        showContent();
        setupYiers();
    }

    private void intentData() {
        groupChannelAddition = getIntent().getParcelableExtra(ExtraKeys.GROUP_CHANNEL_ADDITION_INFO);
        Self self = SharedPreferencesAccessor.UserProfilePref.getCurrentUserProfile(this);
        isRequester = groupChannelAddition.getRequesterChannel().getImessageId().equals(self.getImessageId());
        requesterChannel = groupChannelAddition.getRequesterChannel();
        responderGroupChannel = groupChannelAddition.getResponderGroupChannel();
    }

    private void showContent() {

    }

    private void setupYiers() {

    }
}