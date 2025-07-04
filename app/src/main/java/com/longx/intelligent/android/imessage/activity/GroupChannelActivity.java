package com.longx.intelligent.android.imessage.activity;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import com.longx.intelligent.android.imessage.util.ErrorLogger;
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
    private ActivityResultLauncher<Intent> groupChannelSettingResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGroupChannelBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupDefaultBackNavigation(binding.toolbar);
        groupChannelSettingResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            if(data.getBooleanExtra(ExtraKeys.TRUE, false)){
                                binding.sendMessageButton.setVisibility(GONE);
                                binding.joinChannelButton.setVisibility(VISIBLE);
                                binding.toolbar.getMenu().findItem(R.id.more).setVisible(false);
                            }
                        }
                    }
                });
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
        try {
            for (GroupChannelAssociation groupChannelAssociation : groupChannel.getGroupChannelAssociations()) {
                if (groupChannelAssociation.getRequester().getImessageId().equals(currentUserImessageId)) {
                    inGroup = true;
                    break;
                }
            }
        } catch (Exception ignore) {
        }
        networkFetch = getIntent().getBooleanExtra(ExtraKeys.MAY_NOT_ASSOCIATED, false);
        inviteUuid = getIntent().getStringExtra(ExtraKeys.INVITE_UUID);
    }

    private void showContent() {
        if (groupChannel.getGroupAvatar() == null || groupChannel.getGroupAvatar().getHash() == null) {
            GlideApp.with(this)
                    .load(AppCompatResources.getDrawable(this, R.drawable.group_channel_default_avatar))
                    .override(Target.SIZE_ORIGINAL)
                    .into(binding.avatar);
        } else {
            GlideApp.with(this)
                    .load(NetDataUrls.getGroupAvatarUrl(this, groupChannel.getGroupAvatar().getHash()))
                    .into(binding.avatar);
        }
        if(groupChannel.getNote() != null){
            binding.name.setText(groupChannel.getNote());
            binding.name1.setText(groupChannel.getName());
            binding.layoutName.setVisibility(VISIBLE);
        }else {
            binding.name.setText(groupChannel.getName());
            binding.layoutName.setVisibility(GONE);
        }
        binding.groupChannelIdUser.setText(groupChannel.getGroupChannelIdUser());
        String regionDesc = groupChannel.buildRegionDesc();
        if(regionDesc == null){
            binding.layoutRegion.setVisibility(GONE);
            binding.regionDivider.setVisibility(GONE);
        }else {
            binding.layoutRegion.setVisibility(VISIBLE);
            if(groupChannel.getNote() == null){
                binding.regionDivider.setVisibility(GONE);
            }else {
                binding.regionDivider.setVisibility(VISIBLE);
            }
            binding.region.setText(regionDesc);
        }
        if((binding.layoutName.getVisibility() == GONE) && (binding.layoutRegion.getVisibility() == GONE)){
            binding.infos.setVisibility(GONE);
        }else {
            binding.infos.setVisibility(VISIBLE);
        }
        if(groupChannel.isTerminated()) {
            binding.toolbar.getMenu().findItem(R.id.more).setVisible(false);
            binding.toolbar.getMenu().findItem(R.id.qr_code).setVisible(false);
            binding.layoutAllGroupMembers.setVisibility(GONE);
            binding.editInfoButton.setVisibility(GONE);
            binding.joinChannelButton.setVisibility(GONE);
            binding.sendMessageButton.setVisibility(GONE);
            binding.terminatedText.setVisibility(VISIBLE);
        }else {
            binding.terminatedText.setVisibility(GONE);
            if (networkFetch && !inGroup)
                binding.toolbar.getMenu().findItem(R.id.more).setVisible(false);
            if (isOwner) {
                binding.joinChannelButton.setVisibility(GONE);
            } else {
                binding.editInfoButton.setVisibility(GONE);
                if (inGroup) {
                    binding.joinChannelButton.setVisibility(GONE);
                    binding.toolbar.getMenu().findItem(R.id.more).setVisible(true);
                } else {
                    binding.sendMessageButton.setVisibility(GONE);
                    binding.toolbar.getMenu().findItem(R.id.more).setVisible(false);
                }
            }
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
            if(item.getItemId() == R.id.qr_code){
                Intent intent = new Intent(GroupChannelActivity.this, QrCodeActivity.class);
                intent.putExtra(ExtraKeys.TYPE_OBJECT, groupChannel);
                intent.putExtra(ExtraKeys.DESCRIPTION, "扫描二维码打开群频道");
                startActivity(intent);
            }else if(item.getItemId() == R.id.more){
                Intent intent = new Intent(this, GroupChannelSettingActivity.class);
                intent.putExtra(ExtraKeys.GROUP_CHANNEL, groupChannel);
                groupChannelSettingResultLauncher.launch(intent);
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
        binding.clickLayoutAllGroupMembers.setOnClickListener(v -> {
            Intent intent = new Intent(this, GroupMembersActivity.class);
            intent.putExtra(ExtraKeys.GROUP_CHANNEL_ID, groupChannel.getGroupChannelId());
            startActivity(intent);
        });
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