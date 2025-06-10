package com.longx.intelligent.android.imessage.bottomsheet;

import android.app.Activity;
import android.view.View;

import com.longx.intelligent.android.imessage.databinding.BottomSheetScanQrCodeByBinding;

/**
 * Created by LONG on 2025/6/11 at 上午1:42.
 */
public class ScanQrCodeByBottomSheet extends AbstractBottomSheet{
    private BottomSheetScanQrCodeByBinding binding;
    private final View.OnClickListener chooseImageYier;
    private final View.OnClickListener openCameraYier;

    public ScanQrCodeByBottomSheet(Activity activity, View.OnClickListener chooseImageYier, View.OnClickListener openCameraYier) {
        super(activity);
        this.chooseImageYier = chooseImageYier;
        this.openCameraYier = openCameraYier;
        create();
    }

    @Override
    protected void onCreate() {
        binding = BottomSheetScanQrCodeByBinding.inflate(getActivity().getLayoutInflater());
        setContentView(binding.getRoot());
        setupYiers();
    }

    private void setupYiers() {
        binding.byImage.setOnClickListener(v -> {
            dismiss();
            chooseImageYier.onClick(v);
        });
        binding.byCamera.setOnClickListener(v -> {
            dismiss();
            openCameraYier.onClick(v);
        });
    }
}
