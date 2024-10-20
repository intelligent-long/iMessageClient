package com.longx.intelligent.android.ichat2.activity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Size;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.tabs.TabLayoutMediator;
import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.activity.helper.BaseActivity;
import com.longx.intelligent.android.ichat2.adapter.ForwardMessagePagerAdapter;
import com.longx.intelligent.android.ichat2.da.database.manager.OpenedChatDatabaseManager;
import com.longx.intelligent.android.ichat2.data.ChatMessage;
import com.longx.intelligent.android.ichat2.data.OpenedChat;
import com.longx.intelligent.android.ichat2.data.request.SendImageChatMessagePostBody;
import com.longx.intelligent.android.ichat2.data.request.SendTextChatMessagePostBody;
import com.longx.intelligent.android.ichat2.data.response.OperationData;
import com.longx.intelligent.android.ichat2.databinding.ActivityForwardMessageBinding;
import com.longx.intelligent.android.ichat2.dialog.ConfirmDialog;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.ChatApiCaller;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.ichat2.procedure.MessageDisplayer;
import com.longx.intelligent.android.ichat2.ui.glide.GlideApp;
import com.longx.intelligent.android.ichat2.util.AudioUtil;
import com.longx.intelligent.android.ichat2.util.FileUtil;
import com.longx.intelligent.android.ichat2.util.TimeUtil;
import com.longx.intelligent.android.ichat2.util.UiUtil;
import com.longx.intelligent.android.ichat2.value.Constants;
import com.longx.intelligent.android.ichat2.yier.GlobalYiersHolder;
import com.longx.intelligent.android.ichat2.yier.NewContentBadgeDisplayYier;
import com.longx.intelligent.android.ichat2.yier.OpenedChatsUpdateYier;

import java.io.File;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Response;

