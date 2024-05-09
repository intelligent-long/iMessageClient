package com.longx.intelligent.android.ichat2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.activity.helper.BaseActivity;
import com.longx.intelligent.android.ichat2.activity.settings.EditUserSettingsActivity;
import com.longx.intelligent.android.ichat2.behavior.ContentUpdater;
import com.longx.intelligent.android.ichat2.behavior.GlideBehaviours;
import com.longx.intelligent.android.ichat2.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.ichat2.data.Channel;
import com.longx.intelligent.android.ichat2.data.Self;
import com.longx.intelligent.android.ichat2.databinding.ActivityChannelBinding;
import com.longx.intelligent.android.ichat2.net.dataurl.NetDataUrls;
import com.longx.intelligent.android.ichat2.yier.CopyTextOnLongClickYier;
import com.longx.intelligent.android.ichat2.yier.GlobalYiersHolder;

public class ChannelActivity extends BaseActivity implements ContentUpdater.OnServerContentUpdateYier{
    private ActivityChannelBinding binding;
    private Channel channel;
    private Self self;
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
        channel = getIntent().getParcelableExtra(ExtraKeys.CHANNEL_INFO);
        self = SharedPreferencesAccessor.UserInfoPref.getCurrentUserInfo(this);
        isSelf = (ichatId == null && channel == null)
                || (ichatId != null && ichatId.equals(self.getIchatId())
                || (channel != null && channel.getIchatId().equals(self.getIchatId())));
        if(!isSelf && channel == null){
            channel = channel; //TODO: get channelInfo by ichatId
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
        showContent(self.getAvatar().getHash(), self.getUsername(), self.getSex(), self.getIchatId(), self.getIchatIdUser(), self.getEmail(), self.buildRegionDesc());
    }

    private void showChannelContent() {
        binding.editMyInfoButton.setVisibility(View.GONE);
        if(channel.isAssociated()){
            binding.addChannelButton.setVisibility(View.GONE);
        }else {
            binding.sendMessageButton.setVisibility(View.GONE);
        }
        showContent(channel.getAvatar().getHash(), channel.getUsername(), channel.getSex(), channel.getIchatId(), channel.getIchatIdUser(), channel.getEmail(), channel.buildRegionDesc());
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
            if((self != null && self.getAvatar() != null && self.getAvatar().getHash() != null)
                    || (channel != null && channel.getAvatar() != null && channel.getAvatar().getHash() != null)) {
                    Intent intent = new Intent(this, AvatarActivity.class);
                    intent.putExtra(ExtraKeys.ICHAT_ID, isSelf ? self.getIchatId() : channel.getIchatId());
                    intent.putExtra(ExtraKeys.AVATAR_HASH, isSelf ? self.getAvatar().getHash() : channel.getAvatar().getHash());
                    intent.putExtra(ExtraKeys.AVATAR_EXTENSION, isSelf ? self.getAvatar().getExtension() : channel.getAvatar().getExtension());
                    startActivity(intent);
            }
        });
        binding.editMyInfoButton.setOnClickListener(v -> {
            startActivity(new Intent(this, EditUserSettingsActivity.class));
        });
        binding.addChannelButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, RequestAddChannelActivity.class);
            intent.putExtra(ExtraKeys.CHANNEL_INFO, channel);
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
        if(isSelf) {
            if (id.equals(ContentUpdater.OnServerContentUpdateYier.ID_CURRENT_USER_INFO)) {
                bindValues();
                showContent();
            }
        }
    }
}