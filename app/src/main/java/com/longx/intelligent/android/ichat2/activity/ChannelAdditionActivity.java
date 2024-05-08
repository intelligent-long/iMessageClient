package com.longx.intelligent.android.ichat2.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;

import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.activity.helper.BaseActivity;
import com.longx.intelligent.android.ichat2.behavior.GlideBehaviours;
import com.longx.intelligent.android.ichat2.data.ChannelAdditionInfo;
import com.longx.intelligent.android.ichat2.data.ChannelInfo;
import com.longx.intelligent.android.ichat2.data.request.AcceptAddChannelPostBody;
import com.longx.intelligent.android.ichat2.data.response.OperationStatus;
import com.longx.intelligent.android.ichat2.databinding.ActivityChannelAdditionBinding;
import com.longx.intelligent.android.ichat2.dialog.ConfirmDialog;
import com.longx.intelligent.android.ichat2.dialog.MessageDialog;
import com.longx.intelligent.android.ichat2.net.dataurl.NetDataUrls;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.ChannelApiCaller;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.ichat2.util.ErrorLogger;
import com.longx.intelligent.android.ichat2.yier.CopyTextOnLongClickYier;

import java.text.SimpleDateFormat;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Response;

public class ChannelAdditionActivity extends BaseActivity {
    private ActivityChannelAdditionBinding binding;
    private ChannelAdditionInfo channelAdditionInfo;
    private boolean isRequester;
    private ChannelInfo channelInfo;
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-HH-mm dd:MM:ss", Locale.CHINA);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChannelAdditionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupDefaultBackNavigation(binding.toolbar);
        channelAdditionInfo = getIntent().getParcelableExtra(ExtraKeys.CHANNEL_ADDITION_INFO);
        isRequester = channelAdditionInfo.isRequester(this);
        if(isRequester){
            channelInfo = channelAdditionInfo.getResponderChannelInfo();
        }else {
            channelInfo = channelAdditionInfo.getRequesterChannelInfo();
        }
        showContent();
        setupYiers();
    }

    private void showContent(){
        if (channelInfo.getAvatarInfo().getHash() == null) {
            GlideBehaviours.loadToImageView(getApplicationContext(), R.drawable.default_avatar, binding.avatar);
        } else {
            GlideBehaviours.loadToImageView(getApplicationContext(), NetDataUrls.getAvatarUrl(this, channelInfo.getAvatarInfo().getHash()), binding.avatar);
        }
        binding.username.setText(channelInfo.getUsername());
        if(channelInfo.getSex() == null || (channelInfo.getSex() != 0 && channelInfo.getSex() != 1)){
            binding.sexIcon.setVisibility(View.GONE);
        }else {
            binding.sexIcon.setVisibility(View.VISIBLE);
            if(channelInfo.getSex() == 0){
                binding.sexIcon.setImageResource(R.drawable.female_24px);
            }else {
                binding.sexIcon.setImageResource(R.drawable.male_24px);
            }
        }
        binding.ichatIdUser.setText(channelInfo.getIchatIdUser());
        if(channelInfo.getEmail() == null){
            binding.layoutEmail.setVisibility(View.GONE);
        }else {
            binding.layoutEmail.setVisibility(View.VISIBLE);
            binding.email.setText(channelInfo.getEmail());
        }
        String regionDesc = channelInfo.buildRegionDesc();
        if(regionDesc == null){
            binding.layoutRegion.setVisibility(View.GONE);
            binding.regionDivider.setVisibility(View.GONE);
        }else {
            binding.layoutRegion.setVisibility(View.VISIBLE);
            binding.regionDivider.setVisibility(View.VISIBLE);
            binding.region.setText(regionDesc);
        }
        binding.requestTime.setText(SIMPLE_DATE_FORMAT.format(channelAdditionInfo.getRequestTime()));
        if(channelAdditionInfo.getRespondTime() == null){
            binding.respondTimeDivider.setVisibility(View.GONE);
            binding.layoutRespondTime.setVisibility(View.GONE);
        }else {
            binding.respondTimeDivider.setVisibility(View.VISIBLE);
            binding.layoutRespondTime.setVisibility(View.VISIBLE);
            binding.respondTime.setText(SIMPLE_DATE_FORMAT.format(channelAdditionInfo.getRespondTime()));
        }
        String message = channelAdditionInfo.getMessage();
        if(message == null || message.equals("")){
            binding.messageLayout.setVisibility(View.GONE);
        }else {
            binding.messageLayout.setVisibility(View.VISIBLE);
            binding.messageText.setText(message);
        }
        if(channelAdditionInfo.isAccepted()){
            binding.addedText.setVisibility(View.VISIBLE);
            binding.expiredText.setVisibility(View.GONE);
            binding.acceptAddButton.setVisibility(View.GONE);
            binding.pendingConfirmText.setVisibility(View.GONE);
        }else if(channelAdditionInfo.isExpired()){
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
        setLongClickCopyYiers();
        binding.avatar.setOnClickListener(v -> {
            if(channelInfo != null && channelInfo.getAvatarInfo() != null && channelInfo.getAvatarInfo().getHash() != null) {
                Intent intent = new Intent(this, AvatarActivity.class);
                intent.putExtra(ExtraKeys.ICHAT_ID, channelInfo.getIchatId());
                intent.putExtra(ExtraKeys.AVATAR_HASH, channelInfo.getAvatarInfo().getHash());
                intent.putExtra(ExtraKeys.AVATAR_EXTENSION, channelInfo.getAvatarInfo().getExtension());
                startActivity(intent);
            }
        });
        binding.acceptAddButton.setOnClickListener(v -> {
            new ConfirmDialog(this, "是否接受添加频道请求？")
                    .setNegativeButton("取消", null)
                    .setPositiveButton("确定", (dialog, which) -> {
                        ChannelApiCaller.acceptAddChannel(this, new AcceptAddChannelPostBody(channelAdditionInfo.getUuid()), new RetrofitApiCaller.CommonYier<OperationStatus>(this){
                            @Override
                            public void ok(OperationStatus data, Response<OperationStatus> row, Call<OperationStatus> call) {
                                super.ok(data, row, call);
                                data.commonHandleResult(ChannelAdditionActivity.this, new int[]{-101, -102}, () -> {
                                    new MessageDialog(ChannelAdditionActivity.this, "添加频道", "频道已添加").show();
                                });
                            }
                        });
                    })
                    .show();
        });
    }

    private void setLongClickCopyYiers() {
        binding.username.setOnLongClickListener(new CopyTextOnLongClickYier(this, binding.ichatIdUser.getText().toString()));
        binding.ichatIdUser.setOnLongClickListener(new CopyTextOnLongClickYier(this, binding.ichatIdUser.getText().toString()));
        binding.email.setOnLongClickListener(new CopyTextOnLongClickYier(this, binding.ichatIdUser.getText().toString()));
        binding.region.setOnLongClickListener(new CopyTextOnLongClickYier(this, binding.ichatIdUser.getText().toString()));
    }

}