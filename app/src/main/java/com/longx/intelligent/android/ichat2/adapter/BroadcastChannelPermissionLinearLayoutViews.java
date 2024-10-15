package com.longx.intelligent.android.ichat2.adapter;

import android.app.Activity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.core.widget.NestedScrollView;

import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.activity.BroadcastChannelPermissionActivity;
import com.longx.intelligent.android.ichat2.data.Channel;
import com.longx.intelligent.android.ichat2.databinding.LinearLayoutViewsBroadcastChannelPermissionBinding;
import com.longx.intelligent.android.ichat2.dialog.FastLocateDialog;
import com.longx.intelligent.android.ichat2.net.dataurl.NetDataUrls;
import com.longx.intelligent.android.ichat2.procedure.GlideBehaviours;
import com.longx.intelligent.android.ichat2.ui.LinearLayoutViews;
import com.longx.intelligent.android.ichat2.util.PinyinUtil;

/**
 * Created by LONG on 2024/10/14 at 上午2:22.
 */
public class BroadcastChannelPermissionLinearLayoutViews extends LinearLayoutViews<BroadcastChannelPermissionLinearLayoutViews.ItemData> {
    public BroadcastChannelPermissionLinearLayoutViews(BroadcastChannelPermissionActivity activity, LinearLayout linearLayout, NestedScrollView nestedScrollView) {
        super(activity, linearLayout, nestedScrollView);
    }

    public BroadcastChannelPermissionLinearLayoutViews(BroadcastChannelPermissionActivity activity, LinearLayout linearLayout, ScrollView scrollView) {
        super(activity, linearLayout, scrollView);
    }

    public static class ItemData{
        private Character indexChar;
        private Channel channel;

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
        LinearLayoutViewsBroadcastChannelPermissionBinding binding = LinearLayoutViewsBroadcastChannelPermissionBinding.inflate(activity.getLayoutInflater());
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
        setupYiers(binding, itemData, activity);
        return binding.getRoot();
    }

    private void setupYiers(LinearLayoutViewsBroadcastChannelPermissionBinding binding, ItemData itemData, Activity activity) {
        binding.indexBar.setOnClickListener(v -> {
            FastLocateDialog fastLocateDialog = new FastLocateDialog(activity, FastLocateDialog.LOCATE_CHANNEL, getExistTexts());
            fastLocateDialog.setLocateYier((positionSelect, textSelect) -> {
                int locatePosition = -1;
                for (int i = 0; i < getAllItems().size(); i++) {
                    ItemData data = getAllItems().get(i);
                    if (String.valueOf(data.indexChar).equals(textSelect)) {
                        locatePosition = i;
                        break;
                    }
                }
                if(locatePosition != -1) {
                    ((BroadcastChannelPermissionActivity)activity).getBinding().appbar.setExpanded(false);
                    scrollTo(locatePosition, true);
                }
                fastLocateDialog.dismiss();
            });
            fastLocateDialog.forShow();
        });
    }

    private String[] getExistTexts(){
        String[] result = new String[getAllItems().size()];
        for (int i = 0; i < getAllItems().size(); i++) {
            result[i] = String.valueOf(getAllItems().get(i).indexChar);
        }
        return result;
    }
}
