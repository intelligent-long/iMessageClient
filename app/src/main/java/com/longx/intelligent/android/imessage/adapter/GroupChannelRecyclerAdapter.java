package com.longx.intelligent.android.imessage.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.imessage.activity.GroupChannelsActivity;
import com.longx.intelligent.android.imessage.behaviorcomponents.GlideBehaviours;
import com.longx.intelligent.android.imessage.data.Channel;
import com.longx.intelligent.android.imessage.data.GroupChannel;
import com.longx.intelligent.android.imessage.databinding.RecyclerItemChannelBinding;
import com.longx.intelligent.android.imessage.databinding.RecyclerItemGroupChannelBinding;
import com.longx.intelligent.android.imessage.dialog.FastLocateDialog;
import com.longx.intelligent.android.imessage.fragment.main.ChannelsFragment;
import com.longx.intelligent.android.imessage.net.dataurl.NetDataUrls;
import com.longx.intelligent.android.imessage.util.PinyinUtil;
import com.longx.intelligent.android.lib.recyclerview.RecyclerView;
import com.longx.intelligent.android.lib.recyclerview.WrappableRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LONG on 2025/4/14 at 上午5:37.
 */

public class GroupChannelRecyclerAdapter extends WrappableRecyclerViewAdapter<GroupChannelRecyclerAdapter.ViewHolder, GroupChannelRecyclerAdapter.ItemData> {
    private final GroupChannelsActivity activity;
    private final List<ItemData> itemDataList;

    public GroupChannelRecyclerAdapter(GroupChannelsActivity activity, List<GroupChannel> allAssociations) {
        this.activity = activity;
        this.itemDataList = new ArrayList<>();
        allAssociations.forEach(association -> {
            this.itemDataList.add(new GroupChannelRecyclerAdapter.ItemData(association));
        });
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

        public Character getIndexChar() {
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

        holder.binding.indexBar.setText(String.valueOf(itemData.indexChar));
        int previousPosition = position - 1;
        if(position == 0){
            holder.binding.indexBar.setVisibility(View.VISIBLE);
        } else {
            ItemData previousItemData = itemDataList.get(previousPosition);
            if (previousItemData.indexChar.equals(itemData.indexChar)) {
                holder.binding.indexBar.setVisibility(View.GONE);
            } else {
                holder.binding.indexBar.setVisibility(View.VISIBLE);
            }
        }
        holder.binding.name.setText(itemData.groupChannel.getName());
        setupYiers(holder, position);
    }

    private void setupYiers(@NonNull ViewHolder holder, int position) {

    }
}