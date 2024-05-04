package com.longx.intelligent.android.ichat2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.signature.ObjectKey;
import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.activity.helper.BaseActivity;
import com.longx.intelligent.android.ichat2.activity.settings.EditUserSettingsActivity;
import com.longx.intelligent.android.ichat2.behavior.ContentUpdater;
import com.longx.intelligent.android.ichat2.behavior.GlideBehaviours;
import com.longx.intelligent.android.ichat2.da.cachefile.CacheFilesAccessor;
import com.longx.intelligent.android.ichat2.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.ichat2.data.ChannelInfo;
import com.longx.intelligent.android.ichat2.data.SelfInfo;
import com.longx.intelligent.android.ichat2.databinding.ActivityChannelBinding;
import com.longx.intelligent.android.ichat2.net.dataurl.NetDataUrls;
import com.longx.intelligent.android.ichat2.ui.glide.GlideApp;
import com.longx.intelligent.android.ichat2.yier.CopyTextOnLongClickYier;
import com.longx.intelligent.android.ichat2.yier.GlobalYiersHolder;

import java.io.File;

public class ChannelActivity extends BaseActivity implements ContentUpdater.OnServerContentUpdateYier{
    private ActivityChannelBinding binding;
    private ChannelInfo channelInfo;
    private SelfInfo selfInfo;
    private boolean isNetworkFetched;
    private boolean isSelf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChannelBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupDefaultBackNavigation(binding.toolbar);
        bindValues();
        showContent();
        setupYiers();
        GlobalYiersHolder.holdYier(this, ContentUpdater.OnServerContentUpdateYier.class, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GlobalYiersHolder.removeYier(this, ContentUpdater.OnServerContentUpdateYier.class, this);
    }

    /**
     * to use self or channel
     */
    private void bindValues() {
        isNetworkFetched = getIntent().getBooleanExtra(ExtraKeys.IS_NETWORK_FETCHED, false);
        String ichatId = getIntent().getStringExtra(ExtraKeys.ICHAT_ID);
        channelInfo = getIntent().getParcelableExtra(ExtraKeys.CHANNEL_INFO);
        selfInfo = SharedPreferencesAccessor.UserInfoPref.getCurrentUserInfo(this);
        isSelf = (ichatId == null && channelInfo == null)
                || (ichatId != null && ichatId.equals(selfInfo.getIchatId())
                || (channelInfo != null && channelInfo.getIchatId().equals(selfInfo.getIchatId())));
        if(!isSelf && channelInfo == null){
            channelInfo = channelInfo; //TODO: get channelInfo by ichatId
        }
    }

    private void showContent(){
        if(isSelf) {
            showSelfContent();
        }else {
            showChannelContent();
        }
    }

    private void showSelfContent() {
        binding.addChannelButton.setVisibility(View.GONE);
        binding.sendMessageButton.setVisibility(View.GONE);
        showContent(selfInfo.getAvatarInfo().getHash(), selfInfo.getUsername(), selfInfo.getSex(), selfInfo.getIchatId(), selfInfo.getIchatIdUser(), selfInfo.getEmail(), selfInfo.buildRegionDesc());
    }

    private void showChannelContent() {
        binding.editMyInfoButton.setVisibility(View.GONE);
        if(channelInfo.isConnected()){
            binding.addChannelButton.setVisibility(View.GONE);
        }else {
            binding.sendMessageButton.setVisibility(View.GONE);
        }
        showContent(channelInfo.getAvatarInfo().getHash(), channelInfo.getUsername(), channelInfo.getSex(), channelInfo.getIchatId(), channelInfo.getIchatIdUser(), channelInfo.getEmail(), channelInfo.buildRegionDesc());
    }

    private void showContent(String avatarHash, String username, Integer sex, String ichatId, String ichatIdUser, String email, String regionDesc){
        if (avatarHash == null) {
            GlideBehaviours.loadToImageView(getApplicationContext(), R.drawable.default_avatar, binding.avatar);
        } else {
            GlideBehaviours.loadToImageView(getApplicationContext(), NetDataUrls.getAvatarUrl(this, avatarHash), binding.avatar);
        }
        binding.username.setText(username);
        if(sex == null || (sex != 0 && sex != 1)){
            binding.sexIcon.setVisibility(View.GONE);
        }else {
            binding.sexIcon.setVisibility(View.VISIBLE);
            if(sex == 0){
                binding.sexIcon.setImageResource(R.drawable.female_24px);
            }else {
                binding.sexIcon.setImageResource(R.drawable.male_24px);
            }
        }
        binding.ichatIdUser.setText(ichatIdUser);
        if(email == null){
            binding.layoutEmail.setVisibility(View.GONE);
        }else {
            binding.layoutEmail.setVisibility(View.VISIBLE);
            binding.email.setText(email);
        }
        if(regionDesc == null){
            binding.layoutRegion.setVisibility(View.GONE);
            binding.regionDivider.setVisibility(View.GONE);
        }else {
            binding.layoutRegion.setVisibility(View.VISIBLE);
            binding.regionDivider.setVisibility(View.VISIBLE);
            binding.region.setText(regionDesc);
        }
    }

    private void setupYiers() {
        setLongClickCopyYiers();
        binding.avatar.setOnClickListener(v -> {
            if((selfInfo != null && selfInfo.getAvatarInfo() != null && selfInfo.getAvatarInfo().getHash() != null)
                    || (channelInfo != null && channelInfo.getAvatarInfo() != null && channelInfo.getAvatarInfo().getHash() != null)) {
                    Intent intent = new Intent(this, AvatarActivity.class);
                    intent.putExtra(ExtraKeys.ICHAT_ID, isSelf ? selfInfo.getIchatId() : channelInfo.getIchatId());
                    intent.putExtra(ExtraKeys.AVATAR_HASH, isSelf ? selfInfo.getAvatarInfo().getHash() : channelInfo.getAvatarInfo().getHash());
                    intent.putExtra(ExtraKeys.AVATAR_EXTENSION, isSelf ? selfInfo.getAvatarInfo().getExtension() : channelInfo.getAvatarInfo().getExtension());
                    startActivity(intent);
            }
        });
        binding.editMyInfoButton.setOnClickListener(v -> {
            startActivity(new Intent(this, EditUserSettingsActivity.class));
        });
        binding.addChannelButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, RequestAddChannelActivity.class);
            intent.putExtra(ExtraKeys.CHANNEL_INFO, channelInfo);
            startActivity(intent);
        });
    }

    private void setLongClickCopyYiers() {
        binding.username.setOnLongClickListener(new CopyTextOnLongClickYier(this, binding.ichatIdUser.getText().toString()));
        binding.ichatIdUser.setOnLongClickListener(new CopyTextOnLongClickYier(this, binding.ichatIdUser.getText().toString()));
        binding.email.setOnLongClickListener(new CopyTextOnLongClickYier(this, binding.ichatIdUser.getText().toString()));
        binding.region.setOnLongClickListener(new CopyTextOnLongClickYier(this, binding.ichatIdUser.getText().toString()));
    }

    @Override
    public void onStartUpdate(String id) {

    }

    @Override
    public void onUpdateComplete(String id) {
        if(id.equals(ContentUpdater.OnServerContentUpdateYier.ID_CURRENT_USER_INFO)){
            bindValues();
            showContent();
        }
    }
}