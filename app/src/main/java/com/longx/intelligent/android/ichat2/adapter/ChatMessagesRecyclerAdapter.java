package com.longx.intelligent.android.ichat2.adapter;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.util.Size;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.activity.ChannelActivity;
import com.longx.intelligent.android.ichat2.activity.ChatActivity;
import com.longx.intelligent.android.ichat2.activity.ChatFileActivity;
import com.longx.intelligent.android.ichat2.activity.MediaActivity;
import com.longx.intelligent.android.ichat2.activity.ExtraKeys;
import com.longx.intelligent.android.ichat2.behavior.ChatVoicePlayer;
import com.longx.intelligent.android.ichat2.behavior.GlideBehaviours;
import com.longx.intelligent.android.ichat2.behavior.MessageDisplayer;
import com.longx.intelligent.android.ichat2.da.database.manager.ChannelDatabaseManager;
import com.longx.intelligent.android.ichat2.da.database.manager.ChatMessageDatabaseManager;
import com.longx.intelligent.android.ichat2.da.publicfile.PublicFileAccessor;
import com.longx.intelligent.android.ichat2.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.ichat2.data.Channel;
import com.longx.intelligent.android.ichat2.data.ChatMessage;
import com.longx.intelligent.android.ichat2.data.Self;
import com.longx.intelligent.android.ichat2.databinding.RecyclerItemChatMessageBinding;
import com.longx.intelligent.android.ichat2.dialog.OperatingDialog;
import com.longx.intelligent.android.ichat2.media.MediaType;
import com.longx.intelligent.android.ichat2.media.data.Media;
import com.longx.intelligent.android.ichat2.net.dataurl.NetDataUrls;
import com.longx.intelligent.android.ichat2.popupwindow.ChatMessageActionsPopupWindow;
import com.longx.intelligent.android.ichat2.ui.RecyclerViewScrollDisabler;
import com.longx.intelligent.android.ichat2.ui.glide.GlideApp;
import com.longx.intelligent.android.ichat2.util.AudioUtil;
import com.longx.intelligent.android.ichat2.util.ErrorLogger;
import com.longx.intelligent.android.ichat2.util.FileUtil;
import com.longx.intelligent.android.ichat2.util.TimeUtil;
import com.longx.intelligent.android.ichat2.util.UiUtil;
import com.longx.intelligent.android.ichat2.value.Constants;
import com.longx.intelligent.android.ichat2.yier.GlobalYiersHolder;
import com.longx.intelligent.android.ichat2.yier.KeyboardVisibilityYier;
import com.longx.intelligent.android.ichat2.yier.OpenedChatsUpdateYier;
import com.longx.intelligent.android.lib.recyclerview.WrappableRecyclerViewAdapter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Created by LONG on 2024/5/15 at 1:11 PM.
 */
public class ChatMessagesRecyclerAdapter extends WrappableRecyclerViewAdapter<ChatMessagesRecyclerAdapter.ViewHolder, ChatMessagesRecyclerAdapter.ItemData> {
    private final ChatActivity activity;
    private final com.longx.intelligent.android.lib.recyclerview.RecyclerView recyclerView;
    private final List<ItemData> itemDataList = new ArrayList<>();
    private final RequestOptions requestOptions;
    private final ChatVoicePlayer chatVoicePlayer;

