package com.longx.intelligent.android.imessage.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.longx.intelligent.android.imessage.activity.ChannelActivity;
import com.longx.intelligent.android.imessage.activity.ExtraKeys;
import com.longx.intelligent.android.imessage.activity.GroupChannelActivity;
import com.longx.intelligent.android.imessage.data.Channel;
import com.longx.intelligent.android.imessage.data.GroupChannel;
import com.longx.intelligent.android.imessage.data.GroupChannelNotification;
import com.longx.intelligent.android.imessage.data.request.ViewGroupChannelNotificationsPostBody;
import com.longx.intelligent.android.imessage.databinding.RecyclerItemGroupChannelNotificationBinding;
import com.longx.intelligent.android.imessage.net.dataurl.NetDataUrls;
import com.longx.intelligent.android.imessage.net.retrofit.caller.ChannelApiCaller;
import com.longx.intelligent.android.imessage.net.retrofit.caller.GroupChannelApiCaller;
import com.longx.intelligent.android.imessage.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.imessage.util.ErrorLogger;
import com.longx.intelligent.android.imessage.util.TimeUtil;
import com.longx.intelligent.android.imessage.value.Constants;
import com.longx.intelligent.android.lib.recyclerview.WrappableRecyclerViewAdapter;

import java.util.Date;
import java.util.List;

/**
 * Created by LONG on 2025/6/13 at 下午10:51.
 */
public class GroupChannelNotificationsRecyclerAdapter extends WrappableRecyclerViewAdapter<GroupChannelNotificationsRecyclerAdapter.ViewHolder, GroupChannelNotificationsRecyclerAdapter.ItemData> {
    private final Activity activity;
    private final List<ItemData> itemDataList;

    public GroupChannelNotificationsRecyclerAdapter(Activity activity, List<ItemData> itemDataList) {
        this.activity = activity;
        this.itemDataList = itemDataList;
    }

    public static class ItemData {
        private final GroupChannelNotification groupChannelNotification;

        public ItemData(GroupChannelNotification groupChannelNotification) {
            this.groupChannelNotification = groupChannelNotification;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final RecyclerItemGroupChannelNotificationBinding binding;

        public ViewHolder(RecyclerItemGroupChannelNotificationBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerItemGroupChannelNotificationBinding binding = RecyclerItemGroupChannelNotificationBinding.inflate(activity.getLayoutInflater(), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public int getItemCount() {
        return itemDataList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemData itemData = itemDataList.get(position);
        Glide.with(activity.getApplicationContext())
                .load(NetDataUrls.getAvatarUrl(activity, itemData.groupChannelNotification.getChannel().getAvatar().getHash()))
                .centerCrop()
                .into(holder.binding.channelAvatar);
        Glide.with(activity.getApplicationContext())
                .load(NetDataUrls.getGroupAvatarUrl(activity, itemData.groupChannelNotification.getGroupChannel().getGroupAvatar().getHash()))
                .centerCrop()
                .into(holder.binding.groupChannelAvatar);
        String text = null;
        switch (itemData.groupChannelNotification.getType()){
            case PASSIVE_DISCONNECT:
                Glide.with(activity.getApplicationContext())
                        .load(NetDataUrls.getAvatarUrl(activity, itemData.groupChannelNotification.getByChannel().getAvatar().getHash()))
                        .centerCrop()
                        .into(holder.binding.byAvatar);
                text = itemData.groupChannelNotification.getChannel().autoGetName() + " 被 " + itemData.groupChannelNotification.getByChannel().autoGetName() + " 移除了群聊。";
                break;
            case ACTIVE_DISCONNECT:
                text = itemData.groupChannelNotification.getChannel().autoGetName() + " 离开了群聊。";
                break;
        }
        holder.binding.text.setText(text);
        holder.binding.time.setText(TimeUtil.formatRelativeTime(itemData.groupChannelNotification.getTime()));
        if(itemData.groupChannelNotification.isViewed()){
            holder.binding.badge.setVisibility(View.GONE);
        }else {
            holder.binding.badge.setVisibility(View.VISIBLE);
        }
        holder.binding.channelName.setText(itemData.groupChannelNotification.getChannel().autoGetName());
        holder.binding.groupChannelName.setText(itemData.groupChannelNotification.getGroupChannel().getName());
        if(!itemData.groupChannelNotification.isViewed()) {
            ViewGroupChannelNotificationsPostBody postBody = new ViewGroupChannelNotificationsPostBody(List.of(itemData.groupChannelNotification.getUuid()));
            GroupChannelApiCaller.viewGroupChannelNotifications(null, postBody, new RetrofitApiCaller.CommonYier<>(activity, false, true));
        }
        setupYiers(holder, position);
    }

    private void setupYiers(ViewHolder holder, int position) {
        ItemData itemData = itemDataList.get(position);
        holder.binding.getRoot().setOnClickListener(v -> {
            Intent intent = new Intent(activity, ChannelActivity.class);
            intent.putExtra(ExtraKeys.CHANNEL, itemData.groupChannelNotification.getChannel());
            activity.startActivity(intent);
        });
        holder.binding.byAvatar.setOnClickListener(v -> {
            Intent intent = new Intent(activity, ChannelActivity.class);
            intent.putExtra(ExtraKeys.CHANNEL, itemData.groupChannelNotification.getByChannel());
            activity.startActivity(intent);
        });
        holder.binding.layoutGroupChannel.setOnClickListener(v -> {
            Intent intent = new Intent(activity, GroupChannelActivity.class);
            intent.putExtra(ExtraKeys.GROUP_CHANNEL, itemData.groupChannelNotification.getGroupChannel());
            activity.startActivity(intent);
        });
    }
}
