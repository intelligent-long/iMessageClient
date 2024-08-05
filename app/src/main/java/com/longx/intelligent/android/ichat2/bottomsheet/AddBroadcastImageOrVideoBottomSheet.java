package com.longx.intelligent.android.ichat2.bottomsheet;

import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.longx.intelligent.android.ichat2.databinding.BottomSheetAddBroadcastImageOrVideo2Binding;
import com.longx.intelligent.android.ichat2.databinding.BottomSheetAddBroadcastImageOrVideoBinding;

/**
 * Created by LONG on 2024/8/4 at 上午4:40.
 */
public class AddBroadcastImageOrVideoBottomSheet extends AbstractBottomSheet {
    private BottomSheetAddBroadcastImageOrVideo2Binding binding;
    private View.OnClickListener onClickAddImageYier;
    private View.OnClickListener onClickAddVideoYier;
    private View.OnClickListener onClickTakePhotoYier;
    private View.OnClickListener onClickRecordVideoYier;

    public AddBroadcastImageOrVideoBottomSheet(AppCompatActivity activity) {
        super(activity);
        create();
    }

    @Override
    protected void onCreate() {
        binding = BottomSheetAddBroadcastImageOrVideo2Binding.inflate(getActivity().getLayoutInflater());
        setContentView(binding.getRoot());
        setupListeners();
    }

    private void setupListeners() {
        binding.addImage.setOnClickListener(v -> {
            if(onClickAddImageYier != null) onClickAddImageYier.onClick(v);
            dismiss();
        });
        binding.addVideo.setOnClickListener(v -> {
            if(onClickAddVideoYier != null) onClickAddVideoYier.onClick(v);
            dismiss();
        });
        binding.takePhoto.setOnClickListener(v -> {
            if(onClickTakePhotoYier != null) onClickTakePhotoYier.onClick(v);
            dismiss();
        });
        binding.recordVideo.setOnClickListener(v -> {
            if(onClickRecordVideoYier != null) onClickRecordVideoYier.onClick(v);
            dismiss();
        });
    }

    public void setOnClickAddImageYier(View.OnClickListener onClickAddImageYier) {
        this.onClickAddImageYier = onClickAddImageYier;
    }

    public void setOnClickAddVideoYier(View.OnClickListener onClickAddVideoYier) {
        this.onClickAddVideoYier = onClickAddVideoYier;
    }

    public void setOnClickTakePhotoYier(View.OnClickListener onClickTakePhotoYier) {
        this.onClickTakePhotoYier = onClickTakePhotoYier;
    }

    public void setOnClickRecordVideoYier(View.OnClickListener onClickRecordVideoYier) {
        this.onClickRecordVideoYier = onClickRecordVideoYier;
    }
}
