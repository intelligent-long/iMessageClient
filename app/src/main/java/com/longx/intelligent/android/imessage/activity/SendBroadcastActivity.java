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
import com.longx.intelligent.android.imessage.data.BroadcastPermission;
import com.longx.intelligent.android.imessage.data.response.OperationData;
import com.longx.intelligent.android.imessage.behaviorcomponents.MessageDisplayer;
import com.longx.intelligent.android.imessage.bottomsheet.AddBroadcastMediaBottomSheet;
import com.longx.intelligent.android.imessage.da.FileHelper;
import com.longx.intelligent.android.imessage.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.imessage.data.BroadcastMedia;
import com.longx.intelligent.android.imessage.data.request.SendBroadcastPostBody;
import com.longx.intelligent.android.imessage.databinding.ActivitySendBroadcastBinding;
import com.longx.intelligent.android.imessage.media.MediaType;
import com.longx.intelligent.android.imessage.media.data.Media;
import com.longx.intelligent.android.imessage.media.data.MediaInfo;
import com.longx.intelligent.android.imessage.net.retrofit.caller.BroadcastApiCaller;
import com.longx.intelligent.android.imessage.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.imessage.net.stomp.ServerMessageServiceStompActions;
import com.longx.intelligent.android.imessage.util.ErrorLogger;
import com.longx.intelligent.android.imessage.util.FileUtil;
import com.longx.intelligent.android.imessage.util.UiUtil;
import com.longx.intelligent.android.imessage.util.Utils;
import com.longx.intelligent.android.imessage.value.Constants;
import com.longx.intelligent.android.imessage.yier.BroadcastReloadYier;
import com.longx.intelligent.android.imessage.yier.GlobalYiersHolder;
import com.longx.intelligent.android.imessage.yier.KeyboardVisibilityYier;
import com.longx.intelligent.android.imessage.yier.TextChangedYier;
import com.longx.intelligent.android.lib.recyclerview.decoration.SpaceGridDecorationSetter;
import com.longx.intelligent.android.lib.recyclerview.dragsort.DragSortItemTouchCallback;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Response;

