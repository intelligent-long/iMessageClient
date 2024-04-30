package com.longx.intelligent.android.ichat2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.ObjectKey;
import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.activity.helper.BaseActivity;
import com.longx.intelligent.android.ichat2.activity.settings.EditUserSettingsActivity;
import com.longx.intelligent.android.ichat2.behavior.ContentUpdater;
import com.longx.intelligent.android.ichat2.da.cachefile.CacheFilesAccessor;
import com.longx.intelligent.android.ichat2.da.privatefile.PrivateFilesAccessor;
import com.longx.intelligent.android.ichat2.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.ichat2.data.ChannelInfo;
import com.longx.intelligent.android.ichat2.data.SelfInfo;
import com.longx.intelligent.android.ichat2.databinding.ActivityChannelBinding;
import com.longx.intelligent.android.ichat2.net.dataurl.NetDataUrls;
import com.longx.intelligent.android.ichat2.ui.glide.GlideApp;
import com.longx.intelligent.android.ichat2.yier.GlobalYiersHolder;
import com.longx.intelligent.android.ichat2.yier.ResultsYier;

import java.io.File;
import java.util.Objects;

public class ChannelActivity extends BaseActivity implements ContentUpdater.OnServerContentUpdateYier{
    private ActivityChannelBinding binding;
    private ChannelInfo channelInfo;
    private SelfInfo selfInfo;
    private boolean isSelf;
    private File avatarFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChannelBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupDefaultBackNavigation(binding.toolbar);
        bindValues();
        showContent();
        setupYiers();
        GlobalYiersHolder.holdYier(ContentUpdater.OnServerContentUpdateYier.class, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GlobalYiersHolder.removeYier(ContentUpdater.OnServerContentUpdateYier.class, this);
    }

    /**
     * to use self or channel
     */
    private void bindValues() {
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
        avatarFile = selfInfo.getAvatarFile(this);
        showContent(selfInfo.getAvatarHash(), selfInfo.getUsername(), selfInfo.getSex(), selfInfo.getIchatIdUser(), selfInfo.getEmail(), selfInfo.buildRegionDesc());
    }

    private void showChannelContent() {
        binding.editMyInfoButton.setVisibility(View.GONE);
        if(channelInfo.isConnected()){
            binding.addChannelButton.setVisibility(View.GONE);
        }else {
            binding.sendMessageButton.setVisibility(View.GONE);
        }
        avatarFile = channelInfo.getAvatarFile(this);
        showContent(channelInfo.getAvatarHash(), channelInfo.getUsername(), channelInfo.getSex(), channelInfo.getIchatIdUser(), channelInfo.getEmail(), channelInfo.buildRegionDesc());
    }

    private void showContent(String avatarHash, String username, Integer sex, String ichatIdUser, String email, String regionDesc){
        if (avatarHash == null) {
            GlideApp.with(getApplicationContext())
                    .load(R.drawable.default_avatar)
                    .into(binding.avatar);
        } else {
            if(avatarFile != null) {
                GlideApp.with(getApplicationContext())
                        .load(avatarFile)
                        .signature(new ObjectKey(avatarHash))
                        .into(binding.avatar);
            }else {
                CacheFilesAccessor.cacheAvatarTempFromServer(getApplicationContext(), avatarHash, results -> {
                    avatarFile = (File) results[0];
                    GlideApp.with(getApplicationContext())
                            .load(avatarFile)
                            .signature(new ObjectKey(avatarHash))
                            .into(binding.avatar);
                });
            }
        }
        binding.username.setText(username);
        if(sex == null || (sex != 0 && sex != 1)){
            binding.sexIcon.setVisibility(View.GONE);
        }else {
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
            binding.email.setText(email);
        }
        if(regionDesc == null){
            binding.layoutRegion.setVisibility(View.GONE);
            binding.regionDivider.setVisibility(View.GONE);
        }else {
            binding.region.setText(regionDesc);
        }
    }

    private void setupYiers() {
        binding.avatar.setOnClickListener(v -> {
            if((selfInfo != null && selfInfo.getAvatarHash() != null) || (channelInfo != null && channelInfo.getAvatarHash() != null)) {
                if (avatarFile != null) {
                    Intent intent = new Intent(this, AvatarActivity.class);
                    intent.putExtra(ExtraKeys.ICHAT_ID, selfInfo.getIchatId());
                    intent.putExtra(ExtraKeys.AVATAR_FILE_PATH, avatarFile.getAbsolutePath());
                    startActivity(intent);
                }
            }
        });
        binding.editMyInfoButton.setOnClickListener(v -> {
            startActivity(new Intent(this, EditUserSettingsActivity.class));
        });
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