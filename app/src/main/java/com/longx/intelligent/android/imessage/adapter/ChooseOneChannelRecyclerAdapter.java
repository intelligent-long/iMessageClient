package com.longx.intelligent.android.imessage.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.imessage.activity.ChannelActivity;
import com.longx.intelligent.android.imessage.activity.ExtraKeys;
import com.longx.intelligent.android.imessage.activity.GroupChannelActivity;
import com.longx.intelligent.android.imessage.behaviorcomponents.GlideBehaviours;
import com.longx.intelligent.android.imessage.data.Channel;
import com.longx.intelligent.android.imessage.data.GroupChannel;
import com.longx.intelligent.android.imessage.databinding.RecyclerItemAddChannelToTagBinding;
import com.longx.intelligent.android.imessage.databinding.RecyclerItemChooseOneChannelBinding;
import com.longx.intelligent.android.imessage.net.dataurl.NetDataUrls;
import com.longx.intelligent.android.imessage.util.PinyinUtil;
import com.longx.intelligent.android.lib.recyclerview.WrappableRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by LONG on 2024/6/4 at 5:36 PM.
 */
public class ChooseOneChannelRecyclerAdapter extends WrappableRecyclerViewAdapter<ChooseOneChannelRecyclerAdapter.ViewHolder, ChooseOneChannelRecyclerAdapter.ItemData> {
    private final Activity activity;
    private final List<ItemData> itemDataList;
    private int selectedPosition = -1;
    private static final Object PAYLOAD_SELECTION_CHANGE = new Object();

    public ChooseOneChannelRecyclerAdapter(Activity activity, List<Channel> channelList) {
        this.activity = activity;
        this.itemDataList = new ArrayList<>();
        channelList.forEach(channel -> this.itemDataList.add(new ItemData(channel)));
        itemDataList.sort(Comparator.comparing(o -> o.indexChar));
    }

    public static class ItemData {
        private Character indexChar;
        private Channel channel;

        public ItemData(Channel channel) {
            indexChar = PinyinUtil.getPinyin(channel.getUsername()).toUpperCase().charAt(0);
            if (!((indexChar >= 65 && indexChar <= 90) || (indexChar >= 97 && indexChar <= 122))) {
                indexChar = '#';
            }
            this.channel = channel;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final RecyclerItemChooseOneChannelBinding binding;

        public ViewHolder(RecyclerItemChooseOneChannelBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerItemChooseOneChannelBinding binding = RecyclerItemChooseOneChannelBinding.inflate(activity.getLayoutInflater(), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public int getItemCount() {
        return itemDataList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
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
        holder.binding.clickView.setOnClickListener(v -> {
            Intent intent = new Intent(activity, ChannelActivity.class);
            intent.putExtra(ExtraKeys.CHANNEL, itemData.channel);
            activity.startActivity(intent);
        });
    }

    public Channel getSelected(){
        try {
            return itemDataList.get(selectedPosition).channel;
        }catch (Exception e){
            return null;
        }
    }
}