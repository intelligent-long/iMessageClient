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
import com.longx.intelligent.android.imessage.activity.GroupChannelActivity;
import com.longx.intelligent.android.imessage.activity.SearchChannelActivity;
import com.longx.intelligent.android.imessage.activity.SearchGroupChannelActivity;
import com.longx.intelligent.android.imessage.data.Channel;
import com.longx.intelligent.android.imessage.data.GroupChannel;
import com.longx.intelligent.android.imessage.databinding.LinearLayoutViewsSearchChannelBinding;
import com.longx.intelligent.android.imessage.databinding.LinearLayoutViewsSearchGroupChannelBinding;
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
public class SearchGroupChannelLinearLayoutViews extends LinearLayoutViews<SearchGroupChannelLinearLayoutViews.ItemData> {
    public SearchGroupChannelLinearLayoutViews(SearchGroupChannelActivity activity, LinearLayout linearLayout, NestedScrollView nestedScrollView) {
        super(activity, linearLayout, nestedScrollView);
    }

    public static class ItemData{
        private Character indexChar;
        private GroupChannel groupChannel;

        public ItemData(GroupChannel groupChannel) {
            indexChar = PinyinUtil.getPinyin(groupChannel.autoGetName()).toUpperCase().charAt(0);
            if(!((indexChar >= 65 && indexChar <= 90) || (indexChar >= 97 && indexChar <= 122))){
                indexChar = '#';
            }
            this.groupChannel = groupChannel;
        }

        public Character getIndexChar() {
            return indexChar;
        }

        public GroupChannel getGroupChannel() {
            return groupChannel;
        }
    }

    @Override
    public SearchGroupChannelActivity getActivity() {
        return (SearchGroupChannelActivity) super.getActivity();
    }

    @Override
    public View getView(ItemData itemData, Activity activity) {
        LinearLayoutViewsSearchGroupChannelBinding binding = LinearLayoutViewsSearchGroupChannelBinding.inflate(activity.getLayoutInflater());
        String avatarHash = itemData.groupChannel.getAvatarHash() == null ? null : itemData.groupChannel.getAvatarHash();
        if(avatarHash == null){
            GlideApp
                    .with(activity.getApplicationContext())
                    .load(R.drawable.default_avatar)
                    .into(binding.avatar);
        }else {
            GlideApp
                    .with(activity.getApplicationContext())
                    .load(NetDataUrls.getGroupAvatarUrl(activity, avatarHash))
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
        binding.name.setText(itemData.groupChannel.autoGetName());
        showMatchingIn(binding, itemData, activity);
        setupYiers(binding, itemData, activity);
        return binding.getRoot();
    }

    private void showMatchingIn(LinearLayoutViewsSearchGroupChannelBinding binding, ItemData itemData, Activity activity) {
        String searchStr = getActivity().getSearchStr();
        SpannableStringBuilder matchingInText = new SpannableStringBuilder();
        if(itemData.groupChannel.getGroupChannelIdUser() != null && Utils.containsIgnoreCase(itemData.groupChannel.getGroupChannelIdUser(), searchStr)){
            appendMatchingText(matchingInText, Constants.APP_NAME + " ID  ", itemData.groupChannel.getGroupChannelIdUser(), searchStr);
        }
        if(itemData.groupChannel.getName() != null && Utils.containsIgnoreCase(itemData.groupChannel.getName(), searchStr)){
            appendMatchingText(matchingInText, "群频道名称  ", itemData.groupChannel.getName(), searchStr);
        }
        if(itemData.groupChannel.getNote() != null && Utils.containsIgnoreCase(itemData.groupChannel.getNote(), searchStr)){
            appendMatchingText(matchingInText, "备注  ", itemData.groupChannel.getNote(), searchStr);
        }
        if ((itemData.groupChannel.getFirstRegion() != null && Utils.containsIgnoreCase(itemData.groupChannel.getFirstRegion().getName(), searchStr)) ||
                (itemData.groupChannel.getSecondRegion() != null && Utils.containsIgnoreCase(itemData.groupChannel.getSecondRegion().getName(), searchStr)) ||
                (itemData.groupChannel.getThirdRegion() != null && Utils.containsIgnoreCase(itemData.groupChannel.getThirdRegion().getName(), searchStr))) {
            appendMatchingText(matchingInText, "地区  ", itemData.groupChannel.buildRegionDesc(), searchStr);
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

    private void setupYiers(LinearLayoutViewsSearchGroupChannelBinding binding, ItemData itemData, Activity activity){
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
            Intent intent = new Intent(activity, GroupChannelActivity.class);
            intent.putExtra(ExtraKeys.GROUP_CHANNEL, itemData.getGroupChannel());
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
