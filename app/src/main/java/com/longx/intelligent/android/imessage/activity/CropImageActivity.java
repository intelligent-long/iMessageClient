package com.longx.intelligent.android.imessage.activity;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.canhub.cropper.CropImageView;
import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.imessage.activity.helper.BaseActivity;
import com.longx.intelligent.android.imessage.behaviorcomponents.MessageDisplayer;
import com.longx.intelligent.android.imessage.da.SharedImageViewModel;
import com.longx.intelligent.android.imessage.databinding.ActivityCropImageBinding;
import com.longx.intelligent.android.imessage.dialog.OperatingDialog;

public class CropImageActivity extends BaseActivity {
    private ActivityCropImageBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCropImageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupDefaultBackNavigation(binding.toolbar);
        setupYiers();
        setupToolbar();
        setupCropImageView();
    }

    private void setupToolbar() {
        binding.toolbar.setOnMenuItemClickListener(item -> {
            if(item.getItemId() == R.id.rotate){
                binding.cropImageView.rotateImage(90);
            }else if(item.getItemId() == R.id.crop){
                new Thread(() -> {
                    OperatingDialog operatingDialog = new OperatingDialog(this);
                    runOnUiThread(() -> operatingDialog.create().show());
                    Bitmap croppedImage = binding.cropImageView.getCroppedImage();
                    if(croppedImage == null){
                        runOnUiThread(operatingDialog::dismiss);
                        MessageDisplayer.autoShow(this, "错误", MessageDisplayer.Duration.SHORT);
                    }else {
                        runOnUiThread(operatingDialog::dismiss);
                        onImageCropped(croppedImage);
                    }
                }).start();
            }
            return true;
        });
    }

    private void setupCropImageView() {
        String uriString = getIntent().getStringExtra(ExtraKeys.URI);
        Uri uri = Uri.parse(uriString);
        binding.cropImageView.setCropShape(CropImageView.CropShape.RECTANGLE);
        binding.cropImageView.setAspectRatio(1, 1);
        binding.cropImageView.setImageUriAsync(uri);
    }

    private void setupYiers() {
        binding.cropImageView.setOnCropImageCompleteListener((view, result) -> {
            if(result.isSuccessful()) {
                binding.cropImageView.setImageUriAsync(result.getUriContent());
            }else {
                MessageDisplayer.autoShow(this, "错误", MessageDisplayer.Duration.SHORT);
            }
        });
    }

    private void onImageCropped(Bitmap bitmap){
        runOnUiThread(() -> {
            SharedImageViewModel viewModel = new ViewModelProvider(
                    getApplicationContext() instanceof ViewModelStoreOwner
                            ? (ViewModelStoreOwner) getApplicationContext()
                            : this // fallback
            ).get(SharedImageViewModel.class);
            viewModel.setImage(bitmap);
            setResult(RESULT_OK);
            finish();
        });
    }
}