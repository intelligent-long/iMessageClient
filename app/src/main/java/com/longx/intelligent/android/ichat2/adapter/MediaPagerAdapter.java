package com.longx.intelligent.android.ichat2.adapter;

import android.annotation.SuppressLint;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.longx.intelligent.android.ichat2.activity.MediaActivity;
import com.longx.intelligent.android.ichat2.behaviorcomponents.GlideBehaviours;
import com.longx.intelligent.android.ichat2.databinding.RecyclerItemMediaBinding;
import com.longx.intelligent.android.ichat2.media.MediaType;
import com.longx.intelligent.android.ichat2.media.data.Media;
import com.longx.intelligent.android.ichat2.ui.SwipeDownGestureYier;
import com.longx.intelligent.android.ichat2.util.ErrorLogger;
import com.longx.intelligent.android.ichat2.util.TimeUtil;
import com.longx.intelligent.android.ichat2.util.UiUtil;
import com.longx.intelligent.android.ichat2.util.Utils;
import com.longx.intelligent.android.ichat2.yier.RecyclerItemYiers;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by LONG on 2024/5/28 at 9:24 PM.
 */
public class MediaPagerAdapter extends RecyclerView.Adapter<MediaPagerAdapter.ViewHolder> {
    private final MediaActivity activity;
    private final List<ItemData> itemDataList = new ArrayList<>();
    private RecyclerItemYiers.OnRecyclerItemActionYier onRecyclerItemActionYier;
    private RecyclerItemYiers.OnRecyclerItemClickYier onRecyclerItemClickYier;
    private static final int SEEKBAR_MAX = 10000;
    private final Map<Integer, ViewHolder> viewHolderMap = new HashMap<>();
    private final boolean glideLoad;
    private final List<Integer> removedPositions = new ArrayList<>();

    public MediaPagerAdapter(MediaActivity activity, List<Media> mediaList, boolean glideLoad) {
        this.activity = activity;
        mediaList.forEach(media -> {
            itemDataList.add(new ItemData(media));
        });
        this.glideLoad = glideLoad;
    }

    public static class ItemData{
        private final Media media;
        private ExoPlayer player;
        private Runnable updateProgressAction;

        public ItemData(Media media) {
            this.media = media;
        }

