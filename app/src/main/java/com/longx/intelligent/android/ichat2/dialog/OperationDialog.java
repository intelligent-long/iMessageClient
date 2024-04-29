package com.longx.intelligent.android.ichat2.dialog;

import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.longx.intelligent.android.ichat2.databinding.DialogOperationBinding;

/**
 * Created by LONG on 2024/1/12 at 5:45 PM.
 */
public class OperationDialog extends AbstractDialog{
    public interface OnCancelOperationYier{
        void onCancelOperation();
    }

    private final OnCancelOperationYier onCancelOperationYier;

    public OperationDialog(AppCompatActivity activity) {
        this(activity, null);
    }

    public OperationDialog(AppCompatActivity activity, OnCancelOperationYier onCancelOperationYier) {
        super(activity);
        this.onCancelOperationYier = onCancelOperationYier;
    }

    @Override
    protected View createView(LayoutInflater layoutInflater) {
        return DialogOperationBinding.inflate(layoutInflater).getRoot();
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
}
