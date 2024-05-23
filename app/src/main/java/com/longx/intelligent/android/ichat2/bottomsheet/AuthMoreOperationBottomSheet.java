package com.longx.intelligent.android.ichat2.bottomsheet;

import android.content.Intent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.longx.intelligent.android.ichat2.OfflineDetailsActivity;
import com.longx.intelligent.android.ichat2.databinding.BottomSheetAuthMoreOperationBinding;
import com.longx.intelligent.android.ichat2.dialog.ServerSettingDialog;

/**
 * Created by LONG on 2024/3/27 at 3:54 PM.
 */
public class AuthMoreOperationBottomSheet extends AbstractBottomSheet{
    private BottomSheetAuthMoreOperationBinding binding;

    public AuthMoreOperationBottomSheet(AppCompatActivity activity) {
        super(activity);
    }

    @Override
    protected void onCreate() {
        binding = BottomSheetAuthMoreOperationBinding.inflate(getActivity().getLayoutInflater());
        setContentView(binding.getRoot());
        setupListeners();
    }

    private void setupListeners() {
        binding.serverSetting.setOnClickListener(v -> {
            dismiss();
            new ServerSettingDialog(getActivity()).show();
        });
        binding.softwareUpdate.setOnClickListener(v -> {
            dismiss();
            //TODO
        });
        binding.offlineDetail.setOnClickListener(v -> {
            dismiss();
            getActivity().startActivity(new Intent(getActivity(), OfflineDetailsActivity.class));
        });
    }
}
