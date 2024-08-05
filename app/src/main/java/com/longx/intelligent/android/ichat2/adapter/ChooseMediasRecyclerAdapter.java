package com.longx.intelligent.android.ichat2.adapter;

import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.longx.intelligent.android.ichat2.databinding.RecyclerItemChooseMediasBinding;
import com.longx.intelligent.android.ichat2.media.data.MediaInfo;
import com.longx.intelligent.android.ichat2.ui.glide.GlideApp;
import com.longx.intelligent.android.ichat2.util.ErrorLogger;
import com.longx.intelligent.android.ichat2.yier.RecyclerItemYiers;
import com.longx.intelligent.android.lib.recyclerview.WrappableRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Created by LONG on 2024/5/25 at 5:53 PM.
 */
public class ChooseMediasRecyclerAdapter extends WrappableRecyclerViewAdapter<ChooseMediasRecyclerAdapter.ViewHolder, ChooseMediasRecyclerAdapter.ItemData> {
    private final AppCompatActivity activity;
    private RecyclerItemYiers.OnRecyclerItemClickYier onRecyclerItemClickYier;
    private final List<ChooseMediasRecyclerAdapter.ItemData> itemDataList = new ArrayList<>();
    private final List<Integer> checkedPositions = new ArrayList<>();
    private final List<Uri> checkedUris = new ArrayList<>();

    public ChooseMediasRecyclerAdapter(AppCompatActivity activity, List<ChooseMediasRecyclerAdapter.ItemData> itemDataList, List<Uri> chosenUriList) {
        this.activity = activity;
        sortDataList(itemDataList);
        this.itemDataList.addAll(itemDataList);

        if(chosenUriList != null) {
            for (int i = 0; i < itemDataList.size(); i++) {
                Uri uri = itemDataList.get(i).mediaInfo.getUri();
                for (int j = 0; j < chosenUriList.size(); j++) {
                    if (chosenUriList.get(j).equals(uri)) {
                        checkedUris.add(uri);
                        checkedPositions.add(i + 1);
                        List<Uri> sortedCheckedUris = new ArrayList<>(checkedUris);
                        sortedCheckedUris.sort(Comparator.comparingInt(chosenUriList::indexOf));
                        List<Integer> sortedCheckedPositions = new ArrayList<>();
                        for (Uri uri1 : sortedCheckedUris) {
                            int originalIndex = checkedUris.indexOf(uri1);
                            sortedCheckedPositions.add(checkedPositions.get(originalIndex));
                        }
                        checkedUris.clear();
                        checkedUris.addAll(sortedCheckedUris);
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

    public void setOnRecyclerItemClickYier(RecyclerItemYiers.OnRecyclerItemClickYier onRecyclerItemClickYier) {
        this.onRecyclerItemClickYier = onRecyclerItemClickYier;
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
                if(index1 >= index) {
                    notifyItemChanged(checkedPositions.get(index1));
                    ErrorLogger.log(index1);
                }
            });
        });
        switch (itemData.mediaInfo.getMediaType()){
            case IMAGE:{
                showPreviewImage(itemData.mediaInfo.getUri(), holder);
                break;
            }
            case VIDEO:{
                showVideoThumbnail(itemData.mediaInfo.getPath(), holder);
                break;
            }
        }
    }

    private void showPreviewImage(Uri imageFileUri, ChooseMediasRecyclerAdapter.ViewHolder holder) {
        GlideApp
                .with(activity.getApplicationContext())
                .load(imageFileUri)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.binding.imageView);
    }

    private void showVideoThumbnail(String videoPath, @NonNull ViewHolder holder) {
        GlideApp
                .with(activity.getApplicationContext())
                .load(videoPath)
                .frame(1000000)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.binding.imageView);
    }

    public List<Uri> getCheckedUris() {
        return checkedUris;
    }
}
