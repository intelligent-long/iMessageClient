package com.longx.intelligent.android.ichat2.adapter;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.longx.intelligent.android.ichat2.activity.ExtraKeys;
import com.longx.intelligent.android.ichat2.activity.PreviewToChooseImageActivity;
import com.longx.intelligent.android.ichat2.activity.PreviewToChooseVideoActivity;
import com.longx.intelligent.android.ichat2.behavior.MessageDisplayer;
import com.longx.intelligent.android.ichat2.databinding.RecyclerItemChooseMediasBinding;
import com.longx.intelligent.android.ichat2.media.MediaType;
import com.longx.intelligent.android.ichat2.media.data.MediaInfo;
import com.longx.intelligent.android.ichat2.ui.glide.GlideApp;
import com.longx.intelligent.android.ichat2.util.ErrorLogger;
import com.longx.intelligent.android.ichat2.util.TimeUtil;
import com.longx.intelligent.android.lib.recyclerview.WrappableRecyclerViewAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by LONG on 2024/5/25 at 5:53 PM.
 */
public class ChooseMediasRecyclerAdapter extends WrappableRecyclerViewAdapter<ChooseMediasRecyclerAdapter.ViewHolder, ChooseMediasRecyclerAdapter.ItemData> {
    private final AppCompatActivity activity;
    private final List<ItemData> itemDataList = new ArrayList<>();
    private final List<Integer> checkedPositions = new ArrayList<>();
    private final List<MediaInfo> checkedMediaInfos = new ArrayList<>();
    private final int maxAllowImageSize;
    private final int maxAllowVideoSize;

    public ChooseMediasRecyclerAdapter(AppCompatActivity activity, List<ItemData> itemDataList, List<MediaInfo> chosenMediaInfoList, int maxAllowImageSize, int maxAllowVideoSize) {
        this.activity = activity;
        this.maxAllowImageSize = maxAllowImageSize;
        this.maxAllowVideoSize = maxAllowVideoSize;
        sortDataList(itemDataList);
        this.itemDataList.addAll(itemDataList);

        if(chosenMediaInfoList != null) {
            for (int i = 0; i < itemDataList.size(); i++) {
                MediaInfo mediaInfo = itemDataList.get(i).mediaInfo;
                for (int j = 0; j < chosenMediaInfoList.size(); j++) {
                    if (chosenMediaInfoList.get(j).equals(mediaInfo)) {
                        checkedMediaInfos.add(mediaInfo);
                        List<MediaInfo> sortedCheckedMediaInfos = new ArrayList<>(checkedMediaInfos);
                        sortedCheckedMediaInfos.sort(Comparator.comparingInt(chosenMediaInfoList::indexOf));
                        checkedMediaInfos.clear();
                        checkedMediaInfos.addAll(sortedCheckedMediaInfos);
                        checkedPositions.add(i + 1);
                        List<Integer> sortedCheckedPositions = new ArrayList<>();
                        for (MediaInfo mediaInfo1 : sortedCheckedMediaInfos) {
                            int originalIndex = checkedMediaInfos.indexOf(mediaInfo1);
                            sortedCheckedPositions.add(checkedPositions.get(originalIndex));
                        }
                        checkedPositions.clear();
                        checkedPositions.addAll(sortedCheckedPositions);
                        break;
                    }
                }
            }
        }
    }

    public static class ItemData{
        private final MediaInfo mediaInfo;
        public ItemData(MediaInfo mediaInfo) {
            this.mediaInfo = mediaInfo;
        }

