package com.longx.intelligent.android.imessage.adapter;

import android.app.Activity;
import android.view.View;
import android.widget.LinearLayout;

import androidx.core.widget.NestedScrollView;

import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.imessage.data.Channel;
import com.longx.intelligent.android.imessage.databinding.LinearLayoutViewsExcludeBroadcastChannelBinding;
import com.longx.intelligent.android.imessage.net.dataurl.NetDataUrls;
import com.longx.intelligent.android.imessage.behaviorcomponents.GlideBehaviours;
import com.longx.intelligent.android.imessage.ui.LinearLayoutViews;
import com.longx.intelligent.android.imessage.util.PinyinUtil;

import java.util.Set;

/**
 * Created by LONG on 2024/10/16 at 上午5:18.
 */
public class ExcludeBroadcastChannelLinearLayoutViews extends LinearLayoutViews<ExcludeBroadcastChannelLinearLayoutViews.ItemData> {
    private Set<String> excludeBroadcastChannelIds;

    public ExcludeBroadcastChannelLinearLayoutViews(Activity activity, LinearLayout linearLayout, NestedScrollView nestedScrollView, Set<String> excludeBroadcastChannelIds) {
        super(activity, linearLayout, nestedScrollView);
        this.excludeBroadcastChannelIds = excludeBroadcastChannelIds;
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
        LinearLayoutViewsExcludeBroadcastChannelBinding binding = LinearLayoutViewsExcludeBroadcastChannelBinding.inflate(activity.getLayoutInflater());
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
        if(excludeBroadcastChannelIds.contains(itemData.channel.getImessageId())){
            binding.checkBox.setChecked(true);
        }
        setupYiers(binding, itemData, activity);
        return binding.getRoot();
    }

    private void setupYiers(LinearLayoutViewsExcludeBroadcastChannelBinding binding, ItemData itemData, Activity activity) {
        binding.clickView.setOnClickListener(v -> {
            binding.checkBox.setChecked(!binding.checkBox.isChecked());
        });
        binding.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                excludeBroadcastChannelIds.add(itemData.channel.getImessageId());
            }else {
                excludeBroadcastChannelIds.remove(itemData.channel.getImessageId());
            }
        });
    }

    public Set<String> getExcludeBroadcastChannelIds() {
        return excludeBroadcastChannelIds;
    }
}
