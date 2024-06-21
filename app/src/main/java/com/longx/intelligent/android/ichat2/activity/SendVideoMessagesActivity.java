package com.longx.intelligent.android.ichat2.activity;

import android.os.Bundle;
import android.widget.ArrayAdapter;

import androidx.exifinterface.media.ExifInterface;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.activity.helper.BaseActivity;
import com.longx.intelligent.android.ichat2.adapter.SendMediaMessagesRecyclerAdapter;
import com.longx.intelligent.android.ichat2.databinding.ActivitySendVideoMessagesBinding;
import com.longx.intelligent.android.ichat2.databinding.LayoutGalleryFooterBinding;
import com.longx.intelligent.android.ichat2.databinding.LayoutGalleryHeaderBinding;
import com.longx.intelligent.android.ichat2.media.data.DirectoryInfo;
import com.longx.intelligent.android.ichat2.media.data.MediaInfo;
import com.longx.intelligent.android.ichat2.media.helper.LocationHelper;
import com.longx.intelligent.android.ichat2.media.helper.MediaStoreHelper;
import com.longx.intelligent.android.ichat2.ui.LocationNameSwitcher;
import com.longx.intelligent.android.ichat2.util.ColorUtil;
import com.longx.intelligent.android.ichat2.util.ErrorLogger;
import com.longx.intelligent.android.ichat2.util.UiUtil;
import com.longx.intelligent.android.ichat2.util.WindowAndSystemUiUtil;
import com.longx.intelligent.android.ichat2.value.Constants;
import com.longx.intelligent.android.ichat2.value.Variables;
import com.longx.intelligent.android.lib.recyclerview.decoration.SpaceGridDecorationSetter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SendVideoMessagesActivity extends BaseActivity {
    private ActivitySendVideoMessagesBinding binding;
    private LayoutGalleryHeaderBinding headerBinding;
    private LayoutGalleryFooterBinding footerBinding;
    private SpaceGridDecorationSetter spaceGridDecorationSetter;
    private LocationNameSwitcher locationNameSwitcher;
    private String currentDirectoryPath;
    private boolean uiInited;
    private SendMediaMessagesRecyclerAdapter adapter;
    private GridLayoutManager gridLayoutManager;
    private int headerSpaceOriginalHeight;
    private List<DirectoryInfo> allVideoDirectories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySendVideoMessagesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupDefaultBackNavigation(binding.toolbar, getColor(R.color.white));
        changeWindowAndSystemUi();
        init();
        showContent();
    }

    private void changeWindowAndSystemUi() {
        WindowAndSystemUiUtil.checkAndExtendContentUnderSystemBars(this, null, null,
                ColorUtil.getAttrColor(this, com.google.android.material.R.attr.colorSurfaceContainer));
    }

    private void init() {
        headerBinding = LayoutGalleryHeaderBinding.inflate(getLayoutInflater());
        footerBinding = LayoutGalleryFooterBinding.inflate(getLayoutInflater());
        spaceGridDecorationSetter = new SpaceGridDecorationSetter();
        locationNameSwitcher = new LocationNameSwitcher(this, binding.location);
        UiUtil.setViewHeight(footerBinding.navigationSpace, WindowAndSystemUiUtil.getNavigationBarHeight(this));
        initAutoCompleteTextView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(uiInited) return;
        uiInited = true;
        initAutoCompleteTextView();
        binding.recyclerView.scrollToEnd(false);
    }

    private void initAutoCompleteTextView() {
        allVideoDirectories = MediaStoreHelper.getAllVideoDirectories(this);
        List<String> directoryNames = new ArrayList<>();
        directoryNames.add("所有视频");
        allVideoDirectories.forEach(imageDirectory -> {
            directoryNames.add(new File(imageDirectory.getPath()).getName());
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, R.layout.layout_auto_complete_text_view_text, directoryNames);
        binding.directoryAutoCompleteTextView.setAdapter(adapter);

        if (!directoryNames.isEmpty()) {
            binding.directoryAutoCompleteTextView.setText(directoryNames.get(0), false);
            currentDirectoryPath = null;
        }
    }

    private void showContent() {
        adapter = new SendMediaMessagesRecyclerAdapter(this, getData());
        showMedias();
        binding.recyclerView.post(this::updateInfos);
        showTotalSize();
    }

    private List<SendMediaMessagesRecyclerAdapter.ItemData> getData(){
        List<MediaInfo> images;
        if(currentDirectoryPath == null) {
            images = MediaStoreHelper.getAllVideos(this);
        }else {
            images = MediaStoreHelper.getAllDirectoryVideos(this, currentDirectoryPath);
        }
        List<SendMediaMessagesRecyclerAdapter.ItemData> itemDataList = new ArrayList<>();
        images.forEach(image -> {
            itemDataList.add(new SendMediaMessagesRecyclerAdapter.ItemData(image));
        });
        return itemDataList;
    }

    private void showMedias(){
        int gridColumn = 4;
        gridLayoutManager = new GridLayoutManager(this, gridColumn);
        binding.recyclerView.setLayoutManager(gridLayoutManager);
        binding.recyclerView.setAdapter(adapter);
        setupGrid(gridColumn);
    }

    private void setupGrid(int columnCount) {
        gridLayoutManager.setSpanCount(columnCount);
        binding.recyclerView.removeHeaderView();
        binding.recyclerView.removeFooterView();
        binding.recyclerView.setHeaderView(headerBinding.getRoot());
        binding.recyclerView.setFooterView(footerBinding.getRoot());
        spaceGridDecorationSetter.setSpace(this, binding.recyclerView, columnCount, Constants.GRID_SPACE_DP, false, null);
        postCalculateAndSetHeaderSpaceHeight();
    }

    private void postCalculateAndSetHeaderSpaceHeight() {
        headerBinding.getRoot().post(() -> {
            if(headerSpaceOriginalHeight == 0) {
                headerSpaceOriginalHeight = headerBinding.space.getHeight();
            }
            int spaceHeight = headerSpaceOriginalHeight
                    + (WindowAndSystemUiUtil.getActionBarSize(this) - binding.title.getHeight()) / 2
                    + binding.time.getHeight()
                    + UiUtil.getViewMargin(binding.time)[1]
                    + binding.location.getHeight()
                    + UiUtil.getViewMargin(binding.location)[1];
            UiUtil.setViewHeight(headerBinding.space, spaceHeight);
        });
    }

    private void showTotalSize(){
        int imagesCount = MediaStoreHelper.getVideoCount(this, currentDirectoryPath, false);
        footerBinding.total.setText(imagesCount + "个视频");
    }

    private void updateInfos() {
        SendMediaMessagesRecyclerAdapter sendMediaMessagesRecyclerAdapter = (SendMediaMessagesRecyclerAdapter) binding.recyclerView.getAdapter();
        if(sendMediaMessagesRecyclerAdapter.getItemDataList().isEmpty()) return;
        androidx.recyclerview.widget.RecyclerView.LayoutManager layoutManager = binding.recyclerView.getLayoutManager();
        if(layoutManager instanceof LinearLayoutManager){
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
            int firstItemPosition = linearLayoutManager.findFirstCompletelyVisibleItemPosition();
            if(firstItemPosition == -1) firstItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
            int lastItemPosition = linearLayoutManager.findLastCompletelyVisibleItemPosition();
            if(lastItemPosition == -1) lastItemPosition = linearLayoutManager.findLastVisibleItemPosition();
            if(firstItemPosition == -1) return;
            if(lastItemPosition == -1) return;
            if(binding.recyclerView.isPositionFooter(lastItemPosition)){
                lastItemPosition --;
            }
            if(binding.recyclerView.isPositionHeader(firstItemPosition)){
                firstItemPosition ++;
            }
            if(binding.recyclerView.hasHeader()){
                firstItemPosition --;
                lastItemPosition --;
            }
            SendMediaMessagesRecyclerAdapter.ItemData firstItem = sendMediaMessagesRecyclerAdapter.getItemDataList().get(firstItemPosition);
            SendMediaMessagesRecyclerAdapter.ItemData lastItem = sendMediaMessagesRecyclerAdapter.getItemDataList().get(lastItemPosition);
            updateTimeRange(firstItem, lastItem);
            updateLocation(firstItem, lastItem);
        }
    }

    private void updateTimeRange(SendMediaMessagesRecyclerAdapter.ItemData firstItem, SendMediaMessagesRecyclerAdapter.ItemData lastItem){
        long firstCompletelyVisibleItemTime = firstItem.getMediaInfo().getAddedTime() * 1000;
        long lastCompletelyVisibleItemTime = lastItem.getMediaInfo().getAddedTime() * 1000;
        binding.time.setText(Variables.getTimeRangeStr(firstCompletelyVisibleItemTime, lastCompletelyVisibleItemTime));
    }

    private void updateLocation(SendMediaMessagesRecyclerAdapter.ItemData firstItem, SendMediaMessagesRecyclerAdapter.ItemData lastItem){
        try {
            ExifInterface exif = firstItem.getMediaInfo().readExif(this);
            if(exif != null) {
                updateLocation(exif);
            }else {
                exif = lastItem.getMediaInfo().readExif(this);
                if(exif != null) {
                    updateLocation(exif);
                }
            }
        }catch (Exception e){
            ErrorLogger.log(getClass(), e);
        }
    }

    private void updateLocation(ExifInterface exif) {
        String latitude = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
        String longitude = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
        if (latitude != null && longitude != null) {
            double latitudeDegrees = LocationHelper.convertGpsStringToDegrees(latitude);
            double longitudeDegrees = LocationHelper.convertGpsStringToDegrees(longitude);
            locationNameSwitcher.fetchAndSwitchFromCoordinates(latitudeDegrees, longitudeDegrees);
        }else {
            locationNameSwitcher.clear();
        }
    }
}