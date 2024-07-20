package com.longx.intelligent.android.ichat2.activity;

import android.annotation.SuppressLint;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.activity.helper.BaseActivity;
import com.longx.intelligent.android.ichat2.databinding.ActivityPreviewToSendImageBinding;
import com.longx.intelligent.android.ichat2.ui.SwipeDownGestureYier;
import com.longx.intelligent.android.ichat2.util.ColorUtil;
import com.longx.intelligent.android.ichat2.util.WindowAndSystemUiUtil;

public class PreviewToSendImageActivity extends BaseActivity {
    private ActivityPreviewToSendImageBinding binding;
    private Uri uri;
    private boolean purePhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPreviewToSendImageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        WindowAndSystemUiUtil.extendContentUnderSystemBars(this, null, null,
                ColorUtil.getAttrColor(this, com.google.android.material.R.attr.colorSurfaceContainer));
        setupDefaultBackNavigation(binding.toolbar, getColor(R.color.white));
        uri = getIntent().getParcelableExtra(ExtraKeys.URI);
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
                swipeDownGestureYier.setEnabled(newScale == binding.photo.getMinScale());
            }

            @Override
            public void onCenterChanged(PointF newCenter, int origin) {

            }
        });
    }

    private void showPhoto() {
        binding.photo.setImage(ImageSource.uri(uri));
    }

    private void setPurePhoto(boolean purePhoto) {
        if(purePhoto){
            binding.appBarLayout.setVisibility(View.GONE);
            WindowAndSystemUiUtil.setSystemUIShown(PreviewToSendImageActivity.this, false);
            this.purePhoto = true;
        }else {
            binding.appBarLayout.setVisibility(View.VISIBLE);
            WindowAndSystemUiUtil.setSystemUIShown(PreviewToSendImageActivity.this, true);
            this.purePhoto = false;
        }
    }

}