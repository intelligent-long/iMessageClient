package com.longx.intelligent.android.ichat2.activity;

import android.os.Bundle;

import com.longx.intelligent.android.ichat2.activity.helper.BaseActivity;
import com.longx.intelligent.android.ichat2.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.ichat2.data.Channel;
import com.longx.intelligent.android.ichat2.data.Self;
import com.longx.intelligent.android.ichat2.data.request.RequestAddChannelPostBody;
import com.longx.intelligent.android.ichat2.data.response.OperationStatus;
import com.longx.intelligent.android.ichat2.databinding.ActivityRequestAddChannelBinding;
import com.longx.intelligent.android.ichat2.dialog.ConfirmDialog;
import com.longx.intelligent.android.ichat2.dialog.MessageDialog;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.ChannelApiCaller;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.ichat2.util.UiUtil;
import com.longx.intelligent.android.ichat2.value.Variables;

import retrofit2.Call;
import retrofit2.Response;

public class RequestAddChannelActivity extends BaseActivity {
    private ActivityRequestAddChannelBinding binding;
    private Channel channel;
    private Self currentUserInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRequestAddChannelBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupDefaultBackNavigation(binding.toolbar);
        channel = getIntent().getParcelableExtra(ExtraKeys.CHANNEL);
        currentUserInfo = SharedPreferencesAccessor.UserProfilePref.getCurrentUserProfile(this);
        showContent();
        setupYiers();
    }

    private void showContent() {
        binding.messageInput.setText(Variables.getRequestAddChannelDefaultMessage(currentUserInfo.getUsername()));
    }

    private void setupYiers() {
        binding.sendRequestButton.setOnClickListener(v -> {
            new ConfirmDialog(this, "是否发送添加频道请求？")
                    .setNegativeButton(null)
                    .setPositiveButton((dialog, which) -> {
                        String inputtedMessage = UiUtil.getEditTextString(binding.messageInput);
                        RequestAddChannelPostBody postBody = new RequestAddChannelPostBody(channel.getIchatIdUser(), inputtedMessage);
                        ChannelApiCaller.requestAddChannel(this, postBody, new RetrofitApiCaller.CommonYier<OperationStatus>(this){
                            @Override
                            public void ok(OperationStatus data, Response<OperationStatus> row, Call<OperationStatus> call) {
                                super.ok(data, row, call);
                                data.commonHandleResult(RequestAddChannelActivity.this, new int[]{-101, -102, -103, -104}, () -> {
                                    new MessageDialog(RequestAddChannelActivity.this, "发送请求", "已发送添加频道请求").show();
                                });
                            }
                        });
                    })
                    .show();
        });
    }
}