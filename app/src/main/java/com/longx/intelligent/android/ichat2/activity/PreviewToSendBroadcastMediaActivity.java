package com.longx.intelligent.android.ichat2.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.activity.helper.BaseActivity;
import com.longx.intelligent.android.ichat2.adapter.MediaPagerAdapter;
import com.longx.intelligent.android.ichat2.databinding.ActivityPreviewToSendBroadcastMediaBinding;
import com.longx.intelligent.android.ichat2.media.MediaType;
import com.longx.intelligent.android.ichat2.media.data.Media;
import com.longx.intelligent.android.ichat2.util.ColorUtil;
import com.longx.intelligent.android.ichat2.util.ErrorLogger;
import com.longx.intelligent.android.ichat2.util.WindowAndSystemUiUtil;
import com.longx.intelligent.android.ichat2.yier.RecyclerItemYiers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PreviewToSendBroadcastMediaActivity extends BaseActivity implements RecyclerItemYiers.OnRecyclerItemActionYier, RecyclerItemYiers.OnRecyclerItemClickYier {
    private ActivityPreviewToSendBroadcastMediaBinding binding;
    private ArrayList<Media> medias;
    private int position;
    private MediaPagerAdapter adapter;
    private boolean pureContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPreviewToSendBroadcastMediaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        WindowAndSystemUiUtil.extendContentUnderSystemBars(this, null, null,
                ColorUtil.getAttrColor(this, com.google.android.material.R.attr.colorSurfaceContainer));
        setupBackNavigation(binding.toolbar, getColor(R.color.white));
        binding.appBar.bringToFront();
        getIntentData();
        showContent();
        setupYiers();
    }

    private void getIntentData() {
        medias = getIntent().getParcelableArrayListExtra(ExtraKeys.MEDIAS);
        position = getIntent().getIntExtra(ExtraKeys.POSITION, 0);
    }

    private void showContent() {
        binding.toolbar.setTitle((position + 1) + " / " + medias.size());
        adapter = new MediaPagerAdapter(this, medias);
        adapter.setOnRecyclerItemActionYier(this);
        adapter.setOnRecyclerItemClickYier(this);
        binding.viewPager.setAdapter(adapter);
        binding.viewPager.setOffscreenPageLimit(2);
        binding.viewPager.setCurrentItem(position, false);
        setupPageSwitchTransformer();
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

    private void setupYiers() {
        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            boolean right;
            float previousPositionOffset;
            boolean previousPositionOffsetGreaterThanNow;

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if(PreviewToSendBroadcastMediaActivity.this.position != position){
                    right = PreviewToSendBroadcastMediaActivity.this.position > position;
                    PreviewToSendBroadcastMediaActivity.this.position = position;
                    binding.toolbar.setTitle((position + 1) + " / " + medias.size());
                }
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                boolean thisPreviousPositionOffsetGreaterThanNow = previousPositionOffset > positionOffset;
                previousPositionOffset = positionOffset;
                if(positionOffset == 0 || thisPreviousPositionOffsetGreaterThanNow != previousPositionOffsetGreaterThanNow){
                    int previousPosition = right ? PreviewToSendBroadcastMediaActivity.this.position + 1 : PreviewToSendBroadcastMediaActivity.this.position - 1;
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
        binding.removeButton.setOnClickListener(v -> {
            int currentItem = binding.viewPager.getCurrentItem();
            medias.remove(currentItem);
            if(medias.isEmpty()) finish();
            binding.toolbar.setTitle((currentItem == medias.size() ? currentItem : currentItem + 1) + " / " + medias.size());
            adapter.removeItem(currentItem);
            Intent intent = new Intent();
            intent.putParcelableArrayListExtra(ExtraKeys.MEDIAS, medias);
            setResult(RESULT_OK, intent);
        });
    }

    private void setPureContent(boolean pureContent) {
        Map<Integer, MediaPagerAdapter.ViewHolder> viewHolders = adapter.getViewHolders();
        viewHolders.entrySet().forEach(integerViewHolderEntry -> {
            Integer position = integerViewHolderEntry.getKey();
            MediaPagerAdapter.ViewHolder viewHolder = integerViewHolderEntry.getValue();
            if(pureContent) {
                viewHolder.getBinding().topShadowCover.setVisibility(View.GONE);
                if(adapter.getItemDataList().get(position).getMedia().getMediaType() == MediaType.VIDEO) {
                    viewHolder.getBinding().playControl.setVisibility(View.GONE);
                }
            }else {
                viewHolder.getBinding().topShadowCover.setVisibility(View.VISIBLE);
                if(adapter.getItemDataList().get(position).getMedia().getMediaType() == MediaType.VIDEO) {
                    viewHolder.getBinding().playControl.setVisibility(View.VISIBLE);
                }
            }
        });
        if(pureContent){
            binding.appBar.setVisibility(View.GONE);
            WindowAndSystemUiUtil.setSystemUIShown(this, false);
            this.pureContent = true;
        }else {
            binding.appBar.setVisibility(View.VISIBLE);
            WindowAndSystemUiUtil.setSystemUIShown(this, true);
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
    }
}