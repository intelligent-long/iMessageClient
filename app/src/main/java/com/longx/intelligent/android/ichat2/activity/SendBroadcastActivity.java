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
import com.longx.intelligent.android.ichat2.media.data.Media;
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
    private final ArrayList<Media> mediaList = new ArrayList<>();
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
                        Parcelable[] parcelableArrayExtra = Objects.requireNonNull(data.getParcelableArrayExtra(ExtraKeys.URIS));
                        List<Uri> uriList = Utils.parseParcelableArray(parcelableArrayExtra);
                        onImagesChosen(uriList, remove);
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
                        mediaList.clear();
                        mediaList.addAll(medias);
                        adapter.changeAllDataAndShow(medias);
                    }
                }
        );
    }

    private void initUi() {
        binding.recyclerViewMedias.setLayoutManager(new GridLayoutManager(this, MEDIA_COLUMN_COUNT));
        spaceGridDecorationSetter.setSpace(this, binding.recyclerViewMedias, MEDIA_COLUMN_COUNT, Constants.GRID_SPACE_SEND_BROADCAST_DP, false, null, true);
    }

    private void setupYiers() {
        binding.sendBroadcastButton.setOnClickListener(v -> {
            String broadcastText = UiUtil.getEditTextString(binding.textInput);
            if(broadcastText != null && broadcastText.isEmpty()) broadcastText = null;
            ArrayList<Integer> broadcastMediaTypes = new ArrayList<>();
            mediaList.forEach(media -> {
                if(media.getMediaType() == MediaType.IMAGE) {
                    broadcastMediaTypes.add(BroadcastMedia.TYPE_IMAGE);
                }else if(media.getMediaType() == MediaType.VIDEO) {
                    broadcastMediaTypes.add(BroadcastMedia.TYPE_VIDEO);
                }
            });
            ArrayList<String> broadcastMediaExtensions = new ArrayList<>();
            mediaList.forEach(media -> {
                String fileExtensionFromUri = FileHelper.getFileExtensionFromUri(this, media.getUri());
                broadcastMediaExtensions.add(fileExtensionFromUri);
            });
            SendBroadcastPostBody postBody = new SendBroadcastPostBody(broadcastText, broadcastMediaTypes, broadcastMediaExtensions);
            List<Uri> mediaUris = new ArrayList<>();
            mediaList.forEach(media -> {
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
        binding.addImageOrVideoFab.setOnClickListener(v -> {
            AddBroadcastMediaBottomSheet bottomSheet = new AddBroadcastMediaBottomSheet(this);
            bottomSheet.setOnClickAddMediaYier(v1 -> {
                Intent intent = new Intent(this, ChooseMediasActivity.class);
                intent.putExtra(ExtraKeys.TOOLBAR_TITLE, "选择媒体");
                intent.putExtra(ExtraKeys.MENU_TITLE, "完成");
                intent.putExtra(ExtraKeys.RES_ID, R.drawable.check_24px);
                List<Uri> uris = new ArrayList<>();
                mediaList.forEach(media -> {
                    if(media.getMediaType() == MediaType.IMAGE) {
                        uris.add(media.getUri());
                    }
                });
                intent.putExtra(ExtraKeys.URIS, uris.toArray(new Uri[0]));
                intent.putExtra(ExtraKeys.REMOVE, true);
                intent.putExtra(ExtraKeys.MAX_ALLOW_SIZE, Constants.MAX_BROADCAST_IMAGE_COUNT);
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

    private void onImagesChosen(List<Uri> uriList, boolean remove) {
        List<Media> imageMediaList = new ArrayList<>();
        uriList.forEach(uri -> {
            Media media = new Media(MediaType.IMAGE, uri);
            imageMediaList.add(media);
        });
        if(imageMediaList.size() > Constants.MAX_BROADCAST_IMAGE_COUNT){
            MessageDisplayer.autoShow(this, "最多附带 " + Constants.MAX_BROADCAST_IMAGE_COUNT + " 张图片", MessageDisplayer.Duration.LONG);
            return;
        }
        if(remove) mediaList.clear();
        mediaList.addAll(imageMediaList);
        showImages();
    }

    private void showImages(){
        if(mediaList.isEmpty()){
            binding.recyclerViewMedias.setVisibility(View.GONE);
        }else {
            binding.recyclerViewMedias.setVisibility(View.VISIBLE);
            spaceGridDecorationSetter.setSpace(this, binding.recyclerViewMedias, MEDIA_COLUMN_COUNT, Constants.GRID_SPACE_SEND_BROADCAST_DP, false, null, true);
            adapter = new SendBroadcastMediasRecyclerAdapter(this, returnFromPreviewToSendMediaResultLauncher, mediaList);
            binding.recyclerViewMedias.setAdapter(adapter);
        }
    }

    private void onVideosChosen(List<Uri> uriList){

    }
}