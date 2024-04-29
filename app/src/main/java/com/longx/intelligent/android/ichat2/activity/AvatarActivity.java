package com.longx.intelligent.android.ichat2.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.activity.helper.BaseActivity;
import com.longx.intelligent.android.ichat2.behavior.MessageDisplayer;
import com.longx.intelligent.android.ichat2.da.privatefile.PrivateFilesAccessor;
import com.longx.intelligent.android.ichat2.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.ichat2.data.ChannelInfo;
import com.longx.intelligent.android.ichat2.data.SelfInfo;
import com.longx.intelligent.android.ichat2.databinding.ActivityAvatarBinding;
import com.longx.intelligent.android.ichat2.ui.glide.GlideApp;
import com.longx.intelligent.android.ichat2.behavior.ImageSaver;
import com.longx.intelligent.android.ichat2.util.FileUtil;

import java.io.File;
import java.util.Objects;

public class AvatarActivity extends BaseActivity {
    private ActivityAvatarBinding binding;
    private String ichatId;
    private File avatarFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAvatarBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupDefaultBackNavigation(binding.toolbar);
        ichatId = Objects.requireNonNull(getIntent().getStringExtra(ExtraKeys.ICHAT_ID));
        avatarFile = new File(Objects.requireNonNull(getIntent().getStringExtra(ExtraKeys.AVATAR_FILE_PATH)));
        setupToolbar();
        showAvatar();
    }

    private void setupToolbar() {
        binding.toolbar.setOnMenuItemClickListener(item -> {
            if(item.getItemId() == R.id.save_avatar){
                saveAvatarToDcim(avatarFile, ichatId);
            }
            return true;
        });
    }

    private void saveAvatarToDcim(File avatar, String ichatId) {
        ImageSaver.saveImageToDcim(this, avatar, ichatId + "_" + System.currentTimeMillis() + FileUtil.getFileExtension(avatar), "iChat" + File.separator + "Avatar",
                results -> {
                    Uri uri = (Uri) results[0];
                    String displayMessage;
                    if(uri != null){
                        displayMessage = "保存成功";
                    }else {
                        displayMessage = "保存失败";
                    }
                    MessageDisplayer.autoShow(AvatarActivity.this, displayMessage, MessageDisplayer.Duration.LONG);
                });
    }

    private void showAvatar() {
        binding.loadingIndicator.hide();
        binding.loadingIndicator.show();

        GlideApp
                .with(getApplicationContext())
                .asBitmap()
                .override(Target.SIZE_ORIGINAL)
                .load(avatarFile)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(new CustomTarget<Bitmap>() {
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
                });
    }
}