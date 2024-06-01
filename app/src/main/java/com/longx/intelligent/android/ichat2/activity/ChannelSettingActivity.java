package com.longx.intelligent.android.ichat2.activity;

import android.content.DialogInterface;
import android.os.Bundle;

import com.longx.intelligent.android.ichat2.activity.helper.ActivityOperator;
import com.longx.intelligent.android.ichat2.activity.helper.BaseActivity;
import com.longx.intelligent.android.ichat2.data.Channel;
import com.longx.intelligent.android.ichat2.data.request.DeleteChannelAssociationPostBody;
import com.longx.intelligent.android.ichat2.data.response.OperationStatus;
import com.longx.intelligent.android.ichat2.databinding.ActivityChannelSettingBinding;
import com.longx.intelligent.android.ichat2.dialog.ConfirmDialog;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.ChannelApiCaller;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.RetrofitApiCaller;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

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
        setupYiers();
    }

    private void setupYiers() {
        binding.deleteChannel.setOnClickListener(v -> {
            new ConfirmDialog(this, "是否继续？")
                    .setNegativeButton(null)
                    .setPositiveButton((dialog, which) -> {
                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                new ConfirmDialog(ChannelSettingActivity.this, "是否继续？")
                                        .setNegativeButton(null)
                                        .setPositiveButton((dialog1, which1) -> {
                                            deleteChannel();
                                        })
                                        .show();
                            }
                        }, 150);
                    })
                    .show();
        });
    }

    private void deleteChannel() {
        DeleteChannelAssociationPostBody postBody = new DeleteChannelAssociationPostBody(channel.getIchatId());
        ChannelApiCaller.deleteAssociatedChannel(this, postBody, new RetrofitApiCaller.CommonYier<OperationStatus>(this){
            @Override
            public void ok(OperationStatus data, Response<OperationStatus> row, Call<OperationStatus> call) {
                super.ok(data, row, call);
                data.commonHandleResult(getActivity(), new int[]{}, () -> {
                    ActivityOperator.getActivitiesOf(ChatActivity.class).forEach(chatActivity -> {
                        if(chatActivity.getChannel().getIchatId().equals(channel.getIchatId())){
                            chatActivity.finish();
                        }
                    });
                    ActivityOperator.getActivitiesOf(ChannelActivity.class).forEach(channelActivity -> {
                        if(channelActivity.getChannel().getIchatId().equals(channel.getIchatId())){
                            channelActivity.finish();
                        }
                    });
                    getActivity().finish();
                });
            }
        });
    }
}