package com.longx.intelligent.android.ichat2.adapter;

import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.longx.intelligent.android.ichat2.behavior.MessageDisplayer;
import com.longx.intelligent.android.ichat2.da.cachefile.CacheFilesAccessor;
import com.longx.intelligent.android.ichat2.databinding.RecyclerItemSendMediaMessagesBinding;
import com.longx.intelligent.android.ichat2.media.data.MediaInfo;
import com.longx.intelligent.android.ichat2.ui.glide.GlideApp;
import com.longx.intelligent.android.ichat2.value.Constants;
import com.longx.intelligent.android.ichat2.yier.RecyclerItemYiers;
import com.longx.intelligent.android.lib.recyclerview.WrappableRecyclerViewAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by LONG on 2024/5/25 at 5:53 PM.
 */
public class SendMediaMessagesRecyclerAdapter extends WrappableRecyclerViewAdapter<SendMediaMessagesRecyclerAdapter.ViewHolder, SendMediaMessagesRecyclerAdapter.ItemData> {
    private final AppCompatActivity activity;
    private RecyclerItemYiers.OnRecyclerItemClickYier onRecyclerItemClickYier;
    private final List<SendMediaMessagesRecyclerAdapter.ItemData> itemDataList = new ArrayList<>();
    private final List<Integer> checkedPositions = new ArrayList<>();
    private final List<Uri> checkedUris = new ArrayList<>();

    public SendMediaMessagesRecyclerAdapter(AppCompatActivity activity, List<SendMediaMessagesRecyclerAdapter.ItemData> itemDataList) {
        this.activity = activity;
        sortDataList(itemDataList);
        this.itemDataList.addAll(itemDataList);
    }

    public static class ItemData{
        private MediaInfo mediaInfo;
        public ItemData(MediaInfo mediaInfo) {
            this.mediaInfo = mediaInfo;
        }

        public MediaInfo getMediaInfo() {
            return mediaInfo;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private RecyclerItemSendMediaMessagesBinding binding;

        public ViewHolder(RecyclerItemSendMediaMessagesBinding binding){
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    private void sortDataList(List<ItemData> itemDataList) {
        itemDataList.sort(Comparator.comparingLong(t0 -> t0.getMediaInfo().getAddedTime()));
    }

    public void setOnRecyclerItemClickYier(RecyclerItemYiers.OnRecyclerItemClickYier onRecyclerItemClickYier) {
        this.onRecyclerItemClickYier = onRecyclerItemClickYier;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerItemSendMediaMessagesBinding binding = RecyclerItemSendMediaMessagesBinding.inflate(activity.getLayoutInflater());
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
        resetItemView(holder);
        ItemData itemData = itemDataList.get(position);
        Uri uri = itemData.mediaInfo.getUri();
        holder.binding.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        holder.binding.getRoot().setOnClickListener(v -> {
            if (onRecyclerItemClickYier != null)
                onRecyclerItemClickYier.onRecyclerItemClick(position, v);
        });
        if(checkedPositions.contains(position + 1)){
            holder.binding.checkButton.setVisibility(View.GONE);
            holder.binding.cancelCheckButton.setVisibility(View.VISIBLE);
            holder.binding.darkCover.setVisibility(View.VISIBLE);
            holder.binding.index.setVisibility(View.VISIBLE);
            holder.binding.index.setText(String.valueOf(checkedUris.indexOf(uri) + 1));
        }else {
            holder.binding.checkButton.setVisibility(View.VISIBLE);
            holder.binding.cancelCheckButton.setVisibility(View.GONE);
            holder.binding.darkCover.setVisibility(View.GONE);
            holder.binding.index.setVisibility(View.GONE);
            holder.binding.index.setText(null);
        }
        holder.binding.checkButton.setOnClickListener(v -> {
            if(checkedUris.size() == Constants.MAX_ONCE_SEND_CHAT_MESSAGE_IMAGE_COUNT){
                MessageDisplayer.autoShow(activity, "最多选择 " + Constants.MAX_ONCE_SEND_CHAT_MESSAGE_IMAGE_COUNT + " 张图片", MessageDisplayer.Duration.LONG);
                return;
            }
            checkedUris.add(uri);
            checkedPositions.add(position + 1);
            notifyItemChanged(position + 1);
        });
        holder.binding.cancelCheckButton.setOnClickListener(v -> {
            int index = checkedUris.indexOf(uri);
            checkedUris.remove(uri);
            checkedPositions.remove((Integer) (position + 1));
            notifyItemChanged(position + 1);
            checkedUris.forEach(uri1 -> {
                int index1 = checkedUris.indexOf(uri1);
                if(index1 >= index) notifyItemChanged(checkedPositions.get(index1));
            });
        });
        switch (itemData.mediaInfo.getMediaType()){
            case IMAGE:{
                showPreviewImage(itemData.mediaInfo.getUri(), holder);
                break;
            }
            case VIDEO:{
                showVideoThumbnail(itemData.mediaInfo.getPath(), holder, itemData);
                break;
            }
        }
    }

    private void showPreviewImage(Uri imageFileUri, SendMediaMessagesRecyclerAdapter.ViewHolder holder) {
        GlideApp
                .with(activity.getApplicationContext())
                .load(imageFileUri)
                .into(holder.binding.imageView);
    }

    private void showPreviewImage(File imageFile, SendMediaMessagesRecyclerAdapter.ViewHolder holder) {
        GlideApp
                .with(activity.getApplicationContext())
                .load(imageFile)
                .into(holder.binding.imageView);
    }

    private void showVideoThumbnail(String videoPath, @NonNull ViewHolder holder, ItemData itemData) {
        int holderPosition = holder.getAbsoluteAdapterPosition() - 1;
        File videoThumbnail = CacheFilesAccessor.VideoThumbnail.getVideoThumbnailFile(activity, videoPath);
        if(videoThumbnail.exists()){
            if (holderPosition == itemDataList.indexOf(itemData)) {
                showPreviewImage(videoThumbnail, holder);
            }
        }else {
            new Thread(() -> {
                File videoThumbnail1 = CacheFilesAccessor.VideoThumbnail.cacheAndGetVideoThumbnail(activity, videoPath);
                activity.runOnUiThread(() -> {
                    if (holderPosition == itemDataList.indexOf(itemData)) {
                        showPreviewImage(videoThumbnail1, holder);
                    }
                });
            }).start();
        }
    }

    public List<Uri> getCheckedUris() {
        return checkedUris;
    }
}
