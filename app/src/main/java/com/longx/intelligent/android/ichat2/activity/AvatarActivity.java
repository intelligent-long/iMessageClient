package com.longx.intelligent.android.ichat2.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.activity.helper.BaseActivity;
import com.longx.intelligent.android.ichat2.behaviorcomponents.GlideBehaviours;
import com.longx.intelligent.android.ichat2.behaviorcomponents.MessageDisplayer;
import com.longx.intelligent.android.ichat2.databinding.ActivityAvatarBinding;
import com.longx.intelligent.android.ichat2.net.dataurl.NetDataUrls;
import com.longx.intelligent.android.ichat2.behaviorcomponents.ImageSaver;

import java.io.File;
import java.util.Objects;

public class AvatarActivity extends BaseActivity {
    private ActivityAvatarBinding binding;
    private String ichatId;
    private String avatarExtension;
    private String avatarHash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAvatarBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupDefaultBackNavigation(binding.toolbar);
        ichatId = Objects.requireNonNull(getIntent().getStringExtra(ExtraKeys.ICHAT_ID));
        avatarHash = Objects.requireNonNull(getIntent().getStringExtra(ExtraKeys.AVATAR_HASH));
        avatarExtension = Objects.requireNonNull(getIntent().getStringExtra(ExtraKeys.AVATAR_EXTENSION));
        setupToolbar();
        showAvatar();
    }

    private void setupToolbar() {
        binding.toolbar.setOnMenuItemClickListener(item -> {
            if(item.getItemId() == R.id.save_avatar){
                saveAvatarToDcim();
            }
            return true;
        });
    }

    private void saveAvatarToDcim() {
        GlideBehaviours.loadToFile(getApplicationContext(), NetDataUrls.getAvatarUrl(this, avatarHash),
                new CustomTarget<File>() {
                    @Override
                    public void onResourceReady(@NonNull File resource, @Nullable Transition<? super File> transition) {
                        ImageSaver.saveImageToDcim(AvatarActivity.this, resource,
                                ichatId + "_" + avatarHash + "_" + System.currentTimeMillis() + avatarExtension,
                                "iChat" + File.separator + "Avatar",
                                results -> {
                                    Uri uri = (Uri) results[0];
                                    MessageDisplayer.autoShow(AvatarActivity.this, uri == null ? "保存失败" : "保存成功", MessageDisplayer.Duration.LONG);
                                });
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                }, true);
    }

    private void showAvatar() {
        binding.loadingIndicator.hide();
        binding.loadingIndicator.show();

        GlideBehaviours.loadToBitmap(getApplicationContext(), NetDataUrls.getAvatarUrl(this, avatarHash),
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