package com.longx.intelligent.android.ichat2.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.activity.BroadcastActivity;
import com.longx.intelligent.android.ichat2.activity.ChannelActivity;
import com.longx.intelligent.android.ichat2.activity.ExtraKeys;
import com.longx.intelligent.android.ichat2.data.BroadcastComment;
import com.longx.intelligent.android.ichat2.databinding.RecyclerItemBroadcastRepliesInteractionBinding;
import com.longx.intelligent.android.ichat2.net.dataurl.NetDataUrls;
import com.longx.intelligent.android.ichat2.ui.glide.GlideApp;
import com.longx.intelligent.android.ichat2.util.TimeUtil;
import com.longx.intelligent.android.lib.recyclerview.WrappableRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LONG on 2024/10/10 at 下午10:19.
 */
public class BroadcastRepliesInteractionRecyclerAdapter extends WrappableRecyclerViewAdapter<BroadcastRepliesInteractionRecyclerAdapter.ViewHolder, BroadcastRepliesInteractionRecyclerAdapter.ItemData> {
    private final Activity activity;
    private final List<ItemData> itemDataList = new ArrayList<>();

    public BroadcastRepliesInteractionRecyclerAdapter(Activity activity) {
        this.activity = activity;
    }

    public static class ItemData {
        private final BroadcastComment broadcastReplyComment;

        public ItemData(BroadcastComment broadcastReplyComment) {
            this.broadcastReplyComment = broadcastReplyComment;
        }

        public BroadcastComment getBroadcastReplyComment() {
            return broadcastReplyComment;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final RecyclerItemBroadcastRepliesInteractionBinding binding;
        public ViewHolder(RecyclerItemBroadcastRepliesInteractionBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerItemBroadcastRepliesInteractionBinding binding = RecyclerItemBroadcastRepliesInteractionBinding.inflate(activity.getLayoutInflater());
        return new ViewHolder(binding);
    }

    @Override
    public int getItemCount() {
        return itemDataList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BroadcastComment broadcastReplyComment = itemDataList.get(position).broadcastReplyComment;
        if (broadcastReplyComment.getAvatarHash() == null) {
            GlideApp
                    .with(activity.getApplicationContext())
                    .load(R.drawable.default_avatar)
                    .into(holder.binding.avatar);
        } else {
            GlideApp
                    .with(activity.getApplicationContext())
                    .load(NetDataUrls.getAvatarUrl(activity, broadcastReplyComment.getAvatarHash()))
                    .into(holder.binding.avatar);
        }
        holder.binding.name.setText(broadcastReplyComment.getFromNameIncludeNote());
        holder.binding.time.setText(TimeUtil.formatRelativeTime(broadcastReplyComment.getCommentTime()));
        if(broadcastReplyComment.isNew()){
            holder.binding.badge.setVisibility(View.VISIBLE);
        }else {
            holder.binding.badge.setVisibility(View.GONE);
        }
        holder.binding.text.setText(broadcastReplyComment.getText());
        GlideApp.with(activity.getApplicationContext())
                .load(NetDataUrls.getBroadcastMediaDataUrl(activity, broadcastReplyComment.getCoverMediaId()))
                .centerCrop()
                .into(holder.binding.broadcastCover);
        holder.binding.broadcastTime.setText(TimeUtil.formatRelativeTime(broadcastReplyComment.getBroadcastTime()));
        holder.binding.broadcastText.setText(broadcastReplyComment.getToComment().getText());
        setupYiers(holder, position);
    }

    private void setupYiers(ViewHolder holder, int position) {
        BroadcastComment broadcastReplyComment = itemDataList.get(position).broadcastReplyComment;
        holder.binding.getRoot().setOnClickListener(v -> {
            Intent intent = new Intent(activity, BroadcastActivity.class);
            intent.putExtra(ExtraKeys.BROADCAST_ID, broadcastReplyComment.getBroadcastId());
            activity.startActivity(intent);
        });
        holder.binding.avatar.setOnClickListener(v -> {
            Intent intent = new Intent(activity, ChannelActivity.class);
            intent.putExtra(ExtraKeys.ICHAT_ID, broadcastReplyComment.getFromId());
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
