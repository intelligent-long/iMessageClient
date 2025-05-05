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

/**
 * Created by LONG on 2024/6/4 at 5:36 PM.
 */
public class AddGroupChannelToTagRecyclerAdapter extends WrappableRecyclerViewAdapter<AddGroupChannelToTagRecyclerAdapter.ViewHolder, AddGroupChannelToTagRecyclerAdapter.ItemData> {
    private final Activity activity;
    private final List<ItemData> itemDataList;
    private final List<GroupChannel> checkedGroupChannels = new ArrayList<>();

    public AddGroupChannelToTagRecyclerAdapter(Activity activity, List<GroupChannel> channelList) {
        this.activity = activity;
        this.itemDataList = new ArrayList<>();
        channelList.forEach(channel -> {
            this.itemDataList.add(new ItemData(channel));
        });
        itemDataList.sort(Comparator.comparing(o -> o.indexChar));
    }

    public static class ItemData{
        private Character indexChar;
        private GroupChannel groupChannel;

        public ItemData(GroupChannel groupChannel) {
            indexChar = PinyinUtil.getPinyin(groupChannel.getName()).toUpperCase().charAt(0);
            if(!((indexChar >= 65 && indexChar <= 90) || (indexChar >= 97 && indexChar <= 122))){
                indexChar = '#';
            }
            this.groupChannel = groupChannel;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private RecyclerItemAddGroupChannelToTagBinding binding;
        public ViewHolder(RecyclerItemAddGroupChannelToTagBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerItemAddGroupChannelToTagBinding binding = RecyclerItemAddGroupChannelToTagBinding.inflate(activity.getLayoutInflater(), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public int getItemCount() {
        return itemDataList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AddGroupChannelToTagRecyclerAdapter.ItemData itemData = itemDataList.get(position);
        String avatarHash = itemData.groupChannel.getGroupAvatar() == null ? null : itemData.groupChannel.getGroupAvatar().getHash();
        if (avatarHash == null) {
            GlideBehaviours.loadToImageView(activity.getApplicationContext(), R.drawable.group_channel_default_avatar, holder.binding.avatar);
        } else {
            GlideBehaviours.loadToImageView(activity.getApplicationContext(), NetDataUrls.getGroupAvatarUrl(activity, avatarHash), holder.binding.avatar);
        }
        holder.binding.indexBar.setText(String.valueOf(itemData.indexChar));
        int previousPosition = position - 1;
        if(position == 0){
            holder.binding.indexBar.setVisibility(View.VISIBLE);
        } else {
            AddGroupChannelToTagRecyclerAdapter.ItemData previousItemData = itemDataList.get(previousPosition);
            if (previousItemData.indexChar.equals(itemData.indexChar)) {
                holder.binding.indexBar.setVisibility(View.GONE);
            } else {
                holder.binding.indexBar.setVisibility(View.VISIBLE);
            }
        }
        holder.binding.name.setText(itemData.groupChannel.getNote() == null ? itemData.groupChannel.getName() : itemData.groupChannel.getNote());
        setupYiers(holder, position);
    }

    private void setupYiers(ViewHolder holder, int position) {
        AddGroupChannelToTagRecyclerAdapter.ItemData itemData = itemDataList.get(position);
        holder.binding.clickView.setOnClickListener(v -> {
            holder.binding.checkBox.setChecked(!holder.binding.checkBox.isChecked());
            if(holder.binding.checkBox.isChecked()){
                checkedGroupChannels.add(itemData.groupChannel);
            }else {
                checkedGroupChannels.remove(itemData.groupChannel);
            }
        });
    }

    public List<GroupChannel> getCheckedGroupChannels() {
        return checkedGroupChannels;
    }
}
