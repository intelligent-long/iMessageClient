package com.longx.intelligent.android.ichat2.adapter;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.activity.ChannelActivity;
import com.longx.intelligent.android.ichat2.activity.ChatImageActivity;
import com.longx.intelligent.android.ichat2.activity.ExtraKeys;
import com.longx.intelligent.android.ichat2.behavior.GlideBehaviours;
import com.longx.intelligent.android.ichat2.da.database.manager.ChannelDatabaseManager;
import com.longx.intelligent.android.ichat2.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.ichat2.data.Channel;
import com.longx.intelligent.android.ichat2.data.ChatMessage;
import com.longx.intelligent.android.ichat2.data.Self;
import com.longx.intelligent.android.ichat2.databinding.RecyclerItemChatMessageBinding;
import com.longx.intelligent.android.ichat2.net.dataurl.NetDataUrls;
import com.longx.intelligent.android.ichat2.popupwindow.ChatMessageActionsPopupWindow;
import com.longx.intelligent.android.ichat2.ui.RecyclerViewScrollDisabler;
import com.longx.intelligent.android.ichat2.ui.glide.GlideApp;
import com.longx.intelligent.android.ichat2.util.ErrorLogger;
import com.longx.intelligent.android.ichat2.util.TimeUtil;
import com.longx.intelligent.android.ichat2.util.UiUtil;
import com.longx.intelligent.android.ichat2.value.Constants;
import com.longx.intelligent.android.lib.recyclerview.WrappableRecyclerViewAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Created by LONG on 2024/5/15 at 1:11 PM.
 */
public class ChatMessagesRecyclerAdapter extends WrappableRecyclerViewAdapter<ChatMessagesRecyclerAdapter.ViewHolder, ChatMessagesRecyclerAdapter.ItemData> {
    private final AppCompatActivity activity;
    private final com.longx.intelligent.android.lib.recyclerview.RecyclerView recyclerView;
    private final List<ChatMessagesRecyclerAdapter.ItemData> itemDataList = new ArrayList<>();
    private final RequestOptions requestOptions;

    public ChatMessagesRecyclerAdapter(AppCompatActivity activity, com.longx.intelligent.android.lib.recyclerview.RecyclerView recyclerView) {
        this.activity = activity;
        this.recyclerView = recyclerView;
        requestOptions = new RequestOptions()
                .transform(new RoundedCorners(UiUtil.dpToPx(activity, 7)))
                .diskCacheStrategy(DiskCacheStrategy.ALL);
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
        if(itemData.chatMessage.isShowTime()) {
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
            //头像
            holder.binding.avatarSend.setOnClickListener(v -> {
                Intent intent = new Intent(activity, ChannelActivity.class);
                intent.putExtra(ExtraKeys.ICHAT_ID, itemData.chatMessage.getFrom());
                activity.startActivity(intent);
            });
            Self currentUserInfo = SharedPreferencesAccessor.UserInfoPref.getCurrentUserInfo(activity);
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
                    holder.binding.textSend.setText(itemData.chatMessage.getText());
                    break;
                }
                case ChatMessage.TYPE_IMAGE:{
                    holder.binding.layoutTextSend.setVisibility(View.GONE);
                    holder.binding.imageSend.setVisibility(View.VISIBLE);
                    setupImageViewSize(holder.binding.imageSend, itemData);
                    String imageFilePath = itemData.chatMessage.getImageFilePath();
                    GlideApp.with(activity.getApplicationContext())
                            .load(new File(imageFilePath))
                            .apply(requestOptions)
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .into(holder.binding.imageSend);
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
                    holder.binding.textReceive.setText(itemData.chatMessage.getText());
                    break;
                }
                case ChatMessage.TYPE_IMAGE:{
                    holder.binding.layoutTextReceive.setVisibility(View.GONE);
                    holder.binding.imageReceive.setVisibility(View.VISIBLE);
                    setupImageViewSize(holder.binding.imageReceive, itemData);
                    String imageFilePath = itemData.chatMessage.getImageFilePath();
                    GlideApp.with(activity.getApplicationContext())
                            .load(new File(imageFilePath))
                            .apply(requestOptions)
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .into(holder.binding.imageReceive);
                    break;
                }
            }
        }
    }

    private void setupImageViewSize(@NonNull View imageView, ItemData itemData) {
        int imageWidth = itemData.chatMessage.getImageSize().getWidth();
        int imageHeight = itemData.chatMessage.getImageSize().getHeight();
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
        View.OnLongClickListener onMessageReceiveLongClickYier = v -> {
            scrollDisabler.setScrollingDisabled(true);
            popupWindow.show(holder.binding.layoutMessageReceive, false);
            return true;
        };
        View.OnLongClickListener onMessageSendLongClickYier = v -> {
            scrollDisabler.setScrollingDisabled(true);
            popupWindow.show(holder.binding.layoutMessageSend, true);
            return true;
        };
        holder.binding.layoutTextReceive.setOnLongClickListener(onMessageReceiveLongClickYier);
        holder.binding.layoutTextSend.setOnLongClickListener(onMessageSendLongClickYier);
        holder.binding.imageReceive.setOnLongClickListener(onMessageReceiveLongClickYier);
        holder.binding.imageSend.setOnLongClickListener(onMessageSendLongClickYier);
        popupWindow.getPopupWindow().setOnDismissListener(() -> {
            scrollDisabler.setScrollingDisabled(false);
        });
        View.OnClickListener onImageClickYier = v -> {
            Intent intent = new Intent(activity, ChatImageActivity.class);
            ArrayList<String> imageFilePaths = new ArrayList<>();
            itemDataList.forEach(itemData -> {
                ChatMessage chatMessage = itemData.chatMessage;
                if(chatMessage.getType() == ChatMessage.TYPE_IMAGE) {
                    imageFilePaths.add(chatMessage.getImageFilePath());
                    if (currentItemData.chatMessage.equals(chatMessage)) {
                        intent.putExtra(ExtraKeys.POSITION, imageFilePaths.size() - 1);
                    }
                }
            });
            intent.putStringArrayListExtra(ExtraKeys.FILE_PATHS, imageFilePaths);
            activity.startActivity(intent);
        };
        holder.binding.imageSend.setOnClickListener(onImageClickYier);
        holder.binding.imageReceive.setOnClickListener(onImageClickYier);
    }

    @Override
    public int getItemCount() {
        return itemDataList.size();
    }

    private static void sort(List<ItemData> itemDataList) {
        itemDataList.sort(Comparator.comparing(o -> o.chatMessage.getTime()));
    }

    public synchronized void addItemToEndAndShow(ChatMessage chatMessage){
        if(itemDataList.contains(new ItemData(chatMessage))) return;
        ItemData itemData = new ItemData(chatMessage);
        itemDataList.add(itemData);
        notifyItemInserted(getItemCount() - 1);
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
    }

    public void clearAndShow(){
        int size = itemDataList.size();
        itemDataList.clear();
        notifyItemRangeRemoved(0, size);
    }

}
