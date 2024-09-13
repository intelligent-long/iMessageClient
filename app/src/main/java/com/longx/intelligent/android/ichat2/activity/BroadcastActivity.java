package com.longx.intelligent.android.ichat2.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatImageView;

import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.activity.helper.BaseActivity;
import com.longx.intelligent.android.ichat2.behavior.MessageDisplayer;
import com.longx.intelligent.android.ichat2.bottomsheet.SelfBroadcastMoreOperationBottomSheet;
import com.longx.intelligent.android.ichat2.da.database.manager.ChannelDatabaseManager;
import com.longx.intelligent.android.ichat2.da.publicfile.PublicFileAccessor;
import com.longx.intelligent.android.ichat2.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.ichat2.data.Broadcast;
import com.longx.intelligent.android.ichat2.data.BroadcastMedia;
import com.longx.intelligent.android.ichat2.data.Channel;
import com.longx.intelligent.android.ichat2.data.Self;
import com.longx.intelligent.android.ichat2.data.response.OperationStatus;
import com.longx.intelligent.android.ichat2.databinding.ActivityBroadcastBinding;
import com.longx.intelligent.android.ichat2.dialog.ConfirmDialog;
import com.longx.intelligent.android.ichat2.dialog.CopyTextDialog;
import com.longx.intelligent.android.ichat2.dialog.OperatingDialog;
import com.longx.intelligent.android.ichat2.media.MediaType;
import com.longx.intelligent.android.ichat2.media.data.Media;
import com.longx.intelligent.android.ichat2.net.dataurl.NetDataUrls;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.BroadcastApiCaller;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.ichat2.net.stomp.ServerMessageServiceStompActions;
import com.longx.intelligent.android.ichat2.ui.NoPaddingTextView;
import com.longx.intelligent.android.ichat2.ui.glide.GlideApp;
import com.longx.intelligent.android.ichat2.util.ErrorLogger;
import com.longx.intelligent.android.ichat2.util.ResourceUtil;
import com.longx.intelligent.android.ichat2.util.TimeUtil;
import com.longx.intelligent.android.ichat2.yier.BroadcastDeletedYier;
import com.longx.intelligent.android.ichat2.yier.BroadcastUpdateYier;
import com.longx.intelligent.android.ichat2.yier.GlobalYiersHolder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class BroadcastActivity extends BaseActivity implements BroadcastUpdateYier {
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
        setupYiers();
        GlobalYiersHolder.holdYier(this, BroadcastUpdateYier.class, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GlobalYiersHolder.removeYier(this, BroadcastUpdateYier.class, this);
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
        }
        List<BroadcastMedia> broadcastMedias = broadcast.getBroadcastMedias();
        if(broadcastMedias != null && !broadcastMedias.isEmpty()){
            binding.mediasFrame.setVisibility(View.VISIBLE);
            broadcastMedias.sort(Comparator.comparingInt(BroadcastMedia::getIndex));
            if(broadcastMedias.size() > 4) {
                binding.medias.setVisibility(View.VISIBLE);
                binding.medias2To4.setVisibility(View.GONE);
                binding.mediaSingle.setVisibility(View.GONE);
                int forTimes = Math.min(30, broadcastMedias.size());
                for (int i = 0; i < forTimes; i++) {
                    BroadcastMedia broadcastMedia = broadcastMedias.get(i);
                    int imageResId = ResourceUtil.getResId("media_" + (i + 1), R.id.class);
                    AppCompatImageView imageView = binding.medias.findViewById(imageResId);
                    imageView.setVisibility(View.VISIBLE);
                    switch (broadcastMedia.getType()) {
                        case BroadcastMedia.TYPE_IMAGE: {
                            GlideApp
                                    .with(getApplicationContext())
                                    .load(NetDataUrls.getBroadcastMediaDataUrl(this, broadcastMedia.getMediaId()))
                                    .centerCrop()
                                    .into(imageView);
                            break;
                        }
                        case BroadcastMedia.TYPE_VIDEO: {
                            GlideApp
                                    .with(getApplicationContext())
                                    .load(NetDataUrls.getBroadcastMediaDataUrl(this, broadcastMedia.getMediaId()))
                                    .centerCrop()
                                    .into(imageView);
                            int videoDurationResId = ResourceUtil.getResId("video_duration_" + (i + 1), R.id.class);
                            NoPaddingTextView videoDuration = binding.medias.findViewById(videoDurationResId);
                            videoDuration.setVisibility(View.VISIBLE);
                            videoDuration.bringToFront();
                            if(broadcastMedia.getVideoDuration() != null) {
                                videoDuration.setText(TimeUtil.formatTime(broadcastMedia.getVideoDuration()));
                            }else {
                                videoDuration.setText("video");
                            }
                            break;
                        }
                    }
                }
            }else if(broadcastMedias.size() > 1){
                binding.medias.setVisibility(View.GONE);
                binding.medias2To4.setVisibility(View.VISIBLE);
                binding.mediaSingle.setVisibility(View.GONE);
                for (int i = 0; i < broadcastMedias.size(); i++) {
                    BroadcastMedia broadcastMedia = broadcastMedias.get(i);
                    int imageResId = ResourceUtil.getResId("media_2_to_4_" + (i + 1), R.id.class);
                    AppCompatImageView imageView = binding.medias2To4.findViewById(imageResId);
                    imageView.setVisibility(View.VISIBLE);
                    switch (broadcastMedia.getType()) {
                        case BroadcastMedia.TYPE_IMAGE: {
                            GlideApp
                                    .with(getApplicationContext())
                                    .load(NetDataUrls.getBroadcastMediaDataUrl(this, broadcastMedia.getMediaId()))
                                    .centerCrop()
                                    .into(imageView);
                            break;
                        }
                        case BroadcastMedia.TYPE_VIDEO: {
                            GlideApp
                                    .with(getApplicationContext())
                                    .load(NetDataUrls.getBroadcastMediaDataUrl(this, broadcastMedia.getMediaId()))
                                    .centerCrop()
                                    .into(imageView);
                            int videoDurationResId = ResourceUtil.getResId("video_duration_2_to_4_" + (i + 1), R.id.class);
                            NoPaddingTextView videoDuration = binding.medias2To4.findViewById(videoDurationResId);
                            videoDuration.setVisibility(View.VISIBLE);
                            videoDuration.bringToFront();
                            if(broadcastMedia.getVideoDuration() != null) {
                                videoDuration.setText(TimeUtil.formatTime(broadcastMedia.getVideoDuration()));
                            }else {
                                videoDuration.setText("video");
                            }
                            break;
                        }
                    }
                }
            }else {
                binding.medias.setVisibility(View.GONE);
                binding.medias2To4.setVisibility(View.GONE);
                binding.mediaSingle.setVisibility(View.VISIBLE);
                BroadcastMedia broadcastMedia = broadcastMedias.get(0);
                switch (broadcastMedia.getType()) {
                    case BroadcastMedia.TYPE_IMAGE: {
                        GlideApp
                                .with(getApplicationContext())
                                .load(NetDataUrls.getBroadcastMediaDataUrl(this, broadcastMedia.getMediaId()))
                                .into(binding.mediaSingle);
                        break;
                    }
                    case BroadcastMedia.TYPE_VIDEO: {
                        GlideApp
                                .with(getApplicationContext())
                                .load(NetDataUrls.getBroadcastMediaDataUrl(this, broadcastMedia.getMediaId()))
                                .into(binding.mediaSingle);
                        binding.videoDurationSingle.setVisibility(View.VISIBLE);
                        binding.videoDurationSingle.bringToFront();
                        if (broadcastMedia.getVideoDuration() != null) {
                            binding.videoDurationSingle.setText(TimeUtil.formatTime(broadcastMedia.getVideoDuration()));
                        } else {
                            binding.videoDurationSingle.setText("video");
                        }
                        break;
                    }
                }
            }
        }else {
            binding.mediasFrame.setVisibility(View.GONE);
            binding.medias.setVisibility(View.GONE);
            binding.medias2To4.setVisibility(View.GONE);
            binding.mediaSingle.setVisibility(View.GONE);
        }
    }

    private void setupYiers() {
        List<BroadcastMedia> broadcastMedias = broadcast.getBroadcastMedias();
        if(broadcastMedias.size() > 4) {
            int forTimes = Math.min(30, broadcastMedias.size());
            for (int i = 0; i < forTimes; i++) {
                int imageResId = ResourceUtil.getResId("media_" + (i + 1), R.id.class);
                AppCompatImageView imageView = binding.medias.findViewById(imageResId);
                int finalI = i;
                imageView.setOnClickListener(v -> {
                    setupAndStartMediaActivity(finalI);
                });
            }
        }else if(broadcastMedias.size() > 1){
            int times = broadcastMedias.size();
            for (int i = 0; i < times; i++) {
                int imageResId = ResourceUtil.getResId("media_2_to_4_" + (i + 1), R.id.class);
                AppCompatImageView imageView = binding.medias2To4.findViewById(imageResId);
                int finalI = i;
                imageView.setOnClickListener(v -> {
                    setupAndStartMediaActivity(finalI);
                });
            }
        }else {
            binding.mediaSingle.setOnClickListener(v -> {
                setupAndStartMediaActivity(0);
            });
        }
        binding.userInfo.setOnClickListener(v -> {
            Intent intent = new Intent(this, ChannelActivity.class);
            intent.putExtra(ExtraKeys.ICHAT_ID, broadcast.getIchatId());
            startActivity(intent);
        });
        if(broadcast.getIchatId().equals(SharedPreferencesAccessor.UserProfilePref.getCurrentUserProfile(this).getIchatId())) {
            SelfBroadcastMoreOperationBottomSheet moreOperationBottomSheet = new SelfBroadcastMoreOperationBottomSheet(this);
            binding.more.setOnClickListener(v -> moreOperationBottomSheet.show());
            moreOperationBottomSheet.setDeleteClickYier(v -> {
                ConfirmDialog confirmDialog = new ConfirmDialog(this);
                confirmDialog.setNegativeButton(null);
                confirmDialog.setPositiveButton((dialog, which) -> {
                    BroadcastApiCaller.deleteBroadcast(this, broadcast.getBroadcastId(), new RetrofitApiCaller.CommonYier<OperationStatus>(this) {
                        @Override
                        public void ok(OperationStatus data, Response<OperationStatus> raw, Call<OperationStatus> call) {
                            super.ok(data, raw, call);
                            data.commonHandleResult(BroadcastActivity.this, new int[]{}, () -> {
                                MessageDisplayer.showToast(getApplicationContext(), "已删除", Toast.LENGTH_LONG);
                                finish();
                                GlobalYiersHolder.getYiers(BroadcastDeletedYier.class).ifPresent(broadcastDeletedYiers -> {
                                    broadcastDeletedYiers.forEach(broadcastDeletedYier -> broadcastDeletedYier.onBroadcastDeleted(broadcast.getBroadcastId()));
                                });
                                ServerMessageServiceStompActions.updateRecentBroadcastMedias(BroadcastActivity.this, broadcast.getIchatId());
                            });
                        }
                    });
                });
                confirmDialog.show();
            });
            moreOperationBottomSheet.setEditClickYier(v -> {
                Intent intent = new Intent(this, EditBroadcastActivity.class);
                intent.putExtra(ExtraKeys.BROADCAST, broadcast);
                startActivity(intent);
            });
        }else {

        }
        binding.text.setOnLongClickListener(v -> {
            new CopyTextDialog(this, broadcast.getText()).show();
            return false;
        });
        binding.like.setOnClickListener(v -> {

        });
    }

    private void setupAndStartMediaActivity(int position){
        Intent intent = new Intent(this, MediaActivity.class);
        intent.putExtra(ExtraKeys.POSITION, position);
        ArrayList<Media> mediaList = new ArrayList<>();
        List<BroadcastMedia> broadcastMedias = broadcast.getBroadcastMedias();
        broadcastMedias.forEach(broadcastMedia -> {
            switch (broadcastMedia.getType()){
                case BroadcastMedia.TYPE_IMAGE:
                    mediaList.add(new Media(MediaType.IMAGE, Uri.parse(NetDataUrls.getBroadcastMediaDataUrl(this, broadcastMedia.getMediaId()))));
                    break;
                case BroadcastMedia.TYPE_VIDEO:
                    mediaList.add(new Media(MediaType.VIDEO, Uri.parse(NetDataUrls.getBroadcastMediaDataUrl(this, broadcastMedia.getMediaId()))));
                    break;
            }
        });
        intent.putParcelableArrayListExtra(ExtraKeys.MEDIAS, mediaList);
        intent.putExtra(ExtraKeys.BUTTON_TEXT, "保存");
        intent.putExtra(ExtraKeys.GLIDE_LOAD, true);
        MediaActivity.setActionButtonYier(v -> {
            int currentItem = MediaActivity.getInstance().getCurrentItemIndex();
            if(currentItem == -1) return;
            BroadcastMedia broadcastMedia = broadcastMedias.get(currentItem);
            switch (broadcastMedia.getType()){
                case BroadcastMedia.TYPE_IMAGE:
                    new Thread(() -> {
                        OperatingDialog operatingDialog = new OperatingDialog(MediaActivity.getInstance());
                        operatingDialog.show();
                        try {
                            PublicFileAccessor.BroadcastMedia.saveImage(MediaActivity.getInstance(), broadcast, currentItem);
                            operatingDialog.dismiss();
                            MessageDisplayer.autoShow(MediaActivity.getInstance(), "已保存", MessageDisplayer.Duration.SHORT);
                        }catch (IOException | InterruptedException e){
                            ErrorLogger.log(e);
                            MessageDisplayer.autoShow(MediaActivity.getInstance(), "保存失败", MessageDisplayer.Duration.SHORT);
                        }
                    }).start();
                    break;
                case BroadcastMedia.TYPE_VIDEO:
                    new Thread(() -> {
                        PublicFileAccessor.BroadcastMedia.saveVideo(MediaActivity.getInstance(), broadcast, currentItem, results -> {
                            if(results[0] != null){
                                if(results[0] == Boolean.TRUE){
                                    MessageDisplayer.autoShow(MediaActivity.getInstance(), "已保存", MessageDisplayer.Duration.SHORT);
                                }else if(results[0] == Boolean.FALSE){
                                    MessageDisplayer.autoShow(MediaActivity.getInstance(), "保存失败", MessageDisplayer.Duration.LONG);
                                }
                            }
                        });
                    }).start();
                    break;
            }

        });
        startActivity(intent);
    }

    @Override
    public void updateOneBroadcast(Broadcast newBroadcast) {
        broadcast = newBroadcast;
        showContent();
        setupYiers();
    }
}