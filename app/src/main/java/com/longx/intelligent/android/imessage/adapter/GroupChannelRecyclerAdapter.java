package com.longx.intelligent.android.imessage.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.imessage.activity.GroupChannelsActivity;
import com.longx.intelligent.android.imessage.behaviorcomponents.GlideBehaviours;
import com.longx.intelligent.android.imessage.data.Channel;
import com.longx.intelligent.android.imessage.databinding.RecyclerItemChannelBinding;
import com.longx.intelligent.android.imessage.databinding.RecyclerItemGroupChannelBinding;
import com.longx.intelligent.android.imessage.dialog.FastLocateDialog;
import com.longx.intelligent.android.imessage.fragment.main.ChannelsFragment;
import com.longx.intelligent.android.imessage.net.dataurl.NetDataUrls;
import com.longx.intelligent.android.imessage.util.PinyinUtil;
import com.longx.intelligent.android.lib.recyclerview.RecyclerView;
import com.longx.intelligent.android.lib.recyclerview.WrappableRecyclerViewAdapter;

import java.util.List;

/**
 * Created by LONG on 2025/4/14 at 上午5:37.
 */

public class GroupChannelRecyclerAdapter extends WrappableRecyclerViewAdapter<GroupChannelRecyclerAdapter.ViewHolder, GroupChannelRecyclerAdapter.ItemData> {
    private final GroupChannelsActivity activity;
    private final List<ItemData> itemDataList;

    public GroupChannelRecyclerAdapter(GroupChannelsActivity activity, List<ItemData> itemDataList) {
        this.activity = activity;
        this.itemDataList = itemDataList;
    }

    public static class ItemData{
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private RecyclerItemGroupChannelBinding binding;
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
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    }

    private void setupYiers(@NonNull ViewHolder holder, int position) {
    }

    @Override
    public int getItemCount() {
        return itemDataList.size();
    }
}