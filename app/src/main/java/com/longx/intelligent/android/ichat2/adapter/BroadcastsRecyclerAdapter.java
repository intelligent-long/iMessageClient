package com.longx.intelligent.android.ichat2.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.activity.BroadcastActivity;
import com.longx.intelligent.android.ichat2.activity.ChannelActivity;
import com.longx.intelligent.android.ichat2.activity.ExtraKeys;
import com.longx.intelligent.android.ichat2.behavior.MessageDisplayer;
import com.longx.intelligent.android.ichat2.bottomsheet.BroadcastMoreOperationBottomSheet;
import com.longx.intelligent.android.ichat2.da.database.manager.ChannelDatabaseManager;
import com.longx.intelligent.android.ichat2.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.ichat2.data.Broadcast;
import com.longx.intelligent.android.ichat2.data.BroadcastMedia;
import com.longx.intelligent.android.ichat2.data.Channel;
import com.longx.intelligent.android.ichat2.data.Self;
import com.longx.intelligent.android.ichat2.data.Size;
import com.longx.intelligent.android.ichat2.data.response.OperationStatus;
import com.longx.intelligent.android.ichat2.databinding.RecyclerItemBroadcastBinding;
import com.longx.intelligent.android.ichat2.dialog.ConfirmDialog;
import com.longx.intelligent.android.ichat2.net.dataurl.NetDataUrls;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.BroadcastApiCaller;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.ichat2.ui.NoPaddingTextView;
import com.longx.intelligent.android.ichat2.ui.SquareFrameLayout;
import com.longx.intelligent.android.ichat2.ui.glide.GlideApp;
import com.longx.intelligent.android.ichat2.util.TimeUtil;
import com.longx.intelligent.android.ichat2.util.UiUtil;
import com.longx.intelligent.android.ichat2.value.Constants;
import com.longx.intelligent.android.ichat2.yier.BroadcastDeletedYier;
import com.longx.intelligent.android.ichat2.yier.GlobalYiersHolder;
import com.longx.intelligent.android.lib.recyclerview.WrappableRecyclerViewAdapter;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by LONG on 2024/7/29 at 下午12:13.
 */
public class BroadcastsRecyclerAdapter extends WrappableRecyclerViewAdapter<BroadcastsRecyclerAdapter.ViewHolder, BroadcastsRecyclerAdapter.ItemData> {
    private final Activity activity;
    private final com.longx.intelligent.android.lib.recyclerview.RecyclerView recyclerView;
    private final List<ItemData> itemDataList;
    private final Map<String, Size> singleMediaViewSizeMap= new HashMap<>();
    private final Self currentUserProfile;

