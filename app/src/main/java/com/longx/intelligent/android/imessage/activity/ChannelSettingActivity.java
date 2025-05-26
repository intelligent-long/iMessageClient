package com.longx.intelligent.android.imessage.activity;

import android.content.Intent;
import android.os.Bundle;

import com.longx.intelligent.android.imessage.activity.helper.ActivityOperator;
import com.longx.intelligent.android.imessage.activity.helper.BaseActivity;
import com.longx.intelligent.android.imessage.da.database.manager.ChannelDatabaseManager;
import com.longx.intelligent.android.imessage.data.Channel;
import com.longx.intelligent.android.imessage.data.ChatMessageAllow;
import com.longx.intelligent.android.imessage.data.request.ChangeAllowChatMessagePostBody;
import com.longx.intelligent.android.imessage.data.request.DeleteChannelAssociationPostBody;
import com.longx.intelligent.android.imessage.data.response.OperationStatus;
import com.longx.intelligent.android.imessage.databinding.ActivityChannelSettingBinding;
import com.longx.intelligent.android.imessage.dialog.ConfirmDialog;
import com.longx.intelligent.android.imessage.net.retrofit.caller.ChannelApiCaller;
import com.longx.intelligent.android.imessage.net.retrofit.caller.PermissionApiCaller;
import com.longx.intelligent.android.imessage.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.imessage.util.UiUtil;

import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Response;

public class ChannelSettingActivity extends BaseActivity {
    private ActivityChannelSettingBinding binding;
    private Channel channel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChannelSettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupDefaultBackNavigation(binding.toolbar);
        channel = getIntent().getParcelableExtra(ExtraKeys.CHANNEL);
        showContent();
        setupYiers();
    }

    private void showContent() {
        ChatMessageAllow chatMessageAllowToMe = ChannelDatabaseManager.getInstance().findOneAssociation(channel.getImessageId()).getChatMessageAllowToMe();
        binding.switchVoiceMessage.setChecked(chatMessageAllowToMe.isAllowVoice());
        binding.switchNotice.setChecked(chatMessageAllowToMe.isAllowNotice());
    }

    private void setupYiers() {
        binding.clickViewVoiceMessage.setOnClickListener(v -> {
            UiUtil.setViewGroupEnabled(binding.clickViewVoiceMessage, false, true);
            boolean changeTo = !binding.switchVoiceMessage.isChecked();
            boolean switchNoticeChecked = binding.switchNotice.isChecked();
            PermissionApiCaller.changeAllowChatMessage(this, new ChangeAllowChatMessagePostBody(channel.getImessageId(), new ChatMessageAllow(changeTo, switchNoticeChecked)),
                    new RetrofitApiCaller.DelayedShowDialogCommonYier<OperationStatus>(this){
                        @Override
                        public void ok(OperationStatus data, Response<OperationStatus> raw, Call<OperationStatus> call) {
                            super.ok(data, raw, call);
                            data.commonHandleResult(ChannelSettingActivity
                                    .this, new int[]{}, () -> {
                                binding.switchVoiceMessage.setChecked(changeTo);
                            });
                        }

                        @Override
                        public synchronized void complete(Call<OperationStatus> call) {
                            super.complete(call);
                            UiUtil.setViewGroupEnabled(binding.clickViewVoiceMessage, true, true);
                        }
                    });
        });
        binding.clickViewNotice.setOnClickListener(v -> {
            UiUtil.setViewGroupEnabled(binding.clickViewNotice, false, true);
            boolean switchVoiceMessageChecked = binding.switchVoiceMessage.isChecked();
            boolean changeTo = !binding.switchNotice.isChecked();
            PermissionApiCaller.changeAllowChatMessage(this, new ChangeAllowChatMessagePostBody(channel.getImessageId(), new ChatMessageAllow(switchVoiceMessageChecked, changeTo)),
                    new RetrofitApiCaller.DelayedShowDialogCommonYier<OperationStatus>(this){
                        @Override
                        public void ok(OperationStatus data, Response<OperationStatus> raw, Call<OperationStatus> call) {
                            super.ok(data, raw, call);
                            data.commonHandleResult(ChannelSettingActivity.this, new int[]{}, () -> {
                                binding.switchNotice.setChecked(changeTo);
                            });
                        }

                        @Override
                        public synchronized void complete(Call<OperationStatus> call) {
                            super.complete(call);
                            UiUtil.setViewGroupEnabled(binding.clickViewNotice, true, true);
                        }
                    });
        });
        binding.deleteChannel.setOnClickListener(v -> {
            new ConfirmDialog(this, "是否继续？")
                    .setNegativeButton()
                    .setPositiveButton((dialog, which) -> {
                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                runOnUiThread(() -> {
                                    new ConfirmDialog(ChannelSettingActivity.this, "是否继续？")
                                            .setNegativeButton()
                                            .setPositiveButton((dialog1, which1) -> {
                                                deleteChannel();
                                            })
                                            .create().show();
                                });
                            }
                        }, 150);
                    })
                    .create().show();
        });
        binding.clickViewNote.setOnClickListener(v -> {
            Intent intent = new Intent(this, SetChannelNoteActivity.class);
            intent.putExtra(ExtraKeys.IMESSAGE_ID, channel.getImessageId());
            startActivity(intent);
        });
        binding.clickViewTag.setOnClickListener(v -> {
            Intent intent = new Intent(this, SetChannelTagActivity.class);
            intent.putExtra(ExtraKeys.IMESSAGE_ID, channel.getImessageId());
            startActivity(intent);
        });
    }

    private void deleteChannel() {
        DeleteChannelAssociationPostBody postBody = new DeleteChannelAssociationPostBody(channel.getImessageId());
        ChannelApiCaller.deleteAssociatedChannel(this, postBody, new RetrofitApiCaller.CommonYier<OperationStatus>(this){
            @Override
            public void ok(OperationStatus data, Response<OperationStatus> raw, Call<OperationStatus> call) {
                super.ok(data, raw, call);
                data.commonHandleResult(getActivity(), new int[]{}, () -> {
                    ActivityOperator.getActivitiesOf(ChatActivity.class).forEach(chatActivity -> {
                        if(chatActivity.getChannel().getImessageId().equals(channel.getImessageId())){
                            chatActivity.finish();
                        }
                    });
                    ActivityOperator.getActivitiesOf(ChannelActivity.class).forEach(channelActivity -> {
                        if(channelActivity.getChannel().getImessageId().equals(channel.getImessageId())){
                            channelActivity.finish();
                        }
                    });
                    getActivity().finish();
                });
            }
        });
    }
}