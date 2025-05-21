package com.longx.intelligent.android.imessage.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.imessage.activity.ChannelActivity;
import com.longx.intelligent.android.imessage.activity.ChatActivity;
import com.longx.intelligent.android.imessage.activity.ExtraKeys;
import com.longx.intelligent.android.imessage.activity.GroupChannelActivity;
import com.longx.intelligent.android.imessage.activity.TransferGroupChannelAdminActivity;
import com.longx.intelligent.android.imessage.data.Channel;
import com.longx.intelligent.android.imessage.data.GroupChannel;
import com.longx.intelligent.android.imessage.databinding.RecyclerItemTransferGroupChannelAdminBinding;
import com.longx.intelligent.android.imessage.dialog.FastLocateDialog;
import com.longx.intelligent.android.imessage.net.dataurl.NetDataUrls;
import com.longx.intelligent.android.imessage.ui.glide.GlideApp;
import com.longx.intelligent.android.imessage.util.PinyinUtil;
import com.longx.intelligent.android.lib.recyclerview.WrappableRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by LONG on 2025/5/21 at 下午1:45.
 */
public class TransferGroupChannelAdminRecyclerAdapter extends WrappableRecyclerViewAdapter<TransferGroupChannelAdminRecyclerAdapter.ViewHolder, TransferGroupChannelAdminRecyclerAdapter.ItemData> {
    private final TransferGroupChannelAdminActivity activity;
    private final List<ItemData> itemDataList;
    private int selectedPosition = -1;
    private static final Object PAYLOAD_SELECTION_CHANGE = new Object();

    public TransferGroupChannelAdminRecyclerAdapter(TransferGroupChannelAdminActivity activity, List<Channel> channelList) {
        this.activity = activity;
        this.itemDataList = new ArrayList<>();
        channelList.forEach(channel -> {
            this.itemDataList.add(new ItemData(channel));
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
        private final Character indexChar;
        private final String fullPinyin;
        private final Channel channel;

        public ItemData(Channel channel) {
            this.fullPinyin = PinyinUtil.getPinyin(channel.autoGetName());
            char firstChar = fullPinyin.charAt(0);
            if (Character.isLetter(firstChar)) {
                indexChar = Character.toUpperCase(firstChar);
            } else {
                indexChar = '#';
            }
            this.channel = channel;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final RecyclerItemTransferGroupChannelAdminBinding binding;

        public ViewHolder(RecyclerItemTransferGroupChannelAdminBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerItemTransferGroupChannelAdminBinding binding = RecyclerItemTransferGroupChannelAdminBinding.inflate(activity.getLayoutInflater());
        return new ViewHolder(binding);
    }

    @Override
    public int getItemCount() {
        return itemDataList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemData itemData = itemDataList.get(position);
        String avatarHash = itemData.channel.getAvatar() == null ? null : itemData.channel.getAvatar().getHash();
        if (avatarHash == null) {
            GlideApp
                    .with(activity.getApplicationContext())
                    .load(R.drawable.default_avatar)
                    .into(holder.binding.avatar);
        } else {
            GlideApp
                    .with(activity.getApplicationContext())
                    .load(NetDataUrls.getAvatarUrl(activity, avatarHash))
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
        holder.binding.name.setText(itemData.channel.autoGetName());
        setupYiers(holder, position);
    }

    private void setupYiers(@NonNull ViewHolder holder, int position) {
        ItemData itemData = itemDataList.get(position);
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
                    activity.getBinding().appbar.setExpanded(locatePosition == 0);
                    activity.getBinding().recyclerView.smoothScrollToPosition(locatePosition);
                }
                fastLocateDialog.dismiss();
            });
            fastLocateDialog.create().show();
        });
        holder.binding.clickView.setOnClickListener(v -> {
            Intent intent = new Intent(activity, ChannelActivity.class);
            intent.putExtra(ExtraKeys.CHANNEL, itemData.channel);
            activity.startActivity(intent);
        });
        holder.binding.radioButton.setOnCheckedChangeListener(null);
        holder.binding.radioButton.setChecked(position == selectedPosition);
        holder.binding.radioButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked && selectedPosition != position) {
                int previous = selectedPosition;
                selectedPosition = position;
                if (previous != -1) notifyItemChanged(previous, PAYLOAD_SELECTION_CHANGE);
                notifyItemChanged(selectedPosition, PAYLOAD_SELECTION_CHANGE);
            }
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

    public Channel getSelected(){
        try {
            return itemDataList.get(selectedPosition).channel;
        }catch (Exception e){
            return null;
        }
    }
}
