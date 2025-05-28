package com.longx.intelligent.android.imessage.activity;

import androidx.annotation.NonNull;
import androidx.exifinterface.media.ExifInterface;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.imessage.activity.helper.BaseActivity;
import com.longx.intelligent.android.imessage.adapter.ChooseMediasRecyclerAdapter;
import com.longx.intelligent.android.imessage.databinding.ActivityChooseMediasBinding;
import com.longx.intelligent.android.imessage.databinding.RecyclerFooterGalleryBinding;
import com.longx.intelligent.android.imessage.databinding.RecyclerHeaderGalleryBinding;
import com.longx.intelligent.android.imessage.media.MediaType;
import com.longx.intelligent.android.imessage.media.data.DirectoryInfo;
import com.longx.intelligent.android.imessage.media.data.MediaInfo;
import com.longx.intelligent.android.imessage.media.helper.LocationHelper;
import com.longx.intelligent.android.imessage.media.helper.MediaStoreHelper;
import com.longx.intelligent.android.imessage.ui.LocationNameSwitcher;
import com.longx.intelligent.android.imessage.ui.glide.GlideApp;
import com.longx.intelligent.android.imessage.util.ColorUtil;
import com.longx.intelligent.android.imessage.util.ErrorLogger;
import com.longx.intelligent.android.imessage.util.UiUtil;
import com.longx.intelligent.android.imessage.util.Utils;
import com.longx.intelligent.android.imessage.util.WindowAndSystemUiUtil;
import com.longx.intelligent.android.imessage.value.Constants;
import com.longx.intelligent.android.imessage.value.Mutables;
import com.longx.intelligent.android.imessage.yier.AutoCompleteTextViewAutoSelectOnItemClickYier;
import com.longx.intelligent.android.lib.recyclerview.decoration.SpaceGridDecorationSetter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChooseMediasActivity extends BaseActivity {
    private ActivityChooseMediasBinding binding;
    private RecyclerHeaderGalleryBinding headerBinding;
    private RecyclerFooterGalleryBinding footerBinding;
    private MediaType mediaType;
    private ChooseMediasRecyclerAdapter adapter;
    private GridLayoutManager gridLayoutManager;
    private SpaceGridDecorationSetter spaceGridDecorationSetter;
    private int headerSpaceOriginalHeight;
    private LocationNameSwitcher locationNameSwitcher;
    private List<DirectoryInfo> allMediaDirectories;
    private String currentDirectoryPath;
    private boolean uiInited;
    private List<MediaInfo> chosenMediaList;
    private int maxAllowImageCount;
    private int maxAllowVideoCount;
    private long maxAllowImageSize;
    private long maxAllowVideoSize;
    private boolean remove;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setFontThemes(R.style.DarkStatusBarActivity_Font1, R.style.DarkStatusBarActivity_Font2);
        super.onCreate(savedInstanceState);
        binding = ActivityChooseMediasBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        intentData();
        setupBackNavigation(binding.toolbar, getColor(R.color.white));
        changeWindowAndSystemUi();
        init();
        showContent();
        setupYiers();
    }

    private void intentData() {
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
        Parcelable[] parcelableArrayExtra = getIntent().getParcelableArrayExtra(ExtraKeys.MEDIA_INFOS);
        if(parcelableArrayExtra != null) {
            chosenMediaList = Utils.parseParcelableArray(parcelableArrayExtra);
        }
        maxAllowImageCount = getIntent().getIntExtra(ExtraKeys.MAX_ALLOW_IMAGE_COUNT, -1);
        maxAllowVideoCount = getIntent().getIntExtra(ExtraKeys.MAX_ALLOW_VIDEO_COUNT, -1);
        maxAllowImageSize = getIntent().getLongExtra(ExtraKeys.MAX_ALLOW_IMAGE_SIZE, -1);
        maxAllowVideoSize = getIntent().getLongExtra(ExtraKeys.MAX_ALLOW_VIDEO_SIZE, -1);
    }

    private void changeWindowAndSystemUi() {
        WindowAndSystemUiUtil.extendContentUnderSystemBars(this, null, null,
                ColorUtil.getAttrColor(this, com.google.android.material.R.attr.colorSurfaceContainer));
    }

    private void init() {
        headerBinding = RecyclerHeaderGalleryBinding.inflate(getLayoutInflater());
        footerBinding = RecyclerFooterGalleryBinding.inflate(getLayoutInflater());
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
        binding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    GlideApp.with(ChooseMediasActivity.this).resumeRequests();
                } else if (newState == RecyclerView.SCROLL_STATE_DRAGGING || newState == RecyclerView.SCROLL_STATE_SETTLING) {
                    GlideApp.with(ChooseMediasActivity.this).pauseRequests();
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (Math.abs(dy) < 100) {
                    GlideApp.with(ChooseMediasActivity.this).resumeRequests();
                }
            }
        });
        binding.toolbar.setOnMenuItemClickListener(item -> {
            if(item.getItemId() == R.id.action){
                Intent intent = new Intent();
                intent.putExtra(ExtraKeys.MEDIA_INFOS, adapter.getCheckedMediaInfos().toArray(new MediaInfo[0]));
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
        adapter = new ChooseMediasRecyclerAdapter(this, getData(), chosenMediaList, maxAllowImageCount, maxAllowVideoCount, maxAllowImageSize, maxAllowVideoSize);
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
        gridLayoutManager = new GridLayoutManager(this, Constants.GRID_COLUMN_COUNT);
        binding.recyclerView.setLayoutManager(gridLayoutManager);
        binding.recyclerView.setAdapter(adapter);
        setupGrid();
    }

    private void setupGrid() {
        gridLayoutManager.setSpanCount(Constants.GRID_COLUMN_COUNT);
        binding.recyclerView.removeHeaderView();
        binding.recyclerView.removeFooterView();
        binding.recyclerView.setHeaderView(headerBinding.getRoot());
        binding.recyclerView.setFooterView(footerBinding.getRoot());
        spaceGridDecorationSetter.setSpace(this, binding.recyclerView, Constants.GRID_COLUMN_COUNT, Constants.GRID_SPACE_DP, false, null);
        postCalculateAndSetHeaderSpaceHeight();
    }

    private void postCalculateAndSetHeaderSpaceHeight() {
        headerBinding.getRoot().post(() -> {
            if(headerSpaceOriginalHeight == 0) {
                headerSpaceOriginalHeight = headerBinding.space.getHeight();
            }
            int spaceHeight = headerSpaceOriginalHeight
                    + (WindowAndSystemUiUtil.getActionBarHeight(this) - binding.title.getHeight()) / 2
                    + binding.time.getHeight()
                    + UiUtil.getViewMargin(binding.time)[1]
                    + binding.location.getHeight()
                    + UiUtil.getViewMargin(binding.location)[1];
            UiUtil.setViewHeight(headerBinding.space, spaceHeight);
        });
    }

    private void showTotalSize(){
        int imagesCount = MediaStoreHelper.getImagesCount(this, currentDirectoryPath, false);
        int videoCount = MediaStoreHelper.getVideoCount(this, currentDirectoryPath, false);
        String text = "";
        if(mediaType == null){
            if(imagesCount > 0 && videoCount > 0){
                text = imagesCount + "张图片、" + videoCount + "个视频";
            }else if(imagesCount > 0){
                text = imagesCount + "张图片";
            }else if(videoCount > 0){
                text = videoCount + "个视频";
            }
        }else if(mediaType.equals(MediaType.IMAGE)){
            if(imagesCount > 0){
                text = imagesCount + "张图片";
            }
        }else if(mediaType.equals(MediaType.VIDEO)){
            if(videoCount > 0){
                text = videoCount + "个视频";
            }
        }
        footerBinding.total.setText(text);
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
        binding.time.setText(Mutables.getTimeRangeStr(firstCompletelyVisibleItemTime, lastCompletelyVisibleItemTime));
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