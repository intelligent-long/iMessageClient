package com.longx.intelligent.android.imessage.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;

import androidx.appcompat.content.res.AppCompatResources;

import com.bumptech.glide.request.target.Target;
import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.imessage.activity.helper.BaseActivity;
import com.longx.intelligent.android.imessage.activity.settings.EditGroupInfoSettingsActivity;
import com.longx.intelligent.android.imessage.behaviorcomponents.ContentUpdater;
import com.longx.intelligent.android.imessage.da.database.manager.GroupChannelDatabaseManager;
import com.longx.intelligent.android.imessage.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.imessage.data.GroupChannel;
import com.longx.intelligent.android.imessage.data.GroupChannelAssociation;
import com.longx.intelligent.android.imessage.databinding.ActivityGroupChannelBinding;
import com.longx.intelligent.android.imessage.data.AvatarType;
import com.longx.intelligent.android.imessage.net.dataurl.NetDataUrls;
import com.longx.intelligent.android.imessage.ui.glide.GlideApp;
import com.longx.intelligent.android.imessage.yier.CopyTextOnLongClickYier;
import com.longx.intelligent.android.imessage.yier.GlobalYiersHolder;

import java.util.List;

public class GroupChannelActivity extends BaseActivity implements ContentUpdater.OnServerContentUpdateYier {
    private ActivityGroupChannelBinding binding;
    private GroupChannel groupChannel;
    private boolean isOwner;
    private boolean inGroup;
    private boolean networkFetch;
    private String inviteUuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGroupChannelBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupDefaultBackNavigation(binding.toolbar);
        intentData();
        showContent();
        setupYiers();
        GlobalYiersHolder.holdYier(this, ContentUpdater.OnServerContentUpdateYier.class, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GlobalYiersHolder.removeYier(this, ContentUpdater.OnServerContentUpdateYier.class, this);
    }

    private void intentData() {
        groupChannel = getIntent().getParcelableExtra(ExtraKeys.GROUP_CHANNEL);
        String currentUserImessageId = SharedPreferencesAccessor.UserProfilePref.getCurrentUserProfile(this).getImessageId();
        if(groupChannel.getOwner().equals(currentUserImessageId)){
            isOwner = true;
        }
        for (GroupChannelAssociation groupChannelAssociation : groupChannel.getGroupChannelAssociations()) {
            if(groupChannelAssociation.getRequester().getImessageId().equals(currentUserImessageId)){
                inGroup = true;
                break;
            }
        }
        networkFetch = getIntent().getBooleanExtra(ExtraKeys.MAY_NOT_ASSOCIATED, false);
        inviteUuid = getIntent().getStringExtra(ExtraKeys.INVITE_UUID);
    }

    private void showContent() {
//        if(inviteUuid != null) binding.toolbar.setTitle("接受邀请");
        if(networkFetch && !inGroup) binding.toolbar.getMenu().findItem(R.id.more).setVisible(false);
        if(groupChannel.getGroupAvatar() == null || groupChannel.getGroupAvatar().getHash() == null){
            GlideApp.with(this)
                    .load(AppCompatResources.getDrawable(this, R.drawable.group_channel_default_avatar))
                    .override(Target.SIZE_ORIGINAL)
                    .into(binding.avatar);
        }else {
            GlideApp.with(this)
                    .load(NetDataUrls.getGroupAvatarUrl(this, groupChannel.getGroupAvatar().getHash()))
                    .into(binding.avatar);
        }
        if(isOwner){
            binding.joinChannelButton.setVisibility(View.GONE);
        }else {
            binding.editInfoButton.setVisibility(View.GONE);
            if(inGroup){
                binding.joinChannelButton.setVisibility(View.GONE);
            }else {
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
        binding.groupChannelIdUser.setText(groupChannel.getGroupChannelIdUser());
        String regionDesc = groupChannel.buildRegionDesc();
        if(regionDesc == null){
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
        binding.avatar.setOnClickListener(v -> {
            if(groupChannel != null && groupChannel.getGroupAvatar() != null && groupChannel.getGroupAvatar().getHash() != null) {
                Intent intent = new Intent(this, AvatarActivity.class);
                intent.putExtra(ExtraKeys.IMESSAGE_ID, groupChannel.getGroupChannelId());
                intent.putExtra(ExtraKeys.AVATAR_HASH, groupChannel.getGroupAvatar().getHash());
                intent.putExtra(ExtraKeys.AVATAR_EXTENSION, groupChannel.getGroupAvatar().getExtension());
                intent.putExtra(ExtraKeys.AVATAR_TYPE, (Parcelable) AvatarType.GROUP_CHANNEL);
                startActivity(intent);
            }
        });
        binding.toolbar.setOnMenuItemClickListener(item -> {
            if(item.getItemId() == R.id.more){
                Intent intent = new Intent(this, GroupChannelSettingActivity.class);
                intent.putExtra(ExtraKeys.GROUP_CHANNEL, groupChannel);
                startActivity(intent);
            }
            return true;
        });
        binding.editInfoButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditGroupInfoSettingsActivity.class);
            intent.putExtra(ExtraKeys.GROUP_CHANNEL_ID, groupChannel.getGroupChannelId());
            startActivity(intent);
        });
        binding.joinChannelButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, RequestJoinGroupChannelActivity.class);
            intent.putExtra(ExtraKeys.GROUP_CHANNEL, groupChannel);
            intent.putExtra(ExtraKeys.INVITE_UUID, inviteUuid);
            startActivity(intent);
        });
        binding.sendMessageButton.setOnClickListener(v -> {

        });
        setLongClickCopyYiers();
    }
    private void setLongClickCopyYiers() {
        binding.name.setOnLongClickListener(new CopyTextOnLongClickYier(this, binding.name.getText().toString()));
        binding.name1.setOnLongClickListener(new CopyTextOnLongClickYier(this, binding.name1.getText().toString()));
        binding.groupChannelIdUser.setOnLongClickListener(new CopyTextOnLongClickYier(this, binding.groupChannelIdUser.getText().toString()));
        binding.region.setOnLongClickListener(new CopyTextOnLongClickYier(this, binding.region.getText().toString()));
    }

    @Override
    public void onStartUpdate(String id, List<String> updatingIds, Object... objects) {

    }

    @Override
    public void onUpdateComplete(String id, List<String> updatingIds, Object... objects) {
        if(id.equals(ContentUpdater.OnServerContentUpdateYier.ID_GROUP_CHANNEL) && objects[0].equals(groupChannel.getGroupChannelId())){
            groupChannel = GroupChannelDatabaseManager.getInstance().findOneAssociation(groupChannel.getGroupChannelId());
            showContent();
            setLongClickCopyYiers();
        }
    }
}