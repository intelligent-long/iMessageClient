package com.longx.intelligent.android.ichat2.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.activity.BroadcastActivity;
import com.longx.intelligent.android.ichat2.activity.ExtraKeys;
import com.longx.intelligent.android.ichat2.behavior.GlideBehaviours;
import com.longx.intelligent.android.ichat2.da.database.manager.ChannelDatabaseManager;
import com.longx.intelligent.android.ichat2.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.ichat2.data.Broadcast;
import com.longx.intelligent.android.ichat2.data.BroadcastMedia;
import com.longx.intelligent.android.ichat2.data.Channel;
import com.longx.intelligent.android.ichat2.data.Self;
import com.longx.intelligent.android.ichat2.data.Size;
import com.longx.intelligent.android.ichat2.databinding.RecyclerItemBroadcastBinding;
import com.longx.intelligent.android.ichat2.net.dataurl.NetDataUrls;
import com.longx.intelligent.android.ichat2.ui.glide.GlideApp;
import com.longx.intelligent.android.ichat2.ui.glide.GlideRequest;
import com.longx.intelligent.android.ichat2.util.ErrorLogger;
import com.longx.intelligent.android.ichat2.util.ResourceUtil;
import com.longx.intelligent.android.ichat2.util.TimeUtil;
import com.longx.intelligent.android.ichat2.util.UiUtil;
import com.longx.intelligent.android.ichat2.value.Constants;
import com.longx.intelligent.android.lib.recyclerview.WrappableRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by LONG on 2024/7/29 at 下午12:13.
 */
