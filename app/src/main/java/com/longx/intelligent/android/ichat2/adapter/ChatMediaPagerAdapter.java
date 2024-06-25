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
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.longx.intelligent.android.ichat2.activity.ChatMediaActivity;
import com.longx.intelligent.android.ichat2.data.ChatMessage;
import com.longx.intelligent.android.ichat2.databinding.RecyclerItemChatMediaBinding;
import com.longx.intelligent.android.ichat2.ui.SwipeDownGestureYier;
import com.longx.intelligent.android.ichat2.util.ErrorLogger;
import com.longx.intelligent.android.ichat2.util.TimeUtil;
import com.longx.intelligent.android.ichat2.yier.RecyclerItemYiers;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by LONG on 2024/5/28 at 9:24 PM.
 */
public class ChatMediaPagerAdapter extends RecyclerView.Adapter<ChatMediaPagerAdapter.ViewHolder> {
    private final ChatMediaActivity activity;
    private final List<ItemData> itemDatas;
    private RecyclerItemYiers.OnRecyclerItemActionYier onRecyclerItemActionYier;
    private RecyclerItemYiers.OnRecyclerItemClickYier onRecyclerItemClickYier;
    private Handler handler;
    private static final int SEEKBAR_MAX = 10000;

    public ChatMediaPagerAdapter(ChatMediaActivity activity, List<ChatMessage> chatMessages) {
        this.activity = activity;
        List<ItemData> itemDataList = new ArrayList<>();
        chatMessages.forEach(chatMessage -> {
            itemDataList.add(new ItemData(chatMessage));
        });
        this.itemDatas = itemDataList;
    }

    public static class ItemData{
        private final ChatMessage chatMessage;
        private ExoPlayer player;
        private Runnable updateProgressAction;

        public ItemData(ChatMessage chatMessage) {
            this.chatMessage = chatMessage;
        }

        public ChatMessage getChatMessage() {
            return chatMessage;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private RecyclerItemChatMediaBinding binding;

        public ViewHolder(RecyclerItemChatMediaBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerItemChatMediaBinding binding = RecyclerItemChatMediaBinding.inflate(activity.getLayoutInflater(), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public int getItemCount() {
        return itemDatas.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        resetView(holder);
        ChatMessage chatMessage = itemDatas.get(position).chatMessage;
        switch (chatMessage.getType()){
            case ChatMessage.TYPE_IMAGE:{
                holder.binding.photoView.setVisibility(View.VISIBLE);
                String imagePath = chatMessage.getImageFilePath();
                holder.binding.photoView.setOnImageEventListener(new SubsamplingScaleImageView.DefaultOnImageEventListener(){
                    @Override
                    public void onImageLoadError(Exception e) {
                        super.onImageLoadError(e);
                        holder.binding.photoView.setVisibility(View.GONE);
                        holder.binding.loadFailedView.setVisibility(View.VISIBLE);
                        Glide.with(activity.getApplicationContext())
                                .load(new File(imagePath))
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
                holder.binding.photoView.setImage(ImageSource.uri(Uri.fromFile(new File(imagePath))));
                setupPhotoView(holder.binding, position);
                break;
            }
            case ChatMessage.TYPE_VIDEO:{
                initializePlayer(holder.binding, position);
                initializeSeekBar(holder.binding, position);
                break;
            }
        }
    }

    private void resetView(ViewHolder holder) {
        holder.binding.photoView.setVisibility(View.GONE);
        holder.binding.loadFailedView.setVisibility(View.GONE);
        holder.binding.playControl.setVisibility(View.GONE);
        holder.binding.playerView.setVisibility(View.GONE);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupPhotoView(RecyclerItemChatMediaBinding binding, int position) {
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
                swipeDownGestureYier.setEnabled(newScale == binding.photoView.getMinScale());
            }

            @Override
            public void onCenterChanged(PointF newCenter, int origin) {

            }
        });
    }

    private void initializePlayer(RecyclerItemChatMediaBinding binding, int position) {
        ExoPlayer player = new ExoPlayer.Builder(activity).build();
        itemDatas.get(position).player = player;
        binding.playerView.setPlayer(player);
        binding.playerView.setVisibility(View.GONE);
        binding.playControl.setVisibility(View.VISIBLE);
        binding.playControl.bringToFront();
        MediaItem mediaItem = MediaItem.fromUri(Uri.fromFile(new File(itemDatas.get(position).chatMessage.getVideoFilePath())));
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
                if (playbackState == Player.STATE_ENDED) {
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
        binding.playerView.setOnClickListener(v -> {
            if(onRecyclerItemClickYier != null) onRecyclerItemClickYier.onRecyclerItemClick(position, binding.playerView);
            if(activity.isPureContent()){
                binding.playControl.setVisibility(View.GONE);
            }else {
                binding.playControl.setVisibility(View.VISIBLE);
            }
        });
    }

    private void initializeSeekBar(RecyclerItemChatMediaBinding binding, int position) {
        binding.seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                ExoPlayer player = itemDatas.get(position).player;
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

        handler = new Handler();
        itemDatas.get(position).updateProgressAction = () -> updateProgress(binding, position);
    }

    private void startProgressUpdates(int position) {
        handler.post(itemDatas.get(position).updateProgressAction);
    }

    private void stopProgressUpdates(int position) {
        handler.removeCallbacks(itemDatas.get(position).updateProgressAction);
    }

    private void updateProgress(RecyclerItemChatMediaBinding binding, int position) {
        ExoPlayer player = itemDatas.get(position).player;
        if (player != null && player.isPlaying()) {
            binding.seekbar.setMax(SEEKBAR_MAX);
            long currentPosition = player.getCurrentPosition();
            long duration = player.getDuration();
            int progress = (int) ((currentPosition / (double) duration) * SEEKBAR_MAX);
            binding.seekbar.setProgress(progress);
            binding.timePlay.setText(TimeUtil.formatTime(currentPosition) + " / " + TimeUtil.formatTime(duration));
            handler.postDelayed(itemDatas.get(position).updateProgressAction, 1);
        }
    }

    public void startPlayer(int position) {
        ExoPlayer player = itemDatas.get(position).player;
        if (player != null) {
            if (player.getPlaybackState() == Player.STATE_ENDED) {
                player.seekTo(0);
            }
            player.prepare();
            player.play();
        }
    }

    public void pausePlayer(int position) {
        ExoPlayer player = itemDatas.get(position).player;
        if (player != null) {
            player.pause();
        }
    }

    public void releasePlayer(int position) {
        ExoPlayer player = itemDatas.get(position).player;
        if (player != null) {
            player.release();
        }
    }

    public void releaseAllPlayer(){
        for (int i = 0; i < itemDatas.size(); i++) {
            releasePlayer(i);
        }
    }

    public void setOnRecyclerItemActionYier(RecyclerItemYiers.OnRecyclerItemActionYier onRecyclerItemActionYier) {
        this.onRecyclerItemActionYier = onRecyclerItemActionYier;
    }

    public void setOnRecyclerItemClickYier(RecyclerItemYiers.OnRecyclerItemClickYier onRecyclerItemClickYier) {
        this.onRecyclerItemClickYier = onRecyclerItemClickYier;
    }

    public List<ItemData> getItemDatas() {
        return itemDatas;
    }
}
