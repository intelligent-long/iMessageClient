package com.longx.intelligent.android.ichat2.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.recyclerview.widget.GridLayoutManager;

import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.activity.helper.BaseActivity;
import com.longx.intelligent.android.ichat2.adapter.SendBroadcastMediasRecyclerAdapter;
import com.longx.intelligent.android.ichat2.behavior.MessageDisplayer;
import com.longx.intelligent.android.ichat2.bottomsheet.AddBroadcastMediaBottomSheet;
import com.longx.intelligent.android.ichat2.da.FileHelper;
import com.longx.intelligent.android.ichat2.data.BroadcastMedia;
import com.longx.intelligent.android.ichat2.data.request.SendBroadcastPostBody;
import com.longx.intelligent.android.ichat2.data.response.OperationStatus;
import com.longx.intelligent.android.ichat2.databinding.ActivitySendBroadcastBinding;
import com.longx.intelligent.android.ichat2.media.MediaType;
import com.longx.intelligent.android.ichat2.media.data.MediaInfo;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.BroadcastApiCaller;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.ichat2.util.UiUtil;
import com.longx.intelligent.android.ichat2.util.Utils;
import com.longx.intelligent.android.ichat2.value.Constants;
import com.longx.intelligent.android.ichat2.yier.BroadcastReloadYier;
import com.longx.intelligent.android.ichat2.yier.GlobalYiersHolder;
import com.longx.intelligent.android.lib.recyclerview.decoration.SpaceGridDecorationSetter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Response;

