package com.longx.intelligent.android.ichat2.activity;

import androidx.annotation.NonNull;
import androidx.exifinterface.media.ExifInterface;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.activity.helper.BaseActivity;
import com.longx.intelligent.android.ichat2.adapter.SendImageMessagesRecyclerAdapter;
import com.longx.intelligent.android.ichat2.databinding.ActivitySendImageMessagesBinding;
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
import com.longx.intelligent.android.ichat2.yier.AutoCompleteTextViewAutoSelectOnItemClickYier;
import com.longx.intelligent.android.lib.recyclerview.decoration.SpaceGridDecorationSetter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SendImageMessagesActivity extends BaseActivity{
    private ActivitySendImageMessagesBinding binding;
    private LayoutGalleryHeaderBinding headerBinding;
    private LayoutGalleryFooterBinding footerBinding;
    private SendImageMessagesRecyclerAdapter adapter;
    private GridLayoutManager gridLayoutManager;
    private SpaceGridDecorationSetter spaceGridDecorationSetter;
    private int headerSpaceOriginalHeight;
    private LocationNameSwitcher locationNameSwitcher;
    private List<DirectoryInfo> allImageDirectories;
    private String currentDirectoryPath;
    private boolean uiInited;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySendImageMessagesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupDefaultBackNavigation(binding.toolbar, getColor(R.color.white));
        changeWindowAndSystemUi();
        init();
        showContent();
        setupYiers();
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
        allImageDirectories = MediaStoreHelper.getAllImageDirectories(this);
        List<String> directoryNames = new ArrayList<>();
        directoryNames.add("所有图片");
        allImageDirectories.forEach(imageDirectory -> {
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

    private void setupYiers() {
        binding.recyclerView.addOnScrollListener(new androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull androidx.recyclerview.widget.RecyclerView r, int dx, int dy) {
                super.onScrolled(r, dx, dy);
                updateInfos();
            }
        });
        adapter.setOnRecyclerItemClickYier((position, view) -> {
            ArrayList<MediaInfo> imageInfoList = new ArrayList<>();
            for (SendImageMessagesRecyclerAdapter.ItemData itemData : adapter.getItemDataList()) {
                imageInfoList.add(itemData.getMediaInfo());
            }
            Intent intent = new Intent(this, PreviewToSendImageActivity.class);
            intent.putExtra(ExtraKeys.URI, imageInfoList.get(position).getUri());
            startActivity(intent);
        });
        binding.toolbar.setOnMenuItemClickListener(item -> {
            if(item.getItemId() == R.id.send){
                Intent intent = new Intent();
                intent.putExtra(ExtraKeys.URIS, adapter.getCheckedImageUris().toArray(new Uri[0]));
                setResult(RESULT_OK, intent);
                finish();
            }
            return true;
        });
        binding.directoryAutoCompleteTextView.setOnItemClickListener(new AutoCompleteTextViewAutoSelectOnItemClickYier(binding.directoryAutoCompleteTextView){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                super.onItemClick(parent, view, position, id);
                if(position == 0) {
                    currentDirectoryPath = null;
                }else {
                    DirectoryInfo directoryInfo = allImageDirectories.get(position - 1);
                    currentDirectoryPath = directoryInfo.getPath();
                }
                showContent();
                setupYiers();
                binding.recyclerView.scrollToEnd(false);
            }
        });
    }

    private void showContent() {
        adapter = new SendImageMessagesRecyclerAdapter(this, getData());
        showMedias();
        binding.recyclerView.post(this::updateInfos);
        showTotalSize();
    }

    private List<SendImageMessagesRecyclerAdapter.ItemData> getData(){
        List<MediaInfo> images;
        if(currentDirectoryPath == null) {
            images = MediaStoreHelper.geAllImages(this);
        }else {
            images = MediaStoreHelper.getAllDirectoryImages(this, currentDirectoryPath);
        }
        List<SendImageMessagesRecyclerAdapter.ItemData> itemDataList = new ArrayList<>();
        images.forEach(image -> {
            itemDataList.add(new SendImageMessagesRecyclerAdapter.ItemData(image));
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
        int imagesCount = MediaStoreHelper.getImagesCount(this, currentDirectoryPath, false);
        footerBinding.total.setText(imagesCount + "张照片");
    }

    private void updateInfos() {
        SendImageMessagesRecyclerAdapter sendImageMessagesRecyclerAdapter = (SendImageMessagesRecyclerAdapter) binding.recyclerView.getAdapter();
        if(sendImageMessagesRecyclerAdapter.getItemDataList().size() == 0) return;
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
            SendImageMessagesRecyclerAdapter.ItemData firstItem = sendImageMessagesRecyclerAdapter.getItemDataList().get(firstItemPosition);
            SendImageMessagesRecyclerAdapter.ItemData lastItem = sendImageMessagesRecyclerAdapter.getItemDataList().get(lastItemPosition);
            updateTimeRange(firstItem, lastItem);
            updateLocation(firstItem, lastItem);
        }
    }

    private void updateTimeRange(SendImageMessagesRecyclerAdapter.ItemData firstItem, SendImageMessagesRecyclerAdapter.ItemData lastItem){
        long firstCompletelyVisibleItemTime = firstItem.getMediaInfo().getAddedTime() * 1000;
        long lastCompletelyVisibleItemTime = lastItem.getMediaInfo().getAddedTime() * 1000;
        binding.time.setText(Variables.getTimeRangeStr(firstCompletelyVisibleItemTime, lastCompletelyVisibleItemTime));
    }

    private void updateLocation(SendImageMessagesRecyclerAdapter.ItemData firstItem, SendImageMessagesRecyclerAdapter.ItemData lastItem){
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