    public ChatMessagesRecyclerAdapter(ChatActivity activity, com.longx.intelligent.android.lib.recyclerview.RecyclerView recyclerView) {
        this.activity = activity;
        this.recyclerView = recyclerView;
        requestOptions = new RequestOptions()
                .transform(new RoundedCorners(UiUtil.dpToPx(activity, 7)))
                .diskCacheStrategy(DiskCacheStrategy.ALL);
        chatVoicePlayer = new ChatVoicePlayer(activity);
        ChatVoicePlayer.State chatVoicePlayerState = SharedPreferencesAccessor.ChatPref.getChatVoicePlayerState(activity);
        if(chatVoicePlayerState.getId() != null && chatVoicePlayerState.getUri() != null) {
            chatVoicePlayer.init(chatVoicePlayerState.getUri(), chatVoicePlayerState.getId());
        }
        if(chatVoicePlayerState.getPosition() != -1) {
            chatVoicePlayer.seekTo(chatVoicePlayerState.getPosition());
        }
        chatVoicePlayer.setOnPlayStateChangeYier(new ChatVoicePlayer.OnPlayStateChangeYier() {
            @Override
            public void onStart(String id) {
                for (int i = 0; i < itemDataList.size(); i++) {
                    if(itemDataList.get(i).chatMessage.getUuid().equals(id)){
                        notifyItemChanged(i);
                        break;
                    }
                }
            }

            @Override
            public void onPause(String id) {
                for (int i = 0; i < itemDataList.size(); i++) {
                    if(itemDataList.get(i).chatMessage.getUuid().equals(id)){
                        notifyItemChanged(i);
                        break;
                    }
                }
            }

            @Override
            public void onStop(String id, boolean complete) {
                for (int i = 0; i < itemDataList.size(); i++) {
                    ChatMessage thisChatMessage = itemDataList.get(i).chatMessage;
                    if(thisChatMessage.getUuid().equals(id)){
                        if(complete){
                            thisChatMessage.setVoiceListened(true);
                            ChatMessageDatabaseManager.getInstanceOrInitAndGet(activity, activity.getChannel().getIchatId()).update(thisChatMessage);
                        }
                        notifyItemChanged(i);
                        break;
                    }
                }
            }

            @Override
            public void onError(String id, int what, int extra) {
                for (int i = 0; i < itemDataList.size(); i++) {
                    if(itemDataList.get(i).chatMessage.getUuid().equals(id)){
                        MessageDisplayer.autoShow(activity, "语音播放出错", MessageDisplayer.Duration.LONG);
                        notifyItemChanged(i);
                        break;
                    }
                }
            }
        });
    }
    private RecyclerViewScrollDisabler scrollDisabler;

    public static class ItemData{
        private final ChatMessage chatMessage;

        public ItemData(ChatMessage chatMessage) {
            this.chatMessage = chatMessage;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ItemData itemData = (ItemData) o;
            return chatMessage.equals(itemData.chatMessage);
        }