public class BroadcastsRecyclerAdapter extends WrappableRecyclerViewAdapter<BroadcastsRecyclerAdapter.ViewHolder, BroadcastsRecyclerAdapter.ItemData> {
    private final Activity activity;
    private final List<ItemData> itemDataList;
    private final Map<String, Size> singleMediaViewSizeMap= new HashMap<>();

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
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
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
            holder.binding.media12Layout.setVisibility(View.GONE);
            holder.binding.darkCover.setVisibility(View.GONE);
            holder.binding.moreIcon.setVisibility(View.GONE);
        }
        for (int i = 0; i < 4; i++) {
            int resId = ResourceUtil.getResId("media_2_to_4_" + (i + 1), R.id.class);
            AppCompatImageView imageView = holder.binding.medias2To4.findViewById(resId);
            imageView.setVisibility(View.GONE);
        }
        holder.binding.media11.setVisibility(View.GONE);
        List<BroadcastMedia> broadcastMedias = itemData.broadcast.getBroadcastMedias();
        if(broadcastMedias != null && !broadcastMedias.isEmpty()){
            holder.binding.mediasFrame.setVisibility(View.VISIBLE);
            broadcastMedias.sort(Comparator.comparingInt(BroadcastMedia::getIndex));
            if(broadcastMedias.size() > 4) {
                holder.binding.medias.setVisibility(View.VISIBLE);
                holder.binding.medias2To4.setVisibility(View.GONE);
                holder.binding.media11.setVisibility(View.GONE);
                int forTimes = Math.min(12, broadcastMedias.size());
                for (int i = 0; i < forTimes; i++) {
                    BroadcastMedia broadcastMedia = broadcastMedias.get(i);
                    switch (broadcastMedia.getType()) {
                        case BroadcastMedia.TYPE_IMAGE: {
                            int resId = ResourceUtil.getResId("media_" + (i + 1), R.id.class);
                            AppCompatImageView imageView = holder.binding.medias.findViewById(resId);
                            imageView.setVisibility(View.VISIBLE);
                            GlideApp
                                    .with(activity.getApplicationContext())
                                    .load(NetDataUrls.getBroadcastMediaDataUrl(activity, broadcastMedia.getMediaId()))
                                    .centerCrop()
                                    .into(imageView);
                            if (i == 11) holder.binding.media12Layout.setVisibility(View.VISIBLE);
                            if (broadcastMedias.size() > 12) {
                                holder.binding.darkCover.setVisibility(View.VISIBLE);
                                holder.binding.moreIcon.setVisibility(View.VISIBLE);
                            }
                            break;
                        }
                        case BroadcastMedia.TYPE_VIDEO: {

                            break;
                        }
                    }
                }
            }else if(broadcastMedias.size() > 1){
                holder.binding.medias.setVisibility(View.GONE);
                holder.binding.medias2To4.setVisibility(View.VISIBLE);
                holder.binding.media11.setVisibility(View.GONE);
                int forTimes = Math.min(4, broadcastMedias.size());
                for (int i = 0; i < forTimes; i++) {
                    BroadcastMedia broadcastMedia = broadcastMedias.get(i);
                    switch (broadcastMedia.getType()) {
                        case BroadcastMedia.TYPE_IMAGE: {
                            int resId = ResourceUtil.getResId("media_2_to_4_" + (i + 1), R.id.class);
                            AppCompatImageView imageView = holder.binding.medias2To4.findViewById(resId);
                            imageView.setVisibility(View.VISIBLE);
                            GlideApp
                                    .with(activity.getApplicationContext())
                                    .load(NetDataUrls.getBroadcastMediaDataUrl(activity, broadcastMedia.getMediaId()))
                                    .centerCrop()
                                    .into(imageView);
                            break;
                        }
                        case BroadcastMedia.TYPE_VIDEO: {

                            break;
                        }
                    }
                }
            }else {
                holder.binding.medias.setVisibility(View.GONE);
                holder.binding.medias2To4.setVisibility(View.GONE);
                holder.binding.media11.setVisibility(View.VISIBLE);
                Size size = broadcastMedias.get(0).getSize();
                if(size != null) {
                    boolean successShow = showSingleMedia(holder, position);
                    if(!successShow) {
                        holder.binding.mediasFrame.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                            @Override
                            public void onGlobalLayout() {
                                holder.binding.mediasFrame.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                                Size viewSize = calculateViewSize(holder, position);
                                singleMediaViewSizeMap.put(broadcastMedias.get(0).getMediaId(), viewSize);
                                showSingleMedia(holder, position);
                            }
                        });
                    }
                } else {
                    GlideApp
                            .with(activity.getApplicationContext())
                            .load(NetDataUrls.getBroadcastMediaDataUrl(activity, broadcastMedias.get(0).getMediaId()))
                            .into(new CustomTarget<Drawable>() {
                                @Override
                                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                    ViewGroup.LayoutParams layoutParams = holder.binding.media11.getLayoutParams();
                                    layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                                    layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                                    holder.binding.media11.setLayoutParams(layoutParams);
                                    holder.binding.media11.setImageDrawable(resource);
                                }

                                @Override
                                public void onLoadCleared(@Nullable Drawable placeholder) {

                                }
                            });
                }
            }
        }else {
            holder.binding.mediasFrame.setVisibility(View.GONE);
            holder.binding.medias.setVisibility(View.GONE);
            holder.binding.medias2To4.setVisibility(View.GONE);
            holder.binding.media11.setVisibility(View.GONE);
        }

        setupYiers(holder, position);
    }

    private boolean showSingleMedia(ViewHolder holder, int position) {
        List<BroadcastMedia> broadcastMedias = itemDataList.get(position).broadcast.getBroadcastMedias();
        Size size = singleMediaViewSizeMap.get(broadcastMedias.get(0).getMediaId());
        if(size == null) return false;

        ViewGroup.LayoutParams layoutParams = holder.binding.media11.getLayoutParams();
        layoutParams.width = size.getWidth();
        layoutParams.height = size.getHeight();
        holder.binding.media11.setLayoutParams(layoutParams);
        GlideApp
                .with(activity.getApplicationContext())
                .load(NetDataUrls.getBroadcastMediaDataUrl(activity, broadcastMedias.get(0).getMediaId()))
                .override(size.getWidth(), size.getHeight())
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        holder.binding.media11.setImageDrawable(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }
                });
        return true;
    }

    private Size calculateViewSize(ViewHolder holder, int position){
        List<BroadcastMedia> broadcastMedias = itemDataList.get(position).broadcast.getBroadcastMedias();
        int viewWidth = holder.binding.mediasFrame.getWidth();
        int imageViewMaxWidth = UiUtil.pxToDp(activity, viewWidth - UiUtil.dpToPx(activity, Constants.SINGLE_BROADCAST_IMAGE_VIEW_MARGIN_END_DP));
        Size size = broadcastMedias.get(0).getSize();
        int imageWidth = size.getWidth();
        int imageHeight = size.getHeight();
        int viewHeight;
        if (imageWidth / (double) imageHeight > imageViewMaxWidth / (double) Constants.SINGLE_BROADCAST_IMAGE_VIEW_MAX_HEIGHT_DP) {
            viewWidth = UiUtil.dpToPx(activity, imageViewMaxWidth);
            viewHeight = (int) Math.round((viewWidth / (double) imageWidth) * imageHeight);
        } else {
            viewHeight = UiUtil.dpToPx(activity, Constants.SINGLE_BROADCAST_IMAGE_VIEW_MAX_HEIGHT_DP);
            viewWidth = (int) Math.round((viewHeight / (double) imageHeight) * imageWidth);
        }
        return new Size(viewWidth, viewHeight);
    }

    private void setupYiers(ViewHolder holder, int position) {
        ItemData itemData = itemDataList.get(position);
        holder.binding.getRoot().setOnClickListener(v -> {
            Intent intent = new Intent(activity, BroadcastActivity.class);
            intent.putExtra(ExtraKeys.BROADCAST, itemData.broadcast);
            activity.startActivity(intent);
        });
    }

    private void sortItemDataList(List<ItemData> itemDataList){
        itemDataList.sort((o1, o2) -> - o1.broadcast.getTime().compareTo(o2.broadcast.getTime()));
    }

    @SuppressLint("NotifyDataSetChanged")
    public void clearAndShow(){
        singleMediaViewSizeMap.clear();
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
