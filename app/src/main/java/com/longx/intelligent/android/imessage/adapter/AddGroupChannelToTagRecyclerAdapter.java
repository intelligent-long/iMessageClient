package com.longx.intelligent.android.imessage.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.imessage.behaviorcomponents.GlideBehaviours;
import com.longx.intelligent.android.imessage.data.GroupChannel;
import com.longx.intelligent.android.imessage.databinding.RecyclerItemAddGroupChannelToTagBinding;
import com.longx.intelligent.android.imessage.net.dataurl.NetDataUrls;
import com.longx.intelligent.android.imessage.util.PinyinUtil;
import com.longx.intelligent.android.lib.recyclerview.WrappableRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AddGroupChannelToTagRecyclerAdapter extends WrappableRecyclerViewAdapter<AddGroupChannelToTagRecyclerAdapter.ViewHolder, AddGroupChannelToTagRecyclerAdapter.ItemData> {
    private final Activity activity;
    private final List<ItemData> itemDataList;
    private final List<GroupChannel> checkedGroupChannels = new ArrayList<>();

    public AddGroupChannelToTagRecyclerAdapter(Activity activity, List<GroupChannel> channelList) {
        this.activity = activity;
        this.itemDataList = new ArrayList<>();
        channelList.forEach(channel -> this.itemDataList.add(new ItemData(channel)));
        itemDataList.sort(Comparator.comparing(o -> o.indexChar));
    }

    public static class ItemData {
        private Character indexChar;
        private GroupChannel groupChannel;
        private boolean checked = false;

        public ItemData(GroupChannel groupChannel) {
            indexChar = PinyinUtil.getPinyin(groupChannel.getName()).toUpperCase().charAt(0);
            if (!((indexChar >= 65 && indexChar <= 90) || (indexChar >= 97 && indexChar <= 122))) {
                indexChar = '#';
            }
            this.groupChannel = groupChannel;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final RecyclerItemAddGroupChannelToTagBinding binding;
        public ViewHolder(RecyclerItemAddGroupChannelToTagBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerItemAddGroupChannelToTagBinding binding =
                RecyclerItemAddGroupChannelToTagBinding.inflate(activity.getLayoutInflater(), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public int getItemCount() {
        return itemDataList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemData itemData = itemDataList.get(position);
        String avatarHash = itemData.groupChannel.getGroupAvatar() == null ? null : itemData.groupChannel.getGroupAvatar().getHash();
        if (avatarHash == null) {
            GlideBehaviours.loadToImageView(activity.getApplicationContext(), R.drawable.group_channel_default_avatar, holder.binding.avatar);
        } else {
            GlideBehaviours.loadToImageView(activity.getApplicationContext(), NetDataUrls.getGroupAvatarUrl(activity, avatarHash), holder.binding.avatar);
        }

        holder.binding.indexBar.setText(String.valueOf(itemData.indexChar));
        if (position == 0 || !itemDataList.get(position - 1).indexChar.equals(itemData.indexChar)) {
            holder.binding.indexBar.setVisibility(View.VISIBLE);
        } else {
            holder.binding.indexBar.setVisibility(View.GONE);
        }

        holder.binding.name.setText(itemData.groupChannel.getNote() == null ?
                itemData.groupChannel.getName() : itemData.groupChannel.getNote());
        holder.binding.checkBox.setChecked(itemData.checked);
        setupClickLogic(holder, itemData);
    }

    private void setupClickLogic(ViewHolder holder, ItemData itemData) {
        holder.binding.clickView.setOnClickListener(v -> {
            boolean newChecked = !itemData.checked;
            itemData.checked = newChecked;
            holder.binding.checkBox.setChecked(newChecked);

            if (newChecked) {
                if (!checkedGroupChannels.contains(itemData.groupChannel)) {
                    checkedGroupChannels.add(itemData.groupChannel);
                }
            } else {
                checkedGroupChannels.remove(itemData.groupChannel);
            }
        });
    }

    public List<GroupChannel> getCheckedGroupChannels() {
        return checkedGroupChannels;
    }
}