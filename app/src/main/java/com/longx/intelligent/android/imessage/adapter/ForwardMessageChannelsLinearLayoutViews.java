package com.longx.intelligent.android.imessage.adapter;

import android.app.Activity;
import android.view.View;
import android.widget.LinearLayout;

import androidx.core.widget.NestedScrollView;

import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.imessage.activity.ForwardMessageActivity;
import com.longx.intelligent.android.imessage.data.Channel;
import com.longx.intelligent.android.imessage.databinding.LinearLayoutViewsForwardMessageChannelBinding;
import com.longx.intelligent.android.imessage.dialog.FastLocateDialog;
import com.longx.intelligent.android.imessage.net.dataurl.NetDataUrls;
import com.longx.intelligent.android.imessage.ui.LinearLayoutViews;
import com.longx.intelligent.android.imessage.ui.glide.GlideApp;
import com.longx.intelligent.android.imessage.util.PinyinUtil;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by LONG on 2024/10/19 at 上午9:56.
 */
public class ForwardMessageChannelsLinearLayoutViews extends LinearLayoutViews<ForwardMessageChannelsLinearLayoutViews.ItemData> {
    private final Set<String> checkedChannelIds = new HashSet<>();

    public ForwardMessageChannelsLinearLayoutViews(Activity activity, LinearLayout linearLayout, NestedScrollView nestedScrollView) {
        super(activity, linearLayout, nestedScrollView);
    }

    public static class ItemData{
        private Character indexChar;
        private final Channel channel;

        public ItemData(Channel channel) {
            indexChar = PinyinUtil.getPinyin(channel.autoGetName()).toUpperCase().charAt(0);
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
        if(avatarHash == null){
            GlideApp
                    .with(activity.getApplicationContext())
                    .load(R.drawable.default_avatar)
                    .into(binding.avatar);
        }else {
            GlideApp
                    .with(activity.getApplicationContext())
                    .load(NetDataUrls.getAvatarUrl(activity, avatarHash))
                    .into(binding.avatar);
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
        binding.name.setText(itemData.channel.autoGetName());
        setupYiers(binding, itemData, activity);
        return binding.getRoot();
    }

    private void setupYiers(LinearLayoutViewsForwardMessageChannelBinding binding, ItemData itemData, Activity activity) {
        binding.indexBar.setOnClickListener(v -> {
            FastLocateDialog fastLocateDialog = new FastLocateDialog(activity, FastLocateDialog.LOCATE_HEADER_CHANNEL, getExistTexts());
            fastLocateDialog.setLocateYier((positionSelect, textSelect) -> {
                if(textSelect.equals(".")){
                    ((ForwardMessageActivity) activity).getBinding().appbar.setExpanded(true);
                    getNestedScrollView().smoothScrollTo(0, 0);
                }else {
                    int locatePosition = -1;
                    for (int i = 0; i < getAllItems().size(); i++) {
                        ItemData data = getAllItems().get(i);
                        if (String.valueOf(data.indexChar).equals(textSelect)) {
                            locatePosition = i;
                            break;
                        }
                    }
                    if (locatePosition != -1) {
                        ((ForwardMessageActivity) activity).getBinding().appbar.setExpanded(false);
                        scrollTo(locatePosition, true);
                    }
                }
                fastLocateDialog.dismiss();
            });
            fastLocateDialog.create().show();
        });
        binding.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                checkedChannelIds.add(itemData.channel.getImessageId());
            }else {
                checkedChannelIds.remove(itemData.channel.getImessageId());
            }
        });
    }

    private String[] getExistTexts(){
        String[] result = new String[getAllItems().size() + 1];
        for (int i = 0; i < getAllItems().size(); i++) {
            result[i] = String.valueOf(getAllItems().get(i).indexChar);
        }
        result[result.length - 1] = ".";
        return result;
    }

    public Set<String> getCheckedChannelIds() {
        return checkedChannelIds;
    }
}
