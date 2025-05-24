package com.longx.intelligent.android.imessage.adapter;

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
import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.imessage.activity.ExtraKeys;
import com.longx.intelligent.android.imessage.activity.MediaActivity2;
import com.longx.intelligent.android.imessage.databinding.RecyclerItemEditBroadcastMediasBinding;
import com.longx.intelligent.android.imessage.databinding.RecyclerItemSendBroadcastMediasBinding;
import com.longx.intelligent.android.imessage.media.data.Media;
import com.longx.intelligent.android.imessage.media.data.MediaInfo;
import com.longx.intelligent.android.imessage.ui.glide.GlideApp;
import com.longx.intelligent.android.imessage.util.TimeUtil;
import com.longx.intelligent.android.lib.recyclerview.RecyclerView;
import com.longx.intelligent.android.lib.recyclerview.WrappableRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by LONG on 2024/8/5 at 下午2:45.
 */
public class EditBroadcastMediasRecyclerAdapter extends WrappableRecyclerViewAdapter<EditBroadcastMediasRecyclerAdapter.ViewHolder, Uri> {
    private final Activity activity;
    private final ActivityResultLauncher<Intent> returnFromPreviewToSendMediaResultLauncher;
    private final ArrayList<MediaInfo> mediaInfoList;
    private final boolean mediaActivityGlideLoad;
    private ArrayList<MediaInfo> pastMediaInfoList;

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
        private final RecyclerItemEditBroadcastMediasBinding binding;
        public ViewHolder(RecyclerItemEditBroadcastMediasBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerItemEditBroadcastMediasBinding binding = RecyclerItemEditBroadcastMediasBinding.inflate(activity.getLayoutInflater());
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
                holder.binding.videoDuration.setText(TimeUtil.formatTimeToHHMMSS(mediaInfo.getVideoDuration()));
                holder.binding.videoDuration.bringToFront();
        }
        if(mediaInfo.getPath() == null){
            holder.binding.cloudIcon.setVisibility(View.VISIBLE);
        }else {
            holder.binding.cloudIcon.setVisibility(View.GONE);
        }
        setupYiers(holder, position);
    }

    private void setupYiers(ViewHolder holder, int position) {
        holder.binding.imageView.setOnClickListener(v -> {
            Intent intent = new Intent(activity, MediaActivity2.class);
            ArrayList<Media> mediaList = new ArrayList<>();
            intent.putExtra(ExtraKeys.GLIDE_LOAD, mediaActivityGlideLoad);
            mediaInfoList.forEach(mediaInfo -> mediaList.add(new Media(mediaInfo.getMediaType(), mediaInfo.getUri())));
            intent.putParcelableArrayListExtra(ExtraKeys.MEDIAS, mediaList);
            intent.putExtra(ExtraKeys.POSITION, position);
            intent.putExtra(ExtraKeys.BUTTON_TEXT, "移除");
//            MediaActivity.setActionButtonYier(v1 -> {
//                int currentItem = MediaActivity.getInstance().getCurrentItemIndex();
//                int nextItem = currentItem + 1 >= MediaActivity.getInstance().getMediaList().size() ? currentItem - 1 : currentItem;
//                MediaActivity.getInstance().getBinding().viewPager.setCurrentItem(nextItem);
//                MediaPagerAdapter adapter = MediaActivity.getInstance().getAdapter();
//                if(adapter.getItemCount() > 0) adapter.startPlayer(nextItem);
//                if(adapter.getItemCount() != 0) adapter.pausePlayer(currentItem);
//                if(adapter.getItemCount() != 0) adapter.releasePlayer(currentItem);
//                ArrayList<Media> medias = MediaActivity.getInstance().getMediaList();
//                medias.remove(currentItem);
//                Intent intent1 = new Intent();
//                intent1.putParcelableArrayListExtra(ExtraKeys.MEDIAS, medias);
//                MediaActivity.getInstance().setResult(RESULT_OK, intent1);
//                if(medias.isEmptyOrNull()) MediaActivity.getInstance().finish();
//                MediaActivity.getInstance().getBinding().toolbar.setTitle((currentItem == medias.size() ? currentItem : currentItem + 1) + " / " + medias.size());
//                adapter.removeItem(currentItem);
//            });
            MediaActivity2.setActionButtonYier(v1 -> {
                int currentItem = MediaActivity2.getInstance().getCurrentItemIndex();
                int nextItem = Math.max(currentItem - 1, 0);
                MediaActivity2.getInstance().getBinding().viewPager.setCurrentItem(nextItem);
                MediaPagerAdapter2 adapter = MediaActivity2.getInstance().getAdapter();
//                if(adapter.getItemCount() > 0) adapter.startPlayer(nextItem);
//                if(adapter.getItemCount() != 0) adapter.pausePlayer(currentItem);
//                if(adapter.getItemCount() != 0) adapter.releasePlayer(currentItem);
                ArrayList<Media> medias = MediaActivity2.getInstance().getMediaList();
                medias.remove(currentItem);
                Intent intent1 = new Intent();
                intent1.putParcelableArrayListExtra(ExtraKeys.MEDIAS, medias);
                MediaActivity2.getInstance().setResult(RESULT_OK, intent1);
                if(medias.isEmpty()) MediaActivity2.getInstance().finish();
                MediaActivity2.getInstance().getBinding().toolbar.setTitle((currentItem == medias.size() ? currentItem : currentItem + 1) + " / " + medias.size());
                adapter.removeItem(currentItem);
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

    public void moveAndShow(int from, int to){
        if(pastMediaInfoList == null) {
            pastMediaInfoList = new ArrayList<>(mediaInfoList);
        }
        if (from < to) {
            if(to == mediaInfoList.size()) to --;
            for (int i = from; i < to; i++) {
                Collections.swap(mediaInfoList, i, i + 1);
            }
        } else {
            for (int i = from; i > to; i--) {
                Collections.swap(mediaInfoList, i, i - 1);
            }
        }
        notifyItemMoved(from, to);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void cancelMoveAndShow(){
        if(pastMediaInfoList != null) {
            this.mediaInfoList.clear();
            this.mediaInfoList.addAll(pastMediaInfoList);
            pastMediaInfoList = null;
            notifyDataSetChanged();
        }
    }

    public ArrayList<MediaInfo> getMediaInfoList() {
        return mediaInfoList;
    }
}
