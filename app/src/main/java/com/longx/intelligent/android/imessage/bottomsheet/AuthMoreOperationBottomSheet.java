package com.longx.intelligent.android.imessage.bottomsheet;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.longx.intelligent.android.imessage.activity.ExtraKeys;
import com.longx.intelligent.android.imessage.activity.MainActivity;
import com.longx.intelligent.android.imessage.activity.OfflineDetailsActivity;
import com.longx.intelligent.android.imessage.activity.VersionActivity;
import com.longx.intelligent.android.imessage.activity.settings.RootSettingsActivity;
import com.longx.intelligent.android.imessage.databinding.BottomSheetAuthMoreOperationBinding;
import com.longx.intelligent.android.imessage.dialog.ConfirmDialog;
import com.longx.intelligent.android.imessage.dialog.ServerSettingDialog;
import com.longx.intelligent.android.imessage.util.AppUtil;

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
        setupYiers();
    }

    private void setupYiers() {
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
        binding.restart.setOnClickListener(v -> {
            new ConfirmDialog(getActivity(), "如果应用出现异常，重新启动可能可以解决问题。\n是否确定要继续？\n注意：此操作有极低概率导致数据异常。")
                    .setNegativeButton()
                    .setPositiveButton((dialog, which) -> {
                        dismiss();
                        AppUtil.restartApp(getActivity());
                    })
                    .create().show();
        });
        binding.settings.setOnClickListener(v -> {
            dismiss();
            Intent intent = new Intent(getActivity(), RootSettingsActivity.class);
            intent.putExtra(ExtraKeys.NEED_RESTORE_INSTANCE_STATE, false);
            intent.putExtra(ExtraKeys.AUTH_TO_SETTINGS, true);
            getActivity().startActivity(intent);
        });
    }
}
