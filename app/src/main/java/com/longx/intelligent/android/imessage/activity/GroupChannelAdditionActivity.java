package com.longx.intelligent.android.imessage.activity;

import static com.longx.intelligent.android.imessage.value.Constants.COMMON_SIMPLE_DATE_FORMAT;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.imessage.activity.helper.BaseActivity;
import com.longx.intelligent.android.imessage.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.imessage.data.Avatar;
import com.longx.intelligent.android.imessage.data.Channel;
import com.longx.intelligent.android.imessage.data.GroupAvatar;
import com.longx.intelligent.android.imessage.data.GroupChannel;
import com.longx.intelligent.android.imessage.data.GroupChannelAddition;
import com.longx.intelligent.android.imessage.data.Self;
import com.longx.intelligent.android.imessage.data.request.AcceptAddGroupChannelPostBody;
import com.longx.intelligent.android.imessage.data.response.OperationStatus;
import com.longx.intelligent.android.imessage.databinding.ActivityGroupChannelAdditionBinding;
import com.longx.intelligent.android.imessage.dialog.ConfirmDialog;
import com.longx.intelligent.android.imessage.dialog.MessageDialog;
import com.longx.intelligent.android.imessage.net.dataurl.NetDataUrls;
import com.longx.intelligent.android.imessage.net.retrofit.caller.GroupChannelApiCaller;
import com.longx.intelligent.android.imessage.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.imessage.ui.glide.GlideApp;
import com.longx.intelligent.android.imessage.util.ErrorLogger;

import retrofit2.Call;
import retrofit2.Response;

