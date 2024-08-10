package com.longx.intelligent.android.ichat2.activity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.activity.helper.BaseActivity;
import com.longx.intelligent.android.ichat2.databinding.ActivityPreviewToChooseImageBinding;
import com.longx.intelligent.android.ichat2.media.helper.MediaHelper;
import com.longx.intelligent.android.ichat2.ui.SwipeDownGestureYier;
import com.longx.intelligent.android.ichat2.util.ColorUtil;
import com.longx.intelligent.android.ichat2.util.Utils;
import com.longx.intelligent.android.ichat2.util.WindowAndSystemUiUtil;

import java.io.File;

public class PreviewToChooseImageActivity extends BaseActivity {
    private ActivityPreviewToChooseImageBinding binding;
    private String filePath;
    private boolean purePhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPreviewToChooseImageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        WindowAndSystemUiUtil.extendContentUnderSystemBars(this, null, null,
                ColorUtil.getAttrColor(this, com.google.android.material.R.attr.colorSurfaceContainer));
        setupBackNavigation(binding.toolbar, getColor(R.color.white));
        filePath = getIntent().getStringExtra(ExtraKeys.FILE_PATH);
        binding.appBarLayout.bringToFront();
        showContent();
    }

    private void showContent() {
        setupPhotoView();
        showPhoto();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupPhotoView() {
        binding.photo.setOnClickListener(v -> {
            setPurePhoto(!purePhoto);
        });
        SwipeDownGestureYier swipeDownGestureYier = new SwipeDownGestureYier(this) {
            @Override
            public void onSwipeDown() {
                finish();
            }
        };
        GestureDetector gestureDetector = new GestureDetector(this, swipeDownGestureYier);
        binding.photo.setOnTouchListener((View v, MotionEvent event) -> {
            gestureDetector.onTouchEvent(event);
            return false;
        });
        binding.photo.setOnStateChangedListener(new SubsamplingScaleImageView.OnStateChangedListener() {
            @Override
            public void onScaleChanged(float newScale, int origin) {
                setPurePhoto(true);
                swipeDownGestureYier.setEnabled(Utils.approximatelyEqual(newScale, binding.photo.getMinScale(), 0.001F));
            }

            @Override
            public void onCenterChanged(PointF newCenter, int origin) {

            }
        });
    }

    private void showPhoto() {
        binding.photo.setImage(ImageSource.uri(Uri.fromFile(new File(filePath))));
    }

    private void setPurePhoto(boolean purePhoto) {
        if(purePhoto){
            binding.appBarLayout.setVisibility(View.GONE);
            WindowAndSystemUiUtil.setSystemUIShown(PreviewToChooseImageActivity.this, false);
            this.purePhoto = true;
        }else {
            binding.appBarLayout.setVisibility(View.VISIBLE);
            WindowAndSystemUiUtil.setSystemUIShown(PreviewToChooseImageActivity.this, true);
            this.purePhoto = false;
        }
    }

}