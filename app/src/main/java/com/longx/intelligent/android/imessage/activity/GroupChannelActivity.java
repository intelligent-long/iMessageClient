package com.longx.intelligent.android.imessage.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;

import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.imessage.activity.helper.BaseActivity;
import com.longx.intelligent.android.imessage.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.imessage.data.GroupChannel;
import com.longx.intelligent.android.imessage.data.GroupChannelAssociation;
import com.longx.intelligent.android.imessage.databinding.ActivityGroupChannelBinding;
import com.longx.intelligent.android.imessage.databinding.ActivityGroupChannelsBinding;
import com.longx.intelligent.android.imessage.net.dataurl.NetDataUrls;
import com.longx.intelligent.android.imessage.ui.glide.GlideApp;

public class GroupChannelActivity extends BaseActivity {
    private ActivityGroupChannelBinding binding;
    private GroupChannel groupChannel;
    private boolean isOwner;
    private boolean inGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGroupChannelBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupDefaultBackNavigation(binding.toolbar);
        intentData();
        showContent();
        setupYiers();
    }

    private void intentData() {
        groupChannel = getIntent().getParcelableExtra(ExtraKeys.GROUP_CHANNEL);
        String currentUserImessageId = SharedPreferencesAccessor.UserProfilePref.getCurrentUserProfile(this).getImessageId();
        if(groupChannel.getOwner().equals(currentUserImessageId)){
            isOwner = true;
        }
        for (GroupChannelAssociation groupChannelAssociation : groupChannel.getGroupChannelAssociations()) {
            if(groupChannelAssociation.getChannelImessageId().equals(currentUserImessageId)){
                inGroup = true;
                break;
            }
        }
    }

    private void showContent() {
        if(groupChannel.getGroupAvatar() == null){
            GlideApp.with(this).load(R.drawable.group_channel_default_avatar).into(binding.avatar);
        }else {
            GlideApp.with(this).load(NetDataUrls.getGroupAvatarUrl(this, groupChannel.getGroupAvatar().getHash())).into(binding.avatar);
        }
        if(isOwner){
            binding.addChannelButton.setVisibility(View.GONE);
        }else {
            if(inGroup){
                binding.editMyInfoButton.setVisibility(View.GONE);
                binding.addChannelButton.setVisibility(View.GONE);
            }else {
                binding.editMyInfoButton.setVisibility(View.GONE);
                binding.sendMessageButton.setVisibility(View.GONE);
            }
        }
        if(groupChannel.getNote() != null){
            binding.name.setText(groupChannel.getNote());
            binding.name1.setText(groupChannel.getName());
            binding.layoutName.setVisibility(View.VISIBLE);
        }else {
            binding.name.setText(groupChannel.getName());
            binding.layoutName.setVisibility(View.GONE);
        }
        binding.groupChannelIdUser.setText(groupChannel.getGroupChannelId()); //TODO
        String regionDesc = groupChannel.buildRegionDesc();
        if(groupChannel.getFirstRegion() == null){
            binding.layoutRegion.setVisibility(View.GONE);
            binding.regionDivider.setVisibility(View.GONE);
        }else {
            binding.layoutRegion.setVisibility(View.VISIBLE);
            if(groupChannel.getNote() == null){
                binding.regionDivider.setVisibility(View.GONE);
            }else {
                binding.regionDivider.setVisibility(View.VISIBLE);
            }
            binding.region.setText(regionDesc);
        }
        if((binding.layoutName.getVisibility() == View.GONE) && (binding.layoutRegion.getVisibility() == View.GONE)){
            binding.infos.setVisibility(View.GONE);
        }else {
            binding.infos.setVisibility(View.VISIBLE);
        }
    }

    private void setupYiers() {
        binding.toolbar.setOnMenuItemClickListener(item -> {
            if(item.getItemId() == R.id.more){
                Intent intent = new Intent(this, GroupChannelSettingActivity.class);
                intent.putExtra(ExtraKeys.CHANNEL, "");
                startActivity(intent);
            }
            return true;
        });
    }
}