        @Override
        public int hashCode() {
            return Objects.hash(chatMessage);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private RecyclerItemChatMessageBinding binding;
        public ViewHolder(RecyclerItemChatMessageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerItemChatMessageBinding binding = RecyclerItemChatMessageBinding.inflate(activity.getLayoutInflater());
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemData itemData = itemDataList.get(position);
        //Yier
        setupYiers(holder, position);
        //时间
        if(Boolean.TRUE.equals(itemData.chatMessage.isShowTime())) {
            holder.binding.time.setVisibility(View.VISIBLE);
            String timeText = TimeUtil.formatRelativeTime(itemData.chatMessage.getTime());
            holder.binding.time.setText(timeText);
        }else {
            holder.binding.time.setVisibility(View.GONE);
        }
        //发送还是接收
        if(itemData.chatMessage.isSelfSender(activity)){
            holder.binding.layoutReceive.setVisibility(View.GONE);
            holder.binding.layoutSend.setVisibility(View.VISIBLE);
            //气泡颜色
            int chatBubbleColor = SharedPreferencesAccessor.DefaultPref.getChatBubbleColor(activity);
            switch (chatBubbleColor){
                case 0:
                    holder.binding.textSend.setBackgroundTintList(ColorStateList.valueOf(activity.getColor(R.color.chat_message_send_background_green)));
                    holder.binding.voiceSend.setBackgroundTintList(ColorStateList.valueOf(activity.getColor(R.color.chat_message_send_background_green)));
                    break;
                case 1:
                    holder.binding.textSend.setBackgroundTintList(ColorStateList.valueOf(activity.getColor(R.color.chat_message_send_background_blue)));
                    holder.binding.voiceSend.setBackgroundTintList(ColorStateList.valueOf(activity.getColor(R.color.chat_message_send_background_blue)));
                    break;
            }
            //头像
            holder.binding.avatarSend.setOnClickListener(v -> {
                Intent intent = new Intent(activity, ChannelActivity.class);
                intent.putExtra(ExtraKeys.ICHAT_ID, itemData.chatMessage.getFrom());
                activity.startActivity(intent);
            });
            Self currentUserInfo = SharedPreferencesAccessor.UserProfilePref.getCurrentUserProfile(activity);
            String avatarHash = currentUserInfo.getAvatar() == null ? null : currentUserInfo.getAvatar().getHash();
            if (avatarHash == null) {
                GlideBehaviours.loadToImageView(activity.getApplicationContext(), R.drawable.default_avatar, holder.binding.avatarSend);
            } else {
                GlideBehaviours.loadToImageView(activity.getApplicationContext(), NetDataUrls.getAvatarUrl(activity, avatarHash), holder.binding.avatarSend);
            }
            //不同消息类型
            switch (itemData.chatMessage.getType()){
                case ChatMessage.TYPE_TEXT:{
                    holder.binding.layoutTextSend.setVisibility(View.VISIBLE);
                    holder.binding.imageSend.setVisibility(View.GONE);
                    holder.binding.layoutFileSend.setVisibility(View.GONE);
                    holder.binding.layoutVideoSend.setVisibility(View.GONE);
                    holder.binding.layoutVoiceSend.setVisibility(View.GONE);
                    holder.binding.textSend.setText(itemData.chatMessage.getText());
                    break;
                }
                case ChatMessage.TYPE_IMAGE:{
                    holder.binding.layoutTextSend.setVisibility(View.GONE);
                    holder.binding.imageSend.setVisibility(View.VISIBLE);
                    holder.binding.layoutFileSend.setVisibility(View.GONE);
                    holder.binding.layoutVideoSend.setVisibility(View.GONE);
                    holder.binding.layoutVoiceSend.setVisibility(View.GONE);
                    setupImageViewSize(holder.binding.imageSend, itemData.chatMessage.getImageSize());
                    String imageFilePath = itemData.chatMessage.getImageFilePath();
                    GlideApp.with(activity.getApplicationContext())
                            .load(new File(imageFilePath))
                            .apply(requestOptions)
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .into(holder.binding.imageSend);
                    break;
                }
                case ChatMessage.TYPE_FILE:{
                    holder.binding.layoutTextSend.setVisibility(View.GONE);
                    holder.binding.imageSend.setVisibility(View.GONE);
                    holder.binding.layoutFileSend.setVisibility(View.VISIBLE);
                    holder.binding.layoutVideoSend.setVisibility(View.GONE);
                    holder.binding.layoutVoiceSend.setVisibility(View.GONE);
                    holder.binding.fileNameSend.setText(itemData.chatMessage.getFileName());
                    holder.binding.fileSizeSend.setText(FileUtil.formatFileSize(FileUtil.getFileSize(itemData.chatMessage.getFileFilePath())));
                    break;
                }
                case ChatMessage.TYPE_VIDEO:{
                    holder.binding.layoutTextSend.setVisibility(View.GONE);
                    holder.binding.imageSend.setVisibility(View.GONE);
                    holder.binding.layoutFileSend.setVisibility(View.GONE);
                    holder.binding.layoutVideoSend.setVisibility(View.VISIBLE);
                    holder.binding.layoutVoiceSend.setVisibility(View.GONE);
                    setupImageViewSize(holder.binding.videoThumbnailSend, itemData.chatMessage.getVideoSize());
                    GlideApp
                            .with(activity.getApplicationContext())
                            .load(itemData.chatMessage.getVideoFilePath())
                            .frame(1000000)
                            .apply(requestOptions)
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .into(holder.binding.videoThumbnailSend);
                    if(itemData.chatMessage.getVideoDuration() != null) {
                        holder.binding.videoDurationSend.setText(TimeUtil.formatTime(itemData.chatMessage.getVideoDuration()));
                    }
                    break;
                }
                case ChatMessage.TYPE_VOICE:{
                    holder.binding.layoutTextSend.setVisibility(View.GONE);
                    holder.binding.imageSend.setVisibility(View.GONE);
                    holder.binding.layoutFileSend.setVisibility(View.GONE);
                    holder.binding.layoutVideoSend.setVisibility(View.GONE);
                    holder.binding.layoutVoiceSend.setVisibility(View.VISIBLE);
                    long duration = AudioUtil.getDuration(activity, itemData.chatMessage.getVoiceFilePath());
                    holder.binding.voiceTimeSend.setText(TimeUtil.formatMillisecondsToMinSec(duration));
                    if(Objects.equals(chatVoicePlayer.getId(), itemData.chatMessage.getUuid())) {
                        if(chatVoicePlayer.isPaused()){
                            holder.binding.voiceSendIcon.setVisibility(View.VISIBLE);
                            holder.binding.voiceSendPlayingSwitchingImages.setVisibility(View.GONE);
                            holder.binding.voiceSendPlayingSwitchingImages.stopAnimating();
                            holder.binding.continueVoicePlaybackSend.setVisibility(View.VISIBLE);
                            holder.binding.pauseVoicePlaybackSend.setVisibility(View.GONE);
                        }else if (chatVoicePlayer.isPlaying()) {
                            holder.binding.voiceSendIcon.setVisibility(View.GONE);
                            holder.binding.voiceSendPlayingSwitchingImages.setVisibility(View.VISIBLE);
                            holder.binding.voiceSendPlayingSwitchingImages.startAnimating();
                            holder.binding.continueVoicePlaybackSend.setVisibility(View.GONE);
                            holder.binding.pauseVoicePlaybackSend.setVisibility(View.VISIBLE);
                        } else {
                            holder.binding.voiceSendIcon.setVisibility(View.VISIBLE);
                            holder.binding.voiceSendPlayingSwitchingImages.setVisibility(View.GONE);
                            holder.binding.voiceSendPlayingSwitchingImages.stopAnimating();
                            holder.binding.continueVoicePlaybackSend.setVisibility(View.GONE);
                            holder.binding.pauseVoicePlaybackSend.setVisibility(View.GONE);
                        }
                    }else {
                        holder.binding.voiceSendIcon.setVisibility(View.VISIBLE);
                        holder.binding.voiceSendPlayingSwitchingImages.setVisibility(View.GONE);
                        holder.binding.voiceSendPlayingSwitchingImages.stopAnimating();
                        holder.binding.continueVoicePlaybackSend.setVisibility(View.GONE);
                        holder.binding.pauseVoicePlaybackSend.setVisibility(View.GONE);
                    }
                    break;
                }
            }
        }else {
            holder.binding.layoutReceive.setVisibility(View.VISIBLE);
            holder.binding.layoutSend.setVisibility(View.GONE);
            //头像
            holder.binding.avatarReceive.setOnClickListener(v -> {
                Intent intent = new Intent(activity, ChannelActivity.class);
                intent.putExtra(ExtraKeys.ICHAT_ID, itemData.chatMessage.getFrom());
                activity.startActivity(intent);
            });
            Channel channel = ChannelDatabaseManager.getInstance().findOneChannel(itemData.chatMessage.getFrom());
            String avatarHash = channel.getAvatar() == null ? null : channel.getAvatar().getHash();
            if (avatarHash == null) {
                GlideBehaviours.loadToImageView(activity.getApplicationContext(), R.drawable.default_avatar, holder.binding.avatarReceive);
            } else {
                GlideBehaviours.loadToImageView(activity.getApplicationContext(), NetDataUrls.getAvatarUrl(activity, avatarHash), holder.binding.avatarReceive);
            }
            //不同消息类型
            switch (itemData.chatMessage.getType()){
                case ChatMessage.TYPE_TEXT:{
                    holder.binding.layoutTextReceive.setVisibility(View.VISIBLE);
                    holder.binding.imageReceive.setVisibility(View.GONE);
                    holder.binding.layoutFileReceive.setVisibility(View.GONE);
                    holder.binding.layoutVideoReceive.setVisibility(View.GONE);
                    holder.binding.layoutVoiceReceive.setVisibility(View.GONE);
                    holder.binding.textReceive.setText(itemData.chatMessage.getText());
                    break;
                }
                case ChatMessage.TYPE_IMAGE:{
                    holder.binding.layoutTextReceive.setVisibility(View.GONE);
                    holder.binding.imageReceive.setVisibility(View.VISIBLE);
                    holder.binding.layoutFileReceive.setVisibility(View.GONE);
                    holder.binding.layoutVideoReceive.setVisibility(View.GONE);
                    holder.binding.layoutVoiceReceive.setVisibility(View.GONE);
                    setupImageViewSize(holder.binding.imageReceive, itemData.chatMessage.getImageSize());
                    String imageFilePath = itemData.chatMessage.getImageFilePath();
                    GlideApp.with(activity.getApplicationContext())
                            .load(new File(imageFilePath))
                            .apply(requestOptions)
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .into(holder.binding.imageReceive);
                    break;
                }
                case ChatMessage.TYPE_FILE:{
                    holder.binding.layoutTextReceive.setVisibility(View.GONE);
                    holder.binding.imageReceive.setVisibility(View.GONE);
                    holder.binding.layoutFileReceive.setVisibility(View.VISIBLE);
                    holder.binding.layoutVideoReceive.setVisibility(View.GONE);
                    holder.binding.layoutVoiceReceive.setVisibility(View.GONE);
                    holder.binding.fileNameReceive.setText(itemData.chatMessage.getFileName());
                    holder.binding.fileSizeReceive.setText(FileUtil.formatFileSize(FileUtil.getFileSize(itemData.chatMessage.getFileFilePath())));
                    break;
                }
                case ChatMessage.TYPE_VIDEO:{
                    holder.binding.layoutTextReceive.setVisibility(View.GONE);
                    holder.binding.imageReceive.setVisibility(View.GONE);
                    holder.binding.layoutFileReceive.setVisibility(View.GONE);
                    holder.binding.layoutVideoReceive.setVisibility(View.VISIBLE);
                    holder.binding.layoutVoiceReceive.setVisibility(View.GONE);
                    setupImageViewSize(holder.binding.videoThumbnailReceive, itemData.chatMessage.getVideoSize());
                    GlideApp
                            .with(activity.getApplicationContext())
                            .load(itemData.chatMessage.getVideoFilePath())
                            .frame(1000_000)
                            .apply(requestOptions)
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .into(holder.binding.videoThumbnailReceive);
                    if(itemData.chatMessage.getVideoDuration() != null) {
                        holder.binding.videoDurationReceive.setText(TimeUtil.formatTime(itemData.chatMessage.getVideoDuration()));
                    }
                    break;
                }
                case ChatMessage.TYPE_VOICE:{
                    holder.binding.layoutTextReceive.setVisibility(View.GONE);
                    holder.binding.imageReceive.setVisibility(View.GONE);
                    holder.binding.layoutFileReceive.setVisibility(View.GONE);
                    holder.binding.layoutVideoReceive.setVisibility(View.GONE);
                    holder.binding.layoutVoiceReceive.setVisibility(View.VISIBLE);
                    long duration = AudioUtil.getDuration(activity, itemData.chatMessage.getVoiceFilePath());
                    holder.binding.voiceTimeReceive.setText(TimeUtil.formatMillisecondsToMinSec(duration));
                    if(Objects.equals(chatVoicePlayer.getId(), itemData.chatMessage.getUuid())) {
                        if(chatVoicePlayer.isPaused()){
                            holder.binding.voiceReceiveIcon.setVisibility(View.VISIBLE);
                            holder.binding.voiceReceivePlayingSwitchingImages.setVisibility(View.GONE);
                            holder.binding.voiceReceivePlayingSwitchingImages.stopAnimating();
                            holder.binding.continueVoicePlaybackReceive.setVisibility(View.VISIBLE);
                            holder.binding.pauseVoicePlaybackReceive.setVisibility(View.GONE);
                        }else if (chatVoicePlayer.isPlaying()) {
                            holder.binding.voiceReceiveIcon.setVisibility(View.GONE);
                            holder.binding.voiceReceivePlayingSwitchingImages.setVisibility(View.VISIBLE);
                            holder.binding.voiceReceivePlayingSwitchingImages.startAnimating();
                            holder.binding.continueVoicePlaybackReceive.setVisibility(View.GONE);
                            holder.binding.pauseVoicePlaybackReceive.setVisibility(View.VISIBLE);
                        } else {
                            holder.binding.voiceReceiveIcon.setVisibility(View.VISIBLE);
                            holder.binding.voiceReceivePlayingSwitchingImages.setVisibility(View.GONE);
                            holder.binding.voiceReceivePlayingSwitchingImages.stopAnimating();
                            holder.binding.continueVoicePlaybackReceive.setVisibility(View.GONE);
                            holder.binding.pauseVoicePlaybackReceive.setVisibility(View.GONE);
                        }
                    }else {
                        holder.binding.voiceReceiveIcon.setVisibility(View.VISIBLE);
                        holder.binding.voiceReceivePlayingSwitchingImages.setVisibility(View.GONE);
                        holder.binding.voiceReceivePlayingSwitchingImages.stopAnimating();
                        holder.binding.continueVoicePlaybackReceive.setVisibility(View.GONE);
                        holder.binding.pauseVoicePlaybackReceive.setVisibility(View.GONE);
                    }
                    if (itemData.chatMessage.isVoiceListened() == null || itemData.chatMessage.isVoiceListened()) {
                        holder.binding.voiceNotListenedBadge.setVisibility(View.GONE);
                    } else {
                        holder.binding.voiceNotListenedBadge.setVisibility(View.VISIBLE);
                    }
                    break;
                }
            }
        }
    }

    private void setupImageViewSize(@NonNull View imageView, Size size) {
        int imageWidth = size.getWidth();
        int imageHeight = size.getHeight();
        int viewWidth;
        int viewHeight;
        if(imageWidth / (double) imageHeight > Constants.CHAT_IMAGE_VIEW_MAX_WIDTH_DP / (double)Constants.CHAT_IMAGE_VIEW_MAX_HEIGHT_DP){
            viewWidth = UiUtil.dpToPx(activity, Constants.CHAT_IMAGE_VIEW_MAX_WIDTH_DP);
            viewHeight = (int) Math.round((viewWidth / (double) imageWidth) * imageHeight);
        }else {
            viewHeight = UiUtil.dpToPx(activity, Constants.CHAT_IMAGE_VIEW_MAX_HEIGHT_DP);
            viewWidth = (int) Math.round((viewHeight / (double) imageHeight) * imageWidth);
        }
        ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
        layoutParams.width = viewWidth;
        layoutParams.height = viewHeight;
    }

    private void setupYiers(@NonNull ViewHolder holder, int position) {
        ItemData currentItemData = itemDataList.get(position);
        scrollDisabler = new RecyclerViewScrollDisabler();
        recyclerView.addOnItemTouchListener(scrollDisabler);
        ChatMessageActionsPopupWindow popupWindow = new ChatMessageActionsPopupWindow(activity, currentItemData.chatMessage);
        popupWindow.setOnDeletedYier(() -> {
            itemDataList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, itemDataList.size() - position);
            determineAndUpdateShowTime();
            GlobalYiersHolder.getYiers(OpenedChatsUpdateYier.class).ifPresent(openedChatUpdateYiers -> {
                openedChatUpdateYiers.forEach(OpenedChatsUpdateYier::onOpenedChatsUpdate);
            });
        });
        View.OnLongClickListener onMessageReceiveLongClickYier = v -> {
            UiUtil.hideKeyboard(activity);
            scrollDisabler.setScrollingDisabled(true);
            popupWindow.show(holder.binding.layoutMessageReceive, false);
            return true;
        };
        View.OnLongClickListener onMessageSendLongClickYier = v -> {
            if(KeyboardVisibilityYier.isKeyboardVisible(activity)) {
                activity.setShowMessagePopupOnKeyboardClosed(() -> {
                    scrollDisabler.setScrollingDisabled(true);
                    popupWindow.show(holder.binding.layoutMessageSend, true);
                });
                UiUtil.hideKeyboard(activity);
            }else {
                scrollDisabler.setScrollingDisabled(true);
                popupWindow.show(holder.binding.layoutMessageSend, true);
            }
            return true;
        };
        holder.binding.layoutTextReceive.setOnLongClickListener(onMessageReceiveLongClickYier);
        holder.binding.layoutTextSend.setOnLongClickListener(onMessageSendLongClickYier);
        holder.binding.imageReceive.setOnLongClickListener(onMessageReceiveLongClickYier);
        holder.binding.imageSend.setOnLongClickListener(onMessageSendLongClickYier);
        holder.binding.layoutFileReceive.setOnLongClickListener(onMessageReceiveLongClickYier);
        holder.binding.layoutFileSend.setOnLongClickListener(onMessageSendLongClickYier);
        holder.binding.layoutVideoReceive.setOnLongClickListener(onMessageReceiveLongClickYier);
        holder.binding.layoutVideoSend.setOnLongClickListener(onMessageSendLongClickYier);
        holder.binding.layoutVoiceReceive.setOnLongClickListener(onMessageReceiveLongClickYier);
        holder.binding.layoutVoiceSend.setOnLongClickListener(onMessageSendLongClickYier);
        popupWindow.getPopupWindow().setOnDismissListener(() -> {
            scrollDisabler.setScrollingDisabled(false);
        });
        View.OnClickListener onMediaMessageClickYier = v -> {
            setupAndStartMediaActivity(currentItemData);
        };
        holder.binding.imageSend.setOnClickListener(onMediaMessageClickYier);
        holder.binding.imageReceive.setOnClickListener(onMediaMessageClickYier);
        holder.binding.layoutVideoSend.setOnClickListener(onMediaMessageClickYier);
        holder.binding.layoutVideoReceive.setOnClickListener(onMediaMessageClickYier);
        View.OnClickListener onFileMessageClickYier = v -> {
            Intent intent = new Intent(activity, ChatFileActivity.class);
            intent.putExtra(ExtraKeys.CHAT_MESSAGE, currentItemData.chatMessage);
            activity.startActivity(intent);
        };
        holder.binding.layoutFileSend.setOnClickListener(onFileMessageClickYier);
        holder.binding.layoutFileReceive.setOnClickListener(onFileMessageClickYier);
        View.OnClickListener onVoiceMessageClickYier = v -> {
            chatVoicePlayer.init(Uri.fromFile(new File(currentItemData.chatMessage.getVoiceFilePath())), currentItemData.chatMessage.getUuid());
            chatVoicePlayer.play();
        };
        holder.binding.layoutVoiceSend.setOnClickListener(onVoiceMessageClickYier);
        holder.binding.layoutVoiceReceive.setOnClickListener(onVoiceMessageClickYier);
        View.OnClickListener onVoicePause = v -> chatVoicePlayer.pause();
        View.OnClickListener onVoiceContinue = v -> chatVoicePlayer.play();
        holder.binding.pauseVoicePlaybackSend.setOnClickListener(onVoicePause);
        holder.binding.continueVoicePlaybackSend.setOnClickListener(onVoiceContinue);
        holder.binding.pauseVoicePlaybackReceive.setOnClickListener(onVoicePause);
        holder.binding.continueVoicePlaybackReceive.setOnClickListener(onVoiceContinue);
    }

