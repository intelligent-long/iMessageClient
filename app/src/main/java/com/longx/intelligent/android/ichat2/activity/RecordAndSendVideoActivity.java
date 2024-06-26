package com.longx.intelligent.android.ichat2.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.FileProvider;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.activity.helper.BaseActivity;
import com.longx.intelligent.android.ichat2.behavior.MessageDisplayer;
import com.longx.intelligent.android.ichat2.da.FileAccessHelper;
import com.longx.intelligent.android.ichat2.da.publicfile.PublicFileAccessor;
import com.longx.intelligent.android.ichat2.databinding.ActivityRecordAndSendVideoBinding;
import com.longx.intelligent.android.ichat2.util.ColorUtil;
import com.longx.intelligent.android.ichat2.util.ErrorLogger;
import com.longx.intelligent.android.ichat2.util.TimeUtil;
import com.longx.intelligent.android.ichat2.util.WindowAndSystemUiUtil;

import java.io.File;
import java.io.IOException;

public class RecordAndSendVideoActivity extends BaseActivity {
    private ActivityRecordAndSendVideoBinding binding;
    private ActivityResultLauncher<Intent> recordVideoLauncher;
    private Uri videoUri;
    private File videoFile;
    private boolean purePhoto;
    private ExoPlayer player;
    private Handler handler;
    private Runnable updateProgressAction;
    private static final int SEEKBAR_MAX = 10000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRecordAndSendVideoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        WindowAndSystemUiUtil.extendContentUnderSystemBars(this, null, null,
                ColorUtil.getAttrColor(this, com.google.android.material.R.attr.colorSurfaceContainer));
        setupDefaultBackNavigation(binding.toolbar, getColor(R.color.white));
        recordVideoLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        videoFile = FileAccessHelper.detectAndRenameFile(videoFile);
                        if(videoFile == null){
                            MessageDisplayer.autoShow(this, "创建文件失败", MessageDisplayer.Duration.LONG);
                        }else {
                            getVideoUri(videoFile);
                            showContent();
                            setupYiers();
                        }
                    }else {
                        finish();
                    }
                }
        );
        openCameraForVideo();
    }

    private void openCameraForVideo() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            try {
                videoFile = createVideoFile();
                getVideoUri(videoFile);
                takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, videoUri);
                recordVideoLauncher.launch(takeVideoIntent);
            } catch (IOException e) {
                ErrorLogger.log(getClass(), e);
                MessageDisplayer.autoShow(this, "创建文件失败", MessageDisplayer.Duration.LONG);
            }
        }
    }

    private void getVideoUri(File file) {
        videoUri = FileProvider.getUriForFile(this,
                getApplicationContext().getPackageName() + ".provider",
                file);
    }

    private File createVideoFile() throws IOException {
        return PublicFileAccessor.CapturedMedia.createVideoFile();
    }

    private void setupYiers() {
        binding.toolbar.setOnMenuItemClickListener(item -> {
            if(item.getItemId() == R.id.send){
                Intent intent = new Intent();
                intent.putExtra(ExtraKeys.URIS, new Uri[]{videoUri});
                setResult(RESULT_OK, intent);
                finish();
            }
            return true;
        });
    }

    private void showContent() {
        setupVideoView();
        showVideo();
    }

    private void setupVideoView() {
        binding.appBar.bringToFront();
        binding.playControl.bringToFront();
        binding.playerView.setOnClickListener(v -> {
            setPurePhoto(!purePhoto);
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

    private void setPurePhoto(boolean purePhoto) {
        if(purePhoto){
            binding.appBar.setVisibility(View.GONE);
            binding.playControl.setVisibility(View.GONE);
            WindowAndSystemUiUtil.setSystemUIShown(this, false);
            this.purePhoto = true;
        }else {
            binding.appBar.setVisibility(View.VISIBLE);
            binding.playControl.setVisibility(View.VISIBLE);
            WindowAndSystemUiUtil.setSystemUIShown(this, true);
            this.purePhoto = false;
        }
    }

    private void showVideo() {
        initializePlayer();
        initializeSeekBar();
    }

    private void initializePlayer() {
        player = new ExoPlayer.Builder(this).build();
        binding.playerView.setPlayer(player);
        binding.playerView.setVisibility(View.GONE);
        MediaItem mediaItem = MediaItem.fromUri(videoUri);
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
    }

    private void initializeSeekBar() {
        binding.playControl.setVisibility(View.VISIBLE);
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
}