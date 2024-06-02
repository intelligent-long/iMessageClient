package com.longx.intelligent.android.ichat2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.activity.helper.BaseActivity;
import com.longx.intelligent.android.ichat2.behavior.GlideBehaviours;
import com.longx.intelligent.android.ichat2.data.ChannelAddition;
import com.longx.intelligent.android.ichat2.data.Channel;
import com.longx.intelligent.android.ichat2.data.request.AcceptAddChannelPostBody;
import com.longx.intelligent.android.ichat2.data.response.OperationStatus;
import com.longx.intelligent.android.ichat2.databinding.ActivityChannelAdditionBinding;
import com.longx.intelligent.android.ichat2.dialog.ConfirmDialog;
import com.longx.intelligent.android.ichat2.dialog.MessageDialog;
import com.longx.intelligent.android.ichat2.net.dataurl.NetDataUrls;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.ChannelApiCaller;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.RetrofitApiCaller;

import java.text.SimpleDateFormat;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Response;

public class ChannelAdditionActivity extends BaseActivity {
    private ActivityChannelAdditionBinding binding;
    private ChannelAddition channelAddition;
    private boolean isRequester;
    private Channel channel;
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChannelAdditionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupDefaultBackNavigation(binding.toolbar);
        channelAddition = getIntent().getParcelableExtra(ExtraKeys.CHANNEL_ADDITION_INFO);
        isRequester = channelAddition.isRequester(this);
        if(isRequester){
            channel = channelAddition.getResponderChannel();
        }else {
            channel = channelAddition.getRequesterChannel();
        }
        showContent();
        setupYiers();
    }

    private void showContent(){
        if (channel.getAvatar() == null || channel.getAvatar().getHash() == null) {
            GlideBehaviours.loadToImageView(getApplicationContext(), R.drawable.default_avatar, binding.avatar);
        } else {
            GlideBehaviours.loadToImageView(getApplicationContext(), NetDataUrls.getAvatarUrl(this, channel.getAvatar().getHash()), binding.avatar);
        }
        if(channel.getNote() != null){
            binding.name.setText(channel.getNote());
            binding.username.setText(channel.getUsername());
            binding.layoutUsername.setVisibility(View.VISIBLE);
            binding.emailDivider.setVisibility(View.VISIBLE);
        }else {
            binding.name.setText(channel.getUsername());
            binding.layoutUsername.setVisibility(View.GONE);
            binding.emailDivider.setVisibility(View.GONE);
        }
        if(channel.getSex() == null || (channel.getSex() != 0 && channel.getSex() != 1)){
            binding.sexIcon.setVisibility(View.GONE);
        }else {
            binding.sexIcon.setVisibility(View.VISIBLE);
            if(channel.getSex() == 0){
                binding.sexIcon.setImageResource(R.drawable.female_24px);
            }else {
                binding.sexIcon.setImageResource(R.drawable.male_24px);
            }
        }
        binding.ichatIdUser.setText(channel.getIchatIdUser());
        if(channel.getEmail() == null){
            binding.layoutEmail.setVisibility(View.GONE);
        }else {
            binding.layoutEmail.setVisibility(View.VISIBLE);
            binding.email.setText(channel.getEmail());
        }
        String regionDesc = channel.buildRegionDesc();
        if(regionDesc == null){
            binding.layoutRegion.setVisibility(View.GONE);
            binding.regionDivider.setVisibility(View.GONE);
        }else {
            binding.layoutRegion.setVisibility(View.VISIBLE);
            binding.regionDivider.setVisibility(View.VISIBLE);
            binding.region.setText(regionDesc);
        }
        binding.requestTime.setText(SIMPLE_DATE_FORMAT.format(channelAddition.getRequestTime()));
        if(channelAddition.getRespondTime() == null){
            binding.respondTimeDivider.setVisibility(View.GONE);
            binding.layoutRespondTime.setVisibility(View.GONE);
        }else {
            binding.respondTimeDivider.setVisibility(View.VISIBLE);
            binding.layoutRespondTime.setVisibility(View.VISIBLE);
            binding.respondTime.setText(SIMPLE_DATE_FORMAT.format(channelAddition.getRespondTime()));
        }
        String message = channelAddition.getMessage();
        if(message == null || message.equals("")){
            binding.messageLayout.setVisibility(View.GONE);
        }else {
            binding.messageLayout.setVisibility(View.VISIBLE);
            binding.messageText.setText(message);
        }
        if(channelAddition.isAccepted()){
            binding.addedText.setVisibility(View.VISIBLE);
            binding.expiredText.setVisibility(View.GONE);
            binding.acceptAddButton.setVisibility(View.GONE);
            binding.pendingConfirmText.setVisibility(View.GONE);
        }else if(channelAddition.isExpired()){
            binding.addedText.setVisibility(View.GONE);
            binding.expiredText.setVisibility(View.VISIBLE);
            binding.acceptAddButton.setVisibility(View.GONE);
            binding.pendingConfirmText.setVisibility(View.GONE);
        }else {
            binding.addedText.setVisibility(View.GONE);
            binding.expiredText.setVisibility(View.GONE);
            if(isRequester){
                binding.pendingConfirmText.setVisibility(View.VISIBLE);
                binding.acceptAddButton.setVisibility(View.GONE);
            }else {
                binding.pendingConfirmText.setVisibility(View.GONE);
                binding.acceptAddButton.setVisibility(View.VISIBLE);
            }
        }
    }

    private void setupYiers() {
        binding.clickViewChannel.setOnClickListener(v -> {
            if(channel != null) {
                Intent intent = new Intent(this, ChannelActivity.class);
                intent.putExtra(ExtraKeys.ICHAT_ID, channel.getIchatId());
                startActivity(intent);
            }
        });
        binding.acceptAddButton.setOnClickListener(v -> {
            new ConfirmDialog(this, "是否接受添加频道请求？")
                    .setNegativeButton("取消", null)
                    .setPositiveButton("确定", (dialog, which) -> {
                        ChannelApiCaller.acceptAddChannel(this, new AcceptAddChannelPostBody(channelAddition.getUuid()), new RetrofitApiCaller.CommonYier<OperationStatus>(this){
                            @Override
                            public void ok(OperationStatus data, Response<OperationStatus> row, Call<OperationStatus> call) {
                                super.ok(data, row, call);
                                data.commonHandleResult(ChannelAdditionActivity.this, new int[]{-101, -102, -103}, () -> {
                                    new MessageDialog(ChannelAdditionActivity.this, "添加频道", "频道已添加").show();
                                });
                            }
                        });
                    })
                    .show();
        });
    }

}