public class SendBroadcastActivity extends BaseActivity {
    private ActivitySendBroadcastBinding binding;
    private ActivityResultLauncher<Intent> addMediasResultLauncher;
    private ActivityResultLauncher<Intent> returnFromPreviewToSendMediaResultLauncher;
    private ArrayList<MediaInfo> mediaInfoList = new ArrayList<>();
    private EditBroadcastMediasRecyclerAdapter adapter;
    private final SpaceGridDecorationSetter spaceGridDecorationSetter = new SpaceGridDecorationSetter();
    private BroadcastPermission broadcastPermission = new BroadcastPermission(null, BroadcastPermission.PUBLIC, new HashSet<>());
    private ActivityResultLauncher<Intent> permissionResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySendBroadcastBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupCloseBackNavigation(binding.toolbar);
        registerResultLauncher();
        initUi();
        setupYiers();
    }

    private void registerResultLauncher() {
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
        returnFromPreviewToSendMediaResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = Objects.requireNonNull(result.getData());
                        ArrayList<Media> medias = Objects.requireNonNull(data.getParcelableArrayListExtra(ExtraKeys.MEDIAS));
                        if(medias.isEmpty()){
                            binding.recyclerViewMedias.setVisibility(View.GONE);
                        }
                        ArrayList<MediaInfo> mediaInfos = new ArrayList<>();
                        medias.forEach(media -> {
                            mediaInfoList.forEach(mediaInfo -> {
                                if(media.getUri().equals(mediaInfo.getUri())){
                                    mediaInfos.add(mediaInfo);
                                }
                            });
                        });
                        mediaInfoList.clear();
                        mediaInfoList.addAll(mediaInfos);
                        adapter.changeAllDataAndShow(mediaInfos);
                    }
                }
        );
        permissionResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            broadcastPermission = data.getParcelableExtra(ExtraKeys.BROADCAST_PERMISSION);
                        }
                    }
                }
        );
    }

    private void initUi() {
        binding.recyclerViewMedias.setLayoutManager(new GridLayoutManager(this, Constants.EDIT_BROADCAST_MEDIA_COLUMN_COUNT));
    }

    private void setupYiers() {
        binding.sendBroadcastButton.setOnClickListener(v -> {
            String broadcastText = UiUtil.getEditTextString(binding.textInput);
            if(broadcastText != null && broadcastText.isEmpty()) broadcastText = null;
            if(broadcastText != null && broadcastText.length() > Constants.MAX_BROADCAST_TEXT_LENGTH){
                MessageDisplayer.autoShow(this, "文字数量不合法", MessageDisplayer.Duration.SHORT);
                return;
            }
            ArrayList<Integer> broadcastMediaTypes = new ArrayList<>();
            ArrayList<String> broadcastMediaExtensions = new ArrayList<>();
            List<Uri> mediaUris = new ArrayList<>();
            mediaInfoList.forEach(mediaInfo -> {
                if(mediaInfo.getMediaType() == MediaType.IMAGE) {
                    broadcastMediaTypes.add(BroadcastMedia.TYPE_IMAGE);
                }else if(mediaInfo.getMediaType() == MediaType.VIDEO) {
                    broadcastMediaTypes.add(BroadcastMedia.TYPE_VIDEO);
                }
                String fileExtensionFromUri = FileHelper.getFileExtensionFromUri(this, mediaInfo.getUri());
                broadcastMediaExtensions.add(fileExtensionFromUri);
                mediaUris.add(mediaInfo.getUri());
            });
            if(broadcastText == null && mediaUris.isEmpty()) {
                MessageDisplayer.autoShow(this, "没有内容", MessageDisplayer.Duration.SHORT);
                return;
            };
            SendBroadcastPostBody postBody = new SendBroadcastPostBody(broadcastText, broadcastMediaTypes, broadcastMediaExtensions, broadcastPermission);
            try {
                BroadcastApiCaller.sendBroadcast(null, this, postBody, mediaUris, new RetrofitApiCaller.CommonYier<OperationData>(this, false, true) {
                    @Override
                    public void start(Call<OperationData> call) {
                        super.start(call);
                        binding.sendBroadcastButton.setVisibility(View.GONE);
                        binding.sendIndicator.setVisibility(View.VISIBLE);
                        binding.sendItemCountIndicator.setVisibility(View.VISIBLE);
                        UiUtil.setViewGroupEnabled(binding.content, false, true);
                    }

                    @Override
                    public void ok(OperationData data, Response<OperationData> raw, Call<OperationData> call) {
                        super.ok(data, raw, call);
                        data.commonHandleResult(SendBroadcastActivity.this, new int[]{-101, -102, -103, -104, -105}, () -> {
                            MessageDisplayer.showToast(getContext(), "已发送", Toast.LENGTH_SHORT);
                            GlobalYiersHolder.getYiers(BroadcastReloadYier.class).ifPresent(broadcastReloadYiers -> {
                                broadcastReloadYiers.forEach(BroadcastReloadYier::reloadBroadcast);
                            });
                            ServerMessageServiceStompActions.updateRecentBroadcastMedias(getContext(), SharedPreferencesAccessor.UserProfilePref.getCurrentUserProfile(getContext()).getIchatId());
                            finish();
                        });
                    }

                    @Override
                    public void complete(Call<OperationData> call) {
                        super.complete(call);
                        binding.sendBroadcastButton.setVisibility(View.VISIBLE);
                        binding.sendIndicator.setVisibility(View.GONE);
                        binding.sendItemCountIndicator.setVisibility(View.GONE);
                        UiUtil.setViewGroupEnabled(binding.content, true, true);
                    }
                }, (current, total, index, count) -> {
                    runOnUiThread(() -> {
                        int progress = (int)((current / (double) total) * binding.sendIndicator.getMax());
                        binding.sendIndicator.setProgress(progress, false);
                        if(index + 1 == count && progress == binding.sendIndicator.getMax()) {
                            binding.sendItemCountIndicator.setText("等待中");
                        }else {
                            binding.sendItemCountIndicator.setText(String.valueOf(index + 1));
                        }
                    });
                });
            } catch (Exception e) {
                ErrorLogger.log(e);
                MessageDisplayer.autoShow(this, "出错了 > " + e.getMessage(), MessageDisplayer.Duration.SHORT);
            }
        });
        binding.addMediaFab.setOnClickListener(v -> {
            AddBroadcastMediaBottomSheet bottomSheet = new AddBroadcastMediaBottomSheet(this);
            bottomSheet.setOnClickAddMediaYier(v1 -> {
                Intent intent = new Intent(this, ChooseMediasActivity.class);
                intent.putExtra(ExtraKeys.TOOLBAR_TITLE, "选择媒体");
                intent.putExtra(ExtraKeys.MENU_TITLE, "完成");
                intent.putExtra(ExtraKeys.RES_ID, R.drawable.check_24px);
                intent.putExtra(ExtraKeys.MEDIA_INFOS, mediaInfoList.toArray(new MediaInfo[0]));
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
                if(!mediaInfoList.isEmpty()){
                    binding.recyclerViewMedias.setVisibility(View.GONE);
                }
            }
        });
        binding.textInput.setOnClickListener(v -> {
            if(!mediaInfoList.isEmpty()){
                binding.recyclerViewMedias.setVisibility(View.GONE);
            }
        });
        new KeyboardVisibilityYier(this).setYier(new KeyboardVisibilityYier.Yier() {

            @Override
            public void onKeyboardOpened() {
            }

            @Override
            public void onKeyboardClosed() {
                if(!mediaInfoList.isEmpty()){
                    binding.recyclerViewMedias.setVisibility(View.VISIBLE);
                }
            }
        });

        DragSortItemTouchCallback dragSortItemTouchCallback = new DragSortItemTouchCallback((from, to) -> {
            adapter.moveAndShow(from, to);
            mediaInfoList = new ArrayList<>(adapter.getMediaInfoList());
        });
        new ItemTouchHelper(dragSortItemTouchCallback).attachToRecyclerView(binding.recyclerViewMedias);

        binding.clickViewBroadcastPermission.setOnClickListener(v -> {
            Intent intent = new Intent(this, BroadcastPermissionActivity.class);
            intent.putExtra(ExtraKeys.BROADCAST_PERMISSION, broadcastPermission);
            permissionResultLauncher.launch(intent);
        });
    }

    private void onMediaInfosChosen(List<MediaInfo> mediaInfos, boolean remove) {
        if(remove) mediaInfoList.clear();
        mediaInfoList.addAll(mediaInfos);
        showMedias();
    }

    private void showMedias(){
        if(mediaInfoList.isEmpty()){
            binding.recyclerViewMedias.setVisibility(View.GONE);
        }else {
            binding.recyclerViewMedias.setVisibility(View.VISIBLE);
            spaceGridDecorationSetter.setSpace(this, binding.recyclerViewMedias, Constants.EDIT_BROADCAST_MEDIA_COLUMN_COUNT, 
                    Constants.GRID_SPACE_SEND_BROADCAST_DP, false, null, true);
            adapter = new EditBroadcastMediasRecyclerAdapter(this, returnFromPreviewToSendMediaResultLauncher, mediaInfoList, false);
            binding.recyclerViewMedias.setAdapter(adapter);
        }
    }
}