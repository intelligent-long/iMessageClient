package com.longx.intelligent.android.imessage.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;

import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.imessage.activity.helper.BaseActivity;
import com.longx.intelligent.android.imessage.adapter.EditBroadcastMediasRecyclerAdapter;
import com.longx.intelligent.android.imessage.behaviorcomponents.MessageDisplayer;
import com.longx.intelligent.android.imessage.bottomsheet.AddBroadcastMediaBottomSheet;
import com.longx.intelligent.android.imessage.da.FileHelper;
import com.longx.intelligent.android.imessage.data.Broadcast;
import com.longx.intelligent.android.imessage.data.BroadcastMedia;
import com.longx.intelligent.android.imessage.data.request.EditBroadcastPostBody;
import com.longx.intelligent.android.imessage.data.response.OperationData;
import com.longx.intelligent.android.imessage.databinding.ActivityEditBroadcastBinding;
import com.longx.intelligent.android.imessage.media.MediaType;
import com.longx.intelligent.android.imessage.media.data.Media;
import com.longx.intelligent.android.imessage.media.data.MediaInfo;
import com.longx.intelligent.android.imessage.net.dataurl.NetDataUrls;
import com.longx.intelligent.android.imessage.net.retrofit.caller.BroadcastApiCaller;
import com.longx.intelligent.android.imessage.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.imessage.net.stomp.ServerMessageServiceStompActions;
import com.longx.intelligent.android.imessage.util.CollectionUtil;
import com.longx.intelligent.android.imessage.util.ErrorLogger;
import com.longx.intelligent.android.imessage.util.FileUtil;
import com.longx.intelligent.android.imessage.util.UiUtil;
import com.longx.intelligent.android.imessage.util.Utils;
import com.longx.intelligent.android.imessage.value.Constants;
import com.longx.intelligent.android.imessage.yier.BroadcastUpdateYier;
import com.longx.intelligent.android.imessage.yier.GlobalYiersHolder;
import com.longx.intelligent.android.imessage.yier.KeyboardVisibilityYier;
import com.longx.intelligent.android.imessage.yier.TextChangedYier;
import com.longx.intelligent.android.lib.recyclerview.decoration.SpaceGridDecorationSetter;
import com.longx.intelligent.android.lib.recyclerview.dragsort.DragSortItemTouchCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Response;

