package com.longx.intelligent.android.ichat2.activity;

import android.os.Bundle;
import android.util.Size;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.tabs.TabLayoutMediator;
import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.activity.helper.BaseActivity;
import com.longx.intelligent.android.ichat2.adapter.ForwardMessagePagerAdapter;
import com.longx.intelligent.android.ichat2.data.ChatMessage;
import com.longx.intelligent.android.ichat2.databinding.ActivityForwardMessageBinding;
import com.longx.intelligent.android.ichat2.ui.glide.GlideApp;
import com.longx.intelligent.android.ichat2.util.AudioUtil;
import com.longx.intelligent.android.ichat2.util.FileUtil;
import com.longx.intelligent.android.ichat2.util.TimeUtil;
import com.longx.intelligent.android.ichat2.util.UiUtil;
import com.longx.intelligent.android.ichat2.value.Constants;

import java.io.File;
import java.util.Objects;

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
                binding.layoutText.setVisibility(View.GONE);
                binding.image.setVisibility(View.GONE);
                binding.layoutFile.setVisibility(View.GONE);
                binding.layoutVideo.setVisibility(View.GONE);
                binding.layoutVoice.setVisibility(View.VISIBLE);
                long duration = AudioUtil.getDuration(this, chatMessage.getVoiceFilePath());
                binding.voiceTime.setText(TimeUtil.formatMillisecondsToMinSec(duration));
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

    }
}