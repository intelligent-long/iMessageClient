package com.longx.intelligent.android.imessage.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;

import com.bumptech.glide.request.target.Target;
import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.imessage.activity.GroupChannelsActivity;
import com.longx.intelligent.android.imessage.data.GroupChannel;
import com.longx.intelligent.android.imessage.databinding.RecyclerItemGroupChannelBinding;
import com.longx.intelligent.android.imessage.dialog.FastLocateDialog;
import com.longx.intelligent.android.imessage.net.dataurl.NetDataUrls;
import com.longx.intelligent.android.imessage.ui.glide.GlideApp;
import com.longx.intelligent.android.imessage.util.ErrorLogger;
import com.longx.intelligent.android.imessage.util.PinyinUtil;
import com.longx.intelligent.android.lib.recyclerview.RecyclerView;
import com.longx.intelligent.android.lib.recyclerview.WrappableRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LONG on 2025/4/14 at 上午5:37.
 */

public class GroupChannelsRecyclerAdapter extends WrappableRecyclerViewAdapter<GroupChannelsRecyclerAdapter.ViewHolder, GroupChannelsRecyclerAdapter.ItemData> {
    private final GroupChannelsActivity activity;
    private final List<ItemData> itemDataList;

    public GroupChannelsRecyclerAdapter(GroupChannelsActivity activity, List<GroupChannel> allAssociations) {
        this.activity = activity;
        this.itemDataList = new ArrayList<>();
        allAssociations.forEach(association -> {
            this.itemDataList.add(new GroupChannelsRecyclerAdapter.ItemData(association));
        });
        itemDataList.sort((o1, o2) -> {
            if (o1.indexChar == '#' && o2.indexChar != '#') return 1;
            if (o1.indexChar != '#' && o2.indexChar == '#') return -1;
            if (o1.indexChar == '#' && o2.indexChar == '#') return 0;
            int pinyinCompare = o1.fullPinyin.compareToIgnoreCase(o2.fullPinyin);
            if (pinyinCompare != 0) return pinyinCompare;
            return o1.fullPinyin.compareTo(o2.fullPinyin);
        });

    }

    public static class ItemData {
        private final String fullPinyin;
        private final char indexChar;
        private final GroupChannel groupChannel;

        public ItemData(GroupChannel groupChannel) {
            this.groupChannel = groupChannel;
            String name = groupChannel.autoGetName();
            this.fullPinyin = PinyinUtil.getPinyin(name);
            char firstChar = fullPinyin.charAt(0);
            if (Character.isLetter(firstChar)) {
                indexChar = Character.toUpperCase(firstChar);
            } else {
                indexChar = '#';
            }
        }

        public String getFullPinyin() {
            return fullPinyin;
        }

        public char getIndexChar() {
            return indexChar;
        }

        public GroupChannel getGroupChannel() {
            return groupChannel;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final RecyclerItemGroupChannelBinding binding;
        public ViewHolder(RecyclerItemGroupChannelBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerItemGroupChannelBinding binding = RecyclerItemGroupChannelBinding.inflate(activity.getLayoutInflater());
        return new ViewHolder(binding);
    }

    @Override
    public int getItemCount() {
        return itemDataList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemData itemData = itemDataList.get(position);
        String groupAvatarHash = itemData.groupChannel.getGroupAvatar() == null ? null : itemData.groupChannel.getGroupAvatar().getHash();
        if(groupAvatarHash == null){
            GlideApp.with(activity)
                    .load(AppCompatResources.getDrawable(activity, R.drawable.group_channel_default_avatar))
                    .override(Target.SIZE_ORIGINAL)
                    .into(holder.binding.avatar);
        }else {
            GlideApp.with(activity)
                    .load(NetDataUrls.getGroupAvatarUrl(activity, groupAvatarHash))
                    .into(holder.binding.avatar);
        }
        holder.binding.indexBar.setText(String.valueOf(itemData.indexChar));
        int previousPosition = position - 1;
        if(position == 0){
            holder.binding.indexBar.setVisibility(View.VISIBLE);
        } else {
            ItemData previousItemData = itemDataList.get(previousPosition);
            if (previousItemData.indexChar == itemData.indexChar) {
                holder.binding.indexBar.setVisibility(View.GONE);
            } else {
                holder.binding.indexBar.setVisibility(View.VISIBLE);
            }
        }
        String note = itemData.groupChannel.getNote();
        holder.binding.name.setText(note == null ? itemData.groupChannel.getName() : note);
        setupYiers(holder, position);
    }

    private void setupYiers(@NonNull ViewHolder holder, int position) {
        ItemData itemData = itemDataList.get(position);
        holder.binding.clickView.setOnClickListener(v -> {
            getOnItemClickYier().onItemClick(position, itemData);
        });
        holder.binding.indexBar.setOnClickListener(v -> {
            FastLocateDialog fastLocateDialog = new FastLocateDialog(activity, FastLocateDialog.LOCATE_HEADER_CHANNEL, getExistTexts());
            fastLocateDialog.setLocateYier((positionSelect, textSelect) -> {
                int locatePosition = -1;
                if(textSelect.equals(".")){
                    locatePosition = 0;
                }else {
                    for (int i = 0; i < itemDataList.size(); i++) {
                        ItemData data = itemDataList.get(i);
                        if (String.valueOf(data.indexChar).equals(textSelect)) {
                            locatePosition = i + 1;
                            break;
                        }
                    }
                }
                if(locatePosition != -1) {
                    activity.getBinding().appBar.setExpanded(locatePosition == 0);
                    activity.getBinding().recyclerView.smoothScrollToPosition(locatePosition);
                }
                fastLocateDialog.dismiss();
            });
            fastLocateDialog.create().show();
        });
    }

    private String[] getExistTexts(){
        String[] result = new String[getItemCount() + 1];
        for (int i = 0; i < itemDataList.size(); i++) {
            result[i] = String.valueOf(itemDataList.get(i).indexChar);
        }
        result[result.length - 1] = ".";
        return result;
    }
}