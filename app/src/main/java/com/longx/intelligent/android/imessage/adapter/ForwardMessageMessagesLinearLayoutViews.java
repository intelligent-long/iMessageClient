package com.longx.intelligent.android.imessage.adapter;

import android.app.Activity;
import android.view.View;
import android.widget.LinearLayout;

import androidx.core.widget.NestedScrollView;

import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.imessage.data.Channel;
import com.longx.intelligent.android.imessage.data.ChatMessage;
import com.longx.intelligent.android.imessage.data.OpenedChat;
import com.longx.intelligent.android.imessage.databinding.LinearLayoutViewsForwardMessageMessageBinding;
import com.longx.intelligent.android.imessage.net.dataurl.NetDataUrls;
import com.longx.intelligent.android.imessage.behaviorcomponents.GlideBehaviours;
import com.longx.intelligent.android.imessage.ui.LinearLayoutViews;
import com.longx.intelligent.android.imessage.util.TimeUtil;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by LONG on 2024/10/19 at 上午9:56.
 */
public class ForwardMessageMessagesLinearLayoutViews extends LinearLayoutViews<OpenedChat> {
    private final Set<String> checkedChannelIds = new HashSet<>();

    public ForwardMessageMessagesLinearLayoutViews(Activity activity, LinearLayout linearLayout, NestedScrollView nestedScrollView) {
        super(activity, linearLayout, nestedScrollView);
    }

    @Override
    public View getView(OpenedChat openedChat, Activity activity) {
        LinearLayoutViewsForwardMessageMessageBinding binding = LinearLayoutViewsForwardMessageMessageBinding.inflate(activity.getLayoutInflater());
        ChatMessage newestChatMessage = openedChat.getNewestChatMessage();
        if(newestChatMessage == null){
            return binding.getRoot();
        }
        Channel channel = openedChat.getChannel();
        if(channel != null) {
            String avatarHash = channel.getAvatar() == null ? null : channel.getAvatar().getHash();
            if (avatarHash == null) {
                GlideBehaviours.loadToImageView(activity.getApplicationContext(), R.drawable.default_avatar, binding.avatar);
            } else {
                GlideBehaviours.loadToImageView(activity.getApplicationContext(), NetDataUrls.getAvatarUrl(activity, avatarHash), binding.avatar);
            }
            binding.name.setText(channel.getNote() == null ? channel.getUsername() : channel.getNote());
        }
        switch (newestChatMessage.getType()){
            case ChatMessage.TYPE_TEXT:
                String newestChatMessageText = newestChatMessage.getText();
                if(newestChatMessageText != null) {
                    if (newestChatMessageText.length() > 25) {
                        newestChatMessageText = newestChatMessageText.substring(0, 25) + "...";
                    }
                }
                binding.newestChatMessage.setText(newestChatMessageText);
                break;
            case ChatMessage.TYPE_IMAGE:
                binding.newestChatMessage.setText("[图片]");
                break;
            case ChatMessage.TYPE_FILE:
                binding.newestChatMessage.setText("[文件]");
                break;
            case ChatMessage.TYPE_VIDEO:
                binding.newestChatMessage.setText("[视频]");
                break;
            case ChatMessage.TYPE_VOICE:
                binding.newestChatMessage.setText("[语音]");
                break;
        }
        binding.time.setText(TimeUtil.formatSimpleRelativeTime(newestChatMessage.getTime()));
        setupYiers(binding, openedChat, activity);
        return binding.getRoot();
    }

    private void setupYiers(LinearLayoutViewsForwardMessageMessageBinding binding, OpenedChat openedChat, Activity activity) {
        binding.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                checkedChannelIds.add(openedChat.getChannelIchatId());
            }else {
                checkedChannelIds.remove(openedChat.getChannelIchatId());
            }
        });
    }

    public Set<String> getCheckedChannelIds() {
        return checkedChannelIds;
    }
}
