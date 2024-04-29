package com.longx.intelligent.android.ichat2.dialog;

import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

/**
 * Created by LONG on 2024/1/14 at 10:07 PM.
 */
public class MessageDialog extends AbstractDialog{
    private final String title;
    private final String message;

    public MessageDialog(AppCompatActivity activity, String message) {
        this(activity, null, message);
    }

    public MessageDialog(AppCompatActivity activity, String title, String message) {
        super(activity, true);
        this.title = title;
        this.message = message;
    }

    @Override
    protected AlertDialog create(MaterialAlertDialogBuilder builder) {
        builder
                .setMessage(message)
                .setPositiveButton("确定", null);
        if(title != null){
            builder.setTitle(title);
        }
        return builder.create();
    }
}
