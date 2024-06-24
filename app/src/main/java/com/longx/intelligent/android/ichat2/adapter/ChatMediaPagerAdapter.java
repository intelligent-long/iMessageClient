package com.longx.intelligent.android.ichat2.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import java.util.List;

/**
 * Created by LONG on 2024/5/28 at 9:24 PM.
 */
public class ChatMediaPagerAdapter extends RecyclerView.Adapter<ChatMediaPagerAdapter.ViewHolder> {
    private final ChatMediaActivity activity;
    private final ViewPager2 viewPager;
    private final List<ChatMessage> chatMessages;
    private RecyclerItemYiers.OnRecyclerItemActionYier onRecyclerItemActionYier;
    private RecyclerItemYiers.OnRecyclerItemClickYier onRecyclerItemClickYier;
    private ExoPlayer player;
    private Handler handler;
    private static final int SEEKBAR_MAX = 10000;
    private Runnable updateProgressAction;

    public ChatMediaPagerAdapter(ChatMediaActivity activity, ViewPager2 viewPager, List<ChatMessage> chatMessages) {
        this.activity = activity;
        this.viewPager = viewPager;
        this.chatMessages = chatMessages;
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
        return chatMessages.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        resetView(holder);
        ChatMessage chatMessage = chatMessages.get(position);
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
        player = new ExoPlayer.Builder(activity).build();
        binding.playerView.setPlayer(player);
        binding.playerView.setVisibility(View.GONE);
        binding.playControl.setVisibility(View.VISIBLE);
        binding.playControl.bringToFront();
        MediaItem mediaItem = MediaItem.fromUri(Uri.fromFile(new File(chatMessages.get(position).getVideoFilePath())));
        player.setMediaItem(mediaItem);

        player.addListener(new Player.Listener() {
            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                if (isPlaying) {
                    binding.playerView.setVisibility(View.VISIBLE);
                    startProgressUpdates();
                    binding.pauseButton.setVisibility(View.VISIBLE);
                    binding.playButton.setVisibility(View.GONE);
                } else {
                    stopProgressUpdates();
                    binding.pauseButton.setVisibility(View.GONE);
                    binding.playButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPlaybackStateChanged(int playbackState) {
                if (playbackState == Player.STATE_ENDED) {
                    stopProgressUpdates();
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
                if (fromUser && player != null) {
                    long duration = player.getDuration();
                    player.seekTo((long) ((progress / (double) SEEKBAR_MAX) * duration));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                stopProgressUpdates();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                startProgressUpdates();
            }
        });

        handler = new Handler();
        updateProgressAction = () -> updateProgress(binding);
    }

    private void startProgressUpdates() {
        handler.post(updateProgressAction);
    }

    private void stopProgressUpdates() {
        handler.removeCallbacks(updateProgressAction);
    }

    private void updateProgress(RecyclerItemChatMediaBinding binding) {
        if (player != null && player.isPlaying()) {
            binding.seekbar.setMax(SEEKBAR_MAX);
            long currentPosition = player.getCurrentPosition();
            long duration = player.getDuration();
            int position = (int) ((currentPosition / (double) duration) * SEEKBAR_MAX);
            binding.seekbar.setProgress(position);
            binding.timePlay.setText(TimeUtil.formatTime(currentPosition) + " / " + TimeUtil.formatTime(duration));
            handler.postDelayed(updateProgressAction, 1);
        }
    }

    public void startPlayer() {
        if (player != null) {
            player.prepare();
            player.play();
        }
    }

    public void pausePlayer() {
        if (player != null) {
            player.pause();
        }
    }

    public void releasePlayer() {
        if (player != null) {
            player.release();
            player = null;
        }
    }

    public void setOnRecyclerItemActionYier(RecyclerItemYiers.OnRecyclerItemActionYier onRecyclerItemActionYier) {
        this.onRecyclerItemActionYier = onRecyclerItemActionYier;
    }

    public void setOnRecyclerItemClickYier(RecyclerItemYiers.OnRecyclerItemClickYier onRecyclerItemClickYier) {
        this.onRecyclerItemClickYier = onRecyclerItemClickYier;
    }

    public List<ChatMessage> getChatMessages() {
        return chatMessages;
    }

    private RecyclerView.ViewHolder getViewHolderAtPosition(int position) {
        RecyclerView recyclerView = (RecyclerView) viewPager.getChildAt(0);
        return recyclerView.findViewHolderForAdapterPosition(position);
    }

    public void checkAndPlayAtPosition(int position){
        pausePlayer();
        releasePlayer();
        if(chatMessages.get(position).getType() == ChatMessage.TYPE_VIDEO){
            ChatMediaPagerAdapter.ViewHolder viewHolder = (ViewHolder) getViewHolderAtPosition(position);
            initializePlayer(viewHolder.binding, position);
            initializeSeekBar(viewHolder.binding, position);
            startPlayer();
        }
    }
}