    public BroadcastsRecyclerAdapter(Activity activity, com.longx.intelligent.android.lib.recyclerview.RecyclerView recyclerView, List<ItemData> itemDataList) {
        this.activity = activity;
        this.recyclerView = recyclerView;
        sortItemDataList(itemDataList);
        this.itemDataList = itemDataList;
        currentUserProfile = SharedPreferencesAccessor.UserProfilePref.getCurrentUserProfile(activity);
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
            switch (i){
                case 0:
                    holder.binding.layoutMedia1.setVisibility(View.GONE);
                    break;
                case 1:
                    holder.binding.layoutMedia2.setVisibility(View.GONE);
                    break;
                case 2:
                    holder.binding.layoutMedia3.setVisibility(View.GONE);
                    break;
                case 3:
                    holder.binding.layoutMedia4.setVisibility(View.GONE);
                    break;
                case 4:
                    holder.binding.layoutMedia5.setVisibility(View.GONE);
                    break;
                case 5:
                    holder.binding.layoutMedia6.setVisibility(View.GONE);
                    break;
                case 6:
                    holder.binding.layoutMedia7.setVisibility(View.GONE);
                    break;
                case 7:
                    holder.binding.layoutMedia8.setVisibility(View.GONE);
                    break;
                case 8:
                    holder.binding.layoutMedia9.setVisibility(View.GONE);
                    break;
                case 9:
                    holder.binding.layoutMedia10.setVisibility(View.GONE);
                    break;
                case 10:
                    holder.binding.layoutMedia11.setVisibility(View.GONE);
                    break;
                case 11:
                    holder.binding.layoutMedia12.setVisibility(View.GONE);
                    break;
            }
        }
        for (int i = 0; i < 4; i++) {
            switch (i){
                case 0:
                    holder.binding.layoutMedia2To41.setVisibility(View.GONE);
                    break;
                case 1:
                    holder.binding.layoutMedia2To42.setVisibility(View.GONE);
                    break;
                case 2:
                    holder.binding.layoutMedia2To43.setVisibility(View.GONE);
                    break;
                case 3:
                    holder.binding.layoutMedia2To44.setVisibility(View.GONE);
                    break;
            }
        }
        holder.binding.mediaSingle.setVisibility(View.GONE);
        List<BroadcastMedia> broadcastMedias = itemData.broadcast.getBroadcastMedias();
        if(broadcastMedias != null && !broadcastMedias.isEmpty()){
            holder.binding.mediasFrame.setVisibility(View.VISIBLE);
            broadcastMedias.sort(Comparator.comparingInt(BroadcastMedia::getIndex));
            if(broadcastMedias.size() > 4) {
                holder.binding.medias.setVisibility(View.VISIBLE);
                holder.binding.medias2To4.setVisibility(View.GONE);
                holder.binding.mediaSingle.setVisibility(View.GONE);
                int forTimes = Math.min(12, broadcastMedias.size());
                for (int i = 0; i < forTimes; i++) {
                    setupImage(holder, i, broadcastMedias);
                    setupVideoDuration(holder, i, broadcastMedias);
                }
            }else if(broadcastMedias.size() > 1){
                holder.binding.medias.setVisibility(View.GONE);
                holder.binding.medias2To4.setVisibility(View.VISIBLE);
                holder.binding.mediaSingle.setVisibility(View.GONE);
                for (int i = 0; i < broadcastMedias.size(); i++) {
                    setupImage2To4(holder, i, broadcastMedias);
                    setupVideoDuration2To4(holder, i, broadcastMedias);
                }
            }else {
                holder.binding.mediaSingle.setImageDrawable(null);
                holder.binding.medias.setVisibility(View.GONE);
                holder.binding.medias2To4.setVisibility(View.GONE);
                holder.binding.mediaSingle.setVisibility(View.VISIBLE);
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
                                    ViewGroup.LayoutParams layoutParams = holder.binding.mediaSingle.getLayoutParams();
                                    layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                                    layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                                    holder.binding.mediaSingle.setLayoutParams(layoutParams);
                                    holder.binding.mediaSingle.setImageDrawable(resource);
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
            holder.binding.mediaSingle.setVisibility(View.GONE);
        }

        setupYiers(holder, position);
    }

    private void setupImage(@NonNull ViewHolder holder, int i, List<BroadcastMedia> broadcastMedias) {
        BroadcastMedia broadcastMedia = broadcastMedias.get(i);
        AppCompatImageView imageView = null;
        SquareFrameLayout layout = null;
        switch (i){
            case 0:
                imageView = holder.binding.media1;
                layout = holder.binding.layoutMedia1;
                break;
            case 1:
                imageView = holder.binding.media2;
                layout = holder.binding.layoutMedia2;
                break;
            case 2:
                imageView = holder.binding.media3;
                layout = holder.binding.layoutMedia3;
                break;
            case 3:
                imageView = holder.binding.media4;
                layout = holder.binding.layoutMedia4;
                break;
            case 4:
                imageView = holder.binding.media5;
                layout = holder.binding.layoutMedia5;
                break;
            case 5:
                imageView = holder.binding.media6;
                layout = holder.binding.layoutMedia6;
                break;
            case 6:
                imageView = holder.binding.media7;
                layout = holder.binding.layoutMedia7;
                break;
            case 7:
                imageView = holder.binding.media8;
                layout = holder.binding.layoutMedia8;
                break;
            case 8:
                imageView = holder.binding.media9;
                layout = holder.binding.layoutMedia9;
                break;
            case 9:
                imageView = holder.binding.media10;
                layout = holder.binding.layoutMedia10;
                break;
            case 10:
                imageView = holder.binding.media11;
                layout = holder.binding.layoutMedia11;
                break;
            case 11:
                imageView = holder.binding.media12;
                layout = holder.binding.layoutMedia12;
                if (broadcastMedias.size() > 12) {
                    holder.binding.darkCover.setVisibility(View.VISIBLE);
                    holder.binding.moreIcon.setVisibility(View.VISIBLE);
                }
                break;
        }
        layout.setVisibility(View.VISIBLE);
        if(broadcastMedia.getType() == BroadcastMedia.TYPE_IMAGE) {
            GlideApp
                    .with(activity.getApplicationContext())
                    .load(NetDataUrls.getBroadcastMediaDataUrl(activity, broadcastMedia.getMediaId()))
                    .centerCrop()
                    .into(imageView);
        }else if(broadcastMedia.getType() == BroadcastMedia.TYPE_VIDEO){
            GlideApp
                    .with(activity.getApplicationContext())
                    .load(NetDataUrls.getBroadcastMediaDataUrl(activity, broadcastMedia.getMediaId()))
                    .frame(1000_000)
                    .centerCrop()
                    .into(imageView);
        }
    }

