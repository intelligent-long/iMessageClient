package com.longx.intelligent.android.ichat2.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;

import androidx.core.widget.NestedScrollView;

import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.activity.ExtraKeys;
import com.longx.intelligent.android.ichat2.activity.SearchChatMessageResultItemsActivity;
import com.longx.intelligent.android.ichat2.da.database.manager.ChannelDatabaseManager;
import com.longx.intelligent.android.ichat2.data.Channel;
import com.longx.intelligent.android.ichat2.data.ChatMessage;
import com.longx.intelligent.android.ichat2.databinding.LinearLayoutViewsSearchChatMessageBinding;
import com.longx.intelligent.android.ichat2.net.dataurl.NetDataUrls;
import com.longx.intelligent.android.ichat2.procedure.GlideBehaviours;
import com.longx.intelligent.android.ichat2.ui.LinearLayoutViews;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LONG on 2024/10/22 at 上午10:58.
 */
public class SearchChatMessageLinearLayoutViews extends LinearLayoutViews<List<ChatMessage>> {
    public SearchChatMessageLinearLayoutViews(Activity activity, LinearLayout linearLayout, NestedScrollView nestedScrollView, View... parentViews) {
        super(activity, linearLayout, nestedScrollView, parentViews);
    }

    @Override
    public View getView(List<ChatMessage> searchedData, Activity activity) {
        LinearLayoutViewsSearchChatMessageBinding binding = LinearLayoutViewsSearchChatMessageBinding.inflate(activity.getLayoutInflater());
        Channel channel = ChannelDatabaseManager.getInstance().findOneChannel(searchedData.get(0).getOther(activity));
        if(channel != null) {
            String avatarHash = channel.getAvatar() == null ? null : channel.getAvatar().getHash();
            if (avatarHash == null) {
                GlideBehaviours.loadToImageView(activity.getApplicationContext(), R.drawable.default_avatar, binding.avatar);
            } else {
                GlideBehaviours.loadToImageView(activity.getApplicationContext(), NetDataUrls.getAvatarUrl(activity, avatarHash), binding.avatar);
            }
            binding.name.setText(channel.getName());
        }
        binding.searchedChatMessageCountText.setText(searchedData.size() + " 条相关消息记录");
        setupYiers(binding, (ArrayList<ChatMessage>) searchedData, activity);
        return binding.getRoot();
    }

    private void setupYiers(LinearLayoutViewsSearchChatMessageBinding binding, ArrayList<ChatMessage> searchedData, Activity activity) {
        binding.getRoot().setOnClickListener(v -> {
            Intent intent = new Intent(activity, SearchChatMessageResultItemsActivity.class);
            intent.putParcelableArrayListExtra(ExtraKeys.CHAT_MESSAGES, searchedData);
            activity.startActivity(intent);
        });
    }
}
