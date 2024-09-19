package com.longx.intelligent.android.ichat2.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.activity.BroadcastActivity;
import com.longx.intelligent.android.ichat2.activity.ChannelActivity;
import com.longx.intelligent.android.ichat2.activity.EditBroadcastActivity;
import com.longx.intelligent.android.ichat2.activity.ExtraKeys;
import com.longx.intelligent.android.ichat2.behavior.MessageDisplayer;
import com.longx.intelligent.android.ichat2.bottomsheet.SelfBroadcastMoreOperationBottomSheet;
import com.longx.intelligent.android.ichat2.da.database.manager.ChannelDatabaseManager;
import com.longx.intelligent.android.ichat2.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.ichat2.data.Broadcast;
import com.longx.intelligent.android.ichat2.data.BroadcastMedia;
import com.longx.intelligent.android.ichat2.data.Channel;
import com.longx.intelligent.android.ichat2.data.Self;
import com.longx.intelligent.android.ichat2.data.Size;
import com.longx.intelligent.android.ichat2.data.response.OperationData;
import com.longx.intelligent.android.ichat2.data.response.OperationStatus;
import com.longx.intelligent.android.ichat2.databinding.RecyclerItemBroadcastBinding;
import com.longx.intelligent.android.ichat2.dialog.ConfirmDialog;
import com.longx.intelligent.android.ichat2.net.dataurl.NetDataUrls;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.BroadcastApiCaller;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.ichat2.net.stomp.ServerMessageServiceStompActions;
import com.longx.intelligent.android.ichat2.ui.glide.GlideApp;
import com.longx.intelligent.android.ichat2.util.ErrorLogger;
import com.longx.intelligent.android.ichat2.util.TimeUtil;
import com.longx.intelligent.android.ichat2.util.UiUtil;
import com.longx.intelligent.android.ichat2.value.Constants;
import com.longx.intelligent.android.ichat2.yier.BroadcastDeletedYier;
import com.longx.intelligent.android.ichat2.yier.BroadcastUpdateYier;
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
    private final AppCompatActivity activity;
    private final com.longx.intelligent.android.lib.recyclerview.RecyclerView recyclerView;
    private final List<ItemData> itemDataList;
    private final Map<String, Size> singleMediaViewSizeMap= new HashMap<>();
    private final Self currentUserProfile;
    private final BroadcastUpdateYier ignoreUpdateBroadcastInteractionsBroadcastUpdateYier;

    public BroadcastsRecyclerAdapter(AppCompatActivity activity, com.longx.intelligent.android.lib.recyclerview.RecyclerView recyclerView, List<ItemData> itemDataList, BroadcastUpdateYier ignoreUpdateBroadcastInteractionsBroadcastUpdateYier) {
        this.activity = activity;
        this.recyclerView = recyclerView;
        sortItemDataList(itemDataList);
        this.itemDataList = itemDataList;
        currentUserProfile = SharedPreferencesAccessor.UserProfilePref.getCurrentUserProfile(activity);
        this.ignoreUpdateBroadcastInteractionsBroadcastUpdateYier = ignoreUpdateBroadcastInteractionsBroadcastUpdateYier;
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
                    .load(R.drawable.default_avatar)
                    .into(holder.binding.avatar);
        } else {
            GlideApp
                    .with(activity.getApplicationContext())
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
        holder.binding.mediaSingle.setVisibility(View.GONE);
        List<BroadcastMedia> broadcastMedias = itemData.broadcast.getBroadcastMedias();
        if(broadcastMedias != null && !broadcastMedias.isEmpty()){
            broadcastMedias.sort(Comparator.comparingInt(BroadcastMedia::getIndex));
            if(broadcastMedias.size() > 4) {
                //大于4个
                holder.binding.medias.setVisibility(View.VISIBLE);
                holder.binding.medias2To4.setVisibility(View.GONE);
                holder.binding.mediaSingle.setVisibility(View.GONE);
                holder.binding.videoDurationSingle.setVisibility(View.GONE);
                ImageView[] imageViews = {
                        holder.binding.media1, holder.binding.media2, holder.binding.media3, holder.binding.media4,
                        holder.binding.media5, holder.binding.media6, holder.binding.media7, holder.binding.media8,
                        holder.binding.media9, holder.binding.media10, holder.binding.media11, holder.binding.media12
                };
                TextView[] videoDurationViews = {
                        holder.binding.videoDuration1, holder.binding.videoDuration2, holder.binding.videoDuration3, holder.binding.videoDuration4,
                        holder.binding.videoDuration5, holder.binding.videoDuration6, holder.binding.videoDuration7, holder.binding.videoDuration8,
                        holder.binding.videoDuration9, holder.binding.videoDuration10, holder.binding.videoDuration11, holder.binding.videoDuration12
                };
                setImageViewsVisibility(imageViews, broadcastMedias);
                for (TextView videoDurationView : videoDurationViews) {
                    videoDurationView.setVisibility(View.INVISIBLE);
                }
                int forTimes = Math.min(imageViews.length, broadcastMedias.size());
                for (int i = 0; i < forTimes; i++) {
                    setupImage(imageViews, i, broadcastMedias);
                    setupVideoDuration(videoDurationViews, i, broadcastMedias);
                }
                if(broadcastMedias.size() > imageViews.length){
                    holder.binding.darkCover.setVisibility(View.VISIBLE);
                    holder.binding.moreIcon.setVisibility(View.VISIBLE);
                }else {
                    holder.binding.darkCover.setVisibility(View.GONE);
                    holder.binding.moreIcon.setVisibility(View.GONE);
                }
            }else if(broadcastMedias.size() > 1){
                //2到4个
                holder.binding.medias.setVisibility(View.GONE);
                holder.binding.medias2To4.setVisibility(View.VISIBLE);
                holder.binding.mediaSingle.setVisibility(View.GONE);
                holder.binding.videoDurationSingle.setVisibility(View.GONE);
                ImageView[] imageViews = {
                        holder.binding.media2To41, holder.binding.media2To42, holder.binding.media2To43, holder.binding.media2To44
                };
                TextView[] videoDurationViews = {
                        holder.binding.videoDuration2To41, holder.binding.videoDuration2To42, holder.binding.videoDuration2To43, holder.binding.videoDuration2To44
                };
                setImageViewsVisibility2To4(imageViews, broadcastMedias);
                for (TextView videoDurationView : videoDurationViews) {
                    videoDurationView.setVisibility(View.INVISIBLE);
                }
                for (int i = 0; i < broadcastMedias.size(); i++) {
                    setupImage(imageViews, i, broadcastMedias);
                    setupVideoDuration(videoDurationViews, i, broadcastMedias);
                }
            }else {
                //1个
                holder.binding.mediaSingle.setImageDrawable(null);
                holder.binding.medias.setVisibility(View.GONE);
                holder.binding.medias2To4.setVisibility(View.GONE);
                holder.binding.mediaSingle.setVisibility(View.VISIBLE);
                BroadcastMedia broadcastMedia = broadcastMedias.get(0);
                if(broadcastMedia.getType() == BroadcastMedia.TYPE_VIDEO) {
                    holder.binding.videoDurationSingle.setVisibility(View.VISIBLE);
                    holder.binding.videoDurationSingle.bringToFront();
                    if (broadcastMedia.getVideoDuration() != null) {
                        holder.binding.videoDurationSingle.setText(TimeUtil.formatTime(broadcastMedia.getVideoDuration()));
                    } else {
                        holder.binding.videoDurationSingle.setText("video");
                    }
                }else {
                    holder.binding.videoDurationSingle.setVisibility(View.GONE);
                }
                Size size = broadcastMedia.getSize();
                if(size != null) {
                    boolean successShow = showSingleMedia(holder, position);
                    if(!successShow) {
                        holder.binding.mediaSingle.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                            @Override
                            public boolean onPreDraw() {
                                holder.binding.mediaSingle.getViewTreeObserver().removeOnPreDrawListener(this);
                                Size viewSize = calculateViewSize(holder, position);
                                singleMediaViewSizeMap.put(broadcastMedia.getMediaId(), viewSize);
                                showSingleMedia(holder, position);
                                return true;
                            }
                        });
                    }
                } else {
                    GlideApp
                            .with(activity.getApplicationContext())
                            .load(NetDataUrls.getBroadcastMediaDataUrl(activity, broadcastMedia.getMediaId()))
                            .placeholder(AppCompatResources.getDrawable(activity, R.drawable.cached_24px))
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
            holder.binding.medias.setVisibility(View.GONE);
            holder.binding.medias2To4.setVisibility(View.GONE);
            holder.binding.mediaSingle.setVisibility(View.GONE);
        }

        if(itemData.broadcast.isLiked()){
            holder.binding.like.setImageResource(R.drawable.favorite_fill_broadcast_liked_24px);
        }else {
            holder.binding.like.setImageResource(R.drawable.favorite_outline_24px);
        }

        if(itemData.broadcast.getLikeCount() > 0){
            holder.binding.likeCount.setText(String.valueOf(itemData.broadcast.getLikeCount()));
            holder.binding.likeCount.setVisibility(View.VISIBLE);
        }else {
            holder.binding.likeCount.setVisibility(View.GONE);
        }

        setupYiers(holder, position);
    }

    private void setImageViewsVisibility(ImageView[] imageViews,  List<BroadcastMedia> broadcastMedias){
        int imageSize = broadcastMedias.size();
        for (int i = 0; i < imageViews.length; i++) {
            if(i < imageSize){
                imageViews[i].setVisibility(View.VISIBLE);
            }else {
                if(imageSize <= 3){
                    if(i < 3) {
                        imageViews[i].setVisibility(View.INVISIBLE);
                    }else {
                        imageViews[i].setVisibility(View.GONE);
                    }
                }else if(imageSize <= 6){
                    if(i < 6) {
                        imageViews[i].setVisibility(View.INVISIBLE);
                    }else {
                        imageViews[i].setVisibility(View.GONE);
                    }
                }else if(imageSize <= 9){
                    if(i < 9) {
                        imageViews[i].setVisibility(View.INVISIBLE);
                    }else {
                        imageViews[i].setVisibility(View.GONE);
                    }
                }else {
                    imageViews[i].setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    private void setImageViewsVisibility2To4(ImageView[] imageViews, List<BroadcastMedia> broadcastMedias){
        int imageSize = broadcastMedias.size();
        for (int i = 0; i < imageViews.length; i++) {
            if(i < imageSize){
                imageViews[i].setVisibility(View.VISIBLE);
            }else {
                if(imageSize <= 2){
                    if(i < 2) {
                        imageViews[i].setVisibility(View.INVISIBLE);
                    }else {
                        imageViews[i].setVisibility(View.GONE);
                    }
                }else if(imageSize <= 4){
                    if(i < 4) {
                        imageViews[i].setVisibility(View.INVISIBLE);
                    }else {
                        imageViews[i].setVisibility(View.GONE);
                    }
                }else {
                    imageViews[i].setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    private void setupImage(ImageView[] imageViews, int i, List<BroadcastMedia> broadcastMedias) {
        BroadcastMedia broadcastMedia = broadcastMedias.get(i);
        GlideApp
                .with(activity.getApplicationContext())
                .load(NetDataUrls.getBroadcastMediaDataUrl(activity, broadcastMedia.getMediaId()))
                .placeholder(AppCompatResources.getDrawable(activity, R.drawable.cached_24px))
                .centerCrop()
                .into(imageViews[i]);
    }

    private void setupVideoDuration(TextView[] videoDurationViews, int i, List<BroadcastMedia> broadcastMedias) {
        BroadcastMedia broadcastMedia = broadcastMedias.get(i);
        if(broadcastMedia.getType() == BroadcastMedia.TYPE_IMAGE) {
            videoDurationViews[i].setVisibility(View.INVISIBLE);
        }else if(broadcastMedia.getType() == BroadcastMedia.TYPE_VIDEO){
            videoDurationViews[i].setVisibility(View.VISIBLE);
            videoDurationViews[i].bringToFront();
            if(broadcastMedia.getVideoDuration() != null) {
                videoDurationViews[i].setText(TimeUtil.formatTime(broadcastMedia.getVideoDuration()));
            }else {
                videoDurationViews[i].setText("video");
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
                .placeholder(AppCompatResources.getDrawable(activity, R.drawable.cached_24px))
                .override(size.getWidth(), size.getHeight())
                .into(holder.binding.mediaSingle);
        return true;
    }

    private Size calculateViewSize(ViewHolder holder, int position){
        List<BroadcastMedia> broadcastMedias = itemDataList.get(position).broadcast.getBroadcastMedias();
        int viewWidth = holder.binding.mainContent.getWidth();
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
            intent.putExtra(ExtraKeys.BROADCAST, itemDataList.get(position).broadcast);
            activity.startActivity(intent);
        });
        holder.binding.avatar.setOnClickListener(v -> {
            Intent intent = new Intent(activity, ChannelActivity.class);
            intent.putExtra(ExtraKeys.ICHAT_ID, itemData.broadcast.getIchatId());
            activity.startActivity(intent);
        });
        if(itemData.broadcast.getIchatId().equals(SharedPreferencesAccessor.UserProfilePref.getCurrentUserProfile(activity).getIchatId())) {
            SelfBroadcastMoreOperationBottomSheet moreOperationBottomSheet = new SelfBroadcastMoreOperationBottomSheet(activity);
            holder.binding.more.setOnClickListener(v -> moreOperationBottomSheet.show());
            moreOperationBottomSheet.setDeleteClickYier(v -> {
                ConfirmDialog confirmDialog = new ConfirmDialog(activity);
                confirmDialog.setNegativeButton(null);
                confirmDialog.setPositiveButton((dialog, which) -> {
                    BroadcastApiCaller.deleteBroadcast(activity, itemData.broadcast.getBroadcastId(), new RetrofitApiCaller.CommonYier<OperationStatus>(activity) {
                        @Override
                        public void ok(OperationStatus data, Response<OperationStatus> raw, Call<OperationStatus> call) {
                            super.ok(data, raw, call);
                            data.commonHandleResult(activity, new int[]{}, () -> {
                                MessageDisplayer.autoShow(activity, "已删除", MessageDisplayer.Duration.LONG);
                                GlobalYiersHolder.getYiers(BroadcastDeletedYier.class).ifPresent(broadcastDeletedYiers -> {
                                    broadcastDeletedYiers.forEach(broadcastDeletedYier -> broadcastDeletedYier.onBroadcastDeleted(itemData.broadcast.getBroadcastId()));
                                });
                                ServerMessageServiceStompActions.updateRecentBroadcastMedias(activity, itemData.broadcast.getIchatId());
                            });
                        }
                    });
                });
                confirmDialog.show();
            });
            moreOperationBottomSheet.setEditClickYier(v -> {
                Intent intent = new Intent(activity, EditBroadcastActivity.class);
                intent.putExtra(ExtraKeys.BROADCAST, itemData.broadcast);
                activity.startActivity(intent);
            });
        }else {
            holder.binding.more.setOnClickListener(null);
        }
        UiUtil.setViewEnabled(holder.binding.like, true, false);
        holder.binding.like.setOnClickListener(v -> {
            UiUtil.setViewEnabled(holder.binding.like, false, false);
            if(!itemDataList.get(position).broadcast.isLiked()) {
                BroadcastApiCaller.likeBroadcast(activity, itemDataList.get(position).broadcast.getBroadcastId(), new RetrofitApiCaller.DelayedShowDialogCommonYier<OperationData>(activity) {
                    @Override
                    public void ok(OperationData data, Response<OperationData> raw, Call<OperationData> call) {
                        super.ok(data, raw, call);
                        data.commonHandleResult(activity, new int[]{-101, -102}, () -> {
                            Broadcast broadcast = data.getData(Broadcast.class);
                            updateOneBroadcast(broadcast, false);
                            holder.binding.like.setImageResource(R.drawable.favorite_fill_broadcast_liked_24px);
                            if(itemDataList.get(position).broadcast.getLikeCount() > 0){
                                holder.binding.likeCount.setText(String.valueOf(itemDataList.get(position).broadcast.getLikeCount()));
                                holder.binding.likeCount.setVisibility(View.VISIBLE);
                            }else {
                                holder.binding.likeCount.setVisibility(View.GONE);
                            }
                            GlobalYiersHolder.getYiers(BroadcastUpdateYier.class).ifPresent(broadcastUpdateYiers -> {
                                broadcastUpdateYiers.forEach(broadcastUpdateYier -> {
                                    if(!broadcastUpdateYier.equals(ignoreUpdateBroadcastInteractionsBroadcastUpdateYier)) {
                                        broadcastUpdateYier.updateOneBroadcast(broadcast);
                                    }
                                });
                            });
                        });
                    }

                    @Override
                    public synchronized void complete(Call<OperationData> call) {
                        super.complete(call);
                        UiUtil.setViewEnabled(holder.binding.like, true, false);
                    }
                });
            }else {
                BroadcastApiCaller.cancelLikeBroadcast(activity, itemDataList.get(position).broadcast.getBroadcastId(), new RetrofitApiCaller.DelayedShowDialogCommonYier<OperationData>(activity){
                    @Override
                    public void ok(OperationData data, Response<OperationData> raw, Call<OperationData> call) {
                        super.ok(data, raw, call);
                        data.commonHandleResult(activity, new int[]{-101, -102}, () -> {
                            Broadcast broadcast = data.getData(Broadcast.class);
                            updateOneBroadcast(broadcast, false);
                            holder.binding.like.setImageResource(R.drawable.favorite_outline_24px);
                            if(itemDataList.get(position).broadcast.getLikeCount() > 0){
                                holder.binding.likeCount.setText(String.valueOf(itemDataList.get(position).broadcast.getLikeCount()));
                                holder.binding.likeCount.setVisibility(View.VISIBLE);
                            }else {
                                holder.binding.likeCount.setVisibility(View.GONE);
                            }
                            GlobalYiersHolder.getYiers(BroadcastUpdateYier.class).ifPresent(broadcastUpdateYiers -> {
                                broadcastUpdateYiers.forEach(broadcastUpdateYier -> {
                                    if(!broadcastUpdateYier.equals(ignoreUpdateBroadcastInteractionsBroadcastUpdateYier)) {
                                        broadcastUpdateYier.updateOneBroadcast(broadcast);
                                    }
                                });
                            });
                        });
                    }

                    @Override
                    public synchronized void complete(Call<OperationData> call) {
                        super.complete(call);
                        UiUtil.setViewEnabled(holder.binding.like, true, false);
                    }
                });
            }
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
        notifyItemRangeInserted(insertPosition + (recyclerView.hasHeader() ? 1 : 0), items.size());
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

    public void addItemsToStartAndShow(List<ItemData> items){
        sortItemDataList(items);
        itemDataList.addAll(0, items);
        notifyItemRangeInserted(0, items.size());
    }

    public void updateOneBroadcast(Broadcast newBroadcast, boolean notifyItemChanged){
        for (int i = 0; i < itemDataList.size(); i++) {
            Broadcast broadcast = itemDataList.get(i).broadcast;
            if(broadcast.getBroadcastId().equals(newBroadcast.getBroadcastId())){
                itemDataList.set(i, new ItemData(newBroadcast));
                if(notifyItemChanged) notifyItemChanged(i + (recyclerView.hasHeader() ? 1 : 0));
                break;
            }
        }
    }
}