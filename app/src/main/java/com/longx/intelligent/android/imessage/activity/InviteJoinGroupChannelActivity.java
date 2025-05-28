package com.longx.intelligent.android.imessage.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.content.res.AppCompatResources;

import com.bumptech.glide.request.target.Target;
import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.imessage.activity.helper.BaseActivity;
import com.longx.intelligent.android.imessage.da.database.manager.ChannelDatabaseManager;
import com.longx.intelligent.android.imessage.da.database.manager.GroupChannelDatabaseManager;
import com.longx.intelligent.android.imessage.data.Channel;
import com.longx.intelligent.android.imessage.data.GroupChannel;
import com.longx.intelligent.android.imessage.data.request.InviteJoinGroupChannelPostBody;
import com.longx.intelligent.android.imessage.data.response.OperationStatus;
import com.longx.intelligent.android.imessage.databinding.ActivityInviteJoinGroupChannelBinding;
import com.longx.intelligent.android.imessage.dialog.ChooseOneChannelDialog;
import com.longx.intelligent.android.imessage.dialog.ChooseOneGroupChannelDialog;
import com.longx.intelligent.android.imessage.dialog.ConfirmDialog;
import com.longx.intelligent.android.imessage.dialog.CustomViewMessageDialog;
import com.longx.intelligent.android.imessage.net.dataurl.NetDataUrls;
import com.longx.intelligent.android.imessage.net.retrofit.caller.GroupChannelApiCaller;
import com.longx.intelligent.android.imessage.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.imessage.ui.glide.GlideApp;
import com.longx.intelligent.android.imessage.value.Constants;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class InviteJoinGroupChannelActivity extends BaseActivity {
    private ActivityInviteJoinGroupChannelBinding binding;
    private Channel choseChannel;
    private GroupChannel choseGroupChannel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInviteJoinGroupChannelBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupDefaultBackNavigation(binding.toolbar);
        showContent();
        setupYiers();
    }

    private void showContent() {
        binding.messageInput.setText(Constants.INVITE_JOIN_GROUP_CHANNEL_MESSAGE);
    }

    private void showChannel(){
        if(choseChannel == null){
            binding.channel.setVisibility(View.GONE);
        }else {
            binding.channel.setVisibility(View.VISIBLE);
            if (choseChannel.getAvatar() == null || choseChannel.getAvatar().getHash() == null) {
                GlideApp
                        .with(this)
                        .load(R.drawable.default_avatar)
                        .into(binding.channelAvatar);
            } else {
                GlideApp
                        .with(this)
                        .load(NetDataUrls.getAvatarUrl(this, choseChannel.getAvatar().getHash()))
                        .into(binding.channelAvatar);
            }
            if(choseChannel.getNote() != null){
                binding.channelName.setText(choseChannel.getNote());
            }else {
                binding.channelName.setText(choseChannel.getUsername());
            }
            if(choseChannel.getSex() == null || (choseChannel.getSex() != 0 && choseChannel.getSex() != 1)){
                binding.channelSexIcon.setVisibility(View.GONE);
            }else {
                binding.channelSexIcon.setVisibility(View.VISIBLE);
                if(choseChannel.getSex() == 0){
                    binding.channelSexIcon.setImageResource(R.drawable.female_24px);
                }else {
                    binding.channelSexIcon.setImageResource(R.drawable.male_24px);
                }
            }
            binding.channelImessageIdUser.setText(choseChannel.getImessageIdUser());
        }
    }

    private void showGroupChannel(){
        if(choseGroupChannel == null){
            binding.groupChannel.setVisibility(View.GONE);
        }else {
            binding.groupChannel.setVisibility(View.VISIBLE);
            if(choseGroupChannel.getGroupAvatar() == null || choseGroupChannel.getGroupAvatar().getHash() == null){
                GlideApp.with(this)
                        .load(AppCompatResources.getDrawable(this, R.drawable.group_channel_default_avatar))
                        .override(Target.SIZE_ORIGINAL)
                        .into(binding.groupChannelAvatar);
            }else {
                GlideApp.with(this)
                        .load(NetDataUrls.getGroupAvatarUrl(this, choseGroupChannel.getGroupAvatar().getHash()))
                        .into(binding.groupChannelAvatar);
            }
            if(choseGroupChannel.getNote() != null){
                binding.groupChannelName.setText(choseGroupChannel.getNote());
            }else {
                binding.groupChannelName.setText(choseGroupChannel.getName());
            }
            binding.groupChannelIdUser.setText(choseGroupChannel.getGroupChannelIdUser());
        }
    }

    private void setupYiers() {
        binding.chooseChannelButton.setOnClickListener(v -> {
            List<Channel> channels = new ArrayList<>();
            ChannelDatabaseManager.getInstance().findAllAssociations().forEach(channelAssociation -> {
                channels.add(channelAssociation.getChannel());
            });
            ChooseOneChannelDialog chooseOneChannelDialog = new ChooseOneChannelDialog(this, "选择频道", channels, choseChannel);
            chooseOneChannelDialog
                    .setPositiveButton("确定", (dialog, which) -> {
                        choseChannel = chooseOneChannelDialog.getAdapter().getSelected();
                        showChannel();
                    })
                    .setNegativeButton(null)
                    .create().show();
        });
        binding.chooseGroupChannelButton.setOnClickListener(v -> {
            List<GroupChannel> allAssociations = GroupChannelDatabaseManager.getInstance().findAllAssociations();
            ChooseOneGroupChannelDialog chooseOneGroupChannelDialog = new ChooseOneGroupChannelDialog(this, "选择群频道", allAssociations, choseGroupChannel);
            chooseOneGroupChannelDialog
                    .setNegativeButton(null)
                    .setPositiveButton("确定", (dialog, which) -> {
                        choseGroupChannel = chooseOneGroupChannelDialog.getAdapter().getSelected();
                        showGroupChannel();
                    })
                    .create().show();
        });
        binding.channel.setOnClickListener(v -> {
            if(choseChannel != null) {
                Intent intent = new Intent(this, ChannelActivity.class);
                intent.putExtra(ExtraKeys.CHANNEL, choseChannel);
                startActivity(intent);
            }
        });
        binding.groupChannel.setOnClickListener(v -> {
            if(choseGroupChannel != null){
                Intent intent = new Intent(this, GroupChannelActivity.class);
                intent.putExtra(ExtraKeys.GROUP_CHANNEL, choseGroupChannel);
                startActivity(intent);
            }
        });
        binding.toolbar.setOnMenuItemClickListener(item -> {
            if(item.getItemId() == R.id.invite){
                if(choseChannel == null || choseGroupChannel == null){
                    new CustomViewMessageDialog(this, "请选择频道").create().show();
                    return true;
                }
                new ConfirmDialog(this)
                        .setNegativeButton()
                        .setPositiveButton((dialog, which) -> {
                            String message = binding.messageInput.getText() == null ? null : binding.messageInput.getText() .toString();
                            InviteJoinGroupChannelPostBody postBody = new InviteJoinGroupChannelPostBody(message, choseChannel, choseGroupChannel);
                            GroupChannelApiCaller.invite(this, postBody, new RetrofitApiCaller.CommonYier<OperationStatus>(this){
                                @Override
                                public void ok(OperationStatus data, Response<OperationStatus> raw, Call<OperationStatus> call) {
                                    super.ok(data, raw, call);
                                    data.commonHandleResult(InviteJoinGroupChannelActivity.this, new int[]{-101, -102, -103}, () -> {
                                        new CustomViewMessageDialog(InviteJoinGroupChannelActivity.this, "已发送邀请添加群频道请求").create().show();
                                    });
                                }
                            });
                        })
                        .create().show();
            }
            return true;
        });
    }
}