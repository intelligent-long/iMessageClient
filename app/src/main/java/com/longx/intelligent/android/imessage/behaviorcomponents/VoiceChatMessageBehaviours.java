package com.longx.intelligent.android.imessage.behaviorcomponents;

import android.net.Uri;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.HapticFeedbackConstants;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.longx.intelligent.android.imessage.activity.ChatActivity;
import com.longx.intelligent.android.imessage.da.cachefile.CacheFilesAccessor;
import com.longx.intelligent.android.imessage.da.database.manager.OpenedChatDatabaseManager;
import com.longx.intelligent.android.imessage.data.ChatMessage;
import com.longx.intelligent.android.imessage.data.OpenedChat;
import com.longx.intelligent.android.imessage.data.request.SendVoiceChatMessagePostBody;
import com.longx.intelligent.android.imessage.data.response.OperationData;
import com.longx.intelligent.android.imessage.net.retrofit.caller.ChatApiCaller;
import com.longx.intelligent.android.imessage.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.imessage.permission.PermissionOperator;
import com.longx.intelligent.android.imessage.permission.ToRequestPermissions;
import com.longx.intelligent.android.imessage.permission.ToRequestPermissionsItems;
import com.longx.intelligent.android.imessage.util.AudioUtil;
import com.longx.intelligent.android.imessage.value.Constants;
import com.longx.intelligent.android.imessage.yier.GlobalYiersHolder;
import com.longx.intelligent.android.imessage.yier.NewContentBadgeDisplayYier;
import com.longx.intelligent.android.imessage.yier.OpenedChatsUpdateYier;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by LONG on 2024/7/3 at 下午2:26.
 */
public class VoiceChatMessageBehaviours {
    private final ChatActivity chatActivity;
    private boolean sendVoiceStopped;
    private final AudioRecorder audioRecorder;
    private CountDownTimer reachMaxVoiceTimeTimer;
    private Snackbar reachMaxVoiceTimeNoticeSnackbar;

    public VoiceChatMessageBehaviours(ChatActivity chatActivity) {
        this.chatActivity = chatActivity;
        audioRecorder = new AudioRecorder(chatActivity);
    }

