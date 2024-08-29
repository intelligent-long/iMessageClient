package com.longx.intelligent.android.ichat2.adapter;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;

import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.activity.ExtraKeys;
import com.longx.intelligent.android.ichat2.activity.MediaActivity;
import com.longx.intelligent.android.ichat2.databinding.RecyclerItemSendBroadcastMediasBinding;
import com.longx.intelligent.android.ichat2.media.data.Media;
import com.longx.intelligent.android.ichat2.media.data.MediaInfo;
import com.longx.intelligent.android.ichat2.ui.glide.GlideApp;
import com.longx.intelligent.android.ichat2.util.TimeUtil;
import com.longx.intelligent.android.lib.recyclerview.RecyclerView;
import com.longx.intelligent.android.lib.recyclerview.WrappableRecyclerViewAdapter;

import java.util.ArrayList;

/**
 * Created by LONG on 2024/8/5 at 下午2:45.
 */
public class EditBroadcastMediasRecyclerAdapter extends WrappableRecyclerViewAdapter<EditBroadcastMediasRecyclerAdapter.ViewHolder, Uri> {
    private final Activity activity;
    private final ActivityResultLauncher<Intent> returnFromPreviewToSendMediaResultLauncher;
    private final ArrayList<MediaInfo> mediaInfoList;
    private boolean mediaActivityGlideLoad;

    public EditBroadcastMediasRecyclerAdapter(Activity activity,
                                              ActivityResultLauncher<Intent> returnFromPreviewToSendMediaResultLauncher,
                                              ArrayList<MediaInfo> mediaInfoList,
                                              boolean mediaActivityGlideLoad) {
        this.activity = activity;
        this.returnFromPreviewToSendMediaResultLauncher = returnFromPreviewToSendMediaResultLauncher;
        this.mediaInfoList = mediaInfoList;
        this.mediaActivityGlideLoad = mediaActivityGlideLoad;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final RecyclerItemSendBroadcastMediasBinding binding;
        public ViewHolder(RecyclerItemSendBroadcastMediasBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerItemSendBroadcastMediasBinding binding = RecyclerItemSendBroadcastMediasBinding.inflate(activity.getLayoutInflater());
        return new ViewHolder(binding);
    }

    @Override
    public int getItemCount() {
        return mediaInfoList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MediaInfo mediaInfo = mediaInfoList.get(position);
        switch (mediaInfo.getMediaType()){
            case IMAGE:
                GlideApp
                        .with(activity.getApplicationContext())
                        .load(mediaInfo.getUri())
                        .centerCrop()
                        .placeholder(R.drawable.glide_placeholder)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(holder.binding.imageView);
                holder.binding.videoDuration.setVisibility(View.GONE);
                break;
            case VIDEO:
                GlideApp
                        .with(activity.getApplicationContext())
                        .load(mediaInfo.getUri())
                        .centerCrop()
                        .placeholder(R.drawable.glide_placeholder)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(holder.binding.imageView);
                holder.binding.videoDuration.setVisibility(View.VISIBLE);
                holder.binding.videoDuration.setText(TimeUtil.formatTime(mediaInfo.getVideoDuration()));
                holder.binding.videoDuration.bringToFront();
        }
        setupYiers(holder, position);
    }

    private void setupYiers(ViewHolder holder, int position) {
        holder.binding.imageView.setOnClickListener(v -> {
            Intent intent = new Intent(activity, MediaActivity.class);
            ArrayList<Media> mediaList = new ArrayList<>();
            intent.putExtra(ExtraKeys.GLIDE_LOAD, mediaActivityGlideLoad);
            mediaInfoList.forEach(mediaInfo -> mediaList.add(new Media(mediaInfo.getMediaType(), mediaInfo.getUri())));
            intent.putParcelableArrayListExtra(ExtraKeys.MEDIAS, mediaList);
            intent.putExtra(ExtraKeys.POSITION, position);
            intent.putExtra(ExtraKeys.BUTTON_TEXT, "移除");
            MediaActivity.setActionButtonYier(v1 -> {
                int currentItem = MediaActivity.getInstance().getCurrentItemIndex();
                ArrayList<Media> medias = MediaActivity.getInstance().getMediaList();
                medias.remove(currentItem);
                Intent intent1 = new Intent();
                intent1.putParcelableArrayListExtra(ExtraKeys.MEDIAS, medias);
                MediaActivity.getInstance().setResult(RESULT_OK, intent1);
                if(medias.isEmpty()) MediaActivity.getInstance().finish();
                MediaActivity.getInstance().getBinding().toolbar.setTitle((currentItem == medias.size() ? currentItem : currentItem + 1) + " / " + medias.size());
                MediaActivity.getInstance().getAdapter().removeItem(currentItem);
            });
            returnFromPreviewToSendMediaResultLauncher.launch(intent);
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    public void changeAllDataAndShow(ArrayList<MediaInfo> mediaInfos){
        this.mediaInfoList.clear();
        this.mediaInfoList.addAll(mediaInfos);
        notifyDataSetChanged();
    }
}
