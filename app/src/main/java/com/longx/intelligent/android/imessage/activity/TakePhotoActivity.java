package com.longx.intelligent.android.imessage.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.FileProvider;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.imessage.activity.helper.BaseActivity;
import com.longx.intelligent.android.imessage.behaviorcomponents.MessageDisplayer;
import com.longx.intelligent.android.imessage.da.FileHelper;
import com.longx.intelligent.android.imessage.da.publicfile.PublicFileAccessor;
import com.longx.intelligent.android.imessage.databinding.ActivityTakePhotoBinding;
import com.longx.intelligent.android.imessage.media.data.MediaInfo;
import com.longx.intelligent.android.imessage.media.helper.MediaStoreHelper;
import com.longx.intelligent.android.imessage.util.ColorUtil;
import com.longx.intelligent.android.imessage.util.ErrorLogger;
import com.longx.intelligent.android.imessage.util.WindowAndSystemUiUtil;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class TakePhotoActivity extends BaseActivity {
    private ActivityTakePhotoBinding binding;
    private ActivityResultLauncher<Intent> takePictureLauncher;
    private Uri photoUri;
    private File photoFile;
    private boolean purePhoto;
    private boolean remove;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTakePhotoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        WindowAndSystemUiUtil.extendContentUnderSystemBars(this, null, null,
                ColorUtil.getAttrColor(this, com.google.android.material.R.attr.colorSurfaceContainer));
        setupBackNavigation(binding.toolbar, getColor(R.color.white));
        binding.appBar.bringToFront();
        intentData();
        takePictureLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        photoFile = FileHelper.detectAndRenameFile(photoFile);
                        if(photoFile == null){
                            MessageDisplayer.autoShow(this, "创建文件失败", MessageDisplayer.Duration.LONG);
                        }else {
                            getPhotoUri(photoFile);
                            showContent();
                            setupYiers();
                        }
                    }else {
                        photoFile.delete();
                        finish();
                    }
                }
        );
        dispatchTakePictureIntent();
    }

    private void intentData() {
        remove = getIntent().getBooleanExtra(ExtraKeys.REMOVE, false);
        int actionIconResId = getIntent().getIntExtra(ExtraKeys.RES_ID, -1);
        String menuTitle = getIntent().getStringExtra(ExtraKeys.MENU_TITLE);
        MenuItem item = binding.toolbar.getMenu().findItem(R.id.action);
        item.setIcon(actionIconResId);
        item.setTitle(menuTitle);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            try {
                photoFile = createPhotoFile();
                getPhotoUri(photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                takePictureLauncher.launch(takePictureIntent);
            } catch (IOException e) {
                ErrorLogger.log(getClass(), e);
                MessageDisplayer.autoShow(this, "创建文件失败", MessageDisplayer.Duration.LONG);
            }
        }
    }

    private void getPhotoUri(File file) {
        photoUri = FileProvider.getUriForFile(this,
                getApplicationContext().getPackageName() + ".provider",
                file);
    }

    private File createPhotoFile() throws IOException {
        return PublicFileAccessor.CapturedMedia.createPhotoFile(this);
    }

    private void showContent() {
        setupPhotoView();
        showPhoto();
    }

    private void setupPhotoView() {
        binding.photo.setOnClickListener(v -> {
            setPurePhoto(!purePhoto);
        });
        binding.photo.setOnStateChangedListener(new SubsamplingScaleImageView.OnStateChangedListener() {
            @Override
            public void onScaleChanged(float newScale, int origin) {
                setPurePhoto(true);
            }

            @Override
            public void onCenterChanged(PointF newCenter, int origin) {

            }
        });
    }

    private void showPhoto() {
        binding.photo.setImage(ImageSource.uri(Uri.fromFile(photoFile)));
    }

    private void setPurePhoto(boolean purePhoto) {
        if(purePhoto){
            binding.appBar.setVisibility(View.GONE);
            WindowAndSystemUiUtil.setSystemUIShown(this, false);
            this.purePhoto = true;
        }else {
            binding.appBar.setVisibility(View.VISIBLE);
            WindowAndSystemUiUtil.setSystemUIShown(this, true);
            this.purePhoto = false;
        }
    }

    private void setupYiers() {
        binding.toolbar.setOnMenuItemClickListener(item -> {
            if(item.getItemId() == R.id.action){
                Intent intent = new Intent();
                MediaInfo mediaInfoFromUri = MediaStoreHelper.getMediaInfoFromUri(this,
                        Objects.requireNonNull(MediaStoreHelper.getContentUriFromFileUri(this, Uri.fromFile(photoFile))));
                intent.putExtra(ExtraKeys.MEDIA_INFOS, new MediaInfo[]{mediaInfoFromUri});
                intent.putExtra(ExtraKeys.REMOVE, remove);
                setResult(RESULT_OK, intent);
                finish();
            }
            return true;
        });
    }
}