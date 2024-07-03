package com.longx.intelligent.android.ichat2.behavior;

import android.app.Activity;
import android.media.MediaRecorder;

import com.longx.intelligent.android.ichat2.util.ErrorLogger;

import java.io.File;

/**
 * Created by LONG on 2023/9/25 at 6:18 PM.
 */
public class AudioRecorder {
    private static final int SAMPLE_RATE = 48000;
    private static final int BIT_RATE = 512000;
    private MediaRecorder recorder;
    private AudioRecordYier audioRecordYier;
    private final Activity activity;
    private String filePath;
    private boolean stopped = true;

    public AudioRecorder(Activity activity){
        this.activity = activity;
    }

    public void record(String recordedAudioPath, int waitMilliseconds, AudioRecordYier audioRecordYier) throws Exception {
        synchronized (this) {
            if (!stopped) {
                MessageDisplayer.autoShow(activity, "录音出错", MessageDisplayer.Duration.SHORT);
                throw new Exception("开始录音失败，当前录音没有停止。");
            }
            stopped = false;
        }
        this.filePath = recordedAudioPath;
        this.audioRecordYier = audioRecordYier;
        recorder = new MediaRecorder();
        recorder.setOutputFile(recordedAudioPath);
        recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        recorder.setAudioSamplingRate(SAMPLE_RATE);
        recorder.setAudioEncodingBitRate(BIT_RATE);
        recorder.setAudioChannels(2);
        recorder.prepare();
        activity.runOnUiThread(() -> {
            if(!stopped) {
                audioRecordYier.onRecordPrepared();
            }
        });
        if(waitMilliseconds > 0) {
            try {
                Thread.sleep(waitMilliseconds);
            } catch (InterruptedException e) {
                MessageDisplayer.autoShow(activity, "录音出错", MessageDisplayer.Duration.SHORT);
                ErrorLogger.log(getClass(), "录音出错，线程睡眠失败", e);
                throw e;
            }
        }
        recorder.start();
        activity.runOnUiThread(() -> {
            if(!stopped) {
                audioRecordYier.onRecordStarted();
            }
        });
    }

    public File stop(){
        synchronized (this) {
            if (stopped) {
                return null;
            }
            stopped = true;
        }
        try {
            if(recorder != null) {
                recorder.stop();
                recorder.release();
                File file = new File(filePath);
                if(file.exists()){
                    activity.runOnUiThread(() -> {
                        audioRecordYier.onRecordStopped();
                    });
                    return file;
                }
            }
        }catch (Exception e){
            MessageDisplayer.autoShow(activity, "停止录音出错", MessageDisplayer.Duration.SHORT);
            ErrorLogger.log(getClass(), "停止录音出错", e);
            activity.runOnUiThread(() -> {
                audioRecordYier.onRecordStopped();
            });
            return null;
        }
        activity.runOnUiThread(() -> {
            audioRecordYier.onRecordStopped();
        });
        return null;
    }

    public interface AudioRecordYier{

        void onRecordPrepared();

        void onRecordStarted();

        void onRecordStopped();
    }

}
