package com.longx.intelligent.android.ichat2.behavior;

import android.app.Activity;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.HapticFeedbackConstants;

import com.longx.intelligent.android.ichat2.activity.ChatActivity;
import com.longx.intelligent.android.ichat2.util.ErrorLogger;

public class ChatVoicePlayer {
    private MediaPlayer mediaPlayer;
    private final ChatActivity chatActivity;
    private final AudioManager audioManager;
    private final SensorManager sensorManager;
    private final Sensor proximitySensor;
    private final SensorEventListener proximitySensorYier;
    private OnPlayStateChangeYier onPlayStateChangeYier;
    private String id;
    private Uri uri;
    private boolean earpieceNow;

    public interface OnPlayStateChangeYier {
        void onStart(String id);
        void onPause(String id);
        void onStop(String id, boolean complete);
        void onError(String id, int what, int extra);
    }

    public ChatVoicePlayer(ChatActivity chatActivity) {
        this.chatActivity = chatActivity;
        audioManager = (AudioManager) chatActivity.getSystemService(Context.AUDIO_SERVICE);
        sensorManager = (SensorManager) chatActivity.getSystemService(Context.SENSOR_SERVICE);
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        proximitySensorYier = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (proximitySensor != null) {
                    if (event.values[0] < proximitySensor.getMaximumRange()) {
                        earpieceNow = true;
                        switchToEarpiece();
                    } else {
                        earpieceNow = false;
                        switchToSpeaker();
                    }
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };
    }

    public void setOnPlayStateChangeYier(OnPlayStateChangeYier listener) {
        this.onPlayStateChangeYier = listener;
    }

    public void init(Uri uri, String id) {
        release();
        this.id = id;
        this.uri = uri;
        mediaPlayer = MediaPlayer.create(chatActivity, uri);
        setAudioAttributes(AudioManager.STREAM_MUSIC);
        setupYiers();
    }

    public void play() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            registerProximitySensor();
            if (onPlayStateChangeYier != null) {
                onPlayStateChangeYier.onStart(id);
            }
        }
    }

    public void pause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            unregisterProximitySensor();
            if (onPlayStateChangeYier != null) {
                onPlayStateChangeYier.onPause(id);
            }
        }
    }

    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            unregisterProximitySensor();
            if (onPlayStateChangeYier != null) {
                onPlayStateChangeYier.onStop(id, false);
            }
        }
    }

    public State release() {
        State state = null;
        if (mediaPlayer != null) {
            mediaPlayer.pause();
            state = getState();
            mediaPlayer.release();
            if (onPlayStateChangeYier != null) {
                onPlayStateChangeYier.onStop(id, false);
            }
            mediaPlayer = null;
            id = null;
        }
        unregisterProximitySensor();
        return state;
    }

    public void seekTo(int position){
        if(mediaPlayer != null) mediaPlayer.seekTo(position);
    }

    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }

    public boolean isPaused(){
        return mediaPlayer != null && !mediaPlayer.isPlaying() && getPlaybackPosition() != -1 && getPlaybackPosition() != getDuration();
    }

    public int getPlaybackPosition(){
        return mediaPlayer == null ? -1 : mediaPlayer.getCurrentPosition();
    }

    public int getDuration(){
        return mediaPlayer == null ? -1 : mediaPlayer.getDuration();
    }

    private void switchToEarpiece() {
        audioManager.setSpeakerphoneOn(false);
        audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        setAudioAttributes(AudioManager.STREAM_VOICE_CALL);
        Log.d(getClass().getName(), "Switched to earpiece");
    }

    private void switchToSpeaker() {
        audioManager.setSpeakerphoneOn(true);
        audioManager.setMode(AudioManager.MODE_NORMAL);
        setAudioAttributes(AudioManager.STREAM_MUSIC);
        Log.d(getClass().getName(), "Switched to speaker");
    }

    private void setAudioAttributes(int streamType) {
        if (mediaPlayer != null) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setLegacyStreamType(streamType)
                    .build();
            mediaPlayer.setAudioAttributes(audioAttributes);
        }
    }

    private void registerProximitySensor() {
        sensorManager.registerListener(proximitySensorYier, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void unregisterProximitySensor() {
        sensorManager.unregisterListener(proximitySensorYier);
    }

    private void setupYiers() {
        mediaPlayer.setOnCompletionListener(mp -> {
            mediaPlayer.seekTo(getDuration());
            if (onPlayStateChangeYier != null) {
                onPlayStateChangeYier.onStop(id, true);
                if(earpieceNow) {
                    chatActivity.getBinding().holdToTalkButton.performHapticFeedback(
                            HapticFeedbackConstants.CONTEXT_CLICK,
                            HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
                    );
                    try {
                        Thread.sleep(210);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    chatActivity.getBinding().holdToTalkButton.performHapticFeedback(
                            HapticFeedbackConstants.CONTEXT_CLICK,
                            HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
                    );
                }
            }
            unregisterProximitySensor();
        });

        mediaPlayer.setOnErrorListener((mp, what, extra) -> {
            if (onPlayStateChangeYier != null) {
                onPlayStateChangeYier.onError(id, what, extra);
            }
            unregisterProximitySensor();
            return true;
        });
    }

    public String getId() {
        return id;
    }

    public State getState(){
        return new State(id, uri, getPlaybackPosition());
    }

    public static class State{
        private final String id;
        private final Uri uri;
        private final int position;

        public State(String id, Uri uri, int position) {
            this.id = id;
            this.uri = uri;
            this.position = position;
        }

        public String getId() {
            return id;
        }

        public Uri getUri() {
            return uri;
        }

        public int getPosition() {
            return position;
        }
    }
}