    private void setupAndStartMediaActivity(ItemData currentItemData) {
        Intent intent = new Intent(activity, MediaActivity.class);
        ArrayList<ChatMessage> chatMessages = new ArrayList<>();
        itemDataList.forEach(itemData -> {
            ChatMessage chatMessage = itemData.chatMessage;
            if(chatMessage.getType() == ChatMessage.TYPE_IMAGE || chatMessage.getType() == ChatMessage.TYPE_VIDEO) {
                chatMessages.add(chatMessage);
                if (currentItemData.chatMessage.equals(chatMessage)) {
                    intent.putExtra(ExtraKeys.POSITION, chatMessages.size() - 1);
                }
            }
        });
        ArrayList<Media> mediaList = new ArrayList<>();
        chatMessages.forEach(chatMessage -> {
            if(chatMessage.getType() == ChatMessage.TYPE_IMAGE){
                MediaType mediaType = MediaType.IMAGE;
                Uri uri = Uri.fromFile(new File(chatMessage.getImageFilePath()));
                mediaList.add(new Media(mediaType, uri));
            }else if(chatMessage.getType() == ChatMessage.TYPE_VIDEO){
                MediaType mediaType = MediaType.VIDEO;
                Uri uri = Uri.fromFile(new File(chatMessage.getVideoFilePath()));
                mediaList.add(new Media(mediaType, uri));
            }
        });
        intent.putParcelableArrayListExtra(ExtraKeys.MEDIAS, mediaList);
        intent.putExtra(ExtraKeys.BUTTON_TEXT, "保存");
        MediaActivity.setActionButtonYier(v1 -> {
            int currentItem = MediaActivity.getInstance().getCurrentItemIndex();
            if(currentItem == -1) return;
            ChatMessage chatMessage = chatMessages.get(currentItem);
            switch (chatMessage.getType()){
                case ChatMessage.TYPE_IMAGE:{
                    new Thread(() -> {
                        OperatingDialog operatingDialog = new OperatingDialog(MediaActivity.getInstance());
                        operatingDialog.show();
                        try {
                            PublicFileAccessor.ChatMedia.saveImage(chatMessage);
                            operatingDialog.dismiss();
                            MessageDisplayer.autoShow(MediaActivity.getInstance(), "已保存", MessageDisplayer.Duration.SHORT);
                        }catch (IOException e){
                            ErrorLogger.log(e);
                            MessageDisplayer.autoShow(MediaActivity.getInstance(), "保存失败", MessageDisplayer.Duration.SHORT);
                        }
                    }).start();
                    break;
                }
                case ChatMessage.TYPE_VIDEO:{
                    new Thread(() -> {
                        OperatingDialog operatingDialog = new OperatingDialog(MediaActivity.getInstance());
                        operatingDialog.show();
                        try {
                            PublicFileAccessor.ChatMedia.saveVideo(chatMessage);
                            operatingDialog.dismiss();
                            MessageDisplayer.autoShow(MediaActivity.getInstance(), "已保存", MessageDisplayer.Duration.SHORT);
                        }catch (IOException e){
                            ErrorLogger.log(e);
                            MessageDisplayer.autoShow(MediaActivity.getInstance(), "保存失败", MessageDisplayer.Duration.SHORT);
                        }
                    }).start();
                }
            }
        });
        activity.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return itemDataList.size();
    }

