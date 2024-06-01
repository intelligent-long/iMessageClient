package com.longx.intelligent.android.ichat2.dialog;

import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.longx.intelligent.android.ichat2.databinding.DialogMessageBinding;

/**
 * Created by LONG on 2024/1/14 at 10:07 PM.
 */
public class CustomViewMessageDialog extends AbstractDialog{
    private final Integer iconId;
    private final String title;
    private final String message;
    private final DialogMessageBinding binding;

    public CustomViewMessageDialog(AppCompatActivity activity, String message) {
        this(activity, null, null, message);
    }

    public CustomViewMessageDialog(AppCompatActivity activity, Integer iconId, String message) {
        this(activity, iconId, null, message);
    }

    public CustomViewMessageDialog(AppCompatActivity activity, String title, String message) {
        this(activity, null, title, message);
    }

    public CustomViewMessageDialog(AppCompatActivity activity, Integer iconId, String title, String message) {
        super(activity, true);
        this.iconId = iconId;
        this.title = title;
        this.message = message;
        binding = DialogMessageBinding.inflate(activity.getLayoutInflater());
    }

    @Override
    protected AlertDialog create(MaterialAlertDialogBuilder builder) {
        binding.message.setText(message);
        if(iconId != null){
            binding.icon.setImageResource(iconId);
            binding.icon.setVisibility(View.VISIBLE);
        }else {
            binding.icon.setVisibility(View.GONE);
        }
        if(title != null){
            binding.title.setText(title);
            binding.title.setVisibility(View.VISIBLE);
        }else {
            binding.title.setVisibility(View.GONE);
        }
        if(iconId != null && title != null){
            binding.iconTitleSpace.setVisibility(View.VISIBLE);
        }else {
            binding.iconTitleSpace.setVisibility(View.GONE);
        }
        if(iconId == null && title == null){
            binding.messageTopSpace.setVisibility(View.GONE);
        }else {
            binding.messageTopSpace.setVisibility(View.VISIBLE);
        }
        builder
                .setView(binding.getRoot())
                .setPositiveButton("确定", null);
        return builder.create();
    }
}
