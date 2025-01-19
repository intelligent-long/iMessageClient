package com.longx.intelligent.android.imessage.adapter;

import android.content.Intent;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.imessage.activity.ChatActivity;
import com.longx.intelligent.android.imessage.activity.ExtraKeys;
import com.longx.intelligent.android.imessage.da.database.manager.ChannelDatabaseManager;
import com.longx.intelligent.android.imessage.data.Channel;
import com.longx.intelligent.android.imessage.data.ChatMessage;
import com.longx.intelligent.android.imessage.databinding.RecyclerItemSearchChatMessageResultItemBinding;
import com.longx.intelligent.android.imessage.net.dataurl.NetDataUrls;
import com.longx.intelligent.android.imessage.behaviorcomponents.GlideBehaviours;
import com.longx.intelligent.android.imessage.util.TimeUtil;
import com.longx.intelligent.android.lib.recyclerview.WrappableRecyclerViewAdapter;

import java.util.List;

/**
 * Created by LONG on 2024/10/22 at 下午12:06.
 */
public class SearchChatMessageResultItemsRecyclerAdapter extends WrappableRecyclerViewAdapter<SearchChatMessageResultItemsRecyclerAdapter.ViewHolder, SearchChatMessageResultItemsRecyclerAdapter.ItemData> {
    private final AppCompatActivity activity;
    private final List<ItemData> itemDataList;

    public SearchChatMessageResultItemsRecyclerAdapter(AppCompatActivity activity, List<ItemData> itemDataList) {
        this.activity = activity;
        this.itemDataList = itemDataList;
    }

    public static class ItemData{
        private final ChatMessage chatMessage;

        public ItemData(ChatMessage chatMessage) {
            this.chatMessage = chatMessage;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final RecyclerItemSearchChatMessageResultItemBinding binding;

        public ViewHolder(RecyclerItemSearchChatMessageResultItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerItemSearchChatMessageResultItemBinding binding = RecyclerItemSearchChatMessageResultItemBinding.inflate(activity.getLayoutInflater());
        return new ViewHolder(binding);
    }

    @Override
    public int getItemCount() {
        return itemDataList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatMessage chatMessage = itemDataList.get(position).chatMessage;
        Channel channel = ChannelDatabaseManager.getInstance().findOneChannel(chatMessage.getOther(activity));
        if(channel != null) {
            String avatarHash = channel.getAvatar() == null ? null : channel.getAvatar().getHash();
            if (avatarHash == null) {
                GlideBehaviours.loadToImageView(activity.getApplicationContext(), R.drawable.default_avatar, holder.binding.avatar);
            } else {
                GlideBehaviours.loadToImageView(activity.getApplicationContext(), NetDataUrls.getAvatarUrl(activity, avatarHash), holder.binding.avatar);
            }
            holder.binding.name.setText(channel.getName());
        }
        switch (chatMessage.getType()){
            case ChatMessage.TYPE_TEXT:
                String newestChatMessageText = chatMessage.getText();
                if(newestChatMessageText != null) {
                    if (newestChatMessageText.length() > 25) {
                        newestChatMessageText = newestChatMessageText.substring(0, 25) + "...";
                    }
                }
                holder.binding.chatMessage.setText(newestChatMessageText);
                break;
            case ChatMessage.TYPE_IMAGE:
                holder.binding.chatMessage.setText("[图片]");
                break;
            case ChatMessage.TYPE_FILE:
                holder.binding.chatMessage.setText("[文件]");
                break;
            case ChatMessage.TYPE_VIDEO:
                holder.binding.chatMessage.setText("[视频]");
                break;
            case ChatMessage.TYPE_VOICE:
                holder.binding.chatMessage.setText("[语音]");
                break;
            case ChatMessage.TYPE_UNSEND:
                holder.binding.chatMessage.setText("[撤回]");
                break;
        }
        holder.binding.time.setText(TimeUtil.formatRelativeTime(chatMessage.getTime()));
        setupYiers(holder, position, channel);
    }

    private void setupYiers(ViewHolder holder, int position, Channel channel) {
        ChatMessage chatMessage = itemDataList.get(position).chatMessage;
        holder.binding.getRoot().setOnClickListener(v -> {
            Intent intent = new Intent(activity, ChatActivity.class);
            intent.putExtra(ExtraKeys.CHANNEL, channel);
            intent.putExtra(ExtraKeys.CHAT_MESSAGE, chatMessage);
            activity.startActivity(intent);
        });
    }
}
