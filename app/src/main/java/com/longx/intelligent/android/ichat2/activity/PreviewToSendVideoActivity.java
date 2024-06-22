package com.longx.intelligent.android.ichat2.activity;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;

import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.activity.helper.BaseActivity;
import com.longx.intelligent.android.ichat2.databinding.ActivityPreviewToSendVideoBinding;
import com.longx.intelligent.android.ichat2.media.helper.MediaStoreHelper;
import com.longx.intelligent.android.ichat2.util.ColorUtil;
import com.longx.intelligent.android.ichat2.util.ErrorLogger;
import com.longx.intelligent.android.ichat2.util.WindowAndSystemUiUtil;

import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.libvlc.interfaces.IMedia;

import java.util.Timer;
import java.util.TimerTask;

public class PreviewToSendVideoActivity extends BaseActivity {
    private ActivityPreviewToSendVideoBinding binding;
    private Uri uri;
    private LibVLC libVLC;
    private MediaPlayer mediaPlayer;
    private long savedPosition = -1;
    private boolean isMediaPrepared;
    private boolean pureVideo;
    private Timer timer;
    private static final int SEEKBAR_MAX = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPreviewToSendVideoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        WindowAndSystemUiUtil.checkAndExtendContentUnderSystemBars(this, null, null,
                ColorUtil.getAttrColor(this, com.google.android.material.R.attr.colorSurfaceContainer));
        setupDefaultBackNavigation(binding.toolbar, getColor(R.color.white));
        uri = getIntent().getParcelableExtra(ExtraKeys.URI);
        binding.appBarLayout.bringToFront();
        initializePlayer();
        setupYiers();
    }

    private void initializePlayer() {
        libVLC = new LibVLC(this);
        mediaPlayer = new MediaPlayer(libVLC);
        String realPath = MediaStoreHelper.getRealPathFromURI(this, uri);
        if (realPath != null) {
            Media media = new Media(libVLC, realPath);
            mediaPlayer.setMedia(media);
        }
        mediaPlayer.setEventListener(event -> {
            switch (event.type){
                case MediaPlayer.Event.Vout: {
                    IMedia.VideoTrack videoTrack = mediaPlayer.getCurrentVideoTrack();
                    if (videoTrack != null) {
                        int videoWidth = videoTrack.width;
                        int videoHeight = videoTrack.height;
                        binding.videoLayout.setVideoSize(videoWidth, videoHeight);
                    }
                    break;
                }
                case MediaPlayer.Event.Paused:{
                    binding.pauseButton.setVisibility(View.GONE);
                    binding.playButton.setVisibility(View.VISIBLE);
                    stopProgressUpdate();
                    break;
                }
                case MediaPlayer.Event.Playing:{
                    binding.pauseButton.setVisibility(View.VISIBLE);
                    binding.playButton.setVisibility(View.GONE);
                    startProgressUpdate();
                    break;
                }
                case MediaPlayer.Event.EncounteredError:
                    isMediaPrepared = false;
                    break;
                case MediaPlayer.Event.Opening:
                    isMediaPrepared = true;
                    break;
                case MediaPlayer.Event.EndReached:
                    isMediaPrepared = false;
                    binding.pauseButton.setVisibility(View.GONE);
                    binding.playButton.setVisibility(View.VISIBLE);
                    stopProgressUpdate();
                    runOnUiThread(() -> {binding.seekbar.setProgress(binding.seekbar.getMax());});
                    break;
            }
        });
        binding.playButton.setOnClickListener(v -> {
            if (!isMediaPrepared) {
                if (realPath != null) {
                    Media media = new Media(libVLC, realPath);
                    mediaPlayer.setMedia(media);
                    mediaPlayer.play();
                }
            } else {
                mediaPlayer.play();
            }
        });
        binding.pauseButton.setOnClickListener(v -> mediaPlayer.pause());
        binding.seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    if (!isMediaPrepared) {
                        if (realPath != null) {
                            Media media = new Media(libVLC, realPath);
                            mediaPlayer.setMedia(media);
                            mediaPlayer.play();
                        }
                    }
                    mediaPlayer.setTime((long) ((progress / (double) SEEKBAR_MAX) * mediaPlayer.getLength()));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                stopProgressUpdate();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                startProgressUpdate();
            }
        });
    }

    private void startProgressUpdate() {
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                updateSeekbarPosition();
            }
        }, 0, 1000);
    }

    private void updateSeekbarPosition() {
        runOnUiThread(() -> {
            if (mediaPlayer != null) {
                binding.seekbar.setMax(SEEKBAR_MAX);
                long currentPosition = mediaPlayer.getTime();
                long duration = mediaPlayer.getLength();
                int position = (int) ((currentPosition / (double) duration) * SEEKBAR_MAX);
                binding.seekbar.setProgress(position);
            }
        });
    }

    private void stopProgressUpdate() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        try {
            updateSeekbarPosition();
        }catch (Exception ignore){}
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null) {
            mediaPlayer.detachViews();
            savedPosition = (int) mediaPlayer.getTime();
            mediaPlayer.pause();
        }
        stopProgressUpdate();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mediaPlayer.attachViews(binding.videoLayout, null, false, false);
        if (savedPosition != -1) {
            mediaPlayer.setTime(savedPosition);
        }
        mediaPlayer.play();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.detachViews();
            mediaPlayer.release();
        }
        if (libVLC != null) {
            libVLC.release();
        }
        stopProgressUpdate();
    }

    private void setupYiers() {
        binding.videoLayout.setOnClickListener(v -> {
            setPureVideo(!pureVideo);
        });
    }

    private void setPureVideo(boolean purePhoto) {
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