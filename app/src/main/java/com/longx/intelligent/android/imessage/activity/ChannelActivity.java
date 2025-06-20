package com.longx.intelligent.android.imessage.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.FrameLayout;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatImageView;

import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.imessage.activity.helper.BaseActivity;
import com.longx.intelligent.android.imessage.activity.settings.EditUserSettingsActivity;
import com.longx.intelligent.android.imessage.behaviorcomponents.ContentUpdater;
import com.longx.intelligent.android.imessage.da.database.manager.ChannelDatabaseManager;
import com.longx.intelligent.android.imessage.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.imessage.data.BroadcastMedia;
import com.longx.intelligent.android.imessage.data.Channel;
import com.longx.intelligent.android.imessage.data.RecentBroadcastMedia;
import com.longx.intelligent.android.imessage.data.Self;
import com.longx.intelligent.android.imessage.data.response.OperationData;
import com.longx.intelligent.android.imessage.data.response.OperationStatus;
import com.longx.intelligent.android.imessage.databinding.ActivityChannelBinding;
import com.longx.intelligent.android.imessage.data.AvatarType;
import com.longx.intelligent.android.imessage.net.dataurl.NetDataUrls;
import com.longx.intelligent.android.imessage.net.retrofit.caller.ChannelApiCaller;
import com.longx.intelligent.android.imessage.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.imessage.ui.glide.GlideApp;
import com.longx.intelligent.android.imessage.util.ResourceUtil;
import com.longx.intelligent.android.imessage.util.UiUtil;
import com.longx.intelligent.android.imessage.value.Constants;
import com.longx.intelligent.android.imessage.yier.CopyTextOnLongClickYier;
import com.longx.intelligent.android.imessage.yier.GlobalYiersHolder;
import com.longx.intelligent.android.imessage.yier.RecentBroadcastMediasUpdateYier;

