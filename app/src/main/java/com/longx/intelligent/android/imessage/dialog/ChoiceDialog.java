package com.longx.intelligent.android.imessage.dialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.longx.intelligent.android.imessage.R;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by LONG on 2024/10/29 at 上午2:22.
 */
public class ChoiceDialog extends AbstractDialog{
    private final Integer iconId;
    private final String message;
    private final String uuid = UUID.randomUUID().toString();
    private DialogButtonInfo positiveButtonInfo;
    private DialogButtonInfo negativeDialogButtonInfo;
    private DialogButtonInfo neutralButtonInfo;

    public ChoiceDialog(Activity activity){
        this(activity, "请选择");
    }

    public ChoiceDialog(Activity activity, String message) {
        this(activity, null, message);
    }

    public ChoiceDialog(Activity activity, Integer iconId, String message) {
        super(activity, R.style.TitleLargeMaterialAlertDialog);
        this.iconId = iconId;
        this.message = message;
    }

    @Override
    protected AlertDialog create(MaterialAlertDialogBuilder builder) {
        builder
                .setTitle(uuid);
        if(iconId == null){
//            builder.setIcon(R.drawable.radio_button_unchecked_24px_primary_tint);
        }else {
            builder.setIcon(iconId);
        }
        if(positiveButtonInfo != null){
            builder.setPositiveButton(positiveButtonInfo.getText() == null ? "确定" : positiveButtonInfo.getText(), positiveButtonInfo.getYier());
        }
        if(negativeDialogButtonInfo != null){
            builder.setNegativeButton(negativeDialogButtonInfo.getText() == null ? "取消" : negativeDialogButtonInfo.getText(), negativeDialogButtonInfo.getYier());
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

    public ChoiceDialog setPositiveButton(DialogInterface.OnClickListener yier){
        positiveButtonInfo = new DialogButtonInfo(null, yier);
        return this;
    }

    public ChoiceDialog setNegativeButton(DialogInterface.OnClickListener yier){
        negativeDialogButtonInfo = new DialogButtonInfo(null, yier);
        return this;
    }

    public ChoiceDialog setNeutralButton(DialogInterface.OnClickListener yier){
        neutralButtonInfo = new DialogButtonInfo(null, yier);
        return this;
    }

    public ChoiceDialog setPositiveButton(String text, DialogInterface.OnClickListener yier){
        positiveButtonInfo = new DialogButtonInfo(text, yier);
        return this;
    }

    public ChoiceDialog setNegativeButton(String text, DialogInterface.OnClickListener yier){
        negativeDialogButtonInfo = new DialogButtonInfo(text, yier);
        return this;
    }

    public ChoiceDialog setNeutralButton(String text, DialogInterface.OnClickListener yier){
        neutralButtonInfo = new DialogButtonInfo(text, yier);
        return this;
    }
}
