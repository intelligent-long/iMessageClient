package com.longx.intelligent.android.ichat2.activity.edituser;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import com.canhub.cropper.CropImageView;
import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.activity.ExtraKeys;
import com.longx.intelligent.android.ichat2.activity.helper.BaseActivity;
import com.longx.intelligent.android.ichat2.behavior.MessageDisplayer;
import com.longx.intelligent.android.ichat2.data.response.OperationStatus;
import com.longx.intelligent.android.ichat2.databinding.ActivityChangeAvatarBinding;
import com.longx.intelligent.android.ichat2.dialog.MessageDialog;
import com.longx.intelligent.android.ichat2.dialog.OperatingDialog;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.UserApiCaller;
import com.longx.intelligent.android.ichat2.util.Utils;

import retrofit2.Call;
import retrofit2.Response;

public class ChangeAvatarActivity extends BaseActivity {
    private ActivityChangeAvatarBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChangeAvatarBinding.inflate(getLayoutInflater());
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
            }else if(item.getItemId() == R.id.change){
                new Thread(() -> {
                    OperatingDialog operatingDialog = new OperatingDialog(this);
                    operatingDialog.forShow();
                    Bitmap croppedImage = binding.cropImageView.getCroppedImage();
                    if(croppedImage == null){
                        operatingDialog.dismiss();
                        MessageDisplayer.autoShow(this, "错误", MessageDisplayer.Duration.SHORT);
                    }else {
                        byte[] avatar = Utils.encodeBitmapToBytes(croppedImage, Bitmap.CompressFormat.PNG, 100);
                        operatingDialog.dismiss();
                        onImageCropped(avatar);
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

    private void onImageCropped(byte[] avatar){
        UserApiCaller.changeAvatar(this, avatar, ".png", new RetrofitApiCaller.CommonYier<OperationStatus>(this){
            @Override
            public void ok(OperationStatus data, Response<OperationStatus> raw, Call<OperationStatus> call) {
                super.ok(data, raw, call);
                data.commonHandleResult(ChangeAvatarActivity.this, new int[]{-101, -102}, () -> {
                    new MessageDialog(ChangeAvatarActivity.this, "修改成功")
                            .forShow();
                });
            }
        });
    }
}