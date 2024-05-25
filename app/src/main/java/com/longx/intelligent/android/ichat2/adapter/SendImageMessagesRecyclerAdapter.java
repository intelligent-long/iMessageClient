package com.longx.intelligent.android.ichat2.adapter;

import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.longx.intelligent.android.ichat2.data.OfflineDetail;
import com.longx.intelligent.android.ichat2.databinding.RecyclerItemOfflineDetailBinding;
import com.longx.intelligent.android.ichat2.databinding.RecyclerItemSendImageMessagesBinding;
import com.longx.intelligent.android.ichat2.media.data.MediaInfo;
import com.longx.intelligent.android.ichat2.ui.glide.GlideApp;
import com.longx.intelligent.android.ichat2.util.TimeUtil;
import com.longx.intelligent.android.ichat2.yier.RecyclerItemYiers;
import com.longx.intelligent.android.lib.recyclerview.WrappableRecyclerViewAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by LONG on 2024/5/25 at 5:53 PM.
 */
public class SendImageMessagesRecyclerAdapter extends WrappableRecyclerViewAdapter<SendImageMessagesRecyclerAdapter.ViewHolder, SendImageMessagesRecyclerAdapter.ItemData> {
    private final AppCompatActivity activity;
    private RecyclerItemYiers.OnRecyclerItemClickYier onRecyclerItemClickYier;
    private RecyclerItemYiers.OnRecyclerItemActionYier onCheckButtonActionYier;
    private RecyclerItemYiers.OnRecyclerItemActionYier onCancelCheckButtonActionYier;
    private final List<SendImageMessagesRecyclerAdapter.ItemData> itemDataList = new ArrayList<>();

    public SendImageMessagesRecyclerAdapter(AppCompatActivity activity, List<SendImageMessagesRecyclerAdapter.ItemData> itemDataList) {
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
        private RecyclerItemSendImageMessagesBinding binding;

        public ViewHolder(RecyclerItemSendImageMessagesBinding binding){
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

    public void setOnCheckButtonActionYier(RecyclerItemYiers.OnRecyclerItemActionYier onCheckButtonActionYier) {
        this.onCheckButtonActionYier = onCheckButtonActionYier;
    }

    public void setOnCancelCheckButtonActionYier(RecyclerItemYiers.OnRecyclerItemActionYier onCancelCheckButtonActionYier) {
        this.onCancelCheckButtonActionYier = onCancelCheckButtonActionYier;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerItemSendImageMessagesBinding binding = RecyclerItemSendImageMessagesBinding.inflate(activity.getLayoutInflater());
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
        holder.binding.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        holder.binding.getRoot().setOnClickListener(v -> {
            if (onRecyclerItemClickYier != null)
                onRecyclerItemClickYier.onRecyclerItemClick(position, v);
        });
        holder.binding.checkButton.setOnClickListener(v -> {
            holder.binding.checkButton.setVisibility(View.GONE);
            holder.binding.cancelCheckButton.setVisibility(View.VISIBLE);
            if(onCheckButtonActionYier != null) onCheckButtonActionYier.onRecyclerItemAction(position, itemData.mediaInfo.getUri());
        });
        holder.binding.cancelCheckButton.setOnClickListener(v -> {
            holder.binding.checkButton.setVisibility(View.VISIBLE);
            holder.binding.cancelCheckButton.setVisibility(View.GONE);
            if(onCancelCheckButtonActionYier != null) onCancelCheckButtonActionYier.onRecyclerItemAction(position, itemData.mediaInfo.getUri());
        });
        switch (itemData.mediaInfo.getMediaType()){
            case IMAGE:{
                showPreviewImage(itemData.mediaInfo.getUri(), holder);
                break;
            }
        }
    }

    private void showPreviewImage(Uri imageFileUri, SendImageMessagesRecyclerAdapter.ViewHolder holder) {
        holder.binding.imageView.post(() -> {
            int width = holder.binding.imageView.getWidth();
            int height = holder.binding.imageView.getHeight();
            GlideApp
                    .with(activity.getApplicationContext())
                    .load(imageFileUri)
                    .override(width, height)
                    .into(holder.binding.imageView);
        });
    }
}
