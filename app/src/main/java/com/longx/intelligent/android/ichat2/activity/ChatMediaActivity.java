package com.longx.intelligent.android.ichat2.activity;

import androidx.viewpager2.widget.ViewPager2;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.activity.helper.BaseActivity;
import com.longx.intelligent.android.ichat2.adapter.MediaPagerAdapter;
import com.longx.intelligent.android.ichat2.behavior.MessageDisplayer;
import com.longx.intelligent.android.ichat2.da.publicfile.PublicFileAccessor;
import com.longx.intelligent.android.ichat2.data.ChatMessage;
import com.longx.intelligent.android.ichat2.databinding.ActivityChatMediaBinding;
import com.longx.intelligent.android.ichat2.dialog.OperationDialog;
import com.longx.intelligent.android.ichat2.media.MediaType;
import com.longx.intelligent.android.ichat2.media.data.Media;
import com.longx.intelligent.android.ichat2.util.ColorUtil;
import com.longx.intelligent.android.ichat2.util.ErrorLogger;
import com.longx.intelligent.android.ichat2.util.WindowAndSystemUiUtil;
import com.longx.intelligent.android.ichat2.yier.RecyclerItemYiers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChatMediaActivity extends BaseActivity implements RecyclerItemYiers.OnRecyclerItemActionYier, RecyclerItemYiers.OnRecyclerItemClickYier {
    private ActivityChatMediaBinding binding;
    private List<ChatMessage> chatMessages;
    private int position;
    private MediaPagerAdapter adapter;
    private boolean pureContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatMediaBinding.inflate(getLayoutInflater());
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
        chatMessages = getIntent().getParcelableArrayListExtra(ExtraKeys.CHAT_MESSAGES);
        position = getIntent().getIntExtra(ExtraKeys.POSITION, 0);
    }

    private void showContent() {
        binding.toolbar.setTitle((position + 1) + " / " + chatMessages.size());
        List<Media> mediaList = new ArrayList<>();
        chatMessages.forEach(chatMessage -> {
            if(chatMessage.getType() == ChatMessage.TYPE_IMAGE){
                MediaType mediaType = MediaType.IMAGE;
                Uri uri = Uri.fromFile(new File(chatMessage.getImageFilePath()));
                mediaList.add(new Media(mediaType, uri));
            }else if(chatMessage.getType() == ChatMessage.TYPE_VIDEO){
                MediaType mediaType = MediaType.VIDEO;
                Uri uri = Uri.fromFile(new File(chatMessage.getVideoFilePath()));
                mediaList.add(new Media(mediaType, uri));
            }
        });
        adapter = new MediaPagerAdapter(this, mediaList);
        adapter.setOnRecyclerItemActionYier(this);
        adapter.setOnRecyclerItemClickYier(this);
        binding.viewPager.setAdapter(adapter);
        binding.viewPager.setOffscreenPageLimit(2);
        binding.viewPager.setCurrentItem(position, false);
        setupPageSwitchTransformer();
    }

    private void setupYiers() {
        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            boolean right;
            float previousPositionOffset;
            boolean previousPositionOffsetGreaterThanNow;

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if(ChatMediaActivity.this.position != position){
                    right = ChatMediaActivity.this.position > position;
                    ChatMediaActivity.this.position = position;
                    binding.toolbar.setTitle((position + 1) + " / " + chatMessages.size());
                }
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                boolean thisPreviousPositionOffsetGreaterThanNow = previousPositionOffset > positionOffset;
                previousPositionOffset = positionOffset;
                if(positionOffset == 0 || thisPreviousPositionOffsetGreaterThanNow != previousPositionOffsetGreaterThanNow){
                    int previousPosition = right ? ChatMediaActivity.this.position + 1 : ChatMediaActivity.this.position - 1;
                    if(previousPosition != -1) {
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
        binding.saveButton.setOnClickListener(v -> {
            int currentItem = binding.viewPager.getCurrentItem();
            ChatMessage chatMessage = chatMessages.get(currentItem);
            switch (chatMessage.getType()){
                case ChatMessage.TYPE_IMAGE:{
                    new Thread(() -> {
                        OperationDialog operationDialog = new OperationDialog(this);
                        operationDialog.show();
                        try {
                            PublicFileAccessor.ChatImage.save(chatMessage);
                            operationDialog.dismiss();
                            MessageDisplayer.autoShow(this, "已保存", MessageDisplayer.Duration.SHORT);
                        }catch (IOException e){
                            ErrorLogger.log(e);
                            MessageDisplayer.autoShow(this, "保存失败", MessageDisplayer.Duration.SHORT);
                        }
                    }).start();
                    break;
                }
                case ChatMessage.TYPE_VIDEO:{
                    new Thread(() -> {
                        OperationDialog operationDialog = new OperationDialog(this);
                        operationDialog.show();
                        try {
                            PublicFileAccessor.ChatVideo.save(chatMessage);
                            operationDialog.dismiss();
                            MessageDisplayer.autoShow(this, "已保存", MessageDisplayer.Duration.SHORT);
                        }catch (IOException e){
                            ErrorLogger.log(e);
                            MessageDisplayer.autoShow(this, "保存失败", MessageDisplayer.Duration.SHORT);
                        }
                    }).start();
                }
            }
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
        adapter.startPlayer(position);
    }

    @Override
    protected void onPause() {
        super.onPause();
        adapter.pausePlayer(position);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        adapter.releaseAllPlayer();
    }
}