import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class ChannelActivity extends BaseActivity implements ContentUpdater.OnServerContentUpdateYier, RecentBroadcastMediasUpdateYier {
    private ActivityChannelBinding binding;
    private String imessageId;
    private Channel channel;
    private Self self;
    private boolean isSelf;
    private boolean mayNotAssociated;
    private boolean needCentralServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChannelBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupDefaultBackNavigation(binding.toolbar);
        getUserInfoAndShow();
        if(!needCentralServer) {
            setupUi();
            setupYiers();
        }else {
            binding.contentView.setVisibility(View.GONE);
            binding.loadingView.setVisibility(View.GONE);
            binding.errorInfo.setText(getString(R.string.need_central_server));
            binding.errorView.setVisibility(View.VISIBLE);
            binding.toolbar.getMenu().findItem(R.id.qr_code).setVisible(false);
            binding.toolbar.getMenu().findItem(R.id.more).setVisible(false);
        }
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
        Uri uri = getIntent().getData();
        if(uri != null){
            String path = uri.getPath();
            if (path != null) {
                imessageId = path.substring(1);
                if(!SharedPreferencesAccessor.ServerPref.isUseCentral(this)) {
                    if (imessageId != null) {
                        needCentralServer = Arrays.asList(Constants.CENTRAL_SERVER_IMESSAGE_IDS).contains(imessageId);
                    }
                    if (needCentralServer) return;
                }
            }
        }
        if(imessageId == null) {
            imessageId = getIntent().getStringExtra(ExtraKeys.IMESSAGE_ID);
            channel = getIntent().getParcelableExtra(ExtraKeys.CHANNEL);
            if(imessageId == null && channel != null) imessageId = channel.getImessageId();
            if(channel == null && imessageId != null) channel = ChannelDatabaseManager.getInstance().findOneChannel(imessageId);
            mayNotAssociated = getIntent().getBooleanExtra(ExtraKeys.MAY_NOT_ASSOCIATED, false);
        }
        self = SharedPreferencesAccessor.UserProfilePref.getCurrentUserProfile(this);
        isSelf = (imessageId == null && channel == null)
                || (imessageId != null && imessageId.equals(self.getImessageId())
                || (channel != null && channel.getImessageId().equals(self.getImessageId())));
        if(isSelf || channel != null){
            showContent();
        }else {
            showOrFetchAndShow(imessageId);
        }
    }

    private void setupUi() {
        if(isSelf || (mayNotAssociated && channel != null && !channel.isAssociated())) binding.toolbar.getMenu().findItem(R.id.more).setVisible(false);
    }

    private void showOrFetchAndShow(String imessageId) {
        channel = ChannelDatabaseManager.getInstance().findOneChannel(imessageId);
        if(channel != null){
            showContent();
        }else {
            mayNotAssociated = true;
            ChannelApiCaller.findChannelByImessageId(this, imessageId, new RetrofitApiCaller.CommonYier<OperationData>(this, false, true){

                @Override
                public void start(Call<OperationData> call) {
                    super.start(call);
                    binding.contentView.setVisibility(View.GONE);
                    binding.loadingView.setVisibility(View.VISIBLE);
                    binding.errorView.setVisibility(View.GONE);
                }

                @Override
                public void ok(OperationData data, Response<OperationData> raw, Call<OperationData> call) {
                    super.ok(data, raw, call);
                    data.commonHandleResult(ChannelActivity.this, new int[]{}, () -> {
                        channel = data.getData(Channel.class);
                        if(channel != null){
                            showContent();
                            binding.contentView.setVisibility(View.VISIBLE);
                            binding.loadingView.setVisibility(View.GONE);
                            binding.errorView.setVisibility(View.GONE);
                            setupUi();
                        }
                    }, new OperationStatus.HandleResult(-101, () -> {
                        binding.contentView.setVisibility(View.GONE);
                        binding.loadingView.setVisibility(View.GONE);
                        binding.errorInfo.setText(getString(R.string.do_not_find_channel));
                        binding.errorView.setVisibility(View.VISIBLE);
                        binding.toolbar.getMenu().findItem(R.id.qr_code).setVisible(false);
                        binding.toolbar.getMenu().findItem(R.id.more).setVisible(false);
                    }));
                }

                @Override
                public void complete(Call<OperationData> call) {
                    super.complete(call);
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
        showContent(self.getAvatar() == null ? null : self.getAvatar().getHash(), self.getUsername(), null, self.getSex(), self.getImessageId(), self.getImessageIdUser(), self.getEmail(), self.buildRegionDesc());
    }

    private void showChannelContent() {
        binding.editMyInfoButton.setVisibility(View.GONE);
        if(channel.isAssociated()){
            binding.addChannelButton.setVisibility(View.GONE);
        }else {
            binding.sendMessageButton.setVisibility(View.GONE);
        }
        showContent(channel.getAvatar() == null ? null : channel.getAvatar().getHash(), channel.getUsername(), channel.getNote(), channel.getSex(), channel.getImessageId(), channel.getImessageIdUser(), channel.getEmail(), channel.buildRegionDesc());
    }

    private void showContent(String avatarHash, String username, String note, Integer sex, String imessageId, String imessageIdUser, String email, String regionDesc){
        if (avatarHash == null) {
            GlideApp
                    .with(this)
                    .load(R.drawable.default_avatar)
                    .into(binding.avatar);
        } else {
            GlideApp
                    .with(this)
                    .load(NetDataUrls.getAvatarUrl(this, avatarHash))
                    .into(binding.avatar);
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
        binding.imessageIdUser.setText(imessageIdUser);
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
        List<RecentBroadcastMedia> recentBroadcastMedias = ChannelDatabaseManager.getInstance().findRecentBroadcastMedias(isSelf ? self.getImessageId() : getImessageId());
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

    private String getImessageId() {
        return imessageId == null ? channel.getImessageId() : imessageId;
    }

    private void setupYiers() {
        setLongClickCopyYiers();
        binding.avatar.setOnClickListener(v -> {
            if(isSelf ? (self != null && self.getAvatar() != null && self.getAvatar().getHash() != null)
                    : (channel != null && channel.getAvatar() != null && channel.getAvatar().getHash() != null)) {
                    Intent intent = new Intent(this, AvatarActivity.class);
                    intent.putExtra(ExtraKeys.IMESSAGE_ID, isSelf ? self.getImessageId() : channel.getImessageId());
                    intent.putExtra(ExtraKeys.AVATAR_HASH, isSelf ? self.getAvatar().getHash() : channel.getAvatar().getHash());
                    intent.putExtra(ExtraKeys.AVATAR_EXTENSION, isSelf ? self.getAvatar().getExtension() : channel.getAvatar().getExtension());
                    intent.putExtra(ExtraKeys.AVATAR_TYPE, (Parcelable) AvatarType.CHANNEL);
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
            if(item.getItemId() == R.id.qr_code){
                Intent intent = new Intent(ChannelActivity.this, QrCodeActivity.class);
                intent.putExtra(ExtraKeys.TYPE_OBJECT, channel);
                intent.putExtra(ExtraKeys.DESCRIPTION, "扫描二维码打开频道");
                startActivity(intent);
            }else if(item.getItemId() == R.id.more){
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
        binding.imessageIdUser.setOnLongClickListener(new CopyTextOnLongClickYier(this, binding.imessageIdUser.getText().toString()));
        binding.email.setOnLongClickListener(new CopyTextOnLongClickYier(this, binding.email.getText().toString()));
        binding.region.setOnLongClickListener(new CopyTextOnLongClickYier(this, binding.region.getText().toString()));
    }

    @Override
    public void onStartUpdate(String id, List<String> updatingIds, Object... objects) {

    }

    @Override
    public void onUpdateComplete(String id, List<String> updatingIds, Object... objects) {
        if(isSelf) {
            if (id.equals(ContentUpdater.OnServerContentUpdateYier.ID_CURRENT_USER_INFO)) {
                getUserInfoAndShow();
            }
        }
        if(id.equals(ContentUpdater.OnServerContentUpdateYier.ID_CHANNELS)){
            showOrFetchAndShow(getImessageId());
            setLongClickCopyYiers();
        }
    }

    public Channel getChannel() {
        return channel;
    }

    @Override
    public void onRecentBroadcastMediasUpdate(String imessageId) {
        if(imessageId.equals(isSelf ? self.getImessageId() : this.imessageId)) {
            showRecentBroadcastMedias();
        }
    }
}