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
import com.longx.intelligent.android.ichat2.da.database.manager.ChannelDatabaseManager;
import com.longx.intelligent.android.ichat2.data.BroadcastComment;
import com.longx.intelligent.android.ichat2.data.Channel;
import com.longx.intelligent.android.ichat2.databinding.ActivityBroadcastCommentReplyBinding;
import com.longx.intelligent.android.ichat2.net.dataurl.NetDataUrls;
import com.longx.intelligent.android.ichat2.ui.glide.GlideApp;
import com.longx.intelligent.android.ichat2.util.TimeUtil;

public class BroadcastCommentReplyActivity extends BaseActivity {
    private ActivityBroadcastCommentReplyBinding binding;
    private BroadcastComment broadcastComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBroadcastCommentReplyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupDefaultBackNavigation(binding.toolbar);
        intentData();
        showContent();
    }

    private void intentData() {
        broadcastComment = getIntent().getParcelableExtra(ExtraKeys.BROADCAST_COMMENT);
    }

    private void showContent() {
        if (broadcastComment.getAvatarHash() == null) {
            GlideApp
                    .with(getApplicationContext())
                    .load(R.drawable.default_avatar)
                    .into(binding.avatar);
        } else {
            GlideApp
                    .with(getApplicationContext())
                    .load(NetDataUrls.getAvatarUrl(this, broadcastComment.getAvatarHash()))
                    .into(binding.avatar);
        }
        Channel channel = ChannelDatabaseManager.getInstance().findOneChannel(broadcastComment.getFromId());
        String name;
        if(channel != null){
            name = channel.getName();
        }else {
            name = broadcastComment.getFromName();
        }
        binding.name.setText(name);
        binding.time.setText(TimeUtil.formatRelativeTime(broadcastComment.getCommentTime()));
        binding.text.setText(broadcastComment.getText());
    }
}