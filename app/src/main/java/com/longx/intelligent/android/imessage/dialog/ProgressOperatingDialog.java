package com.longx.intelligent.android.imessage.dialog;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.longx.intelligent.android.imessage.databinding.DialogProgressOperatingBinding;

/**
 * Created by LONG on 2024/8/14 at 上午3:22.
 */
public class ProgressOperatingDialog extends AbstractDialog{
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
    protected View onCreateView(LayoutInflater layoutInflater) {
        binding = DialogProgressOperatingBinding.inflate(layoutInflater);
        return binding.getRoot();
    }

    @Override
    protected AlertDialog onCreate(MaterialAlertDialogBuilder builder) {
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

    public void updateText(String text){
        getActivity().runOnUiThread(() -> {
            binding.text.setText(text);
        });
    }

    public void updateProgress(long current, long total){
        getActivity().runOnUiThread(() -> {
            int progress = (int) ((current / (double) total) * binding.indicator.getMax());
            progress = Math.min(progress, binding.indicator.getMax());
            progress = Math.max(progress, 0);
            binding.indicator.setProgressCompat(progress, true);
        });
    }

    public DialogProgressOperatingBinding getBinding() {
        return binding;
    }
}
