package com.longx.intelligent.android.imessage.activity;

import static com.longx.intelligent.android.imessage.value.Constants.COMMON_SIMPLE_DATE_FORMAT;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.imessage.activity.helper.BaseActivity;
import com.longx.intelligent.android.imessage.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.imessage.data.Avatar;
import com.longx.intelligent.android.imessage.data.Channel;
import com.longx.intelligent.android.imessage.data.GroupAvatar;
import com.longx.intelligent.android.imessage.data.GroupChannel;
import com.longx.intelligent.android.imessage.data.GroupChannelInvitation;
import com.longx.intelligent.android.imessage.data.Self;
import com.longx.intelligent.android.imessage.data.response.OperationData;
import com.longx.intelligent.android.imessage.databinding.ActivityGroupChannelInvitationBinding;
import com.longx.intelligent.android.imessage.dialog.ConfirmDialog;
import com.longx.intelligent.android.imessage.net.dataurl.NetDataUrls;
import com.longx.intelligent.android.imessage.net.retrofit.caller.GroupChannelApiCaller;
import com.longx.intelligent.android.imessage.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.imessage.ui.glide.GlideApp;

import retrofit2.Call;
import retrofit2.Response;

public class GroupChannelInvitationActivity extends BaseActivity {
    private ActivityGroupChannelInvitationBinding binding;
    private GroupChannelInvitation groupChannelInvitation;
    private boolean isInviter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGroupChannelInvitationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupDefaultBackNavigation(binding.toolbar);
        intentData();
        showContent();
        setupYiers();
    }

    private void intentData() {
        groupChannelInvitation = getIntent().getParcelableExtra(ExtraKeys.GROUP_CHANNEL_INVITATION);
        Self self = SharedPreferencesAccessor.UserProfilePref.getCurrentUserProfile(this);
        isInviter = groupChannelInvitation.getInviter().getImessageId().equals(self.getImessageId());
    }

    private void showContent() {
        String message = groupChannelInvitation.getMessage();
        if (message == null || message.isEmpty()) {
            binding.messageLayout.setVisibility(View.GONE);
            binding.labelMessage.setVisibility(View.GONE);
        } else {
            binding.messageText.setText(message);
        }
        Channel channel;
        if (isInviter) {
            binding.labelInviter.setVisibility(View.GONE);
            binding.labelInvitee.setVisibility(View.VISIBLE);
            channel = groupChannelInvitation.getInvitee();
        } else {
            binding.labelInviter.setVisibility(View.VISIBLE);
            binding.labelInvitee.setVisibility(View.GONE);
            channel = groupChannelInvitation.getInviter();
        }
        Avatar avatar = channel.getAvatar();
        if (avatar != null && avatar.getHash() != null) {
            GlideApp.with(this)
                    .load(NetDataUrls.getAvatarUrl(this, avatar.getHash()))
                    .into(binding.avatarChannel);
        } else {
            GlideApp.with(this)
                    .load(R.drawable.default_avatar)
                    .into(binding.avatarChannel);
        }
        binding.idChannel.setText(channel.getImessageIdUser());
        if (channel.getNote() != null) {
            binding.nameChannel.setText(channel.getNote());
        } else {
            binding.nameChannel.setText(channel.getUsername());
        }
        GroupChannel groupChannel = groupChannelInvitation.getGroupChannelInvitedTo();
        GroupAvatar groupAvatar = groupChannel.getGroupAvatar();
        if (groupAvatar != null && groupAvatar.getHash() != null) {
            GlideApp.with(this)
                    .load(NetDataUrls.getGroupAvatarUrl(this, groupAvatar.getHash()))
                    .into(binding.avatarGroup);
        } else {
            GlideApp.with(this)
                    .load(R.drawable.group_channel_default_avatar)
                    .into(binding.avatarGroup);
        }
        binding.idGroup.setText(groupChannel.getGroupChannelIdUser());
        if (groupChannel.getNote() != null) {
            binding.nameGroup.setText(groupChannel.getNote());
        } else {
            binding.nameGroup.setText(groupChannel.getName());
        }
        binding.inviteTime.setText(COMMON_SIMPLE_DATE_FORMAT.format(groupChannelInvitation.getRequestTime()));
        if (groupChannelInvitation.getRespondTime() == null) {
            binding.acceptTimeDivider.setVisibility(View.GONE);
            binding.layoutAcceptTime.setVisibility(View.GONE);
        } else {
            binding.acceptTime.setText(COMMON_SIMPLE_DATE_FORMAT.format(groupChannelInvitation.getRespondTime()));
        }
        if (groupChannelInvitation.isAccepted()) {
            binding.acceptedText.setVisibility(View.VISIBLE);
            binding.expiredText.setVisibility(View.GONE);
            binding.acceptAddButton.setVisibility(View.GONE);
            binding.pendingConfirmText.setVisibility(View.GONE);
        } else if (groupChannelInvitation.isExpired()) {
            binding.acceptedText.setVisibility(View.GONE);
            binding.expiredText.setVisibility(View.VISIBLE);
            binding.acceptAddButton.setVisibility(View.GONE);
            binding.pendingConfirmText.setVisibility(View.GONE);
        } else {
            binding.acceptedText.setVisibility(View.GONE);
            binding.expiredText.setVisibility(View.GONE);
            if (isInviter) {
                binding.pendingConfirmText.setVisibility(View.VISIBLE);
                binding.acceptAddButton.setVisibility(View.GONE);
            } else {
                binding.pendingConfirmText.setVisibility(View.GONE);
                binding.acceptAddButton.setVisibility(View.VISIBLE);
            }
        }
    }

    private void setupYiers() {
        String imessageId;
        if (isInviter) {
            imessageId = groupChannelInvitation.getInvitee().getImessageId();
        } else {
            imessageId = groupChannelInvitation.getInviter().getImessageId();
        }
        binding.clickViewHeaderChannel.setOnClickListener(v -> {
            Intent intent = new Intent(this, ChannelActivity.class);
            intent.putExtra(ExtraKeys.IMESSAGE_ID, imessageId);
            startActivity(intent);
        });
        binding.clickViewHeaderGroup.setOnClickListener(v -> {
            Intent intent = new Intent(this, GroupChannelActivity.class);
            intent.putExtra(ExtraKeys.GROUP_CHANNEL, groupChannelInvitation.getGroupChannelInvitedTo());
            intent.putExtra(ExtraKeys.MAY_NOT_ASSOCIATED, true);
            startActivity(intent);
        });
        binding.acceptAddButton.setOnClickListener(v -> {
            new ConfirmDialog(this, "是否接受添加群频道邀请？")
                    .setNegativeButton()
                    .setPositiveButton("确定", (dialog, which) -> {
                        Intent intent = new Intent(GroupChannelInvitationActivity.this, GroupChannelActivity.class);
                        intent.putExtra(ExtraKeys.GROUP_CHANNEL, groupChannelInvitation.getGroupChannelInvitedTo());
                        intent.putExtra(ExtraKeys.INVITE_UUID, groupChannelInvitation.getUuid());
                        intent.putExtra(ExtraKeys.MAY_NOT_ASSOCIATED, true);
                        startActivity(intent);
                    })
                    .create().show();
        });
    }
}