public class SendBroadcastActivity extends BaseActivity {
    private ActivitySendBroadcastBinding binding;
    private ActivityResultLauncher<Intent> addMediasResultLauncher;
    private ActivityResultLauncher<Intent> returnFromPreviewToSendMediaResultLauncher;
    private final ArrayList<MediaInfo> mediaInfoList = new ArrayList<>();
    private static final int MEDIA_COLUMN_COUNT = 3;
    private SendBroadcastMediasRecyclerAdapter adapter;
    private final SpaceGridDecorationSetter spaceGridDecorationSetter = new SpaceGridDecorationSetter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySendBroadcastBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupCloseBackNavigation(binding.toolbar);
        initResultLauncher();
        initUi();
        setupYiers();
    }

    private void initResultLauncher() {
        addMediasResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = Objects.requireNonNull(result.getData());
                        boolean remove = data.getBooleanExtra(ExtraKeys.REMOVE, true);
                        Parcelable[] parcelableArrayExtra = Objects.requireNonNull(data.getParcelableArrayExtra(ExtraKeys.MEDIA_INFOS));
                        List<MediaInfo> uriList = Utils.parseParcelableArray(parcelableArrayExtra);
                        onMediaInfosChosen(uriList, remove);
                    }
                }
        );
        returnFromPreviewToSendMediaResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = Objects.requireNonNull(result.getData());
                        ArrayList<MediaInfo> mediaInfos = Objects.requireNonNull(data.getParcelableArrayListExtra(ExtraKeys.MEDIA_INFOS));
                        if(mediaInfos.isEmpty()){
                            binding.recyclerViewMedias.setVisibility(View.GONE);
                        }
                        mediaInfoList.clear();
                        mediaInfoList.addAll(mediaInfos);
                        adapter.changeAllDataAndShow(mediaInfos);
                    }
                }
        );
    }

    private void initUi() {
        binding.recyclerViewMedias.setLayoutManager(new GridLayoutManager(this, MEDIA_COLUMN_COUNT));
    }

    private void setupYiers() {
        binding.sendBroadcastButton.setOnClickListener(v -> {
            String broadcastText = UiUtil.getEditTextString(binding.textInput);
            if(broadcastText != null && broadcastText.isEmpty()) broadcastText = null;
            ArrayList<Integer> broadcastMediaTypes = new ArrayList<>();
            mediaInfoList.forEach(media -> {
                if(media.getMediaType() == MediaType.IMAGE) {
                    broadcastMediaTypes.add(BroadcastMedia.TYPE_IMAGE);
                }else if(media.getMediaType() == MediaType.VIDEO) {
                    broadcastMediaTypes.add(BroadcastMedia.TYPE_VIDEO);
                }
            });
            ArrayList<String> broadcastMediaExtensions = new ArrayList<>();
            mediaInfoList.forEach(media -> {
                String fileExtensionFromUri = FileHelper.getFileExtensionFromUri(this, media.getUri());
                broadcastMediaExtensions.add(fileExtensionFromUri);
            });
            SendBroadcastPostBody postBody = new SendBroadcastPostBody(broadcastText, broadcastMediaTypes, broadcastMediaExtensions);
            List<Uri> mediaUris = new ArrayList<>();
            mediaInfoList.forEach(media -> {
                mediaUris.add(media.getUri());
            });
            if(broadcastText == null && mediaUris.isEmpty()) {
                MessageDisplayer.autoShow(this, "没有内容", MessageDisplayer.Duration.SHORT);
                return;
            };
            BroadcastApiCaller.sendBroadcast(null, this, postBody, mediaUris, new RetrofitApiCaller.CommonYier<OperationStatus>(this, false, true) {
                @Override
                public void start(Call<OperationStatus> call) {
                    super.start(call);
                    binding.sendBroadcastButton.setVisibility(View.GONE);
                    binding.sendIndicator.setVisibility(View.VISIBLE);
                    binding.sendItemCountIndicator.setVisibility(View.VISIBLE);
                    UiUtil.setViewGroupEnabled(binding.content, false, true);
                }

                @Override
                public void ok(OperationStatus data, Response<OperationStatus> row, Call<OperationStatus> call) {
                    super.ok(data, row, call);
                    data.commonHandleResult(SendBroadcastActivity.this, new int[]{-101, -102}, () -> {
                        GlobalYiersHolder.getYiers(BroadcastReloadYier.class).ifPresent(broadcastReloadYiers -> {
                            broadcastReloadYiers.forEach(BroadcastReloadYier::onBroadcastReload);
                        });
                        MessageDisplayer.showToast(getContext(), "已发送", Toast.LENGTH_SHORT);
                        finish();
                    });
                }

                @Override
                public void complete(Call<OperationStatus> call) {
                    super.complete(call);
                    binding.sendBroadcastButton.setVisibility(View.VISIBLE);
                    binding.sendIndicator.setVisibility(View.GONE);
                    binding.sendItemCountIndicator.setVisibility(View.GONE);
                    UiUtil.setViewGroupEnabled(binding.content, true, true);
                }
            }, (current, total, index) -> {
                runOnUiThread(() -> {
                    int progress = (int)((current / (double) total) * binding.sendIndicator.getMax());
                    binding.sendIndicator.setProgress(progress, false);
                    int indexShow = index + 1;
                    if(indexShow == mediaUris.size() && progress == binding.sendIndicator.getMax()) {
                        binding.sendItemCountIndicator.setText("等待中");
                    }else {
                        binding.sendItemCountIndicator.setText(String.valueOf(indexShow));
                    }
                });
            });
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
                intent.putExtra(ExtraKeys.MAX_ALLOW_IMAGE_SIZE, Constants.MAX_BROADCAST_IMAGE_COUNT);
                addMediasResultLauncher.launch(intent);
            });
            bottomSheet.setOnClickTakePhotoYier(v1 -> {
                Intent intent = new Intent(this, TakePhotoActivity.class);
                intent.putExtra(ExtraKeys.RES_ID, R.drawable.check_24px);
                intent.putExtra(ExtraKeys.MENU_TITLE, "完成");
                intent.putExtra(ExtraKeys.REMOVE, false);
                addMediasResultLauncher.launch(intent);
            });
            bottomSheet.show();
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
            spaceGridDecorationSetter.setSpace(this, binding.recyclerViewMedias, MEDIA_COLUMN_COUNT, Constants.GRID_SPACE_SEND_BROADCAST_DP, false, null, true);
            adapter = new SendBroadcastMediasRecyclerAdapter(this, returnFromPreviewToSendMediaResultLauncher, mediaInfoList);
            binding.recyclerViewMedias.setAdapter(adapter);
        }
    }
}