public class GroupChannelAdditionActivity extends BaseActivity {
    private ActivityGroupChannelAdditionBinding binding;
    private GroupChannelAddition groupChannelAddition;
    private boolean isRequester;
    private Channel requesterChannel;
    private GroupChannel responderGroupChannel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGroupChannelAdditionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupDefaultBackNavigation(binding.toolbar);
        intentData();
        showContent();
        setupYiers();
    }

    private void intentData() {
        groupChannelAddition = getIntent().getParcelableExtra(ExtraKeys.GROUP_CHANNEL_ADDITION_INFO);
        Self self = SharedPreferencesAccessor.UserProfilePref.getCurrentUserProfile(this);
        isRequester = groupChannelAddition.getRequesterChannel().getImessageId().equals(self.getImessageId());
        requesterChannel = groupChannelAddition.getRequesterChannel();
        responderGroupChannel = groupChannelAddition.getResponderGroupChannel();
    }

    private void showContent() {
        String message = groupChannelAddition.getMessage();
        if(message == null || message.equals("")){
            binding.messageLayout.setVisibility(View.GONE);
        }else {
            binding.messageText.setText(message);
        }
        if(isRequester){
            GroupAvatar groupAvatar = responderGroupChannel.getGroupAvatar();
            if(groupAvatar != null) {
                GlideApp.with(this)
                        .load(NetDataUrls.getGroupAvatarUrl(this, groupAvatar.getHash()))
                        .into(binding.avatar);
            }else {
                GlideApp.with(this)
                        .load(R.drawable.group_channel_default_avatar)
                        .into(binding.avatar);
            }
            binding.idUser.setText(responderGroupChannel.getGroupChannelIdUser());

            boolean hasPrevious = false;
            if (responderGroupChannel.getNote() != null) {
                binding.name.setText(responderGroupChannel.getNote());
                binding.username.setText(responderGroupChannel.getName());
                binding.layoutUsername.setVisibility(View.VISIBLE);
                hasPrevious = true;
            } else {
                binding.name.setText(responderGroupChannel.getName());
                binding.layoutUsername.setVisibility(View.GONE);
            }

            binding.layoutEmail.setVisibility(View.GONE);
            binding.emailDivider.setVisibility(View.GONE);

            if (responderGroupChannel.getFirstRegion() != null) {
                if (hasPrevious) {
                    binding.regionDivider.setVisibility(View.VISIBLE);
                } else {
                    binding.regionDivider.setVisibility(View.GONE);
                }
                binding.region.setText(responderGroupChannel.buildRegionDesc());
                binding.layoutRegion.setVisibility(View.VISIBLE);
            } else {
                binding.layoutRegion.setVisibility(View.GONE);
                binding.regionDivider.setVisibility(View.GONE);
            }
        }else {
            Avatar avatar = requesterChannel.getAvatar();
            if(avatar != null) {
                GlideApp.with(this)
                        .load(NetDataUrls.getGroupAvatarUrl(this, avatar.getHash()))
                        .into(binding.avatar);
            }else {
                GlideApp.with(this)
                        .load(R.drawable.default_avatar)
                        .into(binding.avatar);
            }
            binding.idUser.setText(requesterChannel.getImessageIdUser());

            boolean hasPrevious = false;
            if (requesterChannel.getNote() != null) {
                binding.name.setText(requesterChannel.getNote());
                binding.username.setText(requesterChannel.getUsername());
                binding.layoutUsername.setVisibility(View.VISIBLE);
                hasPrevious = true;
            } else {
                binding.name.setText(requesterChannel.getUsername());
                binding.layoutUsername.setVisibility(View.GONE);
            }

            if (requesterChannel.getEmail() != null) {
                if (hasPrevious) {
                    binding.emailDivider.setVisibility(View.VISIBLE);
                } else {
                    binding.emailDivider.setVisibility(View.GONE);
                }
                binding.email.setText(requesterChannel.getEmail());
                binding.layoutEmail.setVisibility(View.VISIBLE);
                hasPrevious = true;
            } else {
                binding.layoutEmail.setVisibility(View.GONE);
                binding.emailDivider.setVisibility(View.GONE);
            }

            if (requesterChannel.getFirstRegion() != null) {
                if (hasPrevious) {
                    binding.regionDivider.setVisibility(View.VISIBLE);
                } else {
                    binding.regionDivider.setVisibility(View.GONE);
                }
                binding.region.setText(requesterChannel.buildRegionDesc());
                binding.layoutRegion.setVisibility(View.VISIBLE);
            } else {
                binding.layoutRegion.setVisibility(View.GONE);
                binding.regionDivider.setVisibility(View.GONE);
            }
        }
        binding.requestTime.setText(COMMON_SIMPLE_DATE_FORMAT.format(groupChannelAddition.getRequestTime()));
        if(groupChannelAddition.getRespondTime() == null){
            binding.respondTimeDivider.setVisibility(View.GONE);
            binding.layoutRespondTime.setVisibility(View.GONE);
        }else {
            binding.respondTime.setText(COMMON_SIMPLE_DATE_FORMAT.format(groupChannelAddition.getRespondTime()));
        }
        if(groupChannelAddition.isAccepted()){
            binding.addedText.setVisibility(View.VISIBLE);
            binding.expiredText.setVisibility(View.GONE);
            binding.acceptAddButton.setVisibility(View.GONE);
            binding.pendingConfirmText.setVisibility(View.GONE);
        }else if(groupChannelAddition.isExpired()){
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
        if(isRequester){
            binding.clickViewHeader.setOnClickListener(v -> {
                Intent intent = new Intent(this, GroupChannelActivity.class);
                intent.putExtra(ExtraKeys.GROUP_CHANNEL, groupChannelAddition.getResponderGroupChannel());
                intent.putExtra(ExtraKeys.MAY_NOT_ASSOCIATED, true);
                startActivity(intent);
            });
        }else {
            binding.clickViewHeader.setOnClickListener(v -> {
                if(requesterChannel != null) {
                    Intent intent = new Intent(this, ChannelActivity.class);
                    intent.putExtra(ExtraKeys.IMESSAGE_ID, requesterChannel.getImessageId());
                    startActivity(intent);
                }
            });
        }
        binding.acceptAddButton.setOnClickListener(v -> {
            new ConfirmDialog(this, "是否接受添加频道请求？")
                    .setNegativeButton()
                    .setPositiveButton("确定", (dialog, which) -> {
                        GroupChannelApiCaller.acceptAdd(this, new AcceptAddGroupChannelPostBody(groupChannelAddition.getUuid()), new RetrofitApiCaller.CommonYier<OperationStatus>(this){
                            @Override
                            public void ok(OperationStatus data, Response<OperationStatus> raw, Call<OperationStatus> call) {
                                super.ok(data, raw, call);
                                data.commonHandleResult(GroupChannelAdditionActivity.this, new int[]{-101, -102, -103}, () -> {
                                    new MessageDialog(GroupChannelAdditionActivity.this, "添加群频道", "群频道已添加").create().show();
                                });
                            }
                        });
                    })
                    .create().show();
        });
    }
}