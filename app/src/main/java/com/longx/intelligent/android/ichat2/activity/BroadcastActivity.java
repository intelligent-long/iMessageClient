package com.longx.intelligent.android.ichat2.activity;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.activity.helper.BaseActivity;
import com.longx.intelligent.android.ichat2.da.database.manager.ChannelDatabaseManager;
import com.longx.intelligent.android.ichat2.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.ichat2.data.Broadcast;
import com.longx.intelligent.android.ichat2.data.BroadcastMedia;
import com.longx.intelligent.android.ichat2.data.Channel;
import com.longx.intelligent.android.ichat2.data.Self;
import com.longx.intelligent.android.ichat2.databinding.ActivityBroadcastBinding;
import com.longx.intelligent.android.ichat2.net.dataurl.NetDataUrls;
import com.longx.intelligent.android.ichat2.ui.glide.GlideApp;
import com.longx.intelligent.android.ichat2.util.ColorUtil;
import com.longx.intelligent.android.ichat2.util.ErrorLogger;
import com.longx.intelligent.android.ichat2.util.ResourceUtil;
import com.longx.intelligent.android.ichat2.util.TimeUtil;

import java.util.Comparator;
import java.util.List;

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
        String name = null;
        String avatarHash = null;
        Self currentUserProfile = SharedPreferencesAccessor.UserProfilePref.getCurrentUserProfile(this);
        if(currentUserProfile.getIchatId().equals(broadcast.getIchatId())){
            name = currentUserProfile.getUsername();
            avatarHash = currentUserProfile.getAvatar() == null ? null : currentUserProfile.getAvatar().getHash();
        }else {
            Channel channel = ChannelDatabaseManager.getInstance().findOneChannel(broadcast.getIchatId());
            if(channel != null) {
                name = channel.getName();
                avatarHash = channel.getAvatar() == null ? null : channel.getAvatar().getHash();
            }
        }
        binding.name.setText(name);
        if (avatarHash == null) {
            GlideApp
                    .with(getApplicationContext())
                    .asBitmap()
                    .load(R.drawable.default_avatar)
                    .into(binding.avatar);
        } else {
            GlideApp
                    .with(getApplicationContext())
                    .asBitmap()
                    .load(NetDataUrls.getAvatarUrl(this, avatarHash))
                    .into(binding.avatar);
        }
        binding.time.setText(TimeUtil.formatRelativeTime(broadcast.getTime()));
        if(broadcast.getText() != null) {
            binding.text.setVisibility(View.VISIBLE);
            binding.text.setText(broadcast.getText());
        }else {
            binding.text.setVisibility(View.GONE);
        }
        for (int i = 0; i < 30; i++) {
            int imageLayoutResId = ResourceUtil.getResId("media_" + (i + 1) + "_layout", R.id.class);
            binding.medias.findViewById(imageLayoutResId).setVisibility(View.GONE);
        }
        for (int i = 0; i < 4; i++) {
            int imageLayoutResId = ResourceUtil.getResId("media_2_to_4_" + (i + 1) + "_layout", R.id.class);
            binding.medias2To4.findViewById(imageLayoutResId).setVisibility(View.GONE);
        }
        binding.media11.setVisibility(View.GONE);
        List<BroadcastMedia> broadcastMedias = broadcast.getBroadcastMedias();
        if(broadcastMedias != null && !broadcastMedias.isEmpty()){
            binding.mediasFrame.setVisibility(View.VISIBLE);
            broadcastMedias.sort(Comparator.comparingInt(BroadcastMedia::getIndex));
            if(broadcastMedias.size() > 4) {
                binding.medias.setVisibility(View.VISIBLE);
                binding.medias2To4.setVisibility(View.GONE);
                binding.media11.setVisibility(View.GONE);
                int forTimes = Math.min(30, broadcastMedias.size());
                for (int i = 0; i < forTimes; i++) {
                    BroadcastMedia broadcastMedia = broadcastMedias.get(i);
                    switch (broadcastMedia.getType()) {
                        case BroadcastMedia.TYPE_IMAGE: {
                            int imageLayoutResId = ResourceUtil.getResId("media_" + (i + 1) + "_layout", R.id.class);
                            binding.medias.findViewById(imageLayoutResId).setVisibility(View.VISIBLE);
                            int imageResId = ResourceUtil.getResId("media_" + (i + 1), R.id.class);
                            AppCompatImageView imageView = binding.medias.findViewById(imageResId);
                            GlideApp
                                    .with(getApplicationContext())
                                    .load(NetDataUrls.getBroadcastMediaDataUrl(this, broadcastMedia.getMediaId()))
                                    .centerCrop()
                                    .into(imageView);
                            break;
                        }
                        case BroadcastMedia.TYPE_VIDEO: {

                            break;
                        }
                    }
                }
            }else if(broadcastMedias.size() > 1){
                binding.medias.setVisibility(View.GONE);
                binding.medias2To4.setVisibility(View.VISIBLE);
                binding.media11.setVisibility(View.GONE);
                int forTimes = Math.min(4, broadcastMedias.size());
                for (int i = 0; i < forTimes; i++) {
                    BroadcastMedia broadcastMedia = broadcastMedias.get(i);
                    switch (broadcastMedia.getType()) {
                        case BroadcastMedia.TYPE_IMAGE: {
                            int imageLayoutResId = ResourceUtil.getResId("media_2_to_4_" + (i + 1) + "_layout", R.id.class);
                            binding.medias2To4.findViewById(imageLayoutResId).setVisibility(View.VISIBLE);
                            int imageResId = ResourceUtil.getResId("media_2_to_4_" + (i + 1), R.id.class);
                            AppCompatImageView imageView = binding.medias2To4.findViewById(imageResId);
                            GlideApp
                                    .with(getApplicationContext())
                                    .load(NetDataUrls.getBroadcastMediaDataUrl(this, broadcastMedia.getMediaId()))
                                    .centerCrop()
                                    .into(imageView);
                            break;
                        }
                        case BroadcastMedia.TYPE_VIDEO: {

                            break;
                        }
                    }
                }
            }else {
                binding.medias.setVisibility(View.GONE);
                binding.medias2To4.setVisibility(View.GONE);
                binding.media11.setVisibility(View.VISIBLE);
                GlideApp
                        .with(getApplicationContext())
                        .load(NetDataUrls.getBroadcastMediaDataUrl(this, broadcastMedias.get(0).getMediaId()))
                        .into(binding.media11);
            }
        }else {
            binding.mediasFrame.setVisibility(View.GONE);
            binding.medias.setVisibility(View.GONE);
            binding.medias2To4.setVisibility(View.GONE);
            binding.media11.setVisibility(View.GONE);
        }
    }

}