        public MediaInfo getMediaInfo() {
            return mediaInfo;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final RecyclerItemChooseMediasBinding binding;

        public ViewHolder(RecyclerItemChooseMediasBinding binding){
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    private void sortDataList(List<ItemData> itemDataList) {
        itemDataList.sort(Comparator.comparingLong(t0 -> t0.getMediaInfo().getAddedTime()));
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerItemChooseMediasBinding binding = RecyclerItemChooseMediasBinding.inflate(activity.getLayoutInflater());
        return new ViewHolder(binding);
    }

    @Override
    public int getItemCount() {
        return itemDataList.size();
    }

    public List<ItemData> getItemDataList() {
        return itemDataList;
    }

    private void resetItemView(@NonNull ViewHolder holder) {
        holder.binding.imageView.setImageBitmap(null);
        holder.binding.imageView.bringToFront();
        GlideApp.with(activity.getApplicationContext()).clear(holder.binding.imageView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemData itemData = itemDataList.get(position);
        holder.binding.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        holder.binding.getRoot().setOnClickListener(v -> {
            MediaInfo mediaInfo = itemDataList.get(position).getMediaInfo();
            if(mediaInfo.getMediaType().equals(MediaType.IMAGE)) {
                Intent intent = new Intent(activity, PreviewToChooseImageActivity.class);
                intent.putExtra(ExtraKeys.FILE_PATH, mediaInfo.getPath());
                activity.startActivity(intent);
            }else if(mediaInfo.getMediaType().equals(MediaType.VIDEO)){
                Intent intent = new Intent(activity, PreviewToChooseVideoActivity.class);
                intent.putExtra(ExtraKeys.URI, mediaInfo.getUri());
                activity.startActivity(intent);
            }
        });
        if(checkedPositions.contains(position + 1)){
            holder.binding.checkButton.setVisibility(View.GONE);
            holder.binding.cancelCheckButton.setVisibility(View.VISIBLE);
            holder.binding.darkCover.setVisibility(View.VISIBLE);
            holder.binding.index.setVisibility(View.VISIBLE);
            holder.binding.index.setText(String.valueOf(checkedMediaInfos.indexOf(itemData.mediaInfo) + 1));
        }else {
            holder.binding.checkButton.setVisibility(View.VISIBLE);
            holder.binding.cancelCheckButton.setVisibility(View.GONE);
            holder.binding.darkCover.setVisibility(View.GONE);
            holder.binding.index.setVisibility(View.GONE);
            holder.binding.index.setText(null);
        }
        holder.binding.checkButton.setOnClickListener(v -> {
            AtomicInteger checkedImageSize = new AtomicInteger();
            AtomicInteger checkedVideoSize = new AtomicInteger();
            checkedMediaInfos.forEach(checkedMediaInfo -> {
                if(checkedMediaInfo.getMediaType() == MediaType.IMAGE){
                    checkedImageSize.getAndIncrement();
                }else if(checkedMediaInfo.getMediaType() == MediaType.VIDEO){
                    checkedVideoSize.getAndIncrement();
                }
            });
            if(itemData.mediaInfo.getMediaType().equals(MediaType.IMAGE)
                    && maxAllowImageSize != -1 && checkedImageSize.get() == maxAllowImageSize){
                MessageDisplayer.autoShow(activity, "最多选择 " + maxAllowImageSize + " 个图片", MessageDisplayer.Duration.LONG);
                return;
            }
            if(itemData.mediaInfo.getMediaType().equals(MediaType.VIDEO)
                    && maxAllowVideoSize != -1 && checkedVideoSize.get() == maxAllowVideoSize){
                MessageDisplayer.autoShow(activity, "最多选择 " + maxAllowVideoSize + " 个视频", MessageDisplayer.Duration.LONG);
                return;
            }
            checkedMediaInfos.add(itemData.mediaInfo);
            checkedPositions.add(position + 1);
            notifyItemChanged(position + 1);
        });
        holder.binding.cancelCheckButton.setOnClickListener(v -> {
            checkedMediaInfos.remove(itemData.mediaInfo);
            checkedPositions.remove((Integer) (position + 1));
            notifyItemChanged(position + 1);
            int index = checkedMediaInfos.indexOf(itemData.mediaInfo);
            checkedMediaInfos.forEach(mediaInfo -> {
                int index1 = checkedMediaInfos.indexOf(mediaInfo);
                if(index1 >= index) {
                    notifyItemChanged(checkedPositions.get(index1));
                }
            });
        });
        switch (itemData.mediaInfo.getMediaType()){
            case IMAGE:{
                showImage(itemData.mediaInfo, holder);
                break;
            }
            case VIDEO:{
                showVideo(itemData.mediaInfo, holder);
                break;
            }
        }
    }

    private void showImage(MediaInfo mediaInfo, ViewHolder holder) {
        GlideApp
                .with(activity.getApplicationContext())
                .load(mediaInfo.getPath())
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.binding.imageView);
        holder.binding.videoDuration.setVisibility(View.GONE);
    }

    private void showVideo(MediaInfo mediaInfo, ViewHolder holder) {
        GlideApp
                .with(activity.getApplicationContext())
                .load(mediaInfo.getPath())
                .frame(1000_000)
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.binding.imageView);
        holder.binding.videoDuration.setVisibility(View.VISIBLE);
        holder.binding.videoDuration.setText(TimeUtil.formatTime(mediaInfo.getVideoDuration()));
        holder.binding.videoDuration.bringToFront();
    }

    public List<MediaInfo> getCheckedMediaInfos() {
        return checkedMediaInfos;
    }
}
