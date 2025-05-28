package com.longx.intelligent.android.imessage.activity;

import android.os.Bundle;
import android.view.View;

import androidx.viewpager.widget.ViewPager;

import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.imessage.activity.helper.BaseActivity;
import com.longx.intelligent.android.imessage.adapter.MediaPagerAdapter2;
import com.longx.intelligent.android.imessage.databinding.ActivityMedia2Binding;
import com.longx.intelligent.android.imessage.databinding.PagerItemMediaBinding;
import com.longx.intelligent.android.imessage.media.MediaType;
import com.longx.intelligent.android.imessage.media.data.Media;
import com.longx.intelligent.android.imessage.util.ColorUtil;
import com.longx.intelligent.android.imessage.util.UiUtil;
import com.longx.intelligent.android.imessage.util.Utils;
import com.longx.intelligent.android.imessage.util.WindowAndSystemUiUtil;

import java.util.ArrayList;

public class MediaActivity2 extends BaseActivity {
    private static View.OnClickListener actionButtonYier;
    private static MediaActivity2 instance;
    private ActivityMedia2Binding binding;
    private ArrayList<Media> mediaList;
    private int position;
    private MediaPagerAdapter2 adapter;
    private boolean glideLoad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setFontThemes(R.style.DarkStatusBarActivity_Font1, R.style.DarkStatusBarActivity_Font2);
        super.onCreate(savedInstanceState);
        instance = this;
        binding = ActivityMedia2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        WindowAndSystemUiUtil.extendContentUnderSystemBars(this, null, null,
                ColorUtil.getAttrColor(this, com.google.android.material.R.attr.colorSurfaceContainer));
        setupBackNavigation(binding.toolbar, getColor(R.color.white));
        binding.appBar.bringToFront();
        intentData();
        setupYiers();
        showContent();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        actionButtonYier = null;
        instance = null;
        adapter.releasePlayer();
    }

    private void intentData() {
        mediaList = getIntent().getParcelableArrayListExtra(ExtraKeys.MEDIAS);
        position = getIntent().getIntExtra(ExtraKeys.POSITION, 0);
//        binding.actionButton.setText(getIntent().getStringExtra(ExtraKeys.BUTTON_TEXT));
        glideLoad = getIntent().getBooleanExtra(ExtraKeys.GLIDE_LOAD, false);
    }

    private void setupYiers() {
        if(actionButtonYier != null) {
//            binding.actionButton.setOnClickListener(actionButtonYier);
            binding.toolbar.setOnMenuItemClickListener(item -> {
                actionButtonYier.onClick(binding.toolbar);
                return false;
            });
        }
        binding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            private boolean right;
            private float previousPositionOffset;
            private boolean previousPositionOffsetGreaterThanNow;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                boolean thisPreviousPositionOffsetGreaterThanNow = previousPositionOffset > positionOffset;
                previousPositionOffset = positionOffset;
                if(Utils.approximatelyEqual(positionOffset, 0, 0.001F)) {
                    if (adapter.getMediaList().get(MediaActivity2.this.position).getMediaType() == MediaType.IMAGE) {
                        binding.playControl.setVisibility(View.GONE);
                    } else if (adapter.getMediaList().get(MediaActivity2.this.position).getMediaType() == MediaType.VIDEO) {
                        binding.playControl.setVisibility(View.VISIBLE);
                    }
                }
                if(positionOffset == 0 || thisPreviousPositionOffsetGreaterThanNow != previousPositionOffsetGreaterThanNow){
                    int previousPosition = right ? MediaActivity2.this.position + 1 : MediaActivity2.this.position - 1;
                    if(previousPosition != -1 && !(previousPosition >= adapter.getCount())) {
                        if (adapter.getMediaList().get(previousPosition).getMediaType() == MediaType.IMAGE) {
                            PagerItemMediaBinding binding1 = adapter.getBindingMap().get(previousPosition);
                            if(binding1 != null) {
                                binding.viewPager.post(binding1.photoView::resetScaleAndCenter);
                            }
                        }
                    }
                }
                previousPositionOffsetGreaterThanNow = thisPreviousPositionOffsetGreaterThanNow;
            }

            @Override
            public void onPageSelected(int position) {
                if(MediaActivity2.this.position != position){
                    right = MediaActivity2.this.position > position;
                    adapter.pausePlayer();
                    MediaActivity2.this.position = position;
                    adapter.startPlayer();
                    binding.toolbar.setTitle((position + 1) + " / " + mediaList.size());
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private void showContent() {
        binding.toolbar.setTitle((position + 1) + " / " + mediaList.size());
        adapter = new MediaPagerAdapter2(this, mediaList);
        binding.viewPager.setAdapter(adapter);
        binding.viewPager.setOffscreenPageLimit(2);
        binding.viewPager.setCurrentItem(position, false);
        binding.viewPager.setPageMargin(UiUtil.dpToPx(this, getResources().getDimension(R.dimen.media_page_margin)));
//        binding.actionButton.post(() -> binding.actionButton.setVisibility(actionButtonYier == null ? View.GONE : View.VISIBLE));
        binding.toolbar.post(() -> binding.toolbar.getMenu().findItem(R.id.save).setVisible(actionButtonYier != null));
        binding.viewPager.post(() -> {
            adapter.startPlayer();
        });
    }

    public static void setActionButtonYier(View.OnClickListener yier){
        actionButtonYier = yier;
        if(instance != null && instance.binding != null) {
            instance.binding.toolbar.post(() -> instance.binding.toolbar.getMenu().findItem(R.id.save).setVisible(actionButtonYier != null));
        }
    }

    public int getCurrentItemIndex(){
        return binding.viewPager.getCurrentItem();
    }

    public static MediaActivity2 getInstance() {
        return instance;
    }

    public boolean isPureContent() {
        return adapter.isPureContent();
    }

    public boolean isGlideLoad() {
        return glideLoad;
    }

    public ActivityMedia2Binding getBinding() {
        return binding;
    }

    public MediaPagerAdapter2 getAdapter() {
        return adapter;
    }

    public ArrayList<Media> getMediaList() {
        return mediaList;
    }
}