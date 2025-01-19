package com.longx.intelligent.android.imessage.adapter;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.longx.intelligent.android.imessage.activity.ExtraKeys;
import com.longx.intelligent.android.imessage.activity.MediaActivity2;
import com.longx.intelligent.android.imessage.behaviorcomponents.MessageDisplayer;
import com.longx.intelligent.android.imessage.databinding.RecyclerItemChooseMediasBinding;
import com.longx.intelligent.android.imessage.media.MediaType;
import com.longx.intelligent.android.imessage.media.data.Media;
import com.longx.intelligent.android.imessage.media.data.MediaInfo;
import com.longx.intelligent.android.imessage.ui.glide.GlideApp;
import com.longx.intelligent.android.imessage.util.FileUtil;
import com.longx.intelligent.android.imessage.util.TimeUtil;
import com.longx.intelligent.android.lib.recyclerview.WrappableRecyclerViewAdapter;

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
    private final int maxAllowImageCount;
    private final int maxAllowVideoCount;
    private final long maxAllowImageSize;
    private final long maxAllowVideoSize;

    public ChooseMediasRecyclerAdapter(AppCompatActivity activity, List<ItemData> itemDataList, List<MediaInfo> chosenMediaInfoList,
                                       int maxAllowImageCount, int maxAllowVideoCount, long maxAllowImageSize, long maxAllowVideoSize) {
        this.activity = activity;
        this.maxAllowImageCount = maxAllowImageCount;
        this.maxAllowVideoCount = maxAllowVideoCount;
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
            Intent intent = new Intent(activity, MediaActivity2.class);
            int current = 49;
            int startPosition = position - 49;
            int endPosition = position + 50;
            if(startPosition < 0) {
                startPosition = 0;
                current = position;
            }
            if(endPosition >= itemDataList.size()){
                endPosition = itemDataList.size() - 1;
            }
            ArrayList<Media> mediaList = new ArrayList<>();
            for (int i = startPosition; i <= endPosition; i++) {
                MediaInfo mediaInfo1 = itemDataList.get(i).mediaInfo;
                mediaList.add(new Media(mediaInfo1.getMediaType(), mediaInfo1.getUri()));
            }
            intent.putParcelableArrayListExtra(ExtraKeys.MEDIAS, mediaList);
            intent.putExtra(ExtraKeys.POSITION, current);
            activity.startActivity(intent);
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
                    && maxAllowImageCount != -1 && checkedImageSize.get() == maxAllowImageCount){
                MessageDisplayer.autoShow(activity, "最多选择 " + maxAllowImageCount + " 个图片", MessageDisplayer.Duration.LONG);
                return;
            }
            if(itemData.mediaInfo.getMediaType().equals(MediaType.VIDEO)
                    && maxAllowVideoCount != -1 && checkedVideoSize.get() == maxAllowVideoCount){
                MessageDisplayer.autoShow(activity, "最多选择 " + maxAllowVideoCount + " 个视频", MessageDisplayer.Duration.LONG);
                return;
            }
            if(itemData.mediaInfo.getMediaType().equals(MediaType.IMAGE)){
                if(maxAllowImageSize != -1 && FileUtil.getFileSize(itemData.mediaInfo.getPath()) > maxAllowImageSize){
                    MessageDisplayer.autoShow(activity, "图片文件最大不能超过 " + FileUtil.formatFileSize(maxAllowImageSize), MessageDisplayer.Duration.LONG);
                    return;
                }
            }
            if(itemData.mediaInfo.getMediaType().equals(MediaType.VIDEO)){
                if(maxAllowVideoSize != -1 && FileUtil.getFileSize(itemData.mediaInfo.getPath()) > maxAllowVideoSize){
                    MessageDisplayer.autoShow(activity, "视频文件最大不能超过 " + FileUtil.formatFileSize(maxAllowVideoSize), MessageDisplayer.Duration.LONG);
                    return;
                }
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
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.binding.imageView);
        holder.binding.videoDuration.setVisibility(View.VISIBLE);
        holder.binding.videoDuration.setText(TimeUtil.formatTimeToHHMMSS(mediaInfo.getVideoDuration()));
        holder.binding.videoDuration.bringToFront();
    }

    public List<MediaInfo> getCheckedMediaInfos() {
        return checkedMediaInfos;
    }
}