    private void setupVideoDuration(ViewHolder holder, int i, List<BroadcastMedia> broadcastMedias) {
        BroadcastMedia broadcastMedia = broadcastMedias.get(i);
        NoPaddingTextView videoDuration = null;
        switch (i){
            case 0:
                videoDuration = holder.binding.videoDuration1;
                break;
            case 1:
                videoDuration = holder.binding.videoDuration2;
                break;
            case 2:
                videoDuration = holder.binding.videoDuration3;
                break;
            case 3:
                videoDuration = holder.binding.videoDuration4;
                break;
            case 4:
                videoDuration = holder.binding.videoDuration5;
                break;
            case 5:
                videoDuration = holder.binding.videoDuration6;
                break;
            case 6:
                videoDuration = holder.binding.videoDuration7;
                break;
            case 7:
                videoDuration = holder.binding.videoDuration8;
                break;
            case 8:
                videoDuration = holder.binding.videoDuration9;
                break;
            case 9:
                videoDuration = holder.binding.videoDuration10;
                break;
            case 10:
                videoDuration = holder.binding.videoDuration11;
                break;
            case 11:
                videoDuration = holder.binding.videoDuration12;
                break;
        }
        if(broadcastMedia.getType() == BroadcastMedia.TYPE_IMAGE) {
            videoDuration.setVisibility(View.GONE);
        }else if(broadcastMedia.getType() == BroadcastMedia.TYPE_VIDEO){
            videoDuration.setVisibility(View.VISIBLE);
            videoDuration.bringToFront();
            if(broadcastMedia.getVideoDuration() != null) {
                videoDuration.setText(TimeUtil.formatTime(broadcastMedia.getVideoDuration()));
            }else {
                videoDuration.setText("video");
            }
        }
    }

    private void setupImage2To4(@NonNull ViewHolder holder, int i, List<BroadcastMedia> broadcastMedias) {
        BroadcastMedia broadcastMedia = broadcastMedias.get(i);
        AppCompatImageView imageView = null;
        SquareFrameLayout layout = null;
        switch (i){
            case 0:
                imageView = holder.binding.media2To41;
                layout = holder.binding.layoutMedia2To41;
                break;
            case 1:
                imageView = holder.binding.media2To42;
                layout = holder.binding.layoutMedia2To42;
                break;
            case 2:
                imageView = holder.binding.media2To43;
                layout = holder.binding.layoutMedia2To43;
                break;
            case 3:
                imageView = holder.binding.media2To44;
                layout = holder.binding.layoutMedia2To44;
                break;
        }
        layout.setVisibility(View.VISIBLE);
        if(broadcastMedia.getType() == BroadcastMedia.TYPE_IMAGE) {
            GlideApp
                    .with(activity.getApplicationContext())
                    .load(NetDataUrls.getBroadcastMediaDataUrl(activity, broadcastMedia.getMediaId()))
                    .centerCrop()
                    .into(imageView);
        }else if(broadcastMedia.getType() == BroadcastMedia.TYPE_VIDEO){
            GlideApp
                    .with(activity.getApplicationContext())
                    .load(NetDataUrls.getBroadcastMediaDataUrl(activity, broadcastMedia.getMediaId()))
                    .frame(1000_000)
                    .centerCrop()
                    .into(imageView);
        }
    }

