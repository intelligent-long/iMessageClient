package com.longx.intelligent.android.ichat2.activity;

import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.View;

import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.activity.helper.BaseActivity;
import com.longx.intelligent.android.ichat2.adapter.ChatImagePagerAdapter;
import com.longx.intelligent.android.ichat2.behavior.MessageDisplayer;
import com.longx.intelligent.android.ichat2.da.publicfile.PublicFileAccessor;
import com.longx.intelligent.android.ichat2.data.ChatMessage;
import com.longx.intelligent.android.ichat2.databinding.ActivityChatImageBinding;
import com.longx.intelligent.android.ichat2.dialog.OperationDialog;
import com.longx.intelligent.android.ichat2.util.ColorUtil;
import com.longx.intelligent.android.ichat2.util.WindowAndSystemUiUtil;
import com.longx.intelligent.android.ichat2.yier.RecyclerItemYiers;

import java.util.List;

public class ChatImageActivity extends BaseActivity implements RecyclerItemYiers.OnRecyclerItemActionYier, RecyclerItemYiers.OnRecyclerItemClickYier {
    private ActivityChatImageBinding binding;
    private List<String> imageFilePaths;
    private List<ChatMessage> chatMessages;
    private int position;
    private ChatImagePagerAdapter adapter;
    private boolean purePhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatImageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        WindowAndSystemUiUtil.checkAndExtendContentUnderSystemBars(this, null, null,
                ColorUtil.getAttrColor(this, com.google.android.material.R.attr.colorSurfaceContainer));
        setupDefaultBackNavigation(binding.toolbar, getColor(R.color.white));
        getIntentData();
        binding.appBar.bringToFront();
        showContent();
        setupYiers();
    }

    private void getIntentData() {
        imageFilePaths = getIntent().getStringArrayListExtra(ExtraKeys.FILE_PATHS);
        chatMessages = getIntent().getParcelableArrayListExtra(ExtraKeys.CHAT_MESSAGES);
        position = getIntent().getIntExtra(ExtraKeys.POSITION, 0);
    }

    private void showContent() {
        adapter = new ChatImagePagerAdapter(this, imageFilePaths);
        adapter.setOnRecyclerItemActionYier(this);
        adapter.setOnRecyclerItemClickYier(this);
        binding.viewPager.setAdapter(adapter);
        binding.viewPager.setOffscreenPageLimit(2);
        binding.viewPager.setCurrentItem(position, false);
        setupPageSwitchTransformer();
    }

    private void setupYiers() {
        setupRestorePrevious();
        binding.toolbar.setOnMenuItemClickListener(item -> {
            if(item.getItemId() == R.id.save){
                int currentItem = binding.viewPager.getCurrentItem();
                new Thread(() -> {
                    OperationDialog operationDialog = new OperationDialog(this);
                    operationDialog.show();
                    String saved = PublicFileAccessor.ChatImage.save(imageFilePaths.get(currentItem), chatMessages.get(currentItem));
                    operationDialog.dismiss();
                    if(saved != null){
                        MessageDisplayer.autoShow(this, "已保存", MessageDisplayer.Duration.SHORT);
                    }else {
                        MessageDisplayer.autoShow(this, "保存失败", MessageDisplayer.Duration.SHORT);
                    }
                }).start();
            }
            return true;
        });
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

    private void setupRestorePrevious() {
        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            int position = -1;
            boolean right;

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if(this.position != position){
                    right = this.position > position;
                    this.position = position;
                }
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                if(positionOffset == 0){
                    binding.viewPager.post(() -> adapter.notifyItemChanged(right ? this.position + 1 : this.position - 1));
                }
            }
        });
    }

    private void setPurePhoto(boolean purePhoto) {
        if(purePhoto){
            binding.appBar.setVisibility(View.GONE);
            WindowAndSystemUiUtil.setSystemUIShown(this, false);
            this.purePhoto = true;
        }else {
            binding.appBar.setVisibility(View.VISIBLE);
            WindowAndSystemUiUtil.setSystemUIShown(this, true);
            this.purePhoto = false;
        }
    }

    @Override
    public void onRecyclerItemAction(int position, Object... o) {
        setPurePhoto(true);
    }

    @Override
    public void onRecyclerItemClick(int position, View view) {
        setPurePhoto(!purePhoto);
    }
}