package com.longx.intelligent.android.ichat2.activity;

import androidx.annotation.NonNull;
import androidx.exifinterface.media.ExifInterface;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.activity.helper.BaseActivity;
import com.longx.intelligent.android.ichat2.adapter.ChooseMediasRecyclerAdapter;
import com.longx.intelligent.android.ichat2.databinding.ActivityChooseMediasBinding;
import com.longx.intelligent.android.ichat2.databinding.LayoutGalleryFooterBinding;
import com.longx.intelligent.android.ichat2.databinding.LayoutGalleryHeaderBinding;
import com.longx.intelligent.android.ichat2.media.MediaType;
import com.longx.intelligent.android.ichat2.media.data.DirectoryInfo;
import com.longx.intelligent.android.ichat2.media.data.MediaInfo;
import com.longx.intelligent.android.ichat2.media.helper.LocationHelper;
import com.longx.intelligent.android.ichat2.media.helper.MediaStoreHelper;
import com.longx.intelligent.android.ichat2.ui.LocationNameSwitcher;
import com.longx.intelligent.android.ichat2.util.ColorUtil;
import com.longx.intelligent.android.ichat2.util.ErrorLogger;
import com.longx.intelligent.android.ichat2.util.UiUtil;
import com.longx.intelligent.android.ichat2.util.Utils;
import com.longx.intelligent.android.ichat2.util.WindowAndSystemUiUtil;
import com.longx.intelligent.android.ichat2.value.Constants;
import com.longx.intelligent.android.ichat2.value.Variables;
import com.longx.intelligent.android.ichat2.yier.AutoCompleteTextViewAutoSelectOnItemClickYier;
import com.longx.intelligent.android.lib.recyclerview.decoration.SpaceGridDecorationSetter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChooseMediasActivity extends BaseActivity{
    private ActivityChooseMediasBinding binding;
    private LayoutGalleryHeaderBinding headerBinding;
    private LayoutGalleryFooterBinding footerBinding;
    private MediaType mediaType;
    private ChooseMediasRecyclerAdapter adapter;
    private GridLayoutManager gridLayoutManager;
    private SpaceGridDecorationSetter spaceGridDecorationSetter;
    private int headerSpaceOriginalHeight;
    private LocationNameSwitcher locationNameSwitcher;
    private List<DirectoryInfo> allMediaDirectories;
    private String currentDirectoryPath;
    private boolean uiInited;
    private List<Uri> chosenUriList;
    private int maxAllowSize;
    private boolean remove;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChooseMediasBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getIntentDataAndSetupUi();
        setupBackNavigation(binding.toolbar, getColor(R.color.white));
        changeWindowAndSystemUi();
        init();
        showContent();
        setupYiers();
    }

    private void getIntentDataAndSetupUi() {
        String mediaTypeEnumName = getIntent().getStringExtra(ExtraKeys.MEDIA_TYPE);
        if(mediaTypeEnumName != null){
            mediaType = MediaType.valueOf(mediaTypeEnumName);
        }
        remove = getIntent().getBooleanExtra(ExtraKeys.REMOVE, true);
        String toolbarTitle = getIntent().getStringExtra(ExtraKeys.TOOLBAR_TITLE);
        int actionIconResId = getIntent().getIntExtra(ExtraKeys.RES_ID, -1);
        String menuTitle = getIntent().getStringExtra(ExtraKeys.MENU_TITLE);
        binding.title.setText(toolbarTitle);
        MenuItem item = binding.toolbar.getMenu().findItem(R.id.action);
        item.setIcon(actionIconResId);
        item.setTitle(menuTitle);
        Parcelable[] parcelableArrayExtra = getIntent().getParcelableArrayExtra(ExtraKeys.URIS);
        if(parcelableArrayExtra != null) {
            chosenUriList = Utils.parseParcelableArray(parcelableArrayExtra);
        }
        maxAllowSize = getIntent().getIntExtra(ExtraKeys.MAX_ALLOW_SIZE, -1);
    }

    private void changeWindowAndSystemUi() {
        WindowAndSystemUiUtil.extendContentUnderSystemBars(this, null, null,
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

    private void setupYiers() {
        binding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView r, int dx, int dy) {
                super.onScrolled(r, dx, dy);
                updateInfos();
            }
        });
        binding.toolbar.setOnMenuItemClickListener(item -> {
            if(item.getItemId() == R.id.action){
                Intent intent = new Intent();
                intent.putExtra(ExtraKeys.URIS, adapter.getCheckedUris().toArray(new Uri[0]));
                intent.putExtra(ExtraKeys.REMOVE, remove);
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
                    DirectoryInfo directoryInfo = allMediaDirectories.get(position - 1);
                    currentDirectoryPath = directoryInfo.getPath();
                }
                showContent();
                setupYiers();
                binding.recyclerView.scrollToEnd(false);
            }
        });
    }

    private void initAutoCompleteTextView() {
        List<String> directoryNames = new ArrayList<>();
        if(mediaType == null){
            allMediaDirectories = MediaStoreHelper.getAllMediaDirectories(this);
            directoryNames.add("所有媒体");
        }else if(mediaType.equals(MediaType.IMAGE)) {
            allMediaDirectories = MediaStoreHelper.getAllImageDirectories(this);
            directoryNames.add("所有图片");
        }else if(mediaType.equals(MediaType.VIDEO)){
            allMediaDirectories = MediaStoreHelper.getAllVideoDirectories(this);
            directoryNames.add("所有视频");
        }
        allMediaDirectories.forEach(mediaDirectory -> {
            directoryNames.add(new File(mediaDirectory.getPath()).getName());
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
        adapter = new ChooseMediasRecyclerAdapter(this, getData(), chosenUriList, maxAllowSize);
        showMedias();
        binding.recyclerView.post(this::updateInfos);
        showTotalSize();
    }

    private List<ChooseMediasRecyclerAdapter.ItemData> getData(){
        List<MediaInfo> mediaInfos = Collections.emptyList();
        if(currentDirectoryPath == null) {
            if(mediaType == null){
                mediaInfos = MediaStoreHelper.getAllMedias(this);
            }else if(mediaType.equals(MediaType.IMAGE)) {
                mediaInfos = MediaStoreHelper.getAllImages(this);
            }else if(mediaType.equals(MediaType.VIDEO)){
                mediaInfos = MediaStoreHelper.getAllVideos(this);
            }
        }else {
            if(mediaType == null){
                mediaInfos = MediaStoreHelper.getAllDirectoryMedias(this, currentDirectoryPath);
            }else if(mediaType.equals(MediaType.IMAGE)) {
                mediaInfos = MediaStoreHelper.getAllDirectoryImages(this, currentDirectoryPath);
            }else if(mediaType.equals(MediaType.VIDEO)){
                mediaInfos = MediaStoreHelper.getAllDirectoryVideos(this, currentDirectoryPath);
            }
        }
        List<ChooseMediasRecyclerAdapter.ItemData> itemDataList = new ArrayList<>();
        mediaInfos.forEach(mediaInfo -> {
            itemDataList.add(new ChooseMediasRecyclerAdapter.ItemData(mediaInfo));
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
        ChooseMediasRecyclerAdapter chooseMediasRecyclerAdapter = (ChooseMediasRecyclerAdapter) binding.recyclerView.getAdapter();
        if(chooseMediasRecyclerAdapter.getItemDataList().isEmpty()) return;
        RecyclerView.LayoutManager layoutManager = binding.recyclerView.getLayoutManager();
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
            ChooseMediasRecyclerAdapter.ItemData firstItem = chooseMediasRecyclerAdapter.getItemDataList().get(firstItemPosition);
            ChooseMediasRecyclerAdapter.ItemData lastItem = chooseMediasRecyclerAdapter.getItemDataList().get(lastItemPosition);
            updateTimeRange(firstItem, lastItem);
            updateLocation(firstItem, lastItem);
        }
    }

    private void updateTimeRange(ChooseMediasRecyclerAdapter.ItemData firstItem, ChooseMediasRecyclerAdapter.ItemData lastItem){
        long firstCompletelyVisibleItemTime = firstItem.getMediaInfo().getAddedTime() * 1000;
        long lastCompletelyVisibleItemTime = lastItem.getMediaInfo().getAddedTime() * 1000;
        binding.time.setText(Variables.getTimeRangeStr(firstCompletelyVisibleItemTime, lastCompletelyVisibleItemTime));
    }

    private void updateLocation(ChooseMediasRecyclerAdapter.ItemData firstItem, ChooseMediasRecyclerAdapter.ItemData lastItem){
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