    private void sort(List<ItemData> itemDatas) {
        itemDatas.sort(Comparator.comparing(o -> o.chatMessage.getTime()));
    }

    private void determineAndUpdateShowTime(){
        Date previousShowTime = null;
        for (int i = 0; i < itemDataList.size(); i++) {
            ItemData itemData = itemDataList.get(i);
            boolean showTime = Boolean.TRUE.equals(itemData.chatMessage.isShowTime());
            if(previousShowTime == null){
                previousShowTime = itemData.chatMessage.getTime();
                itemData.chatMessage.setShowTime(true);
                if(!showTime) notifyItemChanged(i);
            }else {
                if(TimeUtil.isDateAfter(previousShowTime, itemData.chatMessage.getTime(), Constants.CHAT_MESSAGE_SHOW_TIME_INTERVAL)){
                    previousShowTime = itemData.chatMessage.getTime();
                    itemData.chatMessage.setShowTime(true);
                    if(!showTime) notifyItemChanged(i);
                }else {
                    itemData.chatMessage.setShowTime(false);
                    if(showTime) notifyItemChanged(i);
                }
            }
        }
    }

    public synchronized void addItemAndShow(ChatMessage chatMessage){
        if(itemDataList.contains(new ItemData(chatMessage))) return;
        ItemData itemData = new ItemData(chatMessage);
        if(itemDataList.isEmpty()){
            itemDataList.add(itemData);
            notifyItemInserted(0);
        }else {
            for (int index = itemDataList.size() - 1; index >= 0; index--) {
                ItemData data = itemDataList.get(index);
                if (data.chatMessage.getTime().getTime() < itemData.chatMessage.getTime().getTime()) {
                    itemDataList.add(index + 1, itemData);
                    notifyItemInserted(index + 1);
                    break;
                }
            }
        }
        determineAndUpdateShowTime();
        recyclerView.scrollToEnd(true);
    }

    public synchronized void addAllToStartAndShow(List<ChatMessage> chatMessages){
        List<ItemData> itemDatas = new ArrayList<>();
        chatMessages.forEach(chatMessage -> {
            itemDatas.add(new ItemData(chatMessage));
        });
        sort(itemDatas);
        itemDataList.addAll(0, itemDatas);
        notifyItemRangeInserted(0, itemDatas.size());
        determineAndUpdateShowTime();
    }

    public void clearAndShow(){
        int size = itemDataList.size();
        itemDataList.clear();
        notifyItemRangeRemoved(0, size);
    }

    public void onActivityDestroy(){
        ChatVoicePlayer.State state = chatVoicePlayer.release();
        if(state != null) SharedPreferencesAccessor.ChatPref.saveChatVoicePlayerState(activity, state);
    }

}
