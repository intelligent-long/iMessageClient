package com.longx.intelligent.android.imessage.activity;

import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;

import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.imessage.activity.helper.BaseActivity;
import com.longx.intelligent.android.imessage.adapter.MediaPagerAdapter;
import com.longx.intelligent.android.imessage.databinding.ActivityMediaBinding;
import com.longx.intelligent.android.imessage.media.MediaType;
import com.longx.intelligent.android.imessage.media.data.Media;
import com.longx.intelligent.android.imessage.util.ColorUtil;
import com.longx.intelligent.android.imessage.util.UiUtil;
import com.longx.intelligent.android.imessage.util.WindowAndSystemUiUtil;
import com.longx.intelligent.android.imessage.yier.RecyclerItemYiers;

import java.util.ArrayList;
import java.util.Map;

public class MediaActivity extends BaseActivity implements RecyclerItemYiers.OnRecyclerItemActionYier, RecyclerItemYiers.OnRecyclerItemClickYier {
    private ActivityMediaBinding binding;
    private ArrayList<Media> mediaList;
    private int position;
    private MediaPagerAdapter adapter;
    private boolean pureContent;
    private static View.OnClickListener actionButtonYier;
    private static MediaActivity instance;
    private boolean glideLoad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setFontThemes(R.style.DarkStatusBarActivity_Font1, R.style.DarkStatusBarActivity_Font2);
        super.onCreate(savedInstanceState);
        instance = this;
        binding = ActivityMediaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        WindowAndSystemUiUtil.extendContentUnderSystemBars(this, null, null,
                ColorUtil.getAttrColor(this, com.google.android.material.R.attr.colorSurfaceContainer));
        setupBackNavigation(binding.toolbar, getColor(R.color.white));
        getIntentData();
        binding.appBar.bringToFront();
        setupYiers();
        showContent();
    }

    private void getIntentData() {
        mediaList = getIntent().getParcelableArrayListExtra(ExtraKeys.MEDIAS);
        position = getIntent().getIntExtra(ExtraKeys.POSITION, 0);
        binding.actionButton.setText(getIntent().getStringExtra(ExtraKeys.BUTTON_TEXT));;
        glideLoad = getIntent().getBooleanExtra(ExtraKeys.GLIDE_LOAD, false);
    }

    private void showContent() {
        binding.toolbar.setTitle((position + 1) + " / " + mediaList.size());
        adapter = new MediaPagerAdapter(this, mediaList, glideLoad);
        adapter.setOnRecyclerItemActionYier(this);
        adapter.setOnRecyclerItemClickYier(this);
        binding.viewPager.setAdapter(adapter);
        binding.viewPager.setOffscreenPageLimit(2);
        binding.viewPager.setCurrentItem(position, false);
        setupPageSwitchTransformer();
        if(actionButtonYier != null){
            binding.actionButton.setVisibility(View.VISIBLE);
        }else {
            binding.actionButton.setVisibility(View.GONE);
        }
    }

    private void setupYiers() {
        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            boolean right;
            float previousPositionOffset;
            boolean previousPositionOffsetGreaterThanNow;

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if(MediaActivity.this.position != position){
                    right = MediaActivity.this.position > position;
                    MediaActivity.this.position = position;
                    binding.toolbar.setTitle((position + 1) + " / " + mediaList.size());
                }
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                boolean thisPreviousPositionOffsetGreaterThanNow = previousPositionOffset > positionOffset;
                previousPositionOffset = positionOffset;
                if(positionOffset == 0 || thisPreviousPositionOffsetGreaterThanNow != previousPositionOffsetGreaterThanNow){
                    int previousPosition = right ? MediaActivity.this.position + 1 : MediaActivity.this.position - 1;
                    if(previousPosition != -1 && !(previousPosition >= adapter.getItemCount())) {
                        if (positionOffset == 0 && adapter.getItemDataList().get(previousPosition).getMedia().getMediaType() == MediaType.IMAGE) {
                            binding.viewPager.post(() -> adapter.notifyItemChanged(previousPosition));
                        }
                        adapter.pausePlayer(previousPosition);
                    }
                    adapter.startPlayer(position);
                }
                previousPositionOffsetGreaterThanNow = thisPreviousPositionOffsetGreaterThanNow;
            }
        });
        if(actionButtonYier != null) {
            binding.actionButton.setOnClickListener(actionButtonYier);
        }
    }

    private void setupPageSwitchTransformer() {
        binding.viewPager.setPageTransformer((page, position) -> {
            float margin = getResources().getDimension(R.dimen.media_page_margin);
            float offset = getResources().getDimension(R.dimen.media_page_offset);
            if (binding.viewPager.getOrientation() == ViewPager2.ORIENTATION_HORIZONTAL) {
                float scaleFactor = 1 - Math.abs(position) * 0.0f;
                float translationX = position * (2 * margin + offset);
                if (position < -1) {
                    page.setTranslationX(-translationX);
                } else if (position <= 1) {
                    float scale = Math.max(scaleFactor, 0.75f);
                    page.setScaleX(scale);
                    page.setScaleY(scale);
                    page.setTranslationX(translationX);
                } else {
                    page.setTranslationX(translationX);
                }
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void setPureContent(boolean pureContent) {
        Map<Integer, MediaPagerAdapter.ViewHolder> viewHolders = adapter.getViewHolders();
        viewHolders.entrySet().forEach(integerViewHolderEntry -> {
            Integer position = integerViewHolderEntry.getKey();
            MediaPagerAdapter.ViewHolder viewHolder = integerViewHolderEntry.getValue();
            if(pureContent) {
                UiUtil.setViewVisibility(viewHolder.getBinding().topTranslucentOverlayWrap, View.GONE);
                if(adapter.getItemDataList().get(position).getMedia().getMediaType() == MediaType.VIDEO) {
                    UiUtil.setViewVisibility(viewHolder.getBinding().playControl, View.GONE);
                }
            }else {
                UiUtil.setViewVisibility(viewHolder.getBinding().topTranslucentOverlayWrap, View.VISIBLE);
                if(adapter.getItemDataList().get(position).getMedia().getMediaType() == MediaType.VIDEO) {
                    UiUtil.setViewVisibility(viewHolder.getBinding().playControl, View.VISIBLE);
                }
            }
        });
        if(pureContent){
            UiUtil.setViewVisibility(binding.appBar, View.GONE);
            WindowAndSystemUiUtil.setSystemUiShown(this, false);
            this.pureContent = true;
        }else {
            UiUtil.setViewVisibility(binding.appBar, View.VISIBLE);
            WindowAndSystemUiUtil.setSystemUiShown(this, true);
            this.pureContent = false;
        }
    }

    @Override
    public void onRecyclerItemAction(int position, Object... o) {
        setPureContent(true);
    }

    @Override
    public void onRecyclerItemClick(int position, View view) {
        setPureContent(!pureContent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(adapter.getItemCount() != 0) adapter.startPlayer(position);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(adapter.getItemCount() != 0) adapter.pausePlayer(position);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(adapter.getItemCount() != 0) adapter.releaseAllPlayer();
        actionButtonYier = null;
        instance = null;
    }

    public int getCurrentItemIndex(){
        if(binding == null) return -1;
        return binding.viewPager.getCurrentItem();
    }

    public static void setActionButtonYier(View.OnClickListener yier){
        actionButtonYier = yier;
        if(instance != null && instance.binding != null) {
            if(yier != null) {
                instance.binding.actionButton.post(() -> instance.binding.actionButton.setVisibility(View.VISIBLE));
            }else {
                instance.binding.actionButton.post(() -> instance.binding.actionButton.setVisibility(View.GONE));
            }
        }
    }

    public static MediaActivity getInstance(){
        return instance;
    }

    public boolean isPureContent() {
        return pureContent;
    }

    public MediaPagerAdapter getAdapter() {
        return adapter;
    }

    public ArrayList<Media> getMediaList() {
        return mediaList;
    }

    public ActivityMediaBinding getBinding() {
        return binding;
    }
}