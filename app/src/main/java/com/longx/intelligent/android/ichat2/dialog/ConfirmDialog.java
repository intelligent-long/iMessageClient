package com.longx.intelligent.android.ichat2.dialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.longx.intelligent.android.ichat2.R;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by LONG on 2024/1/16 at 11:43 PM.
 */
public class ConfirmDialog extends AbstractDialog{
    private final Integer iconId;
    private final String message;
    private final String uuid = UUID.randomUUID().toString();
    private ButtonInfo positiveButtonInfo;
    private ButtonInfo negativeButtonInfo;
    private ButtonInfo neutralButtonInfo;

    public ConfirmDialog(Activity activity, String message) {
        this(activity, null, message);
    }

    public ConfirmDialog(Activity activity, Integer iconId, String message) {
        super(activity, R.style.ConfirmMaterialAlertDialog);
        this.iconId = iconId;
        this.message = message;
    }

    @Override
    protected AlertDialog create(MaterialAlertDialogBuilder builder) {
        builder
                .setTitle(uuid);
        if(iconId == null){
            builder.setIcon(R.drawable.question_mark_24px_primary_tint);
        }else {
            builder.setIcon(iconId);
        }
        if(positiveButtonInfo != null){
            builder.setPositiveButton(positiveButtonInfo.getText() == null ? "确定" : positiveButtonInfo.getText(), positiveButtonInfo.getYier());
        }
        if(negativeButtonInfo != null){
            builder.setNegativeButton(negativeButtonInfo.getText() == null ? "取消" : negativeButtonInfo.getText(), negativeButtonInfo.getYier());
        }
        if(neutralButtonInfo != null){
            builder.setNeutralButton(neutralButtonInfo.getText() == null ? "其他选项" : neutralButtonInfo.getText(), neutralButtonInfo.getYier());
        }
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

    public ConfirmDialog setPositiveButton(DialogInterface.OnClickListener yier){
        positiveButtonInfo = new ButtonInfo(null, yier);
        return this;
    }

    public ConfirmDialog setNegativeButton(DialogInterface.OnClickListener yier){
        negativeButtonInfo = new ButtonInfo(null, yier);
        return this;
    }

    public ConfirmDialog setNeutralButton(DialogInterface.OnClickListener yier){
        neutralButtonInfo = new ButtonInfo(null, yier);
        return this;
    }

    public ConfirmDialog setPositiveButton(String text, DialogInterface.OnClickListener yier){
        positiveButtonInfo = new ButtonInfo(text, yier);
        return this;
    }

    public ConfirmDialog setNegativeButton(String text, DialogInterface.OnClickListener yier){
        negativeButtonInfo = new ButtonInfo(text, yier);
        return this;
    }

    public ConfirmDialog setNeutralButton(String text, DialogInterface.OnClickListener yier){
        neutralButtonInfo = new ButtonInfo(text, yier);
        return this;
    }
}