public class ForwardMessageActivity extends BaseActivity {
    private ActivityForwardMessageBinding binding;
    private ChatMessage chatMessage;
    private static String[] PAGER_TITLES;
    private ForwardMessagePagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PAGER_TITLES = new String[]{getString(R.string.forward_message_messages), getString(R.string.forward_message_channels)};
        binding = ActivityForwardMessageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupCloseBackNavigation(binding.toolbar);
        intentData();
        init();
        showContent();
        setupYiers();
    }

    private void intentData() {
        chatMessage = getIntent().getParcelableExtra(ExtraKeys.CHAT_MESSAGE);
    }

    private void init(){
        pagerAdapter = new ForwardMessagePagerAdapter(this);
        binding.viewPager.setAdapter(pagerAdapter);
        new TabLayoutMediator(binding.tabs, binding.viewPager, (tab, position) -> tab.setText(PAGER_TITLES[position])).attach();
    }

    private void showContent() {
        showChatMessage();
    }

    private void showChatMessage() {
        RequestOptions requestOptions = new RequestOptions()
                .transform(new RoundedCorners(UiUtil.dpToPx(this, 7)))
                .diskCacheStrategy(DiskCacheStrategy.ALL);
        switch (chatMessage.getType()) {
            case ChatMessage.TYPE_TEXT: {
                binding.layoutText.setVisibility(View.VISIBLE);
                binding.image.setVisibility(View.GONE);
                binding.layoutFile.setVisibility(View.GONE);
                binding.layoutVideo.setVisibility(View.GONE);
                binding.layoutVoice.setVisibility(View.GONE);
                binding.text.setText(chatMessage.getText());
                break;
            }
            case ChatMessage.TYPE_IMAGE: {
                binding.layoutText.setVisibility(View.GONE);
                binding.image.setVisibility(View.VISIBLE);
                binding.layoutFile.setVisibility(View.GONE);
                binding.layoutVideo.setVisibility(View.GONE);
                binding.layoutVoice.setVisibility(View.GONE);
                setupImageViewSize(binding.image, chatMessage.getImageSize());
                String imageFilePath = chatMessage.getImageFilePath();
                GlideApp.with(getApplicationContext())
                        .load(new File(imageFilePath))
                        .apply(requestOptions)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(binding.image);
                break;
            }
            case ChatMessage.TYPE_FILE: {
                binding.layoutText.setVisibility(View.GONE);
                binding.image.setVisibility(View.GONE);
                binding.layoutFile.setVisibility(View.VISIBLE);
                binding.layoutVideo.setVisibility(View.GONE);
                binding.layoutVoice.setVisibility(View.GONE);
                binding.fileName.setText(chatMessage.getFileName());
                binding.fileSize.setText(FileUtil.formatFileSize(FileUtil.getFileSize(chatMessage.getFileFilePath())));
                break;
            }
            case ChatMessage.TYPE_VIDEO: {
                binding.layoutText.setVisibility(View.GONE);
                binding.image.setVisibility(View.GONE);
                binding.layoutFile.setVisibility(View.GONE);
                binding.layoutVideo.setVisibility(View.VISIBLE);
                binding.layoutVoice.setVisibility(View.GONE);
                setupImageViewSize(binding.videoThumbnail, chatMessage.getVideoSize());
                GlideApp
                        .with(getApplicationContext())
                        .load(chatMessage.getVideoFilePath())
                        .apply(requestOptions)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(binding.videoThumbnail);
                if (chatMessage.getVideoDuration() != null) {
                    binding.videoDuration.setText(TimeUtil.formatTime(chatMessage.getVideoDuration()));
                }
                break;
            }
            case ChatMessage.TYPE_VOICE: {
                binding.layoutText.setVisibility(View.VISIBLE);
                binding.image.setVisibility(View.GONE);
                binding.layoutFile.setVisibility(View.GONE);
                binding.layoutVideo.setVisibility(View.GONE);
                binding.layoutVoice.setVisibility(View.GONE);
                long duration = AudioUtil.getDuration(this, chatMessage.getVoiceFilePath());
                binding.text.setText("[语音 " + TimeUtil.formatMillisecondsToMinSec(duration) + "]");
            }
        }
    }

    private void setupImageViewSize(@NonNull View imageView, Size size) {
        int imageWidth = size.getWidth();
        int imageHeight = size.getHeight();
        int viewWidth;
        int viewHeight;
        if(imageWidth / (double) imageHeight > Constants.CHAT_IMAGE_VIEW_MAX_WIDTH_DP / (double)Constants.CHAT_IMAGE_VIEW_MAX_HEIGHT_DP){
            viewWidth = UiUtil.dpToPx(this, Constants.CHAT_IMAGE_VIEW_MAX_WIDTH_DP);
            viewHeight = (int) Math.round((viewWidth / (double) imageWidth) * imageHeight);
        }else {
            viewHeight = UiUtil.dpToPx(this, Constants.CHAT_IMAGE_VIEW_MAX_HEIGHT_DP);
            viewWidth = (int) Math.round((viewHeight / (double) imageHeight) * imageWidth);
        }
        ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
        layoutParams.width = viewWidth;
        layoutParams.height = viewHeight;
    }

    private void setupYiers() {
        binding.forwardButton.setOnClickListener(v -> {
            Set<String> checkedChannelIds = pagerAdapter.getCheckedChannelIds();
            if(checkedChannelIds.isEmpty()){
                MessageDisplayer.autoShow(this, "请选择要转发的频道", MessageDisplayer.Duration.SHORT);
                return;
            }
            new ConfirmDialog(this, "是否转发？")
                    .setNegativeButton(null)
                    .setPositiveButton((dialog, which) -> {
                        checkedChannelIds.forEach(toForwardIchatId -> {
                            switch (chatMessage.getType()){
                                case ChatMessage.TYPE_VOICE:
                                case ChatMessage.TYPE_TEXT:{
                                    String message = null;
                                    if(chatMessage.getType() == ChatMessage.TYPE_TEXT) message = chatMessage.getText();
                                    if(chatMessage.getType() == ChatMessage.TYPE_VOICE) {
                                        long duration = AudioUtil.getDuration(this, chatMessage.getVoiceFilePath());
                                        message = "[语音 " + TimeUtil.formatMillisecondsToMinSec(duration) + "]";
                                    }
                                    SendTextChatMessagePostBody postBody = new SendTextChatMessagePostBody(toForwardIchatId, message);
                                    ChatApiCaller.sendTextChatMessage(this, postBody, new RetrofitApiCaller.BaseCommonYier<OperationData>(this){

                                        @Override
                                        public void ok(OperationData data, Response<OperationData> raw, Call<OperationData> call) {
                                            super.ok(data, raw, call);
                                            data.commonHandleResult(ForwardMessageActivity.this, new int[]{-101}, () -> {
                                                ChatMessage chatMessage = data.getData(ChatMessage.class);
                                                chatMessage.setViewed(true);
                                                ChatMessage.mainDoOnNewChatMessage(chatMessage, ForwardMessageActivity.this, results -> {
                                                    OpenedChatDatabaseManager.getInstance().insertOrUpdate(new OpenedChat(chatMessage.getTo(), 0, true));
                                                    GlobalYiersHolder.getYiers(OpenedChatsUpdateYier.class).ifPresent(openedChatUpdateYiers -> {
                                                        openedChatUpdateYiers.forEach(OpenedChatsUpdateYier::onOpenedChatsUpdate);
                                                    });
                                                    GlobalYiersHolder.getYiers(NewContentBadgeDisplayYier.class).ifPresent(newContentBadgeDisplayYiers -> {
                                                        newContentBadgeDisplayYiers.forEach(newContentBadgeDisplayYier -> {
                                                            newContentBadgeDisplayYier.autoShowNewContentBadge(ForwardMessageActivity.this, NewContentBadgeDisplayYier.ID.MESSAGES);
                                                        });
                                                    });
                                                });
                                            });
                                        }
                                    });
                                    break;
                                }
                                case ChatMessage.TYPE_IMAGE:{
                                    SendImageChatMessagePostBody postBody = new SendImageChatMessagePostBody(toForwardIchatId, new File(chatMessage.getImageFilePath()).getName());
                                    ChatApiCaller.sendImageChatMessage(this, this, Uri.fromFile(new File(chatMessage.getImageFilePath())), postBody, new RetrofitApiCaller.BaseCommonYier<OperationData>(this) {

                                        @Override
                                        public void ok(OperationData data, Response<OperationData> raw, Call<OperationData> call) {
                                            super.ok(data, raw, call);
                                            data.commonHandleResult(ForwardMessageActivity.this, new int[]{-101, -102}, () -> {
                                                ChatMessage chatMessage = data.getData(ChatMessage.class);
                                                chatMessage.setViewed(true);
                                                ChatMessage.mainDoOnNewChatMessage(chatMessage, ForwardMessageActivity.this, results -> {
                                                    OpenedChatDatabaseManager.getInstance().insertOrUpdate(new OpenedChat(chatMessage.getTo(), 0, true));
                                                    GlobalYiersHolder.getYiers(OpenedChatsUpdateYier.class).ifPresent(openedChatUpdateYiers -> {
                                                        openedChatUpdateYiers.forEach(OpenedChatsUpdateYier::onOpenedChatsUpdate);
                                                    });
                                                    GlobalYiersHolder.getYiers(NewContentBadgeDisplayYier.class).ifPresent(newContentBadgeDisplayYiers -> {
                                                        newContentBadgeDisplayYiers.forEach(newContentBadgeDisplayYier -> {
                                                            newContentBadgeDisplayYier.autoShowNewContentBadge(ForwardMessageActivity.this, NewContentBadgeDisplayYier.ID.MESSAGES);
                                                        });
                                                    });
                                                });
                                            });
                                        }
                                    }, (current, total) -> {
                                        runOnUiThread(() -> {

                                        });
                                    });
                                }
                            }
                        });
                    })
                    .create()
                    .show();
        });
    }
}