    private void setupVideoDuration2To4(@NonNull ViewHolder holder, int i, List<BroadcastMedia> broadcastMedias) {
        BroadcastMedia broadcastMedia = broadcastMedias.get(i);
        NoPaddingTextView videoDuration = null;
        switch (i) {
            case 0:
                videoDuration = holder.binding.videoDuration2To41;
                break;
            case 1:
                videoDuration = holder.binding.videoDuration2To42;
                break;
            case 2:
                videoDuration = holder.binding.videoDuration2To43;
                break;
            case 3:
                videoDuration = holder.binding.videoDuration2To44;
                break;
        }
        if(broadcastMedia.getType() == BroadcastMedia.TYPE_IMAGE) {
            videoDuration.setVisibility(View.GONE);
        }else if(broadcastMedia.getType() == BroadcastMedia.TYPE_VIDEO){
            videoDuration.setVisibility(View.VISIBLE);
            videoDuration.bringToFront();
            if(broadcastMedia.getVideoDuration() != null) {
                videoDuration.setText(TimeUtil.formatTime(broadcastMedia.getVideoDuration()));
            }else {
                videoDuration.setText("video");
            }
        }
    }

    private boolean showSingleMedia(ViewHolder holder, int position) {
        List<BroadcastMedia> broadcastMedias = itemDataList.get(position).broadcast.getBroadcastMedias();
        Size size = singleMediaViewSizeMap.get(broadcastMedias.get(0).getMediaId());
        if(size == null) return false;

        ViewGroup.LayoutParams layoutParams = holder.binding.mediaSingle.getLayoutParams();
        layoutParams.width = size.getWidth();
        layoutParams.height = size.getHeight();
        holder.binding.mediaSingle.setLayoutParams(layoutParams);
        GlideApp
                .with(activity.getApplicationContext())
                .load(NetDataUrls.getBroadcastMediaDataUrl(activity, broadcastMedias.get(0).getMediaId()))
                .override(size.getWidth(), size.getHeight())
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        holder.binding.mediaSingle.setImageDrawable(resource);
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
        holder.binding.avatar.setOnClickListener(v -> {
            Intent intent = new Intent(activity, ChannelActivity.class);
            intent.putExtra(ExtraKeys.ICHAT_ID, itemData.broadcast.getIchatId());
            activity.startActivity(intent);
        });
        BroadcastMoreOperationBottomSheet moreOperationBottomSheet = new BroadcastMoreOperationBottomSheet(activity);
        holder.binding.more.setOnClickListener(v -> moreOperationBottomSheet.show());
        moreOperationBottomSheet.setDeleteClickYier(v -> {
            ConfirmDialog confirmDialog = new ConfirmDialog(activity);
            confirmDialog.setNegativeButton(null);
            confirmDialog.setPositiveButton((dialog, which) -> {
                BroadcastApiCaller.deleteBroadcast((LifecycleOwner) activity, itemData.broadcast.getBroadcastId(), new RetrofitApiCaller.CommonYier<OperationStatus>(activity) {
                    @Override
                    public void ok(OperationStatus data, Response<OperationStatus> row, Call<OperationStatus> call) {
                        super.ok(data, row, call);
                        data.commonHandleResult(activity, new int[]{}, () -> {
                            MessageDisplayer.autoShow(activity, "已删除", MessageDisplayer.Duration.LONG);
                            GlobalYiersHolder.getYiers(BroadcastDeletedYier.class).ifPresent(broadcastDeletedYiers -> {
                                broadcastDeletedYiers.forEach(broadcastDeletedYier -> broadcastDeletedYier.onBroadcastDeleted(itemData.broadcast.getBroadcastId()));
                            });
                        });
                    }
                });
            });
            confirmDialog.show();
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

    public void removeItemAndShow(String broadcastId){
        for (int i = 0; i < itemDataList.size(); i++) {
            if(itemDataList.get(i).broadcast.getBroadcastId().equals(broadcastId)){
                itemDataList.remove(i);
                notifyItemRemoved(i + (recyclerView.hasHeader() ? 1 : 0));
                break;
            }
        }
    }
}
