package com.longx.intelligent.android.ichat2.dialog;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.longx.intelligent.android.ichat2.databinding.DialogProgressOperatingBinding;

/**
 * Created by LONG on 2024/8/14 at 上午3:22.
 */
public class ProgressOperatingDialog extends AbstractDialog{
    private static final int MAX = 10000;
    private DialogProgressOperatingBinding binding;

    public interface OnCancelOperationYier{
        void onCancelOperation();
    }

    private final OnCancelOperationYier onCancelOperationYier;

    public ProgressOperatingDialog(Activity activity) {
        this(activity, null);
    }

    public ProgressOperatingDialog(Activity activity, OnCancelOperationYier onCancelOperationYier) {
        super(activity);
        this.onCancelOperationYier = onCancelOperationYier;
    }

    @Override
    protected View createView(LayoutInflater layoutInflater) {
        binding = DialogProgressOperatingBinding.inflate(layoutInflater);
        binding.indicator.setMax(MAX);
        return binding.getRoot();
    }

    @Override
    protected AlertDialog create(MaterialAlertDialogBuilder builder) {
        AlertDialog dialog = builder
                .setOnDismissListener(dialogInterface -> {
                    if(onCancelOperationYier != null)
                        onCancelOperationYier.onCancelOperation();
                })
                .setCancelable(onCancelOperationYier != null)
                .create();
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    public void setProgress(long current, long total){
        int progress = (int) ((current / (double) total) * MAX);
        binding.indicator.setProgress(progress);
    }
}
