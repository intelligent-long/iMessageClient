package com.longx.intelligent.android.imessage.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.imessage.activity.ChannelActivity;
import com.longx.intelligent.android.imessage.activity.ChatActivity;
import com.longx.intelligent.android.imessage.activity.ExploreGroupChannelActivity;
import com.longx.intelligent.android.imessage.activity.ExtraKeys;
import com.longx.intelligent.android.imessage.activity.GroupChannelActivity;
import com.longx.intelligent.android.imessage.activity.GroupChannelsActivity;
import com.longx.intelligent.android.imessage.behaviorcomponents.GlideBehaviours;
import com.longx.intelligent.android.imessage.data.Channel;
import com.longx.intelligent.android.imessage.data.GroupChannel;
import com.longx.intelligent.android.imessage.databinding.RecyclerItemChooseOneChannelBinding;
import com.longx.intelligent.android.imessage.databinding.RecyclerItemChooseOneGroupChannelBinding;
import com.longx.intelligent.android.imessage.net.dataurl.NetDataUrls;
import com.longx.intelligent.android.imessage.util.ErrorLogger;
import com.longx.intelligent.android.imessage.util.PinyinUtil;
import com.longx.intelligent.android.lib.recyclerview.WrappableRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by LONG on 2024/6/4 at 5:36 PM.
 */
public class ChooseOneGroupChannelRecyclerAdapter extends WrappableRecyclerViewAdapter<ChooseOneGroupChannelRecyclerAdapter.ViewHolder, ChooseOneGroupChannelRecyclerAdapter.ItemData> {
    private final Activity activity;
    private final List<ItemData> itemDataList;
    private int selectedPosition = -1;
    private static final Object PAYLOAD_SELECTION_CHANGE = new Object();

    public ChooseOneGroupChannelRecyclerAdapter(Activity activity, List<GroupChannel> groupChannelList, GroupChannel choseGroupChannel) {
        this.activity = activity;
        this.itemDataList = new ArrayList<>();
        groupChannelList.forEach(groupChannel -> {
            this.itemDataList.add(new ItemData(groupChannel));
        });
        itemDataList.sort(Comparator.comparing(o -> o.indexChar));
        for (int i = 0; i < itemDataList.size(); i++) {
            GroupChannel groupChannel = itemDataList.get(i).groupChannel;
            if(groupChannel.equals(choseGroupChannel)) selectedPosition = i;
        }
    }

    public static class ItemData {
        private Character indexChar;
        private GroupChannel groupChannel;

        public ItemData(GroupChannel groupChannel) {
            indexChar = PinyinUtil.getPinyin(groupChannel.getName()).toUpperCase().charAt(0);
            if (!((indexChar >= 65 && indexChar <= 90) || (indexChar >= 97 && indexChar <= 122))) {
                indexChar = '#';
            }
            this.groupChannel = groupChannel;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final RecyclerItemChooseOneGroupChannelBinding binding;

        public ViewHolder(RecyclerItemChooseOneGroupChannelBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerItemChooseOneGroupChannelBinding binding = RecyclerItemChooseOneGroupChannelBinding.inflate(activity.getLayoutInflater(), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public int getItemCount() {
        return itemDataList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
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
        holder.binding.name.setText(itemData.groupChannel.getNote() == null ? itemData.groupChannel.getName() : itemData.groupChannel.getNote());
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
            Intent intent = new Intent(activity, GroupChannelActivity.class);
            intent.putExtra(ExtraKeys.GROUP_CHANNEL, itemData.groupChannel);
            activity.startActivity(intent);
        });
    }

    public GroupChannel getSelected(){
        try {
            return itemDataList.get(selectedPosition).groupChannel;
        }catch (Exception e){
            return null;
        }
    }
}