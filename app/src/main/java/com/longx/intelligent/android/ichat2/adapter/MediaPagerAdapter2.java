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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.longx.intelligent.android.ichat2.activity.MediaActivity2;
import com.longx.intelligent.android.ichat2.behaviorcomponents.GlideBehaviours;
import com.longx.intelligent.android.ichat2.databinding.PagerItemMediaBinding;
import com.longx.intelligent.android.ichat2.databinding.PagerItemMediaBinding;
import com.longx.intelligent.android.ichat2.media.MediaType;
import com.longx.intelligent.android.ichat2.media.data.Media;
import com.longx.intelligent.android.ichat2.ui.SwipeDownGestureYier;
import com.longx.intelligent.android.ichat2.util.ErrorLogger;
import com.longx.intelligent.android.ichat2.util.TimeUtil;
import com.longx.intelligent.android.ichat2.util.UiUtil;
import com.longx.intelligent.android.ichat2.util.Utils;
import com.longx.intelligent.android.ichat2.util.WindowAndSystemUiUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by LONG on 2024/12/19 at 下午5:42.
 */
public class MediaPagerAdapter2 extends PagerAdapter {
    private static final int SEEKBAR_MAX = 10000;
    private final MediaActivity2 activity;
    private final List<Media> mediaList;
    private final List<Integer> removedPositions = new ArrayList<>();
    private boolean pureContent;
    private final Map<Integer, PagerItemMediaBinding> bindingMap = new HashMap<>();
    private final ExoPlayer player;
    private boolean stopUpdateProgress;