public class EditBroadcastActivity extends BaseActivity {
    private ActivityEditBroadcastBinding binding;
    private Broadcast broadcast;
    private EditBroadcastMediasRecyclerAdapter adapter;
    private final SpaceGridDecorationSetter spaceGridDecorationSetter = new SpaceGridDecorationSetter();
    private ActivityResultLauncher<Intent> addMediasResultLauncher;
    private ActivityResultLauncher<Intent> returnFromPreviewToSendMediaResultLauncher;
    private final Map<Integer, MediaInfo> leftMediaInfoMap = new HashMap<>();
    private final Map<Integer, MediaInfo> addMediaInfoMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditBroadcastBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupCloseBackNavigation(binding.toolbar);
        intentData();
        init();
        registerResultLauncher();
        setupYiers();
        showContent();
    }

    private void intentData() {
        broadcast = getIntent().getParcelableExtra(ExtraKeys.BROADCAST);
        if(!CollectionUtil.isEmpty(broadcast.getBroadcastMedias())){
            for (int i = 0; i < broadcast.getBroadcastMedias().size(); i++) {
                BroadcastMedia broadcastMedia = broadcast.getBroadcastMedias().get(i);
                MediaType mediaType = null;
                if(broadcastMedia.getType() == BroadcastMedia.TYPE_IMAGE){
                    mediaType = MediaType.IMAGE;
                }else if(broadcastMedia.getType() == BroadcastMedia.TYPE_VIDEO){
                    mediaType = MediaType.VIDEO;
                }
                leftMediaInfoMap.put(i, new MediaInfo(Uri.parse(NetDataUrls.getBroadcastMediaDataUrl(this, broadcastMedia.getMediaId())),
                        null, mediaType, -1, -1, -1,
                        mediaType == MediaType.VIDEO ? (broadcastMedia.getVideoDuration() == null ? -1 : broadcastMedia.getVideoDuration()) : -1,
                        -1, -1, -1, -1));
            }
        }
    }

    private void init(){
        binding.recyclerViewMedias.setLayoutManager(new GridLayoutManager(this, Constants.EDIT_BROADCAST_MEDIA_COLUMN_COUNT));
    }

    private ArrayList<MediaInfo> getSortedMediaInfoList(){
        ArrayList<MediaInfo> mediaInfoList = new ArrayList<>();
        for (int i = 0; i < leftMediaInfoMap.size() + addMediaInfoMap.size(); i++) {
            if(leftMediaInfoMap.get(i) != null){
                mediaInfoList.add(leftMediaInfoMap.get(i));
            }else if(addMediaInfoMap.get(i) != null){
                mediaInfoList.add(addMediaInfoMap.get(i));
            }
        }
        return mediaInfoList;
    }

    private void registerResultLauncher() {
        returnFromPreviewToSendMediaResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = Objects.requireNonNull(result.getData());
                        ArrayList<Media> medias = Objects.requireNonNull(data.getParcelableArrayListExtra(ExtraKeys.MEDIAS));
                        if(medias.isEmpty()){
                            binding.recyclerViewMedias.setVisibility(View.GONE);
                        }
                        Map<Integer, MediaInfo> leftMediasMap = new HashMap<>();
                        Map<Integer, MediaInfo> addMediasMap = new HashMap<>();
                        AtomicInteger index = new AtomicInteger();
                        medias.forEach(media -> {
                            for (int i = 0; i < leftMediaInfoMap.size() + addMediaInfoMap.size(); i++) {
                                if(leftMediaInfoMap.get(i) != null){
                                    if(media.getUri().equals(leftMediaInfoMap.get(i).getUri())){
                                        leftMediasMap.put(index.get(), leftMediaInfoMap.get(i));
                                        index.getAndIncrement();
                                    }
                                }else if(addMediaInfoMap.get(i) != null){
                                    if(media.getUri().equals(addMediaInfoMap.get(i).getUri())){
                                        addMediasMap.put(index.get(), addMediaInfoMap.get(i));
                                        index.getAndIncrement();
                                    }
                                }
                            }
                        });
                        leftMediaInfoMap.clear();
                        leftMediaInfoMap.putAll(leftMediasMap);
                        addMediaInfoMap.clear();
                        addMediaInfoMap.putAll(addMediasMap);
                        adapter.changeAllDataAndShow(getSortedMediaInfoList());
                    }
                }
        );
        addMediasResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = Objects.requireNonNull(result.getData());
                        boolean remove = data.getBooleanExtra(ExtraKeys.REMOVE, true);
                        Parcelable[] parcelableArrayExtra = Objects.requireNonNull(data.getParcelableArrayExtra(ExtraKeys.MEDIA_INFOS));
                        List<MediaInfo> mediaInfos = Utils.parseParcelableArray(parcelableArrayExtra);
                        for (MediaInfo mediaInfo : mediaInfos) {
                            if(mediaInfo.getMediaType().equals(MediaType.IMAGE)){
                                if(FileUtil.getFileSize(mediaInfo.getPath()) > Constants.MAX_BROADCAST_IMAGE_SIZE){
                                    MessageDisplayer.autoShow(this, "图片文件最大不能超过 " + FileUtil.formatFileSize(Constants.MAX_BROADCAST_IMAGE_SIZE), MessageDisplayer.Duration.LONG);
                                    return;
                                }
                            }else if(mediaInfo.getMediaType().equals(MediaType.VIDEO)){
                                if(FileUtil.getFileSize(mediaInfo.getPath()) > Constants.MAX_BROADCAST_VIDEO_SIZE){
                                    MessageDisplayer.autoShow(this, "视频文件最大不能超过 " + FileUtil.formatFileSize(Constants.MAX_BROADCAST_VIDEO_SIZE), MessageDisplayer.Duration.LONG);
                                    return;
                                }
                            }
                        }
                        onMediaInfosChosen(mediaInfos, remove);
                    }
                }
        );
    }

    private void showContent() {
        binding.textInput.setText(broadcast.getText());
        if(leftMediaInfoMap.size() + addMediaInfoMap.size() != 0){
            binding.recyclerViewMedias.setVisibility(View.VISIBLE);
            spaceGridDecorationSetter.setSpace(this, binding.recyclerViewMedias, Constants.EDIT_BROADCAST_MEDIA_COLUMN_COUNT,
                    Constants.GRID_SPACE_SEND_BROADCAST_DP, false, null, true);
            adapter = new EditBroadcastMediasRecyclerAdapter(this, returnFromPreviewToSendMediaResultLauncher, getSortedMediaInfoList(), true);
            binding.recyclerViewMedias.setAdapter(adapter);
        }else {
            binding.recyclerViewMedias.setVisibility(View.GONE);
        }
    }

    private void setupYiers() {
        binding.addMediaFab.setOnClickListener(v -> {
            AddBroadcastMediaBottomSheet bottomSheet = new AddBroadcastMediaBottomSheet(this);
            bottomSheet.show();
        });
        binding.textCounter.setText("0/" + Constants.MAX_BROADCAST_TEXT_LENGTH);
        binding.textInput.addTextChangedListener(new TextChangedYier(){
            private final int textColorNormal = binding.textCounter.getCurrentTextColor();

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                super.onTextChanged(s, start, before, count);
                binding.textCounter.setText(s.length() + "/" + Constants.MAX_BROADCAST_TEXT_LENGTH);
                if (s.length() > Constants.MAX_BROADCAST_TEXT_LENGTH) {
                    binding.textCounter.setTextColor(getColor(R.color.negative_red));
                } else {
                    binding.textCounter.setTextColor(textColorNormal);
                }
            }
        });
        binding.textInput.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus){
                if(!CollectionUtil.isEmpty(broadcast.getBroadcastMedias())){
                    binding.recyclerViewMedias.setVisibility(View.GONE);
                }
            }
        });
        binding.textInput.setOnClickListener(v -> {
            if(!CollectionUtil.isEmpty(broadcast.getBroadcastMedias())){
                binding.recyclerViewMedias.setVisibility(View.GONE);
            }
        });
        new KeyboardVisibilityYier(this).setYier(new KeyboardVisibilityYier.Yier() {

            @Override
            public void onKeyboardOpened() {
            }

            @Override
            public void onKeyboardClosed() {
                if(!CollectionUtil.isEmpty(broadcast.getBroadcastMedias())){
                    binding.recyclerViewMedias.setVisibility(View.VISIBLE);
                }
            }
        });
        binding.addMediaFab.setOnClickListener(v -> {
            AddBroadcastMediaBottomSheet bottomSheet = new AddBroadcastMediaBottomSheet(this);
            bottomSheet.setOnClickAddMediaYier(v1 -> {
                Intent intent = new Intent(this, ChooseMediasActivity.class);
                intent.putExtra(ExtraKeys.TOOLBAR_TITLE, "选择媒体");
                intent.putExtra(ExtraKeys.MENU_TITLE, "完成");
                intent.putExtra(ExtraKeys.RES_ID, R.drawable.check_24px);
                intent.putExtra(ExtraKeys.MEDIA_INFOS, getSortedMediaInfoList().toArray(new MediaInfo[0]));
                intent.putExtra(ExtraKeys.REMOVE, true);
                intent.putExtra(ExtraKeys.MAX_ALLOW_IMAGE_COUNT, Constants.MAX_BROADCAST_IMAGE_COUNT);
                intent.putExtra(ExtraKeys.MAX_ALLOW_VIDEO_COUNT, Constants.MAX_BROADCAST_VIDEO_COUNT);
                intent.putExtra(ExtraKeys.MAX_ALLOW_IMAGE_SIZE, Constants.MAX_BROADCAST_IMAGE_SIZE);
                intent.putExtra(ExtraKeys.MAX_ALLOW_VIDEO_SIZE, Constants.MAX_BROADCAST_VIDEO_SIZE);
                addMediasResultLauncher.launch(intent);
            });
            bottomSheet.setOnClickTakePhotoYier(v1 -> {
                Intent intent = new Intent(this, TakePhotoActivity.class);
                intent.putExtra(ExtraKeys.RES_ID, R.drawable.check_24px);
                intent.putExtra(ExtraKeys.MENU_TITLE, "完成");
                intent.putExtra(ExtraKeys.REMOVE, false);
                addMediasResultLauncher.launch(intent);
            });
            bottomSheet.setOnClickRecordVideoYier(v1 -> {
                Intent intent = new Intent(this, RecordVideoActivity.class);
                intent.putExtra(ExtraKeys.RES_ID, R.drawable.check_24px);
                intent.putExtra(ExtraKeys.MENU_TITLE, "完成");
                intent.putExtra(ExtraKeys.REMOVE, false);
                addMediasResultLauncher.launch(intent);
            });
            bottomSheet.show();
        });
        binding.editBroadcastButton.setOnClickListener(v -> {
            String newBroadcastText = UiUtil.getEditTextString(binding.textInput);
            if(newBroadcastText != null && newBroadcastText.isEmpty()) newBroadcastText = null;
            if(newBroadcastText != null && newBroadcastText.length() > Constants.MAX_BROADCAST_TEXT_LENGTH){
                MessageDisplayer.autoShow(this, "文字数量不合法", MessageDisplayer.Duration.SHORT);
                return;
            }
            List<Integer> addMediaTypes = new ArrayList<>();
            List<String> addMediaExtensions = new ArrayList<>();
            List<Integer> addMediaIndexes = new ArrayList<>();
            List<Uri> addMediaUris = new ArrayList<>();
            addMediaInfoMap.forEach((key, value) -> {
                if(value.getMediaType() == MediaType.IMAGE) {
                    addMediaTypes.add(BroadcastMedia.TYPE_IMAGE);
                }else if(value.getMediaType() == MediaType.VIDEO) {
                    addMediaTypes.add(BroadcastMedia.TYPE_VIDEO);
                }
                String fileExtensionFromUri = FileHelper.getFileExtensionFromUri(this, value.getUri());
                addMediaExtensions.add(fileExtensionFromUri);
                addMediaIndexes.add(key);
                addMediaUris.add(value.getUri());
            });
            if(newBroadcastText == null && addMediaInfoMap.size() + leftMediaInfoMap.size() == 0) {
                MessageDisplayer.autoShow(this, "没有内容", MessageDisplayer.Duration.SHORT);
                return;
            };
            Map<String, Integer> leftMedias = new HashMap<>();
            leftMediaInfoMap.forEach((key, value) -> {
                for (BroadcastMedia broadcastMedia : broadcast.getBroadcastMedias()) {
                    if(Uri.parse(NetDataUrls.getBroadcastMediaDataUrl(this, broadcastMedia.getMediaId())).equals(value.getUri())){
                        leftMedias.put(broadcastMedia.getMediaId(), key);
                        break;
                    }
                }
            });
            EditBroadcastPostBody postBody = new EditBroadcastPostBody(broadcast.getBroadcastId(), newBroadcastText, addMediaTypes, addMediaExtensions, addMediaIndexes, leftMedias);
            try {
                BroadcastApiCaller.editBroadcast(null, this, postBody, addMediaUris, new RetrofitApiCaller.CommonYier<OperationData>(this, false, true) {
                    @Override
                    public void start(Call<OperationData> call) {
                        super.start(call);
                        binding.editBroadcastButton.setVisibility(View.GONE);
                        binding.editIndicator.setVisibility(View.VISIBLE);
                        binding.editItemCountIndicator.setVisibility(View.VISIBLE);
                        UiUtil.setViewGroupEnabled(binding.content, false, true);
                    }

                    @Override
                    public void ok(OperationData data, Response<OperationData> raw, Call<OperationData> call) {
                        super.ok(data, raw, call);
                        data.commonHandleResult(EditBroadcastActivity.this, new int[]{-101, -102, -103, -104}, () -> {
                            MessageDisplayer.showToast(getContext(), "已编辑", Toast.LENGTH_SHORT);
                            finish();
                            Broadcast editedBroadcast = data.getData(Broadcast.class);
                            GlobalYiersHolder.getYiers(BroadcastUpdateYier.class).ifPresent(broadcastUpdateYiers -> {
                                broadcastUpdateYiers.forEach(broadcastUpdateYier -> broadcastUpdateYier.updateOneBroadcast(editedBroadcast));
                            });
                            ServerMessageServiceStompActions.updateRecentBroadcastMedias(EditBroadcastActivity.this, broadcast.getIchatId());
                        });
                    }

                    @Override
                    public void complete(Call<OperationData> call) {
                        super.complete(call);
                        binding.editBroadcastButton.setVisibility(View.VISIBLE);
                        binding.editIndicator.setVisibility(View.GONE);
                        binding.editItemCountIndicator.setVisibility(View.GONE);
                        UiUtil.setViewGroupEnabled(binding.content, true, true);
                    }
                }, (current, total, index, count) -> {
                    runOnUiThread(() -> {
                        int progress = (int)((current / (double) total) * binding.editIndicator.getMax());
                        binding.editIndicator.setProgress(progress, false);
                        if(index + 1 == count && progress == binding.editIndicator.getMax()) {
                            binding.editItemCountIndicator.setText("等待中");
                        }else {
                            binding.editItemCountIndicator.setText(String.valueOf(index + 1));
                        }
                    });
                });
            } catch (Exception e) {
                ErrorLogger.log(e);
                MessageDisplayer.autoShow(this, "出错了 > " + e.getMessage(), MessageDisplayer.Duration.SHORT);
            }
        });

        DragSortItemTouchCallback dragSortItemTouchCallback = new DragSortItemTouchCallback((from, to) -> {
            adapter.moveAndShow(from, to);
            leftMediaInfoMap.clear();
            addMediaInfoMap.clear();
            int index = 0;
            for (MediaInfo mediaInfo : adapter.getMediaInfoList()) {
                if(mediaInfo.getPath() == null){
                    leftMediaInfoMap.put(index ++, mediaInfo);
                }else {
                    addMediaInfoMap.put(index ++, mediaInfo);
                }
            }
        });
        new ItemTouchHelper(dragSortItemTouchCallback).attachToRecyclerView(binding.recyclerViewMedias);
    }

    private void onMediaInfosChosen(List<MediaInfo> mediaInfos, boolean remove) {
        if(remove) addMediaInfoMap.clear();
        int times = leftMediaInfoMap.size() + addMediaInfoMap.size() + mediaInfos.size();
        int nowAdd = 0;
        for (int i = 0; i < times; i++) {
            if(leftMediaInfoMap.get(i) != null || addMediaInfoMap.get(i) != null) continue;
            addMediaInfoMap.put(i, mediaInfos.get(nowAdd));
            nowAdd ++;
        }
        showContent();
    }
}