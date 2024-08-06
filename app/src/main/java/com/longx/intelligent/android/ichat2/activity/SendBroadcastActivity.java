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
import com.longx.intelligent.android.ichat2.bottomsheet.AddBroadcastImageOrVideoBottomSheet;
import com.longx.intelligent.android.ichat2.da.FileHelper;
import com.longx.intelligent.android.ichat2.data.BroadcastMedia;
import com.longx.intelligent.android.ichat2.data.request.SendBroadcastPostBody;
import com.longx.intelligent.android.ichat2.data.response.OperationStatus;
import com.longx.intelligent.android.ichat2.databinding.ActivitySendBroadcastBinding;
import com.longx.intelligent.android.ichat2.media.MediaType;
import com.longx.intelligent.android.ichat2.media.data.Media;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.BroadcastApiCaller;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.ichat2.util.ErrorLogger;
import com.longx.intelligent.android.ichat2.util.UiUtil;
import com.longx.intelligent.android.ichat2.util.Utils;
import com.longx.intelligent.android.ichat2.value.Constants;
import com.longx.intelligent.android.ichat2.yier.BroadcastReloadYier;
import com.longx.intelligent.android.ichat2.yier.GlobalYiersHolder;
import com.longx.intelligent.android.lib.recyclerview.decoration.SpaceGridDecorationSetter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Response;

public class SendBroadcastActivity extends BaseActivity {
    private ActivitySendBroadcastBinding binding;
    private ActivityResultLauncher<Intent> addImageResultLauncher;
    private ActivityResultLauncher<Intent> returnFromPreviewToSendMediaResultLauncher;
    private final ArrayList<Media> mediaList = new ArrayList<>();
    private static final int MEDIA_COLUMN_COUNT = 3;
    private SendBroadcastMediasRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySendBroadcastBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupCloseBackNavigation(binding.toolbar);
        initResultLauncher();
        setupYiers();
    }

    private void initResultLauncher() {
        addImageResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = Objects.requireNonNull(result.getData());
                        Parcelable[] parcelableArrayExtra = Objects.requireNonNull(data.getParcelableArrayExtra(ExtraKeys.URIS));
                        List<Uri> uriList = Utils.parseParcelableArray(parcelableArrayExtra);
                        AtomicInteger imageSize = new AtomicInteger();
                        mediaList.forEach(media -> {
                            if(media.getMediaType() == MediaType.IMAGE) imageSize.getAndIncrement();
                        });
                        if(imageSize.get() + uriList.size() > Constants.MAX_BROADCAST_IMAGE_COUNT){
                            MessageDisplayer.autoShow(this, "最多附带 " + Constants.MAX_BROADCAST_IMAGE_COUNT + " 张图片", MessageDisplayer.Duration.LONG);
                        }else {
                            onImagesChosen(uriList);
                        }
                    }
                }
        );
        returnFromPreviewToSendMediaResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    ErrorLogger.log(result.getResultCode());
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

    private void setupYiers() {
        binding.sendBroadcastButton.setOnClickListener(v -> {
            String broadcastText = UiUtil.getEditTextString(binding.textInput);
            if(broadcastText == null || broadcastText.isEmpty()) {
                MessageDisplayer.autoShow(this, "没有内容", MessageDisplayer.Duration.SHORT);
                return;
            };
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
            BroadcastApiCaller.sendBroadcast(this, this, postBody, mediaUris, new RetrofitApiCaller.CommonYier<OperationStatus>(this){
                @Override
                public void ok(OperationStatus data, Response<OperationStatus> row, Call<OperationStatus> call) {
                    super.ok(data, row, call);
                    data.commonHandleResult(SendBroadcastActivity.this, new int[]{-101, -102}, () -> {
                        GlobalYiersHolder.getYiers(BroadcastReloadYier.class).ifPresent(broadcastReloadYiers -> {
                            broadcastReloadYiers.forEach(BroadcastReloadYier::onBroadcastReload);
                        });
                        MessageDisplayer.showToast(getContext(), "广播已发送", Toast.LENGTH_SHORT);
                        finish();
                    });
                }
            });
        });
        binding.addImageOrVideoFab.setOnClickListener(v -> {
            AddBroadcastImageOrVideoBottomSheet bottomSheet = new AddBroadcastImageOrVideoBottomSheet(this);
            bottomSheet.setOnClickAddImageYier(v1 -> {
                Intent intent = new Intent(this, ChooseImagesActivity.class);
                intent.putExtra(ExtraKeys.TOOLBAR_TITLE, "选择图片");
                intent.putExtra(ExtraKeys.MENU_TITLE, "完成");
                intent.putExtra(ExtraKeys.RES_ID, R.drawable.check_24px);
                List<Uri> uris = new ArrayList<>();
                mediaList.forEach(media -> {
                    if(media.getMediaType() == MediaType.IMAGE) {
                        uris.add(media.getUri());
                    }
                });
                intent.putExtra(ExtraKeys.URIS, uris.toArray(new Uri[0]));
                intent.putExtra(ExtraKeys.MAX_ALLOW_SIZE, Constants.MAX_BROADCAST_IMAGE_COUNT);
                addImageResultLauncher.launch(intent);
            });
            bottomSheet.show();
        });
    }

    private void onImagesChosen(List<Uri> uriList) {
        List<Media> toAdds = new ArrayList<>();
        uriList.forEach(uri -> {
            Media media = new Media(MediaType.IMAGE, uri);
            if(!mediaList.contains(media)) toAdds.add(media);
        });
        List<Media> toRemoves = new ArrayList<>();
        mediaList.forEach(media -> {
            if(!uriList.contains(media.getUri())) toRemoves.add(media);
        });
        mediaList.addAll(toAdds);
        mediaList.removeAll(toRemoves);
        showImages();
    }

    private void showImages(){
        if(mediaList.isEmpty()){
            binding.recyclerViewMedias.setVisibility(View.GONE);
        }else {
            binding.recyclerViewMedias.setVisibility(View.VISIBLE);
            binding.recyclerViewMedias.setLayoutManager(new GridLayoutManager(this, MEDIA_COLUMN_COUNT));
            new SpaceGridDecorationSetter().setSpace(this, binding.recyclerViewMedias, MEDIA_COLUMN_COUNT, Constants.GRID_SPACE_SEND_BROADCAST_DP, false, null, true);
            adapter = new SendBroadcastMediasRecyclerAdapter(this, returnFromPreviewToSendMediaResultLauncher, mediaList);
            binding.recyclerViewMedias.setAdapter(adapter);
        }
    }
}