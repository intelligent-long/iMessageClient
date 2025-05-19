package com.longx.intelligent.android.imessage.activity;

import static com.longx.intelligent.android.imessage.value.Constants.COMMON_SIMPLE_DATE_FORMAT;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.request.target.Target;
import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.imessage.activity.helper.BaseActivity;
import com.longx.intelligent.android.imessage.data.ChannelAddition;
import com.longx.intelligent.android.imessage.data.Channel;
import com.longx.intelligent.android.imessage.data.request.AcceptAddChannelPostBody;
import com.longx.intelligent.android.imessage.data.response.OperationStatus;
import com.longx.intelligent.android.imessage.databinding.ActivityChannelAdditionBinding;
import com.longx.intelligent.android.imessage.dialog.ConfirmDialog;
import com.longx.intelligent.android.imessage.dialog.CustomViewMessageDialog;
import com.longx.intelligent.android.imessage.net.dataurl.NetDataUrls;
import com.longx.intelligent.android.imessage.net.retrofit.caller.ChannelApiCaller;
import com.longx.intelligent.android.imessage.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.imessage.ui.glide.GlideApp;

import retrofit2.Call;
import retrofit2.Response;

public class ChannelAdditionActivity extends BaseActivity {
    private ActivityChannelAdditionBinding binding;
    private ChannelAddition channelAddition;
    private boolean isRequester;
    private Channel channel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChannelAdditionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupDefaultBackNavigation(binding.toolbar);
        channelAddition = getIntent().getParcelableExtra(ExtraKeys.CHANNEL_ADDITION);
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
            GlideApp
                    .with(getApplicationContext())
                    .load(R.drawable.default_avatar)
                    .into(binding.avatar);
        } else {
            GlideApp
                    .with(getApplicationContext())
                    .load(NetDataUrls.getAvatarUrl(this, channel.getAvatar().getHash()))
                    .into(binding.avatar);
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
        binding.imessageIdUser.setText(channel.getImessageIdUser());

        boolean hasPrevious = false;
        if (channel.getNote() != null) {
            binding.name.setText(channel.getNote());
            binding.username.setText(channel.getUsername());
            binding.layoutUsername.setVisibility(View.VISIBLE);
            hasPrevious = true;
        } else {
            binding.name.setText(channel.getUsername());
            binding.layoutUsername.setVisibility(View.GONE);
        }

        if (channel.getEmail() != null) {
            if (hasPrevious) {
                binding.emailDivider.setVisibility(View.VISIBLE);
            } else {
                binding.emailDivider.setVisibility(View.GONE);
            }
            binding.email.setText(channel.getEmail());
            binding.layoutEmail.setVisibility(View.VISIBLE);
            hasPrevious = true;
        } else {
            binding.layoutEmail.setVisibility(View.GONE);
            binding.emailDivider.setVisibility(View.GONE);
        }

        String regionDesc = channel.buildRegionDesc();
        if (regionDesc != null) {
            if (hasPrevious) {
                binding.regionDivider.setVisibility(View.VISIBLE);
            } else {
                binding.regionDivider.setVisibility(View.GONE);
            }
            binding.region.setText(regionDesc);
            binding.layoutRegion.setVisibility(View.VISIBLE);
        } else {
            binding.layoutRegion.setVisibility(View.GONE);
            binding.regionDivider.setVisibility(View.GONE);
        }

        binding.requestTime.setText(COMMON_SIMPLE_DATE_FORMAT.format(channelAddition.getRequestTime()));
        if(channelAddition.getRespondTime() == null){
            binding.respondTimeDivider.setVisibility(View.GONE);
            binding.layoutRespondTime.setVisibility(View.GONE);
        }else {
            binding.respondTimeDivider.setVisibility(View.VISIBLE);
            binding.layoutRespondTime.setVisibility(View.VISIBLE);
            binding.respondTime.setText(COMMON_SIMPLE_DATE_FORMAT.format(channelAddition.getRespondTime()));
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
                intent.putExtra(ExtraKeys.IMESSAGE_ID, channel.getImessageId());
                startActivity(intent);
            }
        });
        binding.acceptAddButton.setOnClickListener(v -> {
            new ConfirmDialog(this, "是否接受添加频道请求？")
                    .setNegativeButton()
                    .setPositiveButton("确定", (dialog, which) -> {
                        ChannelApiCaller.acceptAddChannel(this, new AcceptAddChannelPostBody(channelAddition.getUuid()), new RetrofitApiCaller.CommonYier<OperationStatus>(this){
                            @Override
                            public void ok(OperationStatus data, Response<OperationStatus> raw, Call<OperationStatus> call) {
                                super.ok(data, raw, call);
                                data.commonHandleResult(ChannelAdditionActivity.this, new int[]{-101, -102, -103}, () -> {
                                    new CustomViewMessageDialog(ChannelAdditionActivity.this, "频道已添加").create().show();
                                });
                            }
                        });
                    })
                    .create().show();
        });
    }

}