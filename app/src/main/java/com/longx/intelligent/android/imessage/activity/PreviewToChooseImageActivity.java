package com.longx.intelligent.android.imessage.activity;

import android.annotation.SuppressLint;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.imessage.activity.helper.BaseActivity;
import com.longx.intelligent.android.imessage.databinding.ActivityPreviewToChooseImageBinding;
import com.longx.intelligent.android.imessage.ui.SwipeDownGestureYier;
import com.longx.intelligent.android.imessage.util.ColorUtil;
import com.longx.intelligent.android.imessage.util.Utils;
import com.longx.intelligent.android.imessage.util.WindowAndSystemUiUtil;

import java.io.File;

public class PreviewToChooseImageActivity extends BaseActivity {
    private ActivityPreviewToChooseImageBinding binding;
    private String filePath;
    private boolean purePhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setFontThemes(R.style.DarkStatusBarActivity_Font1, R.style.DarkStatusBarActivity_Font2);
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
            WindowAndSystemUiUtil.setSystemUiShown(PreviewToChooseImageActivity.this, false);
            this.purePhoto = true;
        }else {
            binding.appBarLayout.setVisibility(View.VISIBLE);
            WindowAndSystemUiUtil.setSystemUiShown(PreviewToChooseImageActivity.this, true);
            this.purePhoto = false;
        }
    }

}