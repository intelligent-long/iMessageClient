package com.longx.intelligent.android.imessage.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.imessage.activity.BroadcastActivity;
import com.longx.intelligent.android.imessage.activity.ChannelActivity;
import com.longx.intelligent.android.imessage.activity.ExtraKeys;
import com.longx.intelligent.android.imessage.da.database.manager.ChannelDatabaseManager;
import com.longx.intelligent.android.imessage.data.BroadcastLike;
import com.longx.intelligent.android.imessage.data.Channel;
import com.longx.intelligent.android.imessage.databinding.RecyclerItemBroadcastLikesInteractionBinding;
import com.longx.intelligent.android.imessage.net.dataurl.NetDataUrls;
import com.longx.intelligent.android.imessage.ui.glide.GlideApp;
import com.longx.intelligent.android.imessage.util.TimeUtil;
import com.longx.intelligent.android.lib.recyclerview.WrappableRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LONG on 2024/9/17 at 下午1:32.
 */
public class BroadcastLikesInteractionRecyclerAdapter extends WrappableRecyclerViewAdapter<BroadcastLikesInteractionRecyclerAdapter.ViewHolder, BroadcastLikesInteractionRecyclerAdapter.ItemData> {
    private final Activity activity;
    private final List<ItemData> itemDataList = new ArrayList<>();

    public BroadcastLikesInteractionRecyclerAdapter(Activity activity) {
        this.activity = activity;
    }

    public static class ItemData {
        private final BroadcastLike broadcastLike;

        public ItemData(BroadcastLike broadcastLike) {
            this.broadcastLike = broadcastLike;
        }

        public BroadcastLike getBroadcastLike() {
            return broadcastLike;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final RecyclerItemBroadcastLikesInteractionBinding binding;
        public ViewHolder(RecyclerItemBroadcastLikesInteractionBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerItemBroadcastLikesInteractionBinding binding = RecyclerItemBroadcastLikesInteractionBinding.inflate(activity.getLayoutInflater());
        return new ViewHolder(binding);
    }

    @Override
    public int getItemCount() {
        return itemDataList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BroadcastLike broadcastLike = itemDataList.get(position).broadcastLike;
        if (broadcastLike.getAvatarHash() == null) {
            GlideApp
                    .with(activity.getApplicationContext())
                    .load(R.drawable.default_avatar)
                    .into(holder.binding.avatar);
        } else {
            GlideApp
                    .with(activity.getApplicationContext())
                    .load(NetDataUrls.getAvatarUrl(activity, broadcastLike.getAvatarHash()))
                    .into(holder.binding.avatar);
        }
        Channel channel = ChannelDatabaseManager.getInstance().findOneChannel(broadcastLike.getFromId());
        String name;
        if(channel != null){
            name = channel.autoGetName();
        }else {
            name = broadcastLike.getFromName();
        }
        holder.binding.name.setText(name);
        holder.binding.time.setText(TimeUtil.formatRelativeTime(broadcastLike.getLikeTime()));
        if(broadcastLike.isNew()){
            holder.binding.badge.setVisibility(View.VISIBLE);
        }else {
            holder.binding.badge.setVisibility(View.GONE);
        }
        holder.binding.text.setText("喜欢了");
        GlideApp.with(activity.getApplicationContext())
                .load(NetDataUrls.getBroadcastMediaDataUrl(activity, broadcastLike.getCoverMediaId()))
                .centerCrop()
                .into(holder.binding.broadcastCover);
        holder.binding.broadcastTime.setText(TimeUtil.formatRelativeTime(broadcastLike.getBroadcastTime()));
        holder.binding.broadcastText.setText(broadcastLike.getBroadcastText());
        setupYiers(holder, position);
    }

    private void setupYiers(ViewHolder holder, int position) {
        BroadcastLike broadcastLike = itemDataList.get(position).broadcastLike;
        holder.binding.getRoot().setOnClickListener(v -> {
            Intent intent = new Intent(activity, BroadcastActivity.class);
            intent.putExtra(ExtraKeys.BROADCAST_ID, broadcastLike.getBroadcastId());
            activity.startActivity(intent);
        });
        holder.binding.avatar.setOnClickListener(v -> {
            Intent intent = new Intent(activity, ChannelActivity.class);
            intent.putExtra(ExtraKeys.IMESSAGE_ID, broadcastLike.getFromId());
            activity.startActivity(intent);
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    public void addItemsToEndAndShow(List<BroadcastLike> broadcastLikes){
        broadcastLikes.sort((o1, o2) -> - o1.getLikeTime().compareTo(o2.getLikeTime()));
        broadcastLikes.forEach(broadcastLike -> itemDataList.add(new ItemData(broadcastLike)));
        notifyDataSetChanged();
    }

    public List<ItemData> getItemDataList() {
        return itemDataList;
    }
}
