package com.longx.intelligent.android.imessage.bottomsheet;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.longx.intelligent.android.imessage.activity.OfflineDetailsActivity;
import com.longx.intelligent.android.imessage.activity.VersionActivity;
import com.longx.intelligent.android.imessage.databinding.BottomSheetAuthMoreOperationBinding;
import com.longx.intelligent.android.imessage.dialog.ServerSettingDialog;

/**
 * Created by LONG on 2024/3/27 at 3:54 PM.
 */
public class AuthMoreOperationBottomSheet extends AbstractBottomSheet{
    private BottomSheetAuthMoreOperationBinding binding;

    public AuthMoreOperationBottomSheet(AppCompatActivity activity) {
        super(activity);
        create();
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
            new ServerSettingDialog(getActivity()).create().show();
        });
        binding.softwareUpdate.setOnClickListener(v -> {
            dismiss();
            getActivity().startActivity(new Intent(getActivity(), VersionActivity.class));
        });
        binding.offlineDetail.setOnClickListener(v -> {
            dismiss();
            getActivity().startActivity(new Intent(getActivity(), OfflineDetailsActivity.class));
        });
    }
}
