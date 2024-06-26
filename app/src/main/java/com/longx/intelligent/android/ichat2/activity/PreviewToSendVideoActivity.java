package com.longx.intelligent.android.ichat2.activity;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.SeekBar;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.activity.helper.BaseActivity;
import com.longx.intelligent.android.ichat2.databinding.ActivityPreviewToSendVideoBinding;
import com.longx.intelligent.android.ichat2.util.ColorUtil;
import com.longx.intelligent.android.ichat2.util.TimeUtil;
import com.longx.intelligent.android.ichat2.util.WindowAndSystemUiUtil;

public class PreviewToSendVideoActivity extends BaseActivity {
    private ActivityPreviewToSendVideoBinding binding;
    private Uri uri;
    private ExoPlayer player;
    private Handler handler;
    private Runnable updateProgressAction;
    private static final int SEEKBAR_MAX = 10000;
    private boolean pureVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPreviewToSendVideoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        WindowAndSystemUiUtil.extendContentUnderSystemBars(this, null, null,
                ColorUtil.getAttrColor(this, com.google.android.material.R.attr.colorSurfaceContainer));
        setupDefaultBackNavigation(binding.toolbar, getColor(R.color.white));
        uri = getIntent().getParcelableExtra(ExtraKeys.URI);
        binding.appBarLayout.bringToFront();

        initializePlayer();
        initializeSeekBar();
    }

    private void initializePlayer() {
        player = new ExoPlayer.Builder(this).build();
        binding.playerView.setPlayer(player);
        binding.playerView.setVisibility(View.GONE);
        MediaItem mediaItem = MediaItem.fromUri(uri);
        player.setMediaItem(mediaItem);
        player.prepare();
        player.play();

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
            setPurePhoto(!pureVideo);
        });
    }

    private void initializeSeekBar() {
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
        updateProgressAction = this::updateProgress;
    }

    private void startProgressUpdates() {
        handler.post(updateProgressAction);
    }

    private void stopProgressUpdates() {
        handler.removeCallbacks(updateProgressAction);
    }

    private void updateProgress() {
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            stopProgressUpdates();
            player.release();
            player = null;
        }
    }

    private void setPurePhoto(boolean purePhoto) {
        if(purePhoto){
            binding.appBarLayout.setVisibility(View.GONE);
            WindowAndSystemUiUtil.setSystemUIShown(this, false);
            this.pureVideo = true;
        }else {
            binding.appBarLayout.setVisibility(View.VISIBLE);
            WindowAndSystemUiUtil.setSystemUIShown(this, true);
            this.pureVideo = false;
        }
    }
}