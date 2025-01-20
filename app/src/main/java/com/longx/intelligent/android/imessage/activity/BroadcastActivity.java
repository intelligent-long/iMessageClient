package com.longx.intelligent.android.imessage.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.os.OperationCanceledException;

import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.imessage.activity.helper.BaseActivity;
import com.longx.intelligent.android.imessage.adapter.BroadcastCommentsLinearLayoutViews;
import com.longx.intelligent.android.imessage.bottomsheet.OtherBroadcastMoreOperationBottomSheet;
import com.longx.intelligent.android.imessage.data.BroadcastChannelPermission;
import com.longx.intelligent.android.imessage.behaviorcomponents.MessageDisplayer;
import com.longx.intelligent.android.imessage.bottomsheet.SelfBroadcastMoreOperationBottomSheet;
import com.longx.intelligent.android.imessage.da.database.manager.ChannelDatabaseManager;
import com.longx.intelligent.android.imessage.da.publicfile.PublicFileAccessor;
import com.longx.intelligent.android.imessage.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.imessage.data.Broadcast;
import com.longx.intelligent.android.imessage.data.BroadcastComment;
import com.longx.intelligent.android.imessage.data.BroadcastLike;
import com.longx.intelligent.android.imessage.data.BroadcastMedia;
import com.longx.intelligent.android.imessage.data.Channel;
import com.longx.intelligent.android.imessage.data.Self;
import com.longx.intelligent.android.imessage.data.request.ChangeExcludeBroadcastChannelPostBody;
import com.longx.intelligent.android.imessage.data.request.CommentBroadcastPostBody;
import com.longx.intelligent.android.imessage.data.response.OperationData;
import com.longx.intelligent.android.imessage.data.response.OperationStatus;
import com.longx.intelligent.android.imessage.data.response.PaginatedOperationData;
import com.longx.intelligent.android.imessage.databinding.ActivityBroadcastBinding;
import com.longx.intelligent.android.imessage.databinding.LayoutBroadcastLikePreviewItemBinding;
import com.longx.intelligent.android.imessage.databinding.LayoutBroadcastLikesAllButtonBinding;
import com.longx.intelligent.android.imessage.databinding.RecyclerFooterBroadcastCommentsBinding;
import com.longx.intelligent.android.imessage.dialog.ConfirmDialog;
import com.longx.intelligent.android.imessage.dialog.CopyTextDialog;
import com.longx.intelligent.android.imessage.dialog.OperatingDialog;
import com.longx.intelligent.android.imessage.media.MediaType;
import com.longx.intelligent.android.imessage.media.data.Media;
import com.longx.intelligent.android.imessage.net.dataurl.NetDataUrls;
import com.longx.intelligent.android.imessage.net.retrofit.caller.BroadcastApiCaller;
import com.longx.intelligent.android.imessage.net.retrofit.caller.PermissionApiCaller;
import com.longx.intelligent.android.imessage.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.imessage.net.stomp.ServerMessageServiceStompActions;
import com.longx.intelligent.android.imessage.ui.NoPaddingTextView;
import com.longx.intelligent.android.imessage.ui.glide.GlideApp;
import com.longx.intelligent.android.imessage.util.ErrorLogger;
import com.longx.intelligent.android.imessage.util.ResourceUtil;
import com.longx.intelligent.android.imessage.util.TimeUtil;
import com.longx.intelligent.android.imessage.util.UiUtil;
import com.longx.intelligent.android.imessage.value.Constants;
import com.longx.intelligent.android.imessage.yier.BroadcastDeletedYier;
import com.longx.intelligent.android.imessage.yier.BroadcastUpdateYier;
import com.longx.intelligent.android.imessage.yier.GlobalYiersHolder;
import com.longx.intelligent.android.imessage.yier.KeyboardVisibilityYier;
import com.longx.intelligent.android.imessage.yier.OnSetChannelBroadcastExcludeYier;
import com.longx.intelligent.android.imessage.yier.ResultsYier;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import retrofit2.Call;
import retrofit2.Response;

