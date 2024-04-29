package com.longx.intelligent.android.ichat2.dialog;

import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.longx.intelligent.android.ichat2.databinding.DialogInputBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by LONG on 2024/4/28 at 1:56 AM.
 */
public class InputDialog extends AbstractDialog{
    private final Drawable icon;
    private final String message;
    private final String hint;
    private final Integer inputType;
    private final String uuid = UUID.randomUUID().toString();
    private final InputButtonInfo positiveButtonInfo;

    public InputDialog(AppCompatActivity activity, String message, InputButtonInfo positiveButtonInfo) {
        this(activity, null, message, null, null, positiveButtonInfo);
    }

    public InputDialog(AppCompatActivity activity, String message, String hint, InputButtonInfo positiveButtonInfo) {
        this(activity, null, message, hint, null, positiveButtonInfo);
    }

    public InputDialog(AppCompatActivity activity, String message, Integer inputType, InputButtonInfo positiveButtonInfo) {
        this(activity, null, message, null, inputType, positiveButtonInfo);
    }

    public InputDialog(AppCompatActivity activity, String message, String hint, Integer inputType, InputButtonInfo positiveButtonInfo) {
        this(activity, null, message, hint, inputType, positiveButtonInfo);
    }

    public InputDialog(AppCompatActivity activity, Drawable icon, String message, String hint, Integer inputType, InputButtonInfo positiveButtonInfo) {
        super(activity, true);
        this.icon = icon;
        this.message = message;
        this.hint = hint;
        this.inputType = inputType;
        this.positiveButtonInfo = positiveButtonInfo;
    }

    @Override
    protected AlertDialog create(MaterialAlertDialogBuilder builder) {
        builder.setTitle(uuid);
        if(icon != null) builder.setIcon(icon);
        DialogInputBinding binding = DialogInputBinding.inflate(getActivity().getLayoutInflater());
        if(hint != null) binding.textInputEditText.setHint(hint);
        if(inputType != null) binding.textInputEditText.setInputType(inputType);
        builder.setView(binding.getRoot());
        builder.setPositiveButton(positiveButtonInfo.text == null ? "确定" : positiveButtonInfo.text, (dialog, which) -> {
            positiveButtonInfo.yier.onClick(dialog, binding.textInputEditText);
        });
        return builder.create();
    }

    @Override
    protected void onDialogShowed() {
        super.onDialogShowed();
        List<TextView> allTextViews = findAllTextViewsInView(getDialog().getWindow().getDecorView());
        allTextViews.forEach(textView -> {
            if(textView.getText().toString().equals(uuid)) {
                textView.setText(message);
                textView.setSingleLine(false);
                textView.setMaxLines(Integer.MAX_VALUE);
                float textSize = textView.getTextSize();
                int textLength = textView.getText().length();
                if(textLength > 15){
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize * 0.81F);
                }
            }
        });
    }

    public List<TextView> findAllTextViewsInView(View root) {
        List<TextView> foundTextViews = new ArrayList<>();
        findTextViewsRecursively(root, foundTextViews);
        return foundTextViews;
    }

    private void findTextViewsRecursively(View view, List<TextView> textViewList) {
        if (view instanceof TextView) {
            textViewList.add((TextView) view);
        } else if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View child = viewGroup.getChildAt(i);
                findTextViewsRecursively(child, textViewList);
            }
        }
    }

    public static class InputButtonInfo{
        private final String text;
        private final InputButtonOnClickYier yier;

        public interface InputButtonOnClickYier{
            void onClick(DialogInterface dialog, TextInputEditText editText);
        }

        public InputButtonInfo(String text, InputButtonOnClickYier yier){
            this.text = text;
            this.yier = yier;
        }
    }
}
