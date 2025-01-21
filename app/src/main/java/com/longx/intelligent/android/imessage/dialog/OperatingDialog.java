package com.longx.intelligent.android.imessage.dialog;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.longx.intelligent.android.imessage.databinding.DialogOperatingBinding;

/**
 * Created by LONG on 2024/1/12 at 5:45 PM.
 */
public class OperatingDialog extends AbstractDialog{
    public interface OnCancelOperationYier{
        void onCancelOperation();
    }
    private final OnCancelOperationYier onCancelOperationYier;

    public OperatingDialog(Activity activity) {
        this(activity, null);
    }

    public OperatingDialog(Activity activity, OnCancelOperationYier onCancelOperationYier) {
        super(activity);
        this.onCancelOperationYier = onCancelOperationYier;
    }

    @Override
    protected View onCreateView(LayoutInflater layoutInflater) {
        DialogOperatingBinding binding = DialogOperatingBinding.inflate(layoutInflater);
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
}
