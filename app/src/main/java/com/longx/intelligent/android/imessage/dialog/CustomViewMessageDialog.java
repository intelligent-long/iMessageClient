package com.longx.intelligent.android.imessage.dialog;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.longx.intelligent.android.imessage.databinding.DialogMessageBinding;

/**
 * Created by LONG on 2024/1/14 at 10:07 PM.
 */
public class CustomViewMessageDialog extends AbstractDialog<CustomViewMessageDialog> {
    private final String title;
    private final String message;
    private final DialogMessageBinding binding;

    public CustomViewMessageDialog(AppCompatActivity activity, String message) {
        this(activity, null, message);
    }

    public CustomViewMessageDialog(AppCompatActivity activity, String title, String message) {
        super(activity, true);
        this.title = title;
        this.message = message;
        binding = DialogMessageBinding.inflate(activity.getLayoutInflater());
    }

    @Override
    protected View onCreateView(LayoutInflater layoutInflater) {
        return binding.getRoot();
    }

    @Override
    protected void onSetupIcon(MaterialAlertDialogBuilder builder, Drawable defaultIcon, Drawable icon) {
        if(icon != null){
            binding.icon.setImageDrawable(icon);
            binding.icon.setVisibility(View.VISIBLE);
        }else {
            binding.icon.setVisibility(View.GONE);
        }
        if(icon != null && title != null){
            binding.iconTitleSpace.setVisibility(View.VISIBLE);
        }else {
            binding.iconTitleSpace.setVisibility(View.GONE);
        }
        if(icon == null && title == null){
            binding.messageTopSpace.setVisibility(View.GONE);
        }else {
            binding.messageTopSpace.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected AlertDialog onCreate(MaterialAlertDialogBuilder builder) {
        binding.message.setText(message);
        if(title != null){
            binding.title.setText(title);
            binding.title.setVisibility(View.VISIBLE);
        }else {
            binding.title.setVisibility(View.GONE);
        }
        builder
                .setPositiveButton("确定", null);
        return builder.create();
    }
}