        public Media getMedia() {
            return media;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final RecyclerItemMediaBinding binding;

        public ViewHolder(RecyclerItemMediaBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public RecyclerItemMediaBinding getBinding() {
            return binding;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerItemMediaBinding binding = RecyclerItemMediaBinding.inflate(activity.getLayoutInflater(), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public int getItemCount() {
        return itemDataList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        resetView(holder);
        if(activity.isPureContent()){
            UiUtil.setViewVisibility(holder.binding.topShadowCover, View.GONE);
            if(itemDataList.get(position).media.getMediaType() == MediaType.VIDEO) {
                UiUtil.setViewVisibility(holder.binding.playControl, View.GONE);
            }
        }else {
            UiUtil.setViewVisibility(holder.binding.topShadowCover, View.VISIBLE);
        }
        viewHolderMap.put(position, holder);
        Media media = itemDataList.get(position).media;
        switch (media.getMediaType()){
            case IMAGE:{
                changeTopCoverHeight(holder, false);
                holder.binding.topShadowCover.bringToFront();
                holder.binding.photoView.setVisibility(View.VISIBLE);
                Uri imageUri = media.getUri();
                holder.binding.photoView.setOnImageEventListener(new SubsamplingScaleImageView.DefaultOnImageEventListener(){
                    @Override
                    public void onImageLoadError(Exception e) {
                        super.onImageLoadError(e);
                        holder.binding.photoView.setVisibility(View.GONE);
                        holder.binding.loadFailedView.setVisibility(View.VISIBLE);
                        holder.binding.loadFailedText.bringToFront();
                        Glide.with(activity.getApplicationContext())
                                .load(imageUri)
                                .transition(DrawableTransitionOptions.withCrossFade())
                                .into(new CustomTarget<Drawable>() {
                                    @Override
                                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                        holder.binding.spareImageView.setImageDrawable(resource);
                                    }

                                    @Override
                                    public void onLoadCleared(@Nullable Drawable placeholder) {

                                    }
                                });
                    }
                });
                if(glideLoad) {
                    GlideBehaviours.loadToFile(activity.getApplicationContext(), imageUri, new CustomTarget<File>() {
                        @Override
                        public void onResourceReady(@NonNull File resource, @Nullable Transition<? super File> transition) {
                            holder.binding.photoView.setImage(ImageSource.uri(Uri.fromFile(resource)));
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                        }
                    }, true);
                }else {
                    holder.binding.photoView.setImage(ImageSource.uri(imageUri));
                }
                setupPhotoView(holder.binding, position);
                break;
            }
            case VIDEO:{
                changeTopCoverHeight(holder, true);
                holder.binding.topShadowCover.bringToFront();
                holder.binding.timePlay.setText(TimeUtil.formatTimeToHHMMSS(0) + " / " + TimeUtil.formatTimeToHHMMSS(0));
                holder.binding.playControl.bringToFront();
                initializePlayer(holder.binding, position);
                initializeSeekBar(holder.binding, position);
                setupPlayerView(holder.binding, position);
                break;
            }
        }
    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        super.onViewRecycled(holder);
        viewHolderMap.values().remove(holder);
    }

    public Map<Integer, ViewHolder> getViewHolders() {
        return viewHolderMap;
    }

    private void resetView(ViewHolder holder) {
        holder.binding.photoView.setVisibility(View.GONE);
        holder.binding.loadFailedView.setVisibility(View.GONE);
        holder.binding.playControl.setVisibility(View.GONE);
        holder.binding.playerView.setVisibility(View.GONE);
        holder.binding.loadingIndicator.setVisibility(View.GONE);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupPhotoView(RecyclerItemMediaBinding binding, int position) {
        SwipeDownGestureYier swipeDownGestureYier = new SwipeDownGestureYier(activity) {
            @Override
            public void onSwipeDown() {
                activity.finish();
            }
        };
        GestureDetector gestureDetector = new GestureDetector(activity, swipeDownGestureYier);
        binding.photoView.setOnTouchListener((View v, MotionEvent event) -> {
            gestureDetector.onTouchEvent(event);
            return false;
        });
        binding.photoView.setOnClickListener(v -> {
            if(onRecyclerItemClickYier != null) onRecyclerItemClickYier.onRecyclerItemClick(position, binding.photoView);
        });
        binding.photoView.setOnStateChangedListener(new SubsamplingScaleImageView.OnStateChangedListener() {
            @Override
            public void onScaleChanged(float newScale, int origin) {
                if(onRecyclerItemActionYier != null) onRecyclerItemActionYier.onRecyclerItemAction(position, newScale != binding.photoView.getMinScale());
                swipeDownGestureYier.setEnabled(Utils.approximatelyEqual(newScale, binding.photoView.getMinScale(), 0.001F));
            }

            @Override
            public void onCenterChanged(PointF newCenter, int origin) {

            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupPlayerView(RecyclerItemMediaBinding binding, int position){
        binding.playerView.setOnClickListener(v -> {
            if(onRecyclerItemClickYier != null) onRecyclerItemClickYier.onRecyclerItemClick(position, binding.playerView);
        });
        SwipeDownGestureYier swipeDownGestureYier = new SwipeDownGestureYier(activity) {
            @Override
            public void onSwipeDown() {
                activity.finish();
            }
        };
        GestureDetector gestureDetector = new GestureDetector(activity, swipeDownGestureYier);
        binding.playerView.setOnTouchListener((View v, MotionEvent event) -> {
            gestureDetector.onTouchEvent(event);
            return false;
        });
    }

    private void initializePlayer(RecyclerItemMediaBinding binding, int position) {
        ExoPlayer player = new ExoPlayer.Builder(activity).build();
        itemDataList.get(position).player = player;
        binding.playerView.setPlayer(player);
        binding.playerView.setVisibility(View.GONE);
        MediaItem mediaItem = MediaItem.fromUri(itemDataList.get(position).media.getUri());
        player.setMediaItem(mediaItem);

        player.addListener(new Player.Listener() {
            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                if (isPlaying) {
                    binding.playerView.setVisibility(View.VISIBLE);
                    startProgressUpdates(position);
                    binding.pauseButton.setVisibility(View.VISIBLE);
                    binding.playButton.setVisibility(View.GONE);
                } else {
                    stopProgressUpdates(position);
                    binding.pauseButton.setVisibility(View.GONE);
                    binding.playButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPlaybackStateChanged(int playbackState) {
                if (playbackState == Player.STATE_BUFFERING) {
                    binding.loadingIndicator.setVisibility(View.VISIBLE);
                } else if (playbackState == Player.STATE_READY) {
                    binding.loadingIndicator.setVisibility(View.GONE);
                } else if (playbackState == Player.STATE_ENDED) {
                    stopProgressUpdates(position);
                }
            }
        });

        binding.playButton.setOnClickListener(v -> {
            if (player.getPlaybackState() == Player.STATE_ENDED) {
                player.seekTo(0);
                player.prepare();
            }
            player.play();
        });
        binding.pauseButton.setOnClickListener(v -> player.pause());
    }

    private void initializeSeekBar(RecyclerItemMediaBinding binding, int position) {
        binding.seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int[] position1 = {position};
                removedPositions.forEach(removedPosition -> {
                    if(removedPosition < position1[0]) position1[0]--;
                });
                ExoPlayer player = itemDataList.get(position1[0]).player;
                if (fromUser && player != null) {
                    long duration = player.getDuration();
                    player.seekTo((long) ((progress / (double) SEEKBAR_MAX) * duration));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                stopProgressUpdates(position);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                startProgressUpdates(position);
            }
        });

        itemDataList.get(position).updateProgressAction = () -> {
            updateProgress(binding, position);
        };
    }

    private void startProgressUpdates(int position) {
        int[] position1 = {position};
        removedPositions.forEach(removedPosition -> {
            if(removedPosition < position1[0]) position1[0]--;
        });
        if(position1[0] >= itemDataList.size()) return;
        new Handler().post(itemDataList.get(position1[0]).updateProgressAction);
    }

    private void stopProgressUpdates(int position) {
        int[] position1 = {position};
        removedPositions.forEach(removedPosition -> {
            if(removedPosition < position1[0]) position1[0]--;
        });
        if(position1[0] >= itemDataList.size()) return;
        new Handler().removeCallbacks(itemDataList.get(position1[0]).updateProgressAction);
    }

    private void updateProgress(RecyclerItemMediaBinding binding, int position) {
        try {
            int[] position1 = {position};
            removedPositions.forEach(removedPosition -> {
                if(removedPosition < position1[0]) position1[0]--;
            });
            ExoPlayer player = itemDataList.get(position1[0]).player;
            if (player != null && player.isPlaying()) {
                if(activity.isPureContent()){
                    UiUtil.setViewVisibility(binding.playControl, View.GONE);
                }else {
                    UiUtil.setViewVisibility(binding.playControl, View.VISIBLE);
                }
                binding.seekbar.setMax(SEEKBAR_MAX);
                long currentPosition = player.getCurrentPosition();
                long duration = player.getDuration();
                int progress = (int) ((currentPosition / (double) duration) * SEEKBAR_MAX);
                binding.seekbar.setProgress(progress);
                binding.timePlay.setText(TimeUtil.formatTimeToHHMMSS(currentPosition) + " / " + TimeUtil.formatTimeToHHMMSS(duration));
                new Handler().postDelayed(itemDataList.get(position1[0]).updateProgressAction, 1);
            }
        }catch (Exception e){
            ErrorLogger.log(e);
        }
    }

    public void startPlayer(int position) {
        ExoPlayer player = itemDataList.get(position).player;
        if (player != null) {
            if (player.getPlaybackState() == Player.STATE_ENDED) {
                player.seekTo(0);
            }
            player.prepare();
            player.play();
        }
    }

    public void pausePlayer(int position) {
        ExoPlayer player = itemDataList.get(position).player;
        if (player != null) {
            player.pause();
        }
    }

    public void releasePlayer(int position) {
        ExoPlayer player = itemDataList.get(position).player;
        if (player != null) {
            player.release();
        }
    }

    public void releaseAllPlayer(){
        for (int i = 0; i < itemDataList.size(); i++) {
            releasePlayer(i);
        }
    }

    public void setOnRecyclerItemActionYier(RecyclerItemYiers.OnRecyclerItemActionYier onRecyclerItemActionYier) {
        this.onRecyclerItemActionYier = onRecyclerItemActionYier;
    }

    public void setOnRecyclerItemClickYier(RecyclerItemYiers.OnRecyclerItemClickYier onRecyclerItemClickYier) {
        this.onRecyclerItemClickYier = onRecyclerItemClickYier;
    }

    public List<ItemData> getItemDataList() {
        return itemDataList;
    }

    public void changeTopCoverHeight(ViewHolder holder, boolean big){
        int height;
        if(big){
            height = UiUtil.dpToPx(activity, 150);
        }else {
            height = UiUtil.dpToPx(activity, 110);
        }
        UiUtil.setViewHeight(holder.binding.topShadowCover, height);
    }

    public void removeItem(int position){
        removedPositions.add(position);
        itemDataList.remove(position);
        notifyItemRemoved(position);
    }
}