    public synchronized void onStartTalk() {
        if (checkAndRequestRecordPermission()) return;
        sendVoiceStopped = false;
        new Thread(() -> {
            try {
                String voiceTempFilePath = CacheFilesAccessor.ChatMessage.prepareChatVoiceTempFile(chatActivity, chatActivity.getChannel().getImessageId());
                audioRecorder.record(voiceTempFilePath, 200, new AudioRecorder.AudioRecordYier() {
                    @Override
                    public void onRecordPrepared() {
                        chatActivity.getBinding().holdToTalkButton.performHapticFeedback(
                                HapticFeedbackConstants.CONTEXT_CLICK,
                                HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
                        );
                    }

                    @Override
                    public void onRecordStarted() {
                        chatActivity.getBinding().cancelSendTalkFab.show(new FloatingActionButton.OnVisibilityChangedListener() {
                        });
                        reachMaxVoiceTimeTimer = new CountDownTimer(Constants.MAX_CHAT_VOICE_TIME_SEC * 1000, 1000) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                                long secUntilStopAndSend = millisUntilFinished / 1000 + 1;
                                if(secUntilStopAndSend <= 10) {
                                    if(secUntilStopAndSend == 0){
                                        reachMaxVoiceTimeNoticeSnackbar.dismiss();
                                    }
                                    reachMaxVoiceTimeNoticeSnackbar = MessageDisplayer.showSnackbar(chatActivity, secUntilStopAndSend + " 秒后将发送", Snackbar.LENGTH_LONG);
                                }
                            }

                            @Override
                            public void onFinish() {
                                sendVoice();
                            }
                        };
                        reachMaxVoiceTimeTimer.start();
                    }

                    @Override
                    public void onRecordStopped() {
                        if(reachMaxVoiceTimeTimer != null) reachMaxVoiceTimeTimer.cancel();
                        chatActivity.getBinding().cancelSendTalkFab.hide(new FloatingActionButton.OnVisibilityChangedListener() {
                            @Override
                            public void onHidden(FloatingActionButton fab) {
                                chatActivity.changeCancelSendTalkFabToNormal();
                            }
                        });
                    }
                });
            } catch (Exception e) {
                Log.e(getClass().getName(), "录音失败", e);
                MessageDisplayer.autoShow(chatActivity, "录音失败", MessageDisplayer.Duration.LONG);
            }
        }).start();
    }

    private boolean checkAndRequestRecordPermission() {
        if(!PermissionOperator.hasPermissions(chatActivity, ToRequestPermissionsItems.recordAudio)){
            List<ToRequestPermissions> toRequestPermissionsList = new ArrayList<>();
            toRequestPermissionsList.add(ToRequestPermissionsItems.recordAudio);
            new PermissionOperator(chatActivity, toRequestPermissionsList,
                    new PermissionOperator.ShowCommonMessagePermissionResultCallback(chatActivity){
                        @Override
                        public void onPermissionGranted(int requestCode) {
                            super.onPermissionGranted(requestCode);
                        }
                    })
                    .startRequestPermissions(chatActivity);
            return true;
        }
        return false;
    }

    public synchronized void cancelSendVoice() {
        if(sendVoiceStopped){
            return;
        }
        sendVoiceStopped = true;
        chatActivity.changeHoldToTalkToNormal();
        new Thread(() -> {
            audioRecorder.stop();
        }).start();
    }

    public synchronized void sendVoice() {
        if(sendVoiceStopped){
            return;
        }
        sendVoiceStopped = true;
        chatActivity.changeHoldToTalkToNormal();
        File recordedAudiofile = audioRecorder.stop();
        if (recordedAudiofile == null) {
            MessageDisplayer.autoShow(chatActivity, "说话时间太短", MessageDisplayer.Duration.LONG);
            return;
        }else {
            long duration = AudioUtil.getDuration(chatActivity, recordedAudiofile.getAbsolutePath());
            int sec = (int) Math.round(duration / 1000.0);
            if(sec == 0){
                MessageDisplayer.autoShow(chatActivity, "说话时间太短", MessageDisplayer.Duration.LONG);
                return;
            }
        }
        doSendVoice(recordedAudiofile);
    }

    private void doSendVoice(File recordedAudiofile){
        chatActivity.toSendingProgressState();
        SendVoiceChatMessagePostBody postBody = new SendVoiceChatMessagePostBody(chatActivity.getChannel().getImessageId());
        ChatApiCaller.sendVoiceMessage(chatActivity, chatActivity, Uri.fromFile(recordedAudiofile), postBody, new RetrofitApiCaller.BaseCommonYier<OperationData>(chatActivity) {
            @Override
            public void ok(OperationData data, Response<OperationData> raw, Call<OperationData> call) {
                super.ok(data, raw, call);
                data.commonHandleResult(chatActivity, new int[]{-101, -102, -103, -199}, () -> {
                    ChatMessage chatMessage = data.getData(ChatMessage.class);
                    chatMessage.setViewed(true);
                    ChatMessage.mainDoOnNewMessage(chatMessage, chatActivity, results -> {
                        chatActivity.getAdapter().addItemAndShow(chatMessage);
                        OpenedChatDatabaseManager.getInstance().insertOrUpdate(new OpenedChat(chatMessage.getTo(), 0, true));
                        GlobalYiersHolder.getYiers(OpenedChatsUpdateYier.class).ifPresent(openedChatUpdateYiers -> {
                            openedChatUpdateYiers.forEach(OpenedChatsUpdateYier::onOpenedChatsUpdate);
                        });
                        GlobalYiersHolder.getYiers(NewContentBadgeDisplayYier.class).ifPresent(newContentBadgeDisplayYiers -> {
                            newContentBadgeDisplayYiers.forEach(newContentBadgeDisplayYier -> {
                                newContentBadgeDisplayYier.autoShowNewContentBadge(chatActivity, NewContentBadgeDisplayYier.ID.MESSAGES);
                            });
                        });
                    });
                });
            }

            @Override
            public void notOk(int code, String message, Response<OperationData> row, Call<OperationData> call) {
                super.notOk(code, message, row, call);
                chatActivity.toVoiceState();
            }

            @Override
            public void failure(Throwable t, Call<OperationData> call) {
                super.failure(t, call);
                chatActivity.toVoiceState();
            }

            @Override
            public void complete(Call<OperationData> call) {
                super.complete(call);
                chatActivity.toVoiceState();
            }

        }, (current, total) -> {
            chatActivity.runOnUiThread(() -> {
                int progress = (int)((current / (double) total) * 100);
                chatActivity.getBinding().sendProgressIndicator.setProgress(progress, true);
            });
        });
    }
}
