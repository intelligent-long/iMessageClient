package com.longx.intelligent.android.ichat2.behavior;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class ChatVoicePlayer {
    private MediaPlayer mediaPlayer;
    private final Context context;
    private final AudioManager audioManager;
    private final SensorManager sensorManager;
    private final Sensor proximitySensor;
    private final SensorEventListener proximitySensorYier;
    private OnPlayStateChangeYier onPlayStateChangeYier;
    private String id;

    public interface OnPlayStateChangeYier {
        void onStart(String id);
        void onPause(String id);
        void onStop(String id);
        void onError(String id, int what, int extra);
    }

    public ChatVoicePlayer(Context context) {
        this.context = context;
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        proximitySensorYier = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (proximitySensor != null) {
                    if (event.values[0] < proximitySensor.getMaximumRange()) {
                        switchToEarpiece();
                    } else {
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

    public void init(int resId, String id) {
        release();
        this.id = id;
        mediaPlayer = MediaPlayer.create(context, resId);
        setAudioAttributes(AudioManager.STREAM_MUSIC);
        setupYiers();
    }

    public void init(Uri uri, String id) {
        release();
        this.id = id;
        mediaPlayer = MediaPlayer.create(context, uri);
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
                onPlayStateChangeYier.onStop(id);
            }
        }
    }

    public void release() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            if (onPlayStateChangeYier != null) {
                onPlayStateChangeYier.onStop(id);
            }
            mediaPlayer = null;
            id = null;
        }
        unregisterProximitySensor();
    }

    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
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

    private void registerProximitySensor() {
        sensorManager.registerListener(proximitySensorYier, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void unregisterProximitySensor() {
        sensorManager.unregisterListener(proximitySensorYier);
    }

    private void setAudioAttributes(int streamType) {
        if (mediaPlayer != null) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setLegacyStreamType(streamType)
                    .build();
            mediaPlayer.setAudioAttributes(audioAttributes);
        }
    }

    private void setupYiers() {
        mediaPlayer.setOnCompletionListener(mp -> {
            if (onPlayStateChangeYier != null) {
                onPlayStateChangeYier.onStop(id);
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
}
