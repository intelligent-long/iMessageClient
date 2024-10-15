package com.longx.intelligent.android.ichat2.procedure;

import static android.content.Context.POWER_SERVICE;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioDeviceInfo;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.PowerManager;
import android.util.Log;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.HapticFeedbackConstants;

import com.longx.intelligent.android.ichat2.activity.ChatActivity;

public class ChatVoicePlayer {
    private MediaPlayer mediaPlayer;
    private final ChatActivity chatActivity;
    private final AudioManager audioManager;
    private final SensorManager sensorManager;
    private final PowerManager powerManager;
    private PowerManager.WakeLock wakeLock;
    private final Sensor proximitySensor;
    private final SensorEventListener proximitySensorYier;
    private OnPlayStateChangeYier onPlayStateChangeYier;
    private String id;
    private Uri uri;
    private boolean earpieceNow;
    private AudioFocusRequest audioFocusRequest;

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
        powerManager = (PowerManager) chatActivity.getSystemService(POWER_SERVICE);
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        proximitySensorYier = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (isHeadphonesPlugged() || isBluetoothAudioConnected()) {
                    return;
                }
                if (proximitySensor != null) {
                    if (event.values[0] < proximitySensor.getMaximumRange()) {
                        if(!earpieceNow) {
                            earpieceNow = true;
                            switchToEarpiece();
                        }
                    } else {
                        if(earpieceNow) {
                            earpieceNow = false;
                            switchToSpeaker();
                        }
                    }
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };

        if(audioFocusRequest == null) {
            audioFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
                    .setAudioAttributes(
                            new AudioAttributes.Builder()
                                    .setUsage(AudioAttributes.USAGE_MEDIA)
                                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                    .build()
                    )
                    .setAcceptsDelayedFocusGain(true)
                    .setOnAudioFocusChangeListener(new AudioFocusChangeYier())
                    .build();
        }
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
            int result = audioManager.requestAudioFocus(audioFocusRequest);
            if(result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                mediaPlayer.start();
                registerProximitySensor();
                if (onPlayStateChangeYier != null) {
                    onPlayStateChangeYier.onStart(id);
                }
            }else {
                if (onPlayStateChangeYier != null) {
                    onPlayStateChangeYier.onError(id, -1, -1);
                }
            }
        }
    }

    public void pause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            audioManager.abandonAudioFocusRequest(audioFocusRequest);
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
            audioManager.abandonAudioFocusRequest(audioFocusRequest);
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
        audioManager.abandonAudioFocusRequest(audioFocusRequest);
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
        setScreenOff();
        pause();
        int position = getPlaybackPosition() - 1000;

        audioManager.setSpeakerphoneOn(false);
        audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        setAudioAttributes(AudioManager.STREAM_VOICE_CALL);

        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        seekTo(Math.max(position, 0));
        play();
        Log.d(getClass().getName(), "Switched to earpiece");
    }

    private void switchToSpeaker() {
        setScreenOn();
        pause();
        int position = getPlaybackPosition() - 1000;

        audioManager.setSpeakerphoneOn(true);
        audioManager.setMode(AudioManager.MODE_NORMAL);
        setAudioAttributes(AudioManager.STREAM_MUSIC);

        seekTo(Math.max(position, 0));
        play();
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
            audioManager.abandonAudioFocusRequest(audioFocusRequest);
            mediaPlayer.seekTo(getDuration());
            if (onPlayStateChangeYier != null) {
                onPlayStateChangeYier.onStop(id, true);
                if(earpieceNow) {
                    chatActivity.getBinding().holdToTalkButton.performHapticFeedback(
                            HapticFeedbackConstants.CONTEXT_CLICK,
                            HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
                    );
                    try {
                        Thread.sleep(200);
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
            audioManager.abandonAudioFocusRequest(audioFocusRequest);
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

    private class AudioFocusChangeYier implements AudioManager.OnAudioFocusChangeListener{
        @Override
        public void onAudioFocusChange(int focusChange) {
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_LOSS:
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    pause();
                    break;
            };
        }
    }

    private boolean isHeadphonesPlugged() {
        if (audioManager == null) {
            return false;
        }
        AudioDeviceInfo[] audioDevicesInputs = audioManager.getDevices(AudioManager.GET_DEVICES_INPUTS);
        for (AudioDeviceInfo deviceInfo : audioDevicesInputs) {
            if (deviceInfo.getType() == AudioDeviceInfo.TYPE_WIRED_HEADPHONES
                    || deviceInfo.getType() == AudioDeviceInfo.TYPE_WIRED_HEADSET) {
                return true;
            }
        }
        AudioDeviceInfo[] audioDevicesOutputs = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS);
        for (AudioDeviceInfo deviceInfo : audioDevicesOutputs) {
            if (deviceInfo.getType() == AudioDeviceInfo.TYPE_WIRED_HEADPHONES
                    || deviceInfo.getType() == AudioDeviceInfo.TYPE_WIRED_HEADSET) {
                return true;
            }
        }
        return false;
    }

    private boolean isBluetoothAudioConnected() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        @SuppressLint("MissingPermission") int state = bluetoothAdapter.getProfileConnectionState(BluetoothProfile.A2DP);
        return state == BluetoothAdapter.STATE_CONNECTED;
    }

    @SuppressLint("InvalidWakeLockTag")
    private void setScreenOff() {
        if (wakeLock == null) {
            wakeLock = powerManager.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK, getClass().getName());
        }
        wakeLock.acquire(30 * 60 * 1000);
    }

    private void setScreenOn() {
        if (wakeLock != null) {
            wakeLock.setReferenceCounted(false);
            wakeLock.release();
            wakeLock = null;
        }
    }
}
