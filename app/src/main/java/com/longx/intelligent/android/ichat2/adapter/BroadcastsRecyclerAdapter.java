package com.longx.intelligent.android.ichat2.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.request.target.Target;
import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.behavior.GlideBehaviours;
import com.longx.intelligent.android.ichat2.da.database.manager.ChannelDatabaseManager;
import com.longx.intelligent.android.ichat2.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.ichat2.data.Broadcast;
import com.longx.intelligent.android.ichat2.data.BroadcastMedia;
import com.longx.intelligent.android.ichat2.data.Channel;
import com.longx.intelligent.android.ichat2.data.Self;
import com.longx.intelligent.android.ichat2.databinding.RecyclerItemBroadcastBinding;
import com.longx.intelligent.android.ichat2.net.dataurl.NetDataUrls;
import com.longx.intelligent.android.ichat2.ui.glide.GlideApp;
import com.longx.intelligent.android.ichat2.ui.glide.GlideRequest;
import com.longx.intelligent.android.ichat2.util.ResourceUtil;
import com.longx.intelligent.android.ichat2.util.TimeUtil;
import com.longx.intelligent.android.lib.recyclerview.WrappableRecyclerViewAdapter;

import java.util.Comparator;
import java.util.List;

/**
 * Created by LONG on 2024/7/29 at 下午12:13.
 */
public class BroadcastsRecyclerAdapter extends WrappableRecyclerViewAdapter<BroadcastsRecyclerAdapter.ViewHolder, BroadcastsRecyclerAdapter.ItemData> {
    private final Activity activity;
    private final List<ItemData> itemDataList;

    public BroadcastsRecyclerAdapter(Activity activity, List<ItemData> itemDataList) {
        this.activity = activity;
        sortItemDataList(itemDataList);
        this.itemDataList = itemDataList;
    }

    public static class ItemData{
        private final Broadcast broadcast;
        public ItemData(Broadcast broadcast) {
            this.broadcast = broadcast;
        }

        public Broadcast getBroadcast() {
            return broadcast;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final RecyclerItemBroadcastBinding binding;
        public ViewHolder(RecyclerItemBroadcastBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerItemBroadcastBinding binding = RecyclerItemBroadcastBinding.inflate(activity.getLayoutInflater());
        return new ViewHolder(binding);
    }

    @Override
    public int getItemCount() {
        return itemDataList.size();
    }

    public List<ItemData> getItemDataList() {
        return itemDataList;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemData itemData = itemDataList.get(position);
        String name = null;
        String avatarHash = null;
        Self currentUserProfile = SharedPreferencesAccessor.UserProfilePref.getCurrentUserProfile(activity);
        if(currentUserProfile.getIchatId().equals(itemData.broadcast.getIchatId())){
            name = currentUserProfile.getUsername();
            avatarHash = currentUserProfile.getAvatar() == null ? null : currentUserProfile.getAvatar().getHash();
        }else {
            Channel channel = ChannelDatabaseManager.getInstance().findOneChannel(itemData.broadcast.getIchatId());
            if(channel != null) {
                name = channel.getName();
                avatarHash = channel.getAvatar() == null ? null : channel.getAvatar().getHash();
            }
        }
        holder.binding.name.setText(name);
        if (avatarHash == null) {
            GlideApp
                    .with(activity.getApplicationContext())
                    .asBitmap()
                    .load(R.drawable.default_avatar)
                    .into(holder.binding.avatar);
        } else {
            GlideApp
                    .with(activity.getApplicationContext())
                    .asBitmap()
                    .load(NetDataUrls.getAvatarUrl(activity, avatarHash))
                    .into(holder.binding.avatar);
        }
        holder.binding.time.setText(TimeUtil.formatRelativeTime(itemData.broadcast.getTime()));
        if(itemData.broadcast.getText() != null) {
            holder.binding.text.setVisibility(View.VISIBLE);
            holder.binding.text.setText(itemData.broadcast.getText());
        }else {
            holder.binding.text.setVisibility(View.GONE);
        }
        for (int i = 0; i < 12; i++) {
            int resId = ResourceUtil.getResId("media_" + (i + 1), R.id.class);
            AppCompatImageView imageView = holder.binding.medias.findViewById(resId);
            imageView.setVisibility(View.GONE);
        }
        List<BroadcastMedia> broadcastMedias = itemData.broadcast.getBroadcastMedias();
        if(broadcastMedias != null && !broadcastMedias.isEmpty()){
            holder.binding.medias.setVisibility(View.VISIBLE);
            broadcastMedias.sort(Comparator.comparingInt(BroadcastMedia::getIndex));
            for (int i = 0; i < broadcastMedias.size(); i++) {
                BroadcastMedia broadcastMedia = broadcastMedias.get(i);
                switch (broadcastMedia.getType()){
                    case BroadcastMedia.TYPE_IMAGE:{
                        int resId = ResourceUtil.getResId("media_" + (i + 1), R.id.class);
                        AppCompatImageView imageView = holder.binding.medias.findViewById(resId);
                        if(imageView == null) break;
                        imageView.setVisibility(View.VISIBLE);
                        GlideApp
                                .with(activity.getApplicationContext())
                                .load(NetDataUrls.getBroadcastMediaDataUrl(activity, broadcastMedia.getMediaId()))
                                .centerCrop()
                                .into(imageView);
                        break;
                    }
                    case BroadcastMedia.TYPE_VIDEO:{

                        break;
                    }
                }
            }
        }else {
            holder.binding.medias.setVisibility(View.GONE);
        }
    }

    private void sortItemDataList(List<ItemData> itemDataList){
        itemDataList.sort((o1, o2) -> - o1.broadcast.getTime().compareTo(o2.broadcast.getTime()));
    }

    @SuppressLint("NotifyDataSetChanged")
    public void clearAndShow(){
        itemDataList.clear();
        notifyDataSetChanged();
    }

    public void addItemsAndShow(List<ItemData> items){
        sortItemDataList(items);
        int insertPosition = itemDataList.size();
        itemDataList.addAll(insertPosition, items);
        notifyItemRangeInserted(insertPosition + 1, items.size());
    }
}
