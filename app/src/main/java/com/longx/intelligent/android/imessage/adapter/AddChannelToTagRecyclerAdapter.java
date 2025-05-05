package com.longx.intelligent.android.imessage.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.imessage.behaviorcomponents.GlideBehaviours;
import com.longx.intelligent.android.imessage.data.Channel;
import com.longx.intelligent.android.imessage.databinding.RecyclerItemAddChannelToTagBinding;
import com.longx.intelligent.android.imessage.net.dataurl.NetDataUrls;
import com.longx.intelligent.android.imessage.util.PinyinUtil;
import com.longx.intelligent.android.lib.recyclerview.WrappableRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by LONG on 2024/6/4 at 5:36 PM.
 */
public class AddChannelToTagRecyclerAdapter extends WrappableRecyclerViewAdapter<AddChannelToTagRecyclerAdapter.ViewHolder, AddChannelToTagRecyclerAdapter.ItemData> {
    private final Activity activity;
    private final List<ItemData> itemDataList;
    private final List<Channel> checkedChannels = new ArrayList<>();

    public AddChannelToTagRecyclerAdapter(Activity activity, List<Channel> channelList) {
        this.activity = activity;
        this.itemDataList = new ArrayList<>();
        channelList.forEach(channel -> this.itemDataList.add(new ItemData(channel)));
        itemDataList.sort(Comparator.comparing(o -> o.indexChar));
    }

    public static class ItemData {
        private Character indexChar;
        private Channel channel;
        private boolean checked = false;

        public ItemData(Channel channel) {
            indexChar = PinyinUtil.getPinyin(channel.getUsername()).toUpperCase().charAt(0);
            if (!((indexChar >= 65 && indexChar <= 90) || (indexChar >= 97 && indexChar <= 122))) {
                indexChar = '#';
            }
            this.channel = channel;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final RecyclerItemAddChannelToTagBinding binding;

        public ViewHolder(RecyclerItemAddChannelToTagBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerItemAddChannelToTagBinding binding = RecyclerItemAddChannelToTagBinding.inflate(activity.getLayoutInflater(), parent, false);
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
            GlideBehaviours.loadToImageView(activity.getApplicationContext(), R.drawable.default_avatar, holder.binding.avatar);
        } else {
            GlideBehaviours.loadToImageView(activity.getApplicationContext(), NetDataUrls.getAvatarUrl(activity, avatarHash), holder.binding.avatar);
        }

        holder.binding.indexBar.setText(String.valueOf(itemData.indexChar));
        if (position == 0 || !itemDataList.get(position - 1).indexChar.equals(itemData.indexChar)) {
            holder.binding.indexBar.setVisibility(View.VISIBLE);
        } else {
            holder.binding.indexBar.setVisibility(View.GONE);
        }

        holder.binding.name.setText(itemData.channel.getNote() == null ? itemData.channel.getUsername() : itemData.channel.getNote());
        holder.binding.checkBox.setChecked(itemData.checked);
        setupClickLogic(holder, itemData);
    }

    private void setupClickLogic(ViewHolder holder, ItemData itemData) {
        holder.binding.clickView.setOnClickListener(v -> {
            boolean newChecked = !itemData.checked;
            itemData.checked = newChecked;
            holder.binding.checkBox.setChecked(newChecked);

            if (newChecked) {
                if (!checkedChannels.contains(itemData.channel)) {
                    checkedChannels.add(itemData.channel);
                }
            } else {
                checkedChannels.remove(itemData.channel);
            }
        });
    }

    public List<Channel> getCheckedChannels() {
        return checkedChannels;
    }
}