public class BroadcastActivity extends BaseActivity implements BroadcastUpdateYier, OnSetChannelBroadcastExcludeYier {
    private ActivityBroadcastBinding binding;
    private Broadcast broadcast;
    private boolean onComment;
    private BroadcastCommentsLinearLayoutViews commentsLinearLayoutViews;
    private RecyclerFooterBroadcastCommentsBinding footerBinding;
    private CountDownLatch NEXT_PAGE_LATCH;
    private boolean stopFetchNextPage;
    private BroadcastComment replyToBroadcastComment;
    private ResultsYier endReplyYier;
    private ResultsYier onCommentsNextPageYier;
    private int positionInRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBroadcastBinding.inflate(getLayoutInflater());
        setAutoCancelInput(false);
        setContentView(binding.getRoot());
        setupDefaultBackNavigation(binding.toolbar);
        positionInRecyclerView = getIntent().getIntExtra(ExtraKeys.POSITION, -1);
        BroadcastComment broadcastComment = getIntent().getParcelableExtra(ExtraKeys.BROADCAST_COMMENT);
        if(broadcastComment != null) startLocateComment(broadcastComment);
        broadcast = getIntent().getParcelableExtra(ExtraKeys.BROADCAST);
        init();
        GlobalYiersHolder.holdYier(this, BroadcastUpdateYier.class, this);
        GlobalYiersHolder.holdYier(this, OnSetChannelBroadcastExcludeYier.class, this);
        if(broadcast != null) {
            initDo();
        }else {
            binding.scrollView.setVisibility(View.GONE);
            String broadcastId = getIntent().getStringExtra(ExtraKeys.BROADCAST_ID);
            if(broadcastId != null){
                BroadcastApiCaller.fetchBroadcast(this, broadcastId, new RetrofitApiCaller.DelayedShowDialogCommonYier<OperationData>(this){
                    @Override
                    public void ok(OperationData data, Response<OperationData> raw, Call<OperationData> call) {
                        super.ok(data, raw, call);
                        data.commonHandleResult(BroadcastActivity.this, new int[]{-101, -102}, () -> {
                            binding.scrollView.setVisibility(View.VISIBLE);
                            broadcast = data.getData(Broadcast.class);
                            initDo();
                        }, new OperationStatus.HandleResult(-101, () -> {
                            binding.noBroadcast.setVisibility(View.VISIBLE);
                        }));
                    }
                });
            }
        }
        boolean startComment = getIntent().getBooleanExtra(ExtraKeys.DO_THAT_THING, false);
        if(startComment){
            binding.commentInput.postDelayed(this::startComment, 900);
        }
    }

    private void init(){
        commentsLinearLayoutViews = new BroadcastCommentsLinearLayoutViews(this, binding.commentView, binding.scrollView, binding.layoutComment);
        footerBinding = RecyclerFooterBroadcastCommentsBinding.inflate(getLayoutInflater(), binding.getRoot(), false);
        commentsLinearLayoutViews.setFooter(footerBinding.getRoot());
    }

    private void initDo() {
        showContent();
        setupYiers();
        fetchAndShowLikesPreview();
        commentNextPage();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GlobalYiersHolder.removeYier(this, BroadcastUpdateYier.class, this);
        GlobalYiersHolder.removeYier(this, OnSetChannelBroadcastExcludeYier.class, this);
    }

    private void showContent() {
        String name;
        String avatarHash;
        Self currentUserProfile = SharedPreferencesAccessor.UserProfilePref.getCurrentUserProfile(this);
        if(currentUserProfile.getImessageId().equals(broadcast.getImessageId())){
            name = currentUserProfile.getUsername();
            avatarHash = currentUserProfile.getAvatar() == null ? null : currentUserProfile.getAvatar().getHash();
        }else {
            Channel channel = ChannelDatabaseManager.getInstance().findOneChannel(broadcast.getImessageId());
            if(channel != null) {
                name = channel.getName();
                avatarHash = channel.getAvatar() == null ? null : channel.getAvatar().getHash();
            }else {
                name = broadcast.getChannelName();
                avatarHash = broadcast.getChannelAvatarHash();
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
        if(broadcast.getLastEditTime() != null){
            binding.lastEditTime.setText("编辑于 " + TimeUtil.formatRelativeTime(broadcast.getLastEditTime()));
            binding.lastEditTime.setVisibility(View.VISIBLE);
        }
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
                for (int i = 0; i < 30; i++) {
                    int imageResId = ResourceUtil.getResId("media_" + (i + 1), R.id.class);
                    AppCompatImageView imageView = binding.medias.findViewById(imageResId);
                    if (i < broadcastMedias.size()) {
                        BroadcastMedia broadcastMedia = broadcastMedias.get(i);
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
                                if (broadcastMedia.getVideoDuration() != null) {
                                    videoDuration.setText(TimeUtil.formatTimeToHHMMSS(broadcastMedia.getVideoDuration()));
                                } else {
                                    videoDuration.setText("video");
                                }
                                break;
                            }
                        }
                    }else {
                        imageView.setVisibility(View.GONE);
                    }
                }

            }else if(broadcastMedias.size() > 1){
                binding.medias.setVisibility(View.GONE);
                binding.medias2To4.setVisibility(View.VISIBLE);
                binding.mediaSingle.setVisibility(View.GONE);
                for (int i = 0; i < 4; i++) {
                    int imageResId = ResourceUtil.getResId("media_2_to_4_" + (i + 1), R.id.class);
                    AppCompatImageView imageView = binding.medias2To4.findViewById(imageResId);
                    if (i < broadcastMedias.size()) {
                        BroadcastMedia broadcastMedia = broadcastMedias.get(i);
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
                                if (broadcastMedia.getVideoDuration() != null) {
                                    videoDuration.setText(TimeUtil.formatTimeToHHMMSS(broadcastMedia.getVideoDuration()));
                                } else {
                                    videoDuration.setText("video");
                                }
                                break;
                            }
                        }
                    }else {
                        imageView.setVisibility(View.GONE);
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
                            binding.videoDurationSingle.setText(TimeUtil.formatTimeToHHMMSS(broadcastMedia.getVideoDuration()));
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
        if(broadcast.isLiked()){
            binding.like.setImageResource(R.drawable.favorite_fill_broadcast_liked_24px);
        }else {
            binding.like.setImageResource(R.drawable.favorite_outline_24px);
        }
        binding.likeCount.setText(String.valueOf(broadcast.getLikeCount()));
        if(broadcast.isCommented()){
            binding.comment.setImageResource(R.drawable.mode_comment_fill_24px);
        }else {
            binding.comment.setImageResource(R.drawable.mode_comment_outline_24px);
        }
        binding.commentCount.setText(String.valueOf(broadcast.getCommentCount()));

        if(currentUserProfile.getImessageId().equals(broadcast.getImessageId())) {
            binding.visibilityIcon.setVisibility(View.VISIBLE);
            BroadcastChannelPermission broadcastChannelPermission = SharedPreferencesAccessor.BroadcastPref.getServerBroadcastChannelPermission(this);
            Broadcast.BroadcastVisibility broadcastVisibility = Broadcast.determineBroadcastVisibility(broadcastChannelPermission, broadcast.getBroadcastPermission());
            switch (broadcastVisibility) {
                case ALL: {
                    binding.visibilityIcon.setImageResource(R.drawable.arrow_forward_24px);
                    break;
                }
                case NONE: {
                    binding.visibilityIcon.setImageResource(R.drawable.arrow_upward_24px);
                    break;
                }
                case PARTIAL: {
                    binding.visibilityIcon.setImageResource(R.drawable.arrow_outward_24px);
                    break;
                }
            }
        }else {
            binding.visibilityIcon.setVisibility(View.GONE);
        }

        checkAndShowMoreIcon(currentUserProfile);
    }

    private void checkAndShowMoreIcon(Self currentUserProfile) {
        if ((
                !broadcast.getImessageId().equals(currentUserProfile.getImessageId())
                && ChannelDatabaseManager.getInstance().findOneChannel(broadcast.getImessageId()) == null
            )
                || SharedPreferencesAccessor.BroadcastPref.getServerExcludeBroadcastChannels(this).contains(broadcast.getImessageId())) {
            binding.spaceMore.setVisibility(View.GONE);
            binding.layoutMore.setVisibility(View.GONE);
        } else {
            binding.spaceMore.setVisibility(View.VISIBLE);
            binding.layoutMore.setVisibility(View.VISIBLE);
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
            intent.putExtra(ExtraKeys.IMESSAGE_ID, broadcast.getImessageId());
            startActivity(intent);
        });
        if(broadcast.getImessageId().equals(SharedPreferencesAccessor.UserProfilePref.getCurrentUserProfile(this).getImessageId())) {
            SelfBroadcastMoreOperationBottomSheet moreOperationBottomSheet = new SelfBroadcastMoreOperationBottomSheet(this);
            binding.more.setOnClickListener(v -> moreOperationBottomSheet.show());
            moreOperationBottomSheet.setDeleteClickYier(v -> {
                ConfirmDialog confirmDialog = new ConfirmDialog(this);
                confirmDialog.setNegativeButton();
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
                                ServerMessageServiceStompActions.updateRecentBroadcastMedias(BroadcastActivity.this, broadcast.getImessageId());
                            });
                        }
                    });
                });
                confirmDialog.create().show();
            });
            moreOperationBottomSheet.setEditClickYier(v -> {
                Intent intent = new Intent(this, EditBroadcastActivity.class);
                intent.putExtra(ExtraKeys.BROADCAST, broadcast);
                startActivity(intent);
            });
            moreOperationBottomSheet.setChangePermissionClickYier(v -> {
                Intent intent = new Intent(this, BroadcastPermissionActivity.class);
                intent.putExtra(ExtraKeys.BROADCAST_PERMISSION, broadcast.getBroadcastPermission());
                intent.putExtra(ExtraKeys.CHANGE_PERMISSION, true);
                startActivity(intent);
            });
        }else if(ChannelDatabaseManager.getInstance().findOneChannel(broadcast.getImessageId()) != null){
            OtherBroadcastMoreOperationBottomSheet moreOperationBottomSheet = new OtherBroadcastMoreOperationBottomSheet(this);
            binding.more.setOnClickListener(v -> moreOperationBottomSheet.show());
            moreOperationBottomSheet.setExcludeBroadcastChannelClickYier(v -> {
                ConfirmDialog confirmDialog = new ConfirmDialog(this);
                confirmDialog.setNegativeButton();
                confirmDialog.setPositiveButton((dialog, which) -> {
                    Set<String> serverExcludeBroadcastChannels = SharedPreferencesAccessor.BroadcastPref.getServerExcludeBroadcastChannels(this);
                    Set<String> nowServerExcludeBroadcastChannels = new HashSet<>(serverExcludeBroadcastChannels);
                    nowServerExcludeBroadcastChannels.add(broadcast.getImessageId());
                    ChangeExcludeBroadcastChannelPostBody postBody = new ChangeExcludeBroadcastChannelPostBody(nowServerExcludeBroadcastChannels);
                    PermissionApiCaller.changeExcludeBroadcastChannels(null, postBody, new RetrofitApiCaller.CommonYier<OperationStatus>(this){
                        @Override
                        public void ok(OperationStatus data, Response<OperationStatus> raw, Call<OperationStatus> call) {
                            super.ok(data, raw, call);
                            data.commonHandleResult(BroadcastActivity.this, new int[]{}, () -> {
                                SharedPreferencesAccessor.BroadcastPref.saveAppExcludeBroadcastChannels(BroadcastActivity.this, nowServerExcludeBroadcastChannels);
                                SharedPreferencesAccessor.BroadcastPref.saveServerExcludeBroadcastChannels(BroadcastActivity.this, nowServerExcludeBroadcastChannels);
                                GlobalYiersHolder.getYiers(OnSetChannelBroadcastExcludeYier.class).ifPresent(onSetChannelBroadcastExcludeYiers -> {
                                    onSetChannelBroadcastExcludeYiers.forEach(onSetChannelBroadcastExcludeYier -> onSetChannelBroadcastExcludeYier.onSetChannelBroadcastExclude(positionInRecyclerView, broadcast.getImessageId()));
                                });
                                finish();
                            });
                        }
                    });
                });
                confirmDialog.create().show();
            });
        }
        binding.text.setOnLongClickListener(v -> {
            new CopyTextDialog(this, broadcast.getText()).create().show();
            return false;
        });
        binding.like.setOnClickListener(v -> {
            if(!broadcast.isLiked()) {
                BroadcastApiCaller.likeBroadcast(this, broadcast.getBroadcastId(), new RetrofitApiCaller.DelayedShowDialogCommonYier<OperationData>(this) {
                    @Override
                    public void ok(OperationData data, Response<OperationData> raw, Call<OperationData> call) {
                        super.ok(data, raw, call);
                        data.commonHandleResult(BroadcastActivity.this, new int[]{-101, -102, -103}, () -> {
                            broadcast = data.getData(Broadcast.class);
                            showContent();
                            setupYiers();
                            GlobalYiersHolder.getYiers(BroadcastUpdateYier.class).ifPresent(broadcastUpdateYiers -> {
                                broadcastUpdateYiers.forEach(broadcastUpdateYier -> broadcastUpdateYier.updateOneBroadcast(broadcast));
                            });
                        });
                    }
                });
            }else {
                BroadcastApiCaller.cancelLikeBroadcast(this, broadcast.getBroadcastId(), new RetrofitApiCaller.DelayedShowDialogCommonYier<OperationData>(this){
                    @Override
                    public void ok(OperationData data, Response<OperationData> raw, Call<OperationData> call) {
                        super.ok(data, raw, call);
                        data.commonHandleResult(BroadcastActivity.this, new int[]{-101, -102, -103}, () -> {
                            broadcast = data.getData(Broadcast.class);
                            showContent();
                            setupYiers();
                            GlobalYiersHolder.getYiers(BroadcastUpdateYier.class).ifPresent(broadcastUpdateYiers -> {
                                broadcastUpdateYiers.forEach(broadcastUpdateYier -> broadcastUpdateYier.updateOneBroadcast(broadcast));
                            });
                        });
                    }
                });
            }
        });
        binding.comment.getViewTreeObserver().addOnScrollChangedListener(this::checkAndShowOrHideFab);
        binding.comment.post(this::checkAndShowOrHideFab);
        binding.comment.setOnClickListener(v -> startComment());
        binding.commentFab.setOnClickListener(v -> startComment());
        new KeyboardVisibilityYier(this).setYier(new KeyboardVisibilityYier.Yier() {
            @Override
            public void onKeyboardOpened() {

            }

            @Override
            public void onKeyboardClosed() {
                endComment();
            }
        });
        binding.sendCommentButton.setOnClickListener(v -> {
            String commentText = UiUtil.getEditTextString(binding.commentInput);
            if(commentText == null || commentText.isEmpty()) return;
            CommentBroadcastPostBody postBody = new CommentBroadcastPostBody(broadcast.getBroadcastId(), commentText, replyToBroadcastComment == null ? null : replyToBroadcastComment.getCommentId());
            BroadcastApiCaller.commentBroadcast(BroadcastActivity.this, postBody, new RetrofitApiCaller.BaseCommonYier<OperationData>(this){
                @Override
                public void start(Call<OperationData> call) {
                    super.start(call);
                    binding.sendCommentButton.setVisibility(View.GONE);
                    binding.sendCommentIndicator.setVisibility(View.VISIBLE);
                }

                @Override
                public void complete(Call<OperationData> call) {
                    super.complete(call);
                    binding.sendCommentButton.setVisibility(View.VISIBLE);
                    binding.sendCommentIndicator.setVisibility(View.GONE);
                }

                @Override
                public void ok(OperationData data, Response<OperationData> raw, Call<OperationData> call) {
                    super.ok(data, raw, call);
                    data.commonHandleResult(BroadcastActivity.this, new int[]{-101, -102}, () -> {
                        UiUtil.hideKeyboard(binding.commentInput);
                        binding.commentInput.setText(null);
                        binding.sendCommentBar.setVisibility(View.GONE);
                        commentsLinearLayoutViews.clear();
                        stopFetchNextPage = false;
                        GlobalYiersHolder.getYiers(BroadcastUpdateYier.class).ifPresent(broadcastUpdateYiers -> {
                            broadcastUpdateYiers.forEach(broadcastUpdateYier -> broadcastUpdateYier.updateOneBroadcast(broadcast));
                        });
                    });
                }
            });
        });
        binding.scrollView.setOnScrollChangeListener((View.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            int height = binding.scrollView.getHeight();
            View content = binding.scrollView.getChildAt(0);
            int totalHeight = content.getHeight();
            if (scrollY + height >= totalHeight - 300) {
                if(!stopFetchNextPage) {
                    if(NEXT_PAGE_LATCH == null || NEXT_PAGE_LATCH.getCount() == 0) {
                        commentNextPage();
                    }
                }
            }
        });
    }

    private void checkAndShowOrHideFab() {
        if(onComment) return;
        View view = binding.scrollView.getChildAt(0);
        int diff = (view.getBottom() - (binding.scrollView.getHeight() + binding.scrollView.getScrollY()));
        if (diff == 0) {
            binding.commentFab.hide();
        }else {
            boolean viewVisibleOnScreen = UiUtil.isViewVisibleOnScreen(binding.comment);
            if (viewVisibleOnScreen) {
                binding.commentFab.hide();
            } else {
                binding.commentFab.show();
            }
        }
    }

    private void setupAndStartMediaActivity(int position){
        Intent intent = new Intent(this, MediaActivity2.class);
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
//        MediaActivity.setActionButtonYier(v -> {
//            int currentItem = MediaActivity.getInstance().getCurrentItemIndex();
//            if(currentItem == -1) return;
//            BroadcastMedia broadcastMedia = broadcastMedias.get(currentItem);
//            switch (broadcastMedia.getType()){
//                case BroadcastMedia.TYPE_IMAGE:
//                    new Thread(() -> {
//                        OperatingDialog operatingDialog = new OperatingDialog(MediaActivity.getInstance());
//                        operatingDialog.create().show();
//                        try {
//                            PublicFileAccessor.BroadcastMedia.saveImage(MediaActivity.getInstance(), broadcast, currentItem);
//                            operatingDialog.dismiss();
//                            MessageDisplayer.autoShow(MediaActivity.getInstance(), "已保存", MessageDisplayer.Duration.SHORT);
//                        }catch (IOException | InterruptedException e){
//                            ErrorLogger.log(e);
//                            MessageDisplayer.autoShow(MediaActivity.getInstance(), "保存失败", MessageDisplayer.Duration.SHORT);
//                        }
//                    }).start();
//                    break;
//                case BroadcastMedia.TYPE_VIDEO:
//                    new Thread(() -> {
//                        try {
//                            PublicFileAccessor.BroadcastMedia.saveVideo(MediaActivity.getInstance(), broadcast, currentItem);
//                            MessageDisplayer.autoShow(MediaActivity.getInstance(), "已保存", MessageDisplayer.Duration.SHORT);
//                        } catch (OperationCanceledException e){
//                            ErrorLogger.log(e);
//                        } catch (InterruptedException | IOException e) {
//                            ErrorLogger.log(e);
//                            MessageDisplayer.autoShow(MediaActivity.getInstance(), "保存失败", MessageDisplayer.Duration.SHORT);
//                        }
//                    }).start();
//                    break;
//            }
//        });
        MediaActivity2.setActionButtonYier(v -> {
            int currentItem = MediaActivity2.getInstance().getCurrentItemIndex();
            if(currentItem == -1) return;
            BroadcastMedia broadcastMedia = broadcastMedias.get(currentItem);
            switch (broadcastMedia.getType()){
                case BroadcastMedia.TYPE_IMAGE:
                    new Thread(() -> {
                        OperatingDialog operatingDialog = new OperatingDialog(MediaActivity2.getInstance());
                        operatingDialog.create().show();
                        try {
                            PublicFileAccessor.BroadcastMedia.saveImage(MediaActivity2.getInstance(), broadcast, currentItem);
                            operatingDialog.dismiss();
                            MessageDisplayer.autoShow(MediaActivity2.getInstance(), "已保存", MessageDisplayer.Duration.SHORT);
                        }catch (IOException | InterruptedException e){
                            ErrorLogger.log(e);
                            MessageDisplayer.autoShow(MediaActivity2.getInstance(), "保存失败", MessageDisplayer.Duration.SHORT);
                        }
                    }).start();
                    break;
                case BroadcastMedia.TYPE_VIDEO:
                    new Thread(() -> {
                        try {
                            PublicFileAccessor.BroadcastMedia.saveVideo(MediaActivity2.getInstance(), broadcast, currentItem);
                            MessageDisplayer.autoShow(MediaActivity2.getInstance(), "已保存", MessageDisplayer.Duration.SHORT);
                        } catch (OperationCanceledException e){
                            ErrorLogger.log(e);
                        } catch (InterruptedException | IOException e) {
                            ErrorLogger.log(e);
                            MessageDisplayer.autoShow(MediaActivity2.getInstance(), "保存失败", MessageDisplayer.Duration.SHORT);
                        }
                    }).start();
                    break;
            }
        });
        startActivity(intent);
    }

    @Override
    public void updateOneBroadcast(Broadcast newBroadcast) {
        broadcast = newBroadcast;
        initDo();
    }

    private void fetchAndShowLikesPreview() {
        binding.likeFlowLayout.removeAllViews();
        BroadcastApiCaller.fetchLikesOfBroadcast(this, broadcast.getBroadcastId(), null, 10,
                new RetrofitApiCaller.DelayedActionCommonYier<PaginatedOperationData<BroadcastLike>>(this, 500L, false, results -> {
            boolean state = (boolean) results[0];
            if(state){
                binding.layoutLike.setVisibility(View.VISIBLE);
                binding.likeLoadingIndicator.setVisibility(View.VISIBLE);
            }else {
                binding.likeLoadingIndicator.setVisibility(View.GONE);
            }
        }){
            @Override
            public void failure(Throwable t, Call<PaginatedOperationData<BroadcastLike>> call) {
                super.failure(t, call);
                binding.layoutLike.setVisibility(View.VISIBLE);
                binding.likeLoadFailedText.setVisibility(View.VISIBLE);
                binding.likeLoadFailedText.setText("出错了 > " + t.getClass().getName());
            }

            @Override
            public void notOk(int code, String message, Response<PaginatedOperationData<BroadcastLike>> raw, Call<PaginatedOperationData<BroadcastLike>> call) {
                super.notOk(code, message, raw, call);
                binding.layoutLike.setVisibility(View.VISIBLE);
                binding.likeLoadFailedText.setVisibility(View.VISIBLE);
                binding.likeLoadFailedText.setText("HTTP 状态码异常 > " + code);
            }

            @Override
            public void ok(PaginatedOperationData<BroadcastLike> data, Response<PaginatedOperationData<BroadcastLike>> raw, Call<PaginatedOperationData<BroadcastLike>> call) {
                super.ok(data, raw, call);
                data.commonHandleResult(BroadcastActivity.this, new int[]{-101, -102}, () -> {
                    binding.layoutLike.setVisibility(View.VISIBLE);
                    List<BroadcastLike> broadcastLikeList = data.getData();
                    broadcastLikeList.sort((o1, o2) -> - o1.getLikeTime().compareTo(o2.getLikeTime()));
                    broadcastLikeList.forEach(broadcastLike -> {
                        String channelName;
                        Channel channel = ChannelDatabaseManager.getInstance().findOneChannel(broadcastLike.getFromId());
                        if(channel != null) {
                            channelName = channel.getName();
                        }else {
                            channelName = broadcastLike.getFromName();
                        }
                        LayoutBroadcastLikePreviewItemBinding likePreviewItemBinding = LayoutBroadcastLikePreviewItemBinding.inflate(getLayoutInflater());
                        likePreviewItemBinding.channelName.setText(channelName);
                        binding.likeFlowLayout.addView(likePreviewItemBinding.getRoot());
                        likePreviewItemBinding.clickView.setOnClickListener(v -> {
                            Intent intent = new Intent(BroadcastActivity.this, ChannelActivity.class);
                            intent.putExtra(ExtraKeys.IMESSAGE_ID, broadcastLike.getFromId());
                            startActivity(intent);
                        });
                    });
                    LayoutBroadcastLikesAllButtonBinding likesAllButtonBinding = LayoutBroadcastLikesAllButtonBinding.inflate(getLayoutInflater());
                    binding.likeFlowLayout.addView(likesAllButtonBinding.getRoot());
                    likesAllButtonBinding.clickView.setOnClickListener(v -> {
                        Intent intent = new Intent(BroadcastActivity.this, BroadcastLikesActivity.class);
                        intent.putExtra(ExtraKeys.BROADCAST, broadcast);
                        startActivity(intent);
                    });
                }, new OperationStatus.HandleResult(-103, () -> {
                    binding.layoutLike.setVisibility(View.GONE);
                }));
            }
        });
    }

    private void startComment(){
        onComment = true;
        replyToBroadcastComment = null;
        endReplyYier = null;
        binding.sendCommentBar.setVisibility(View.VISIBLE);
        binding.commentFab.setVisibility(View.GONE);
        binding.commentInput.setHint("评论");
        UiUtil.openKeyboard(binding.commentInput);
    }

    private void endComment(){
        onComment = false;
        binding.sendCommentBar.setVisibility(View.GONE);
        new Handler().postDelayed(this::checkAndShowOrHideFab, 300);
        if(endReplyYier != null){
            endReplyYier.onResults();
            endReplyYier = null;
        }
    }

    public void startReply(BroadcastComment broadcastComment, ResultsYier endReplyYier){
        onComment = true;
        replyToBroadcastComment = broadcastComment;
        this.endReplyYier = endReplyYier;
        binding.sendCommentBar.setVisibility(View.VISIBLE);
        binding.commentFab.setVisibility(View.GONE);
        binding.commentInput.setHint("回复 " + broadcastComment.getFromNameIncludeNote());
        UiUtil.openKeyboard(binding.commentInput);
    }

    private void commentNextPage() {
        NEXT_PAGE_LATCH = new CountDownLatch(1);
        String lastCommentId = null;
        if(!commentsLinearLayoutViews.getAllItems().isEmpty()){
            lastCommentId = commentsLinearLayoutViews.getAllItems().get(commentsLinearLayoutViews.getAllItems().size() - 1).getCommentId();
        }
        BroadcastApiCaller.fetchCommentsOfBroadcast(this, broadcast.getBroadcastId(), lastCommentId, Constants.FETCH_BROADCAST_COMMENTS_PAGE_SIZE,
                new RetrofitApiCaller.DelayedActionCommonYier<PaginatedOperationData<BroadcastComment>>(this, 500L, results -> {
            boolean state = (boolean) results[0];
            Call<PaginatedOperationData<BroadcastComment>> call = (Call<PaginatedOperationData<BroadcastComment>>) results[1];
            if (breakFetchNextPage(call)) return;
            if(state){
                binding.layoutComment.setVisibility(View.VISIBLE);
                footerBinding.loadFailedText.setVisibility(View.GONE);
                footerBinding.loadFailedText.setText(null);
                footerBinding.loadingIndicator.setVisibility(View.VISIBLE);
            }else {
                footerBinding.loadingIndicator.setVisibility(View.GONE);
                NEXT_PAGE_LATCH.countDown();
            }
        }){
            @Override
            public void notOk(int code, String message, Response<PaginatedOperationData<BroadcastComment>> row, Call<PaginatedOperationData<BroadcastComment>> call) {
                super.notOk(code, message, row, call);
                if (breakFetchNextPage(call)) return;
                footerBinding.loadFailedText.setVisibility(View.VISIBLE);
                footerBinding.loadFailedText.setText("HTTP 状态码异常 > " + code);
                binding.scrollView.scrollTo(0, binding.scrollView.getChildAt(0).getHeight());
                stopFetchNextPage = true;
            }

            @Override
            public void failure(Throwable t, Call<PaginatedOperationData<BroadcastComment>> call) {
                super.failure(t, call);
                if (breakFetchNextPage(call)) return;
                footerBinding.loadFailedText.setVisibility(View.VISIBLE);
                footerBinding.loadFailedText.setText("出错了 > " + t.getClass().getName());
                binding.scrollView.scrollTo(0, binding.scrollView.getChildAt(0).getHeight());
                stopFetchNextPage = true;
            }

            @Override
            public void ok(PaginatedOperationData<BroadcastComment> data, Response<PaginatedOperationData<BroadcastComment>> raw, Call<PaginatedOperationData<BroadcastComment>> call) {
                super.ok(data, raw, call);
                data.commonHandleResult(BroadcastActivity.this, new int[]{-101, -102}, () -> {
                    if (breakFetchNextPage(call)) return;
                    stopFetchNextPage = !raw.body().hasMore();
                    List<BroadcastComment> broadcastCommentList = data.getData();
                    commentsLinearLayoutViews.addItemsAndShow(broadcastCommentList);
                    if(onCommentsNextPageYier != null) onCommentsNextPageYier.onResults();
                }, new OperationStatus.HandleResult(-103, () -> {
                    binding.layoutComment.setVisibility(View.GONE);
                }));
            }
        });
    }

    private boolean breakFetchNextPage(Call<PaginatedOperationData<BroadcastComment>> call) {
        if(stopFetchNextPage) {
            call.cancel();
            footerBinding.loadingIndicator.setVisibility(View.GONE);
            if(NEXT_PAGE_LATCH != null && NEXT_PAGE_LATCH.getCount() == 1) NEXT_PAGE_LATCH.countDown();
            return true;
        }
        return false;
    }

    private void startLocateComment(BroadcastComment broadcastComment){
        onCommentsNextPageYier = results -> {
            binding.scrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @SuppressLint("ClickableViewAccessibility")
                @Override
                public void onGlobalLayout() {
                    binding.scrollView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    boolean scrollSuccess = commentsLinearLayoutViews.scrollTo(broadcastComment, true, null);
                    if (!scrollSuccess) {
                        commentNextPage();
                    }
                }
            });
        };
    }

    @Override
    public void onSetChannelBroadcastExclude(int selectedPosition, String excludeChannelImessageId) {
        if(excludeChannelImessageId.equals(broadcast.getImessageId())){
            Self currentUserProfile = SharedPreferencesAccessor.UserProfilePref.getCurrentUserProfile(this);
            checkAndShowMoreIcon(currentUserProfile);
        }
    }
}