    public MediaPagerAdapter2(MediaActivity2 activity, List<Media> mediaList){
        this.activity = activity;
        this.mediaList = new ArrayList<>(mediaList);
        player = new ExoPlayer.Builder(activity).build();
        player.addListener(new Player.Listener() {
            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                if (isPlaying) {
                    if(bindingMap.get(getCurrentItem()) != null) {
                        bindingMap.get(getCurrentItem()).playerView.setVisibility(View.VISIBLE);
                    }
                    startUpdateProgress();
                    activity.getBinding().pauseButton.setVisibility(View.VISIBLE);
                    activity.getBinding().playButton.setVisibility(View.GONE);
                } else {
                    stopUpdateProgress();
                    activity.getBinding().pauseButton.setVisibility(View.GONE);
                    activity.getBinding().playButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPlaybackStateChanged(int playbackState) {
                if (playbackState == Player.STATE_BUFFERING) {
                    activity.getBinding().loadingIndicator.setVisibility(View.VISIBLE);
                } else if (playbackState == Player.STATE_READY) {
                    activity.getBinding().loadingIndicator.setVisibility(View.GONE);
                } else if (playbackState == Player.STATE_ENDED) {
                    stopUpdateProgress();
                }
            }
        });

        activity.getBinding().playButton.setOnClickListener(v -> {
            if (player.getPlaybackState() == Player.STATE_ENDED) {
                player.seekTo(0);
                player.prepare();
            }
            player.play();
        });
        activity.getBinding().pauseButton.setOnClickListener(v -> player.pause());
    }

    @Override
    public int getCount() {
        return mediaList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        PagerItemMediaBinding binding = bindingMap.get(position);
        if (binding != null) {
            binding.playerView.setPlayer(null);
        }
        container.removeView((View) object);
        bindingMap.remove(position);
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @NonNull
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = getView(mediaList.get(position), position);
        container.addView(view);
        return view;
    }
    
    private View getView(Media media, int position){
        PagerItemMediaBinding binding;
        if(bindingMap.get(position) == null) {
            binding = PagerItemMediaBinding.inflate(activity.getLayoutInflater());
            bindingMap.put(position, binding);
        }else {
            binding = bindingMap.get(position);
        }
        if(activity.isPureContent()){
            UiUtil.setViewVisibility(activity.getBinding().topTranslucentOverlayWrap, View.GONE);
            if(media.getMediaType() == MediaType.VIDEO) {
                UiUtil.setViewVisibility(activity.getBinding().playControl, View.GONE);
            }
        }else {
            UiUtil.setViewVisibility(activity.getBinding().topTranslucentOverlayWrap, View.VISIBLE);
        }
        switch (media.getMediaType()){
            case IMAGE:{
                activity.getBinding().topTranslucentOverlayImage.setVisibility(View.VISIBLE);
                activity.getBinding().topTranslucentOverlayVideo.setVisibility(View.GONE);
                activity.getBinding().topTranslucentOverlayWrap.bringToFront();
                binding.photoView.setVisibility(View.VISIBLE);
                binding.playerView.setVisibility(View.GONE);
                binding.loadFailedView.setVisibility(View.GONE);
                Uri imageUri = media.getUri();
                binding.photoView.setOnImageEventListener(new SubsamplingScaleImageView.DefaultOnImageEventListener(){
                    @Override
                    public void onImageLoadError(Exception e) {
                        super.onImageLoadError(e);
                        binding.photoView.setVisibility(View.GONE);
                        binding.loadFailedView.setVisibility(View.VISIBLE);
                        binding.loadFailedText.bringToFront();
                        Glide.with(activity.getApplicationContext())
                                .load(imageUri)
                                .transition(DrawableTransitionOptions.withCrossFade())
                                .into(new CustomTarget<Drawable>() {
                                    @Override
                                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                        binding.spareImageView.setImageDrawable(resource);
                                    }

                                    @Override
                                    public void onLoadCleared(@Nullable Drawable placeholder) {

                                    }
                                });
                    }
                });
                if(activity.isGlideLoad()) {
                    GlideBehaviours.loadToFile(activity.getApplicationContext(), imageUri, new CustomTarget<File>() {
                        @Override
                        public void onResourceReady(@NonNull File resource, @Nullable Transition<? super File> transition) {
                            binding.photoView.setImage(ImageSource.uri(Uri.fromFile(resource)));
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                        }
                    }, true);
                }else {
                    binding.photoView.setImage(ImageSource.uri(imageUri));
                }
                setupPhotoView(binding, position);
                break;
            }
            case VIDEO:{
                activity.getBinding().topTranslucentOverlayVideo.setVisibility(View.VISIBLE);
                activity.getBinding().topTranslucentOverlayImage.setVisibility(View.GONE);
                activity.getBinding().topTranslucentOverlayWrap.bringToFront();
                activity.getBinding().timePlay.setText(TimeUtil.formatTimeToHHMMSS(0) + " / " + TimeUtil.formatTimeToHHMMSS(0));
                activity.getBinding().playControl.bringToFront();
                binding.photoView.setVisibility(View.GONE);
                binding.playerView.setVisibility(View.VISIBLE);
                binding.loadFailedView.setVisibility(View.GONE);
                setupPlayerView(binding, position);
                break;
            }
        }
        return binding.getRoot();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupPhotoView(PagerItemMediaBinding binding, int position) {
        binding.photoView.setOnClickListener(v -> {
            setPureContent(!pureContent);
        });
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
        binding.photoView.setOnStateChangedListener(new SubsamplingScaleImageView.OnStateChangedListener() {
            @Override
            public void onScaleChanged(float newScale, int origin) {
                setPureContent(true);
                swipeDownGestureYier.setEnabled(Utils.approximatelyEqual(newScale, binding.photoView.getMinScale(), 0.001F));
            }

            @Override
            public void onCenterChanged(PointF newCenter, int origin) {

            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupPlayerView(PagerItemMediaBinding binding, int position){
        binding.playerView.setOnClickListener(v -> {
            setPureContent(!pureContent);
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

    private void startUpdateProgress(){
        stopUpdateProgress = false;
        doUpdateProgress();
    }

    private void stopUpdateProgress(){
        stopUpdateProgress = true;
    }

    @SuppressLint("SetTextI18n")
    private void doUpdateProgress() {
        if(stopUpdateProgress) return;
        try {
            if (player != null && player.isPlaying()) {
                if(activity.isPureContent()){
                    UiUtil.setViewVisibility(activity.getBinding().playControl, View.GONE);
                }else {
                    UiUtil.setViewVisibility(activity.getBinding().playControl, View.VISIBLE);
                }
                activity.getBinding().seekbar.setMax(SEEKBAR_MAX);
                long currentPosition = player.getCurrentPosition();
                long duration = player.getDuration();
                int progress = (int) ((currentPosition / (double) duration) * SEEKBAR_MAX);
                activity.getBinding().seekbar.setProgress(progress);
                activity.getBinding().timePlay.setText(TimeUtil.formatTimeToHHMMSS(currentPosition) + " / " + TimeUtil.formatTimeToHHMMSS(duration));
                new Handler().postDelayed(this::doUpdateProgress, 1);
            }
        }catch (Exception e){
            ErrorLogger.log(e);
        }
    }

    public void removeItem(int position){
        removedPositions.add(position);
        mediaList.remove(position);
        bindingMap.remove(position);
        Map<Integer, PagerItemMediaBinding> newBindingMap = new HashMap<>();
        for (Map.Entry<Integer, PagerItemMediaBinding> entry : bindingMap.entrySet()) {
            int oldPosition = entry.getKey();
            PagerItemMediaBinding value = entry.getValue();
            if (oldPosition > position) {
                newBindingMap.put(oldPosition - 1, value);
            } else {
                newBindingMap.put(oldPosition, value);
            }
        }
        bindingMap.clear();
        bindingMap.putAll(newBindingMap);
        notifyDataSetChanged();
    }

    public void setPureContent(boolean pureContent){
        PagerItemMediaBinding binding = bindingMap.get(getCurrentItem());
        if(binding == null) return;
        if(pureContent){
            UiUtil.setViewVisibility(activity.getBinding().appBar, View.GONE);
            WindowAndSystemUiUtil.setSystemUIShown(activity, false);
            activity.getBinding().topTranslucentOverlayWrap.setVisibility(View.GONE);
            if(mediaList.get(getCurrentItem()).getMediaType() == MediaType.VIDEO) {
                UiUtil.setViewVisibility(activity.getBinding().playControl, View.GONE);
            }
            this.pureContent = true;
        }else {
            UiUtil.setViewVisibility(activity.getBinding().appBar, View.VISIBLE);
            WindowAndSystemUiUtil.setSystemUIShown(activity, true);
            activity.getBinding().topTranslucentOverlayWrap.setVisibility(View.VISIBLE);
            if(mediaList.get(getCurrentItem()).getMediaType() == MediaType.VIDEO) {
                UiUtil.setViewVisibility(activity.getBinding().playControl, View.VISIBLE);
            }
            this.pureContent = false;
        }
    }

    public void startPlayer() {
        if (player != null) {
            Media media = mediaList.get(getCurrentItem());
            if(!media.getMediaType().equals(MediaType.VIDEO)) return;
            PagerItemMediaBinding binding = bindingMap.get(getCurrentItem());
            if (binding == null) return;
            MediaItem mediaItem = MediaItem.fromUri(media.getUri());
            binding.playerView.setPlayer(player);
            binding.playerView.setVisibility(View.GONE);
            if (player.getPlaybackState() == Player.STATE_ENDED) {
                player.seekTo(0);
            }
            player.setMediaItem(mediaItem);
            player.prepare();
            player.play();
        }
    }

    public void pausePlayer() {
        if (player != null) {
            player.pause();
        }
        PagerItemMediaBinding binding = bindingMap.get(getCurrentItem());
        if (binding != null) {
            binding.playerView.setPlayer(null);
        }
    }

    public void releasePlayer() {
        if (player != null) {
            player.release();
        }
    }

    private int getCurrentItem(){
        return activity.getBinding().viewPager.getCurrentItem();
    }

    public boolean isPureContent() {
        return pureContent;
    }

    public List<Media> getMediaList() {
        return mediaList;
    }

    public Map<Integer, PagerItemMediaBinding> getBindingMap() {
        return bindingMap;
    }
}
