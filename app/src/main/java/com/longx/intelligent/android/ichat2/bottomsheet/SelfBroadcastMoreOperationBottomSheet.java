package com.longx.intelligent.android.ichat2.bottomsheet;

import android.app.Activity;
import android.view.View;

import com.longx.intelligent.android.ichat2.databinding.BottomSheetBroadcastMoreOperationBinding;

/**
 * Created by LONG on 2024/8/14 at 上午12:24.
 */
public class SelfBroadcastMoreOperationBottomSheet extends AbstractBottomSheet{
    private BottomSheetBroadcastMoreOperationBinding binding;
    private View.OnClickListener deleteClickYier;
    private View.OnClickListener editClickYier;
    private View.OnClickListener changePermissionClickYier;

    public SelfBroadcastMoreOperationBottomSheet(Activity activity) {
        super(activity);
        create();
    }

    @Override
    protected void onCreate() {
        binding = BottomSheetBroadcastMoreOperationBinding.inflate(getActivity().getLayoutInflater());
        setContentView(binding.getRoot());
        setupYiers();
    }

    private void setupYiers() {
        binding.optionDelete.setOnClickListener(v -> {
            dismiss();
            if(deleteClickYier != null) deleteClickYier.onClick(v);
        });
        binding.optionEdit.setOnClickListener(v -> {
            dismiss();
            if(editClickYier != null) editClickYier.onClick(v);
        });
        binding.optionChangePermission.setOnClickListener(v -> {
            dismiss();
            if(changePermissionClickYier != null) changePermissionClickYier.onClick(v);
        });
    }

    public void setDeleteClickYier(View.OnClickListener deleteClickYier) {
        this.deleteClickYier = deleteClickYier;
    }

    public void setEditClickYier(View.OnClickListener editClickYier) {
        this.editClickYier = editClickYier;
    }

    public void setChangePermissionClickYier(View.OnClickListener changePermissionClickYier) {
        this.changePermissionClickYier = changePermissionClickYier;
    }
}
