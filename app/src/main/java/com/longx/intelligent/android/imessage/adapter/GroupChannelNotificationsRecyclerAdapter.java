package com.longx.intelligent.android.imessage.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.imessage.activity.ChannelActivity;
import com.longx.intelligent.android.imessage.activity.ExtraKeys;
import com.longx.intelligent.android.imessage.activity.GroupChannelActivity;
import com.longx.intelligent.android.imessage.data.Avatar;
import com.longx.intelligent.android.imessage.data.Channel;
import com.longx.intelligent.android.imessage.data.GroupAvatar;
import com.longx.intelligent.android.imessage.data.GroupChannel;
import com.longx.intelligent.android.imessage.data.GroupChannelNotification;
import com.longx.intelligent.android.imessage.data.request.ViewGroupChannelNotificationsPostBody;
import com.longx.intelligent.android.imessage.databinding.RecyclerItemGroupChannelNotificationBinding;
import com.longx.intelligent.android.imessage.net.dataurl.NetDataUrls;
import com.longx.intelligent.android.imessage.net.retrofit.caller.ChannelApiCaller;
import com.longx.intelligent.android.imessage.net.retrofit.caller.GroupChannelApiCaller;
import com.longx.intelligent.android.imessage.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.imessage.ui.glide.GlideApp;
import com.longx.intelligent.android.imessage.util.ErrorLogger;
import com.longx.intelligent.android.imessage.util.TimeUtil;
import com.longx.intelligent.android.imessage.value.Constants;
import com.longx.intelligent.android.imessage.yier.ResultsYier;
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
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        ItemData itemData = itemDataList.get(position);
        itemData.groupChannelNotification.getChannel((AppCompatActivity) activity, results -> {
            Channel channel = (Channel) results[0];
            if(channel != null) {
                if (channel.getAvatar() == null || channel.getAvatar().getHash() == null) {
                    GlideApp
                            .with(activity.getApplicationContext())
                            .load(R.drawable.default_avatar)
                            .into(holder.binding.channelAvatar);
                } else {
                    Glide.with(activity.getApplicationContext())
                            .load(NetDataUrls.getAvatarUrl(activity, channel.getAvatar().getHash()))
                            .centerCrop()
                            .into(holder.binding.channelAvatar);
                }
                showText(holder, itemData.groupChannelNotification);
                holder.binding.channelName.setText(channel.autoGetName());
            }
        });
        itemData.groupChannelNotification.getGroupChannel((AppCompatActivity) activity, results -> {
            GroupChannel groupChannel = (GroupChannel) results[0];
            if(groupChannel != null) {
                if (groupChannel.getGroupAvatar() == null || groupChannel.getGroupAvatar().getHash() == null) {
                    GlideApp
                            .with(activity.getApplicationContext())
                            .load(R.drawable.group_channel_default_avatar)
                            .into(holder.binding.groupChannelAvatar);
                } else {
                    Glide.with(activity.getApplicationContext())
                            .load(NetDataUrls.getGroupAvatarUrl(activity, groupChannel.getGroupAvatar().getHash()))
                            .centerCrop()
                            .into(holder.binding.groupChannelAvatar);
                }
                holder.binding.groupChannelName.setText(groupChannel.getName());
            }
        });
        switch (itemData.groupChannelNotification.getType()){
            case PASSIVE_DISCONNECT:
                itemData.groupChannelNotification.getByChannel((AppCompatActivity) activity, results -> {
                    Channel byChannel = (Channel) results[0];
                    if(byChannel != null) {
                        if (byChannel.getAvatar() == null || byChannel.getAvatar().getHash() == null) {
                            GlideApp
                                    .with(activity.getApplicationContext())
                                    .load(R.drawable.default_avatar)
                                    .into(holder.binding.byAvatar);
                        } else {
                            Glide.with(activity.getApplicationContext())
                                    .load(NetDataUrls.getAvatarUrl(activity, byChannel.getAvatar().getHash()))
                                    .centerCrop()
                                    .into(holder.binding.byAvatar);
                        }
                        showText(holder, itemData.groupChannelNotification);
                    }
                });
                break;
            case ACTIVE_DISCONNECT:
                break;
        }
        holder.binding.time.setText(TimeUtil.formatRelativeTime(itemData.groupChannelNotification.getTime()));
        if(itemData.groupChannelNotification.isViewed()){
            holder.binding.badge.setVisibility(View.GONE);
        }else {
            holder.binding.badge.setVisibility(View.VISIBLE);
        }
        if(!itemData.groupChannelNotification.isViewed()) {
            ViewGroupChannelNotificationsPostBody postBody = new ViewGroupChannelNotificationsPostBody(List.of(itemData.groupChannelNotification.getUuid()));
            GroupChannelApiCaller.viewGroupChannelNotifications(null, postBody, new RetrofitApiCaller.CommonYier<>(activity, false, true));
        }
        setupYiers(holder, position);
    }

    private void showText(ViewHolder holder, GroupChannelNotification groupChannelNotification){
        String text = null;
        switch (groupChannelNotification.getType()) {
            case PASSIVE_DISCONNECT:
                if(groupChannelNotification.getChannel() != null && groupChannelNotification.getByChannel() != null) {
                    text = groupChannelNotification.getChannel().autoGetName() + " 被 " + groupChannelNotification.getByChannel().autoGetName() + " 移除了群聊。";
                }
                break;
            case ACTIVE_DISCONNECT:
                if(groupChannelNotification.getChannel() != null) {
                    text = groupChannelNotification.getChannel().autoGetName() + " 离开了群聊。";
                }
                break;
        }
        holder.binding.text.setText(text);
    }

    private void setupYiers(ViewHolder holder, int position) {
        ItemData itemData = itemDataList.get(position);
        holder.binding.getRoot().setOnClickListener(v -> {
            Channel channel = itemData.groupChannelNotification.getChannel();
            if(channel != null) {
                Intent intent = new Intent(activity, ChannelActivity.class);
                intent.putExtra(ExtraKeys.CHANNEL, channel);
                activity.startActivity(intent);
            }
        });
        holder.binding.byAvatar.setOnClickListener(v -> {
            Channel byChannel = itemData.groupChannelNotification.getByChannel();
            if(byChannel != null) {
                Intent intent = new Intent(activity, ChannelActivity.class);
                intent.putExtra(ExtraKeys.CHANNEL, byChannel);
                activity.startActivity(intent);
            }
        });
        holder.binding.layoutGroupChannel.setOnClickListener(v -> {
            GroupChannel groupChannel = itemData.groupChannelNotification.getGroupChannel();
            if(groupChannel != null) {
                Intent intent = new Intent(activity, GroupChannelActivity.class);
                intent.putExtra(ExtraKeys.GROUP_CHANNEL, groupChannel);
                activity.startActivity(intent);
            }
        });
    }
}
