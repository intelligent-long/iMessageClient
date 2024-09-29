package com.longx.intelligent.android.ichat2.activity;

import android.app.KeyguardManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatImageView;

import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.activity.helper.BaseActivity;
import com.longx.intelligent.android.ichat2.activity.settings.EditUserSettingsActivity;
import com.longx.intelligent.android.ichat2.behavior.ContentUpdater;
import com.longx.intelligent.android.ichat2.behavior.GlideBehaviours;
import com.longx.intelligent.android.ichat2.da.database.manager.ChannelDatabaseManager;
import com.longx.intelligent.android.ichat2.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.ichat2.data.BroadcastMedia;
import com.longx.intelligent.android.ichat2.data.Channel;
import com.longx.intelligent.android.ichat2.data.RecentBroadcastMedia;
import com.longx.intelligent.android.ichat2.data.Self;
import com.longx.intelligent.android.ichat2.data.response.OperationData;
import com.longx.intelligent.android.ichat2.databinding.ActivityChannelBinding;
import com.longx.intelligent.android.ichat2.net.dataurl.NetDataUrls;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.ChannelApiCaller;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.ichat2.ui.glide.GlideApp;
import com.longx.intelligent.android.ichat2.util.ResourceUtil;
import com.longx.intelligent.android.ichat2.util.UiUtil;
import com.longx.intelligent.android.ichat2.value.Constants;
import com.longx.intelligent.android.ichat2.yier.CopyTextOnLongClickYier;
import com.longx.intelligent.android.ichat2.yier.GlobalYiersHolder;
import com.longx.intelligent.android.ichat2.yier.RecentBroadcastMediasUpdateYier;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class ChannelActivity extends BaseActivity implements ContentUpdater.OnServerContentUpdateYier, RecentBroadcastMediasUpdateYier {
    private ActivityChannelBinding binding;
    private String ichatId;
    private Channel channel;
    private Self self;
    private boolean isSelf;
    private boolean networkFetch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChannelBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupDefaultBackNavigation(binding.toolbar);
        getUserInfoAndShow();
        setupUi();
        setupYiers();
        GlobalYiersHolder.holdYier(this, ContentUpdater.OnServerContentUpdateYier.class, this);
        GlobalYiersHolder.holdYier(this, RecentBroadcastMediasUpdateYier.class, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GlobalYiersHolder.removeYier(this, ContentUpdater.OnServerContentUpdateYier.class, this);
        GlobalYiersHolder.removeYier(this, RecentBroadcastMediasUpdateYier.class, this);
    }

    private void getUserInfoAndShow() {
        ichatId = getIntent().getStringExtra(ExtraKeys.ICHAT_ID);
        channel = getIntent().getParcelableExtra(ExtraKeys.CHANNEL);
        networkFetch = getIntent().getBooleanExtra(ExtraKeys.NETWORK_FETCH, false);
        self = SharedPreferencesAccessor.UserProfilePref.getCurrentUserProfile(this);
        isSelf = (ichatId == null && channel == null)
                || (ichatId != null && ichatId.equals(self.getIchatId())
                || (channel != null && channel.getIchatId().equals(self.getIchatId())));
        if(isSelf || channel != null){
            showContent();
        }else {
            showOrFetchAndShow(ichatId);
        }
    }

    private void setupUi() {
        if(isSelf || networkFetch) binding.toolbar.getMenu().findItem(R.id.more).setVisible(false);
    }

    private void showOrFetchAndShow(String ichatId) {
        channel = ChannelDatabaseManager.getInstance().findOneChannel(ichatId);
        if(channel != null){
            showContent();
        }else {
            networkFetch = true;
            ChannelApiCaller.findChannelByIchatId(this, ichatId, new RetrofitApiCaller.CommonYier<OperationData>(this, false, true){

                @Override
                public void start(Call<OperationData> call) {
                    super.start(call);
                    binding.contentView.setVisibility(View.GONE);
                    binding.loadingView.setVisibility(View.VISIBLE);
                }

                @Override
                public void ok(OperationData data, Response<OperationData> raw, Call<OperationData> call) {
                    super.ok(data, raw, call);
                    data.commonHandleResult(ChannelActivity.this, new int[]{-101}, () -> {
                        channel = data.getData(Channel.class);
                        if(channel != null){
                            showContent();
                        }
                    });
                }

                @Override
                public void complete(Call<OperationData> call) {
                    super.complete(call);
                    binding.contentView.setVisibility(View.VISIBLE);
                    binding.loadingView.setVisibility(View.GONE);
                }
            });
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
        showContent(self.getAvatar() == null ? null : self.getAvatar().getHash(), self.getUsername(), null, self.getSex(), self.getIchatId(), self.getIchatIdUser(), self.getEmail(), self.buildRegionDesc());
    }

    private void showChannelContent() {
        binding.editMyInfoButton.setVisibility(View.GONE);
        if(channel.isAssociated()){
            binding.addChannelButton.setVisibility(View.GONE);
        }else {
            binding.sendMessageButton.setVisibility(View.GONE);
        }
        showContent(channel.getAvatar() == null ? null : channel.getAvatar().getHash(), channel.getUsername(), channel.getNote(), channel.getSex(), channel.getIchatId(), channel.getIchatIdUser(), channel.getEmail(), channel.buildRegionDesc());
    }

    private void showContent(String avatarHash, String username, String note, Integer sex, String ichatId, String ichatIdUser, String email, String regionDesc){
        if (avatarHash == null) {
            GlideBehaviours.loadToImageView(getApplicationContext(), R.drawable.default_avatar, binding.avatar);
        } else {
            GlideBehaviours.loadToImageView(getApplicationContext(), NetDataUrls.getAvatarUrl(this, avatarHash), binding.avatar);
        }
        if(note != null){
            binding.name.setText(note);
            binding.username.setText(username);
            binding.layoutUsername.setVisibility(View.VISIBLE);
            binding.emailDivider.setVisibility(View.VISIBLE);
        }else {
            binding.name.setText(username);
            binding.layoutUsername.setVisibility(View.GONE);
            binding.emailDivider.setVisibility(View.GONE);
        }
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
            binding.emailDivider.setVisibility(View.GONE);
        }else {
            binding.layoutEmail.setVisibility(View.VISIBLE);
            binding.email.setText(email);
        }
        if(regionDesc == null){
            binding.layoutRegion.setVisibility(View.GONE);
            binding.regionDivider.setVisibility(View.GONE);
        }else {
            binding.layoutRegion.setVisibility(View.VISIBLE);
            if(note == null && email == null){
                binding.regionDivider.setVisibility(View.GONE);
            }else {
                binding.regionDivider.setVisibility(View.VISIBLE);
            }
            binding.region.setText(regionDesc);
        }
        if(binding.layoutEmail.getVisibility() == View.GONE && binding.layoutUsername.getVisibility() == View.GONE && binding.layoutRegion.getVisibility() == View.GONE){
            binding.infos.setVisibility(View.GONE);
        }
        showRecentBroadcastMedias();
    }

    private void showRecentBroadcastMedias() {
        List<RecentBroadcastMedia> recentBroadcastMedias = ChannelDatabaseManager.getInstance().findRecentBroadcastMedias(isSelf ? self.getIchatId() : getIchatId());
        if(recentBroadcastMedias.isEmpty()){
            binding.layoutBroadcastWithMedias.setVisibility(View.GONE);
            binding.layoutBroadcastNoMedias.setVisibility(View.VISIBLE);
        }else {
            binding.layoutBroadcastWithMedias.setVisibility(View.VISIBLE);
            binding.layoutBroadcastNoMedias.setVisibility(View.GONE);
            if (recentBroadcastMedias.size() > Constants.RECENT_BROADCAST_MEDIAS_SHOW_ITEM_SIZE) {
                recentBroadcastMedias = recentBroadcastMedias.subList(0, Constants.RECENT_BROADCAST_MEDIAS_SHOW_ITEM_SIZE);
            }
            for (int i = 0; i < Constants.RECENT_BROADCAST_MEDIAS_SHOW_ITEM_SIZE; i++) {
                int layoutResId = ResourceUtil.getResId("layout_recent_broadcast_media_" + (i + 1), R.id.class);
                FrameLayout layout = findViewById(layoutResId);
                if (i < recentBroadcastMedias.size()) {
                    RecentBroadcastMedia recentBroadcastMedia = recentBroadcastMedias.get(i);
                    layout.setVisibility(View.VISIBLE);
                    int imageResId = ResourceUtil.getResId("recent_broadcast_media_" + (i + 1), R.id.class);
                    AppCompatImageView imageView = findViewById(imageResId);
                    GlideApp.with(getApplicationContext())
                            .load(NetDataUrls.getBroadcastMediaDataUrl(this, recentBroadcastMedia.getMediaId()))
                            .placeholder(AppCompatResources.getDrawable(this, R.drawable.cached_24px))
                            .transform(new MultiTransformation<>(
                                    new CenterCrop(),
                                    new RoundedCorners(UiUtil.dpToPx(this, 10))
                            ))
                            .into(imageView);
                    int videoIconResId = ResourceUtil.getResId("recent_broadcast_video_icon_" + (i + 1), R.id.class);
                    if (recentBroadcastMedia.getType() == BroadcastMedia.TYPE_VIDEO) {
                        findViewById(videoIconResId).setVisibility(View.VISIBLE);
                    } else {
                        findViewById(videoIconResId).setVisibility(View.GONE);
                    }
                } else {
                    layout.setVisibility(View.GONE);
                }
            }
        }
    }

    private String getIchatId() {
        return ichatId == null ? channel.getIchatId() : ichatId;
    }

    private void setupYiers() {
        setLongClickCopyYiers();
        binding.avatar.setOnClickListener(v -> {
            if(isSelf ? (self != null && self.getAvatar() != null && self.getAvatar().getHash() != null)
                    : (channel != null && channel.getAvatar() != null && channel.getAvatar().getHash() != null)) {
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
            intent.putExtra(ExtraKeys.CHANNEL, channel);
            startActivity(intent);
        });
        binding.sendMessageButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, ChatActivity.class);
            intent.putExtra(ExtraKeys.CHANNEL, channel);
            startActivity(intent);
        });
        binding.toolbar.setOnMenuItemClickListener(item -> {
            if(item.getItemId() == R.id.more){
                Intent intent = new Intent(this, ChannelSettingActivity.class);
                intent.putExtra(ExtraKeys.CHANNEL, channel);
                startActivity(intent);
            }
            return true;
        });
        View.OnClickListener yier = v -> {
            Intent intent = new Intent(this, BroadcastChannelActivity.class);
            intent.putExtra(ExtraKeys.CHANNEL, isSelf ? self.toChannel() : channel);
            startActivity(intent);
        };
        binding.clickLayoutBroadcastWithMedias.setOnClickListener(yier);
        binding.clickLayoutBroadcastNoMedias.setOnClickListener(yier);
    }

    private void setLongClickCopyYiers() {
        binding.name.setOnLongClickListener(new CopyTextOnLongClickYier(this, binding.name.getText().toString()));
        binding.username.setOnLongClickListener(new CopyTextOnLongClickYier(this, binding.username.getText().toString()));
        binding.ichatIdUser.setOnLongClickListener(new CopyTextOnLongClickYier(this, binding.ichatIdUser.getText().toString()));
        binding.email.setOnLongClickListener(new CopyTextOnLongClickYier(this, binding.email.getText().toString()));
        binding.region.setOnLongClickListener(new CopyTextOnLongClickYier(this, binding.region.getText().toString()));
    }

    @Override
    public void onStartUpdate(String id, List<String> updatingIds) {

    }

    @Override
    public void onUpdateComplete(String id, List<String> updatingIds) {
        if(isSelf) {
            if (id.equals(ContentUpdater.OnServerContentUpdateYier.ID_CURRENT_USER_INFO)) {
                getUserInfoAndShow();
            }
        }
        if(id.equals(ContentUpdater.OnServerContentUpdateYier.ID_CHANNELS)){
            showOrFetchAndShow(getIchatId());
        }
    }

    public Channel getChannel() {
        return channel;
    }

    @Override
    public void onRecentBroadcastMediasUpdate(String ichatId) {
        if(ichatId.equals(isSelf ? self.getIchatId() : this.ichatId)) {
            showRecentBroadcastMedias();
        }
    }
}