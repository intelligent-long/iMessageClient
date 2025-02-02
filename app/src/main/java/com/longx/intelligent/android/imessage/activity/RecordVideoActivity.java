package com.longx.intelligent.android.imessage.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.FileProvider;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.imessage.activity.helper.BaseActivity;
import com.longx.intelligent.android.imessage.behaviorcomponents.MessageDisplayer;
import com.longx.intelligent.android.imessage.da.FileHelper;
import com.longx.intelligent.android.imessage.da.publicfile.PublicFileAccessor;
import com.longx.intelligent.android.imessage.databinding.ActivityRecordVideoBinding;
import com.longx.intelligent.android.imessage.media.data.MediaInfo;
import com.longx.intelligent.android.imessage.media.helper.MediaHelper;
import com.longx.intelligent.android.imessage.media.helper.MediaStoreHelper;
import com.longx.intelligent.android.imessage.util.ColorUtil;
import com.longx.intelligent.android.imessage.util.ErrorLogger;
import com.longx.intelligent.android.imessage.util.TimeUtil;
import com.longx.intelligent.android.imessage.util.WindowAndSystemUiUtil;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class RecordVideoActivity extends BaseActivity {
    private ActivityRecordVideoBinding binding;
    private ActivityResultLauncher<Intent> recordVideoLauncher;
    private Uri videoUri;
    private File videoFile;
    private boolean purePhoto;
    private ExoPlayer player;
    private Handler handler;
    private Runnable updateProgressAction;
    private static final int SEEKBAR_MAX = 10000;
    private boolean remove;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setFontThemes(R.style.DarkStatusBarActivity_Font1, R.style.DarkStatusBarActivity_Font2);
        super.onCreate(savedInstanceState);
        binding = ActivityRecordVideoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        WindowAndSystemUiUtil.extendContentUnderSystemBars(this, null, null,
                ColorUtil.getAttrColor(this, com.google.android.material.R.attr.colorSurfaceContainer));
        setupBackNavigation(binding.toolbar, getColor(R.color.white));
        binding.appBar.bringToFront();
        intentData();
        recordVideoLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        videoFile = FileHelper.detectAndRenameFile(videoFile);
                        if(videoFile == null){
                            MessageDisplayer.autoShow(this, "创建文件失败", MessageDisplayer.Duration.LONG);
                        }else {
                            getVideoUri(videoFile);
                            showContent();
                            setupYiers();
                        }
                    }else {
                        videoFile.delete();
                        finish();
                    }
                }
        );
        openCameraForVideo();
    }

    private void intentData() {
        remove = getIntent().getBooleanExtra(ExtraKeys.REMOVE, false);
        int actionIconResId = getIntent().getIntExtra(ExtraKeys.RES_ID, -1);
        String menuTitle = getIntent().getStringExtra(ExtraKeys.MENU_TITLE);
        MenuItem item = binding.toolbar.getMenu().findItem(R.id.action);
        item.setIcon(actionIconResId);
        item.setTitle(menuTitle);
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
        return PublicFileAccessor.CapturedMedia.createVideoFile(this);
    }

    private void setupYiers() {
        binding.toolbar.setOnMenuItemClickListener(item -> {
            if(item.getItemId() == R.id.action){
                Intent intent = new Intent();
                MediaInfo mediaInfoFromUri = MediaStoreHelper.getMediaInfoFromUri(this,
                        Objects.requireNonNull(MediaStoreHelper.getContentUriFromFileUri(this, Uri.fromFile(videoFile))));
                if (mediaInfoFromUri.getVideoDuration() <= 0) {
                    if (player != null) {
                        mediaInfoFromUri.setVideoDuration(player.getDuration());
                    } else {
                        mediaInfoFromUri.setVideoDuration(MediaHelper.getVideoDuration(videoFile.getPath()));
                    }
                }
                intent.putExtra(ExtraKeys.MEDIA_INFOS, new MediaInfo[]{mediaInfoFromUri});
                intent.putExtra(ExtraKeys.REMOVE, remove);
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
            WindowAndSystemUiUtil.setSystemUiShown(this, false);
            this.purePhoto = true;
        }else {
            binding.appBar.setVisibility(View.VISIBLE);
            binding.playControl.setVisibility(View.VISIBLE);
            WindowAndSystemUiUtil.setSystemUiShown(this, true);
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
            binding.timePlay.setText(TimeUtil.formatTimeToHHMMSS(currentPosition) + " / " + TimeUtil.formatTimeToHHMMSS(duration));
            handler.postDelayed(updateProgressAction, 1);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(player != null) {
            if (player.getPlaybackState() == Player.STATE_ENDED) {
                player.seekTo(0);
            }
            player.prepare();
            player.play();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(player != null) {
            player.pause();
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
}