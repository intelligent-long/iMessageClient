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
import com.longx.intelligent.android.imessage.data.BroadcastComment;
import com.longx.intelligent.android.imessage.databinding.RecyclerItemBroadcastCommentsInteractionBinding;
import com.longx.intelligent.android.imessage.databinding.RecyclerItemBroadcastLikesInteractionBinding;
import com.longx.intelligent.android.imessage.net.dataurl.NetDataUrls;
import com.longx.intelligent.android.imessage.ui.glide.GlideApp;
import com.longx.intelligent.android.imessage.util.TimeUtil;
import com.longx.intelligent.android.lib.recyclerview.WrappableRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LONG on 2024/9/29 at 下午10:57.
 */
public class BroadcastCommentsInteractionRecyclerAdapter extends WrappableRecyclerViewAdapter<BroadcastCommentsInteractionRecyclerAdapter.ViewHolder, BroadcastCommentsInteractionRecyclerAdapter.ItemData> {
    private final Activity activity;
    private final List<ItemData> itemDataList = new ArrayList<>();

    public BroadcastCommentsInteractionRecyclerAdapter(Activity activity) {
        this.activity = activity;
    }

    public static class ItemData {
        private final BroadcastComment broadcastComment;

        public ItemData(BroadcastComment broadcastComment) {
            this.broadcastComment = broadcastComment;
        }

        public BroadcastComment getBroadcastComment() {
            return broadcastComment;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final RecyclerItemBroadcastCommentsInteractionBinding binding;
        public ViewHolder(RecyclerItemBroadcastCommentsInteractionBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerItemBroadcastCommentsInteractionBinding binding = RecyclerItemBroadcastCommentsInteractionBinding.inflate(activity.getLayoutInflater());
        return new ViewHolder(binding);
    }

    @Override
    public int getItemCount() {
        return itemDataList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BroadcastComment broadcastComment = itemDataList.get(position).broadcastComment;
        if (broadcastComment.getAvatarHash() == null) {
            GlideApp
                    .with(activity.getApplicationContext())
                    .load(R.drawable.default_avatar)
                    .into(holder.binding.avatar);
        } else {
            GlideApp
                    .with(activity.getApplicationContext())
                    .load(NetDataUrls.getAvatarUrl(activity, broadcastComment.getAvatarHash()))
                    .into(holder.binding.avatar);
        }
        holder.binding.name.setText(broadcastComment.getFromNameIncludeNote());
        holder.binding.time.setText(TimeUtil.formatRelativeTime(broadcastComment.getCommentTime()));
        if(broadcastComment.isNew()){
            holder.binding.badge.setVisibility(View.VISIBLE);
        }else {
            holder.binding.badge.setVisibility(View.GONE);
        }
        holder.binding.text.setText(broadcastComment.getText());
        GlideApp.with(activity.getApplicationContext())
                .load(NetDataUrls.getBroadcastMediaDataUrl(activity, broadcastComment.getCoverMediaId()))
                .centerCrop()
                .into(holder.binding.broadcastCover);
        holder.binding.broadcastTime.setText(TimeUtil.formatRelativeTime(broadcastComment.getBroadcastTime()));
        holder.binding.broadcastText.setText(broadcastComment.getBroadcastText());
        setupYiers(holder, position);
    }

    private void setupYiers(ViewHolder holder, int position) {
        BroadcastComment broadcastComment = itemDataList.get(position).broadcastComment;
        holder.binding.getRoot().setOnClickListener(v -> {
            Intent intent = new Intent(activity, BroadcastActivity.class);
            intent.putExtra(ExtraKeys.BROADCAST_ID, broadcastComment.getBroadcastId());
            intent.putExtra(ExtraKeys.BROADCAST_COMMENT, broadcastComment);
            activity.startActivity(intent);
        });
        holder.binding.avatar.setOnClickListener(v -> {
            Intent intent = new Intent(activity, ChannelActivity.class);
            intent.putExtra(ExtraKeys.ICHAT_ID, broadcastComment.getFromId());
            activity.startActivity(intent);
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    public void addItemsToEndAndShow(List<BroadcastComment> broadcastComments){
        broadcastComments.sort((o1, o2) -> - o1.getCommentTime().compareTo(o2.getCommentTime()));
        broadcastComments.forEach(broadcastComment -> itemDataList.add(new ItemData(broadcastComment)));
        notifyDataSetChanged();
    }

    public List<ItemData> getItemDataList() {
        return itemDataList;
    }
}
