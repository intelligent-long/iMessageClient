package com.longx.intelligent.android.imessage.dialog;

import android.app.Activity;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

/**
 * Created by LONG on 2024/1/14 at 10:07 PM.
 */
public class MessageDialog extends AbstractDialog<MessageDialog> {
    private final String title;
    private final String message;

    public MessageDialog(Activity activity, String message) {
        this(activity, null, message);
    }

    public MessageDialog(Activity activity, String title, String message) {
        super(activity, true);
        this.title = title;
        this.message = message;
    }

    @Override
    protected AlertDialog onCreate(MaterialAlertDialogBuilder builder) {
        builder
                .setMessage(message)
                .setPositiveButton("确定", null);
        if(title != null){
            builder.setTitle(title);
        }
        return builder.create();
    }
}
