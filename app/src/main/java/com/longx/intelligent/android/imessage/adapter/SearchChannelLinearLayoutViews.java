package com.longx.intelligent.android.imessage.adapter;

import android.app.Activity;
import android.content.Intent;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.LinearLayout;

import androidx.core.widget.NestedScrollView;

import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.imessage.activity.ChannelActivity;
import com.longx.intelligent.android.imessage.activity.ExtraKeys;
import com.longx.intelligent.android.imessage.activity.SearchChannelActivity;
import com.longx.intelligent.android.imessage.data.Channel;
import com.longx.intelligent.android.imessage.databinding.LinearLayoutViewsSearchChannelBinding;
import com.longx.intelligent.android.imessage.dialog.FastLocateDialog;
import com.longx.intelligent.android.imessage.net.dataurl.NetDataUrls;
import com.longx.intelligent.android.imessage.ui.LinearLayoutViews;
import com.longx.intelligent.android.imessage.ui.glide.GlideApp;
import com.longx.intelligent.android.imessage.util.ColorUtil;
import com.longx.intelligent.android.imessage.util.PinyinUtil;
import com.longx.intelligent.android.imessage.util.Utils;
import com.longx.intelligent.android.imessage.value.Constants;

/**
 * Created by LONG on 2024/10/22 at 上午6:26.
 */
public class SearchChannelLinearLayoutViews extends LinearLayoutViews<SearchChannelLinearLayoutViews.ItemData> {
    public SearchChannelLinearLayoutViews(SearchChannelActivity activity, LinearLayout linearLayout, NestedScrollView nestedScrollView) {
        super(activity, linearLayout, nestedScrollView);
    }

    public static class ItemData{
        private Character indexChar;
        private Channel channel;

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
    public SearchChannelActivity getActivity() {
        return (SearchChannelActivity) super.getActivity();
    }

    @Override
    public View getView(ItemData itemData, Activity activity) {
        LinearLayoutViewsSearchChannelBinding binding = LinearLayoutViewsSearchChannelBinding.inflate(activity.getLayoutInflater());
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
        showMatchingIn(binding, itemData, activity);
        setupYiers(binding, itemData, activity);
        return binding.getRoot();
    }

    private void showMatchingIn(LinearLayoutViewsSearchChannelBinding binding, ItemData itemData, Activity activity) {
        String searchStr = getActivity().getSearchStr();
        SpannableStringBuilder matchingInText = new SpannableStringBuilder();
        if(itemData.channel.getImessageIdUser() != null && Utils.containsIgnoreCase(itemData.channel.getImessageIdUser(), searchStr)){
            appendMatchingText(matchingInText, Constants.APP_NAME + " ID  ", itemData.channel.getImessageIdUser(), searchStr);
        }
        if(itemData.channel.getUsername() != null && Utils.containsIgnoreCase(itemData.channel.getUsername(), searchStr)){
            appendMatchingText(matchingInText, "用户名  ", itemData.channel.getUsername(), searchStr);
        }
        if(itemData.channel.getNote() != null && Utils.containsIgnoreCase(itemData.channel.getNote(), searchStr)){
            appendMatchingText(matchingInText, "备注  ", itemData.channel.getNote(), searchStr);
        }
        if(itemData.channel.getEmail() != null && Utils.containsIgnoreCase(itemData.channel.getEmail(), searchStr)){
            appendMatchingText(matchingInText, "邮箱  ", itemData.channel.getEmail(), searchStr);
        }
        if ((itemData.channel.getFirstRegion() != null && Utils.containsIgnoreCase(itemData.channel.getFirstRegion().getName(), searchStr)) ||
                (itemData.channel.getSecondRegion() != null && Utils.containsIgnoreCase(itemData.channel.getSecondRegion().getName(), searchStr)) ||
                (itemData.channel.getThirdRegion() != null && Utils.containsIgnoreCase(itemData.channel.getThirdRegion().getName(), searchStr))) {
            appendMatchingText(matchingInText, "地区  ", itemData.channel.buildRegionDesc(), searchStr);
        }
        binding.matchingIn.setText(matchingInText);
    }

    private void appendMatchingText(SpannableStringBuilder builder, String prefix, String text, String searchStr) {
        if (builder.length() != 0) builder.append("\r\n");
        int start = text.toLowerCase().indexOf(searchStr.toLowerCase()) + prefix.length();
        int end = start + searchStr.length();
        SpannableString spannableString = new SpannableString(prefix + text);
        if (start >= 0) {
            spannableString.setSpan(new ForegroundColorSpan(ColorUtil.getColor(getActivity(), R.color.imessage)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        builder.append(spannableString);
    }

    private void setupYiers(LinearLayoutViewsSearchChannelBinding binding, ItemData itemData, Activity activity){
        binding.indexBar.setOnClickListener(v -> {
            FastLocateDialog fastLocateDialog = new FastLocateDialog(activity, FastLocateDialog.LOCATE_HEADER_CHANNEL, getExistTexts());
            fastLocateDialog.setLocateYier((positionSelect, textSelect) -> {
                if(textSelect.equals(".")){
                    getActivity().getBinding().appbar.setExpanded(true);
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
                        getActivity().getBinding().appbar.setExpanded(false);
                        scrollTo(locatePosition, true);
                    }
                }
                fastLocateDialog.dismiss();
            });
            fastLocateDialog.create().show();
        });
        binding.clickView.setOnClickListener(v -> {
            Intent intent = new Intent(activity, ChannelActivity.class);
            intent.putExtra(ExtraKeys.IMESSAGE_ID, itemData.getChannel().getImessageId());
            intent.putExtra(ExtraKeys.CHANNEL, itemData.getChannel());
            activity.startActivity(intent);
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
}
