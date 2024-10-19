package com.longx.intelligent.android.ichat2.adapter;

import android.app.Activity;
import android.view.View;
import android.widget.LinearLayout;

import androidx.core.widget.NestedScrollView;

import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.data.Channel;
import com.longx.intelligent.android.ichat2.databinding.LinearLayoutViewsForwardMessageChannelBinding;
import com.longx.intelligent.android.ichat2.net.dataurl.NetDataUrls;
import com.longx.intelligent.android.ichat2.procedure.GlideBehaviours;
import com.longx.intelligent.android.ichat2.ui.LinearLayoutViews;
import com.longx.intelligent.android.ichat2.util.PinyinUtil;

/**
 * Created by LONG on 2024/10/19 at 上午9:56.
 */
public class ForwardMessageChannelsLinearLayoutViews extends LinearLayoutViews<ForwardMessageChannelsLinearLayoutViews.ItemData> {
    public ForwardMessageChannelsLinearLayoutViews(Activity activity, LinearLayout linearLayout, NestedScrollView nestedScrollView) {
        super(activity, linearLayout, nestedScrollView);
    }

    public static class ItemData{
        private Character indexChar;
        private final Channel channel;

        public ItemData(Channel channel) {
            indexChar = PinyinUtil.getPinyin(channel.getName()).toUpperCase().charAt(0);
            if(!((indexChar >= 65 && indexChar <= 90) || (indexChar >= 97 && indexChar <= 122))){
                indexChar = '#';
            }
            this.channel = channel;
        }

        public Character getIndexChar() {
            return indexChar;
        }

        public Channel getChannel() {
            return channel;
        }
    }

    @Override
    public View getView(ItemData itemData, Activity activity) {
        LinearLayoutViewsForwardMessageChannelBinding binding = LinearLayoutViewsForwardMessageChannelBinding.inflate(activity.getLayoutInflater());
        String avatarHash = itemData.channel.getAvatar() == null ? null : itemData.channel.getAvatar().getHash();
        if (avatarHash == null) {
            GlideBehaviours.loadToImageView(activity.getApplicationContext(), R.drawable.default_avatar, binding.avatar);
        } else {
            GlideBehaviours.loadToImageView(activity.getApplicationContext(), NetDataUrls.getAvatarUrl(activity, avatarHash), binding.avatar);
        }
        binding.indexBar.setText(String.valueOf(itemData.indexChar));
        int position = getAllItems().indexOf(itemData);
        int previousPosition = position - 1;
        if(position == 0){
            binding.indexBar.setVisibility(View.VISIBLE);
        } else {
            ItemData previousItemData = getAllItems().get(previousPosition);
            if (previousItemData.indexChar.equals(itemData.indexChar)) {
                binding.indexBar.setVisibility(View.GONE);
            } else {
                binding.indexBar.setVisibility(View.VISIBLE);
            }
        }
        binding.name.setText(itemData.channel.getName());
        return binding.getRoot();
    }
}
