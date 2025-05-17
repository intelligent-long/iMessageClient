package com.longx.intelligent.android.imessage.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.imessage.activity.helper.BaseActivity;
import com.longx.intelligent.android.imessage.behaviorcomponents.GlideBehaviours;
import com.longx.intelligent.android.imessage.behaviorcomponents.MessageDisplayer;
import com.longx.intelligent.android.imessage.da.publicfile.PublicFileAccessor;
import com.longx.intelligent.android.imessage.databinding.ActivityAvatarBinding;
import com.longx.intelligent.android.imessage.dialog.OperatingDialog;
import com.longx.intelligent.android.imessage.data.AvatarType;
import com.longx.intelligent.android.imessage.net.dataurl.NetDataUrls;
import com.longx.intelligent.android.imessage.util.ErrorLogger;

import java.io.IOException;
import java.util.Objects;

public class AvatarActivity extends BaseActivity {
    private ActivityAvatarBinding binding;
    private String imessageId;
    private String avatarExtension;
    private String avatarHash;
    private AvatarType avatarType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAvatarBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupDefaultBackNavigation(binding.toolbar);
        imessageId = Objects.requireNonNull(getIntent().getStringExtra(ExtraKeys.IMESSAGE_ID));
        avatarHash = Objects.requireNonNull(getIntent().getStringExtra(ExtraKeys.AVATAR_HASH));
        avatarExtension = Objects.requireNonNull(getIntent().getStringExtra(ExtraKeys.AVATAR_EXTENSION));
        avatarType = Objects.requireNonNull(getIntent().getParcelableExtra(ExtraKeys.AVATAR_TYPE));
        setupToolbar();
        showAvatar();
    }

    private void setupToolbar() {
        binding.toolbar.setOnMenuItemClickListener(item -> {
            if(item.getItemId() == R.id.save_avatar){
                saveAvatar();
            }
            return true;
        });
    }

    private void saveAvatar() {
        new Thread(() -> {
            OperatingDialog operatingDialog = new OperatingDialog(this);
            operatingDialog.create().show();
            try {
                if(avatarType == AvatarType.CHANNEL) {
                    PublicFileAccessor.User.saveAvatar(this, imessageId, avatarHash, avatarExtension);
                }else if(avatarType == AvatarType.GROUP_CHANNEL){
                    PublicFileAccessor.GroupChannel.saveAvatar(this, imessageId, avatarHash, avatarExtension);
                }
                operatingDialog.dismiss();
                MessageDisplayer.autoShow(this, "已保存", MessageDisplayer.Duration.SHORT);
            } catch (IOException | InterruptedException e) {
                ErrorLogger.log(e);
                MessageDisplayer.autoShow(this, "保存失败", MessageDisplayer.Duration.SHORT);
            }
        }).start();
    }

    private void showAvatar() {
        binding.loadingIndicator.hide();
        binding.loadingIndicator.show();

        String avatarUrl = null;
        if(avatarType == AvatarType.CHANNEL) {
            avatarUrl = NetDataUrls.getAvatarUrl(this, avatarHash);
        }else if(avatarType == AvatarType.GROUP_CHANNEL){
            avatarUrl = NetDataUrls.getGroupAvatarUrl(this, avatarHash);
        }

        GlideBehaviours.loadToBitmap(getApplicationContext(), avatarUrl,
                new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        binding.loadingIndicator.hide();
                        binding.loadingIndicator.setVisibility(View.GONE);
                        binding.avatarView.setVisibility(View.VISIBLE);
                        binding.avatarView.setImage(ImageSource.bitmap(resource));
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                }, true);
    }
}