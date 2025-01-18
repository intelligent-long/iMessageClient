package com.longx.intelligent.android.ichat2.dialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.ui.glide.GlideApp;
import com.longx.intelligent.android.ichat2.util.ErrorLogger;
import com.longx.intelligent.android.ichat2.util.UiUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

/**
 * Created by LONG on 2024/1/16 at 11:43 PM.
 */
public class ConfirmDialog extends AbstractDialog{
    private final Integer iconId;
    private final String title;
    private final String message;
    private final boolean cancelable;
    private final String titleUuid = UUID.randomUUID().toString();
    private final String messageUuid = UUID.randomUUID().toString();
    private DialogButtonInfo positiveDialogButtonInfo;
    private DialogButtonInfo negativeDialogButtonInfo;
    private DialogButtonInfo neutralDialogButtonInfo;
    private ButtonInfo positiveButtonInfo;
    private ButtonInfo negativeButtonInfo;
    private ButtonInfo neutralButtonInfo;

    public ConfirmDialog(Activity activity){
        this(activity, "是否继续？");
    }

    public ConfirmDialog(Activity activity, String message) {
        this(activity, null, null, null, message, true);
    }

    public ConfirmDialog(Activity activity, String title, String message) {
        this(activity, null, null, title, message, true);
    }

    public ConfirmDialog(Activity activity, boolean cancelable){
        this(activity, null, "是否继续？", cancelable);
    }

    public ConfirmDialog(Activity activity, String message, boolean cancelable) {
        this(activity, null, null, null, message, cancelable);
    }

    public ConfirmDialog(Activity activity, String title, String message, boolean cancelable) {
        this(activity, null, null, title, message, cancelable);
    }

    public ConfirmDialog(Activity activity, Integer style, Integer iconId, String title, String message, boolean cancelable) {
        super(activity, style == null ? R.style.TitleLargeMaterialAlertDialog : style);
        this.iconId = iconId;
        this.title = title;
        this.message = message;
        this.cancelable = cancelable;
    }

    @Override
    protected AlertDialog create(MaterialAlertDialogBuilder builder) {
        if(title == null) {
            builder.setTitle(titleUuid);
        }else {
            builder.setTitle(title);
            builder.setMessage(messageUuid);
        }
        if(iconId == null){
            builder.setIcon(R.drawable.question_mark_24px_primary_tint);
        } else {
            CountDownLatch countDownLatch = new CountDownLatch(1);
            getActivity().runOnUiThread(() -> {
                FutureTarget<Drawable> futureTarget = GlideApp.with(getActivity().getApplicationContext())
                        .asDrawable()
                        .load(iconId)
                        .override(UiUtil.dpToPx(getActivity(), 24), UiUtil.dpToPx(getActivity(), 24))
                        .submit();
                new Thread(() -> {
                    try {
                        builder.setIcon(futureTarget.get());
                        countDownLatch.countDown();
                    } catch (ExecutionException | InterruptedException e) {
                        ErrorLogger.log(e);
                    }
                }).start();
            });
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                ErrorLogger.log(e);
            }
        }
        if (positiveButtonInfo != null) {
            builder.setPositiveButton("确定", null);
        }else {
            if(positiveDialogButtonInfo != null) {
                builder.setPositiveButton(positiveDialogButtonInfo.getText() == null ? "确定" : positiveDialogButtonInfo.getText(), positiveDialogButtonInfo.getYier());
            }
        }
        if (negativeButtonInfo != null) {
            builder.setNegativeButton("取消", null);
        }else {
            if (negativeDialogButtonInfo != null) {
                builder.setNegativeButton(negativeDialogButtonInfo.getText() == null ? "取消" : negativeDialogButtonInfo.getText(), negativeDialogButtonInfo.getYier());
            }
        }
        if(neutralButtonInfo != null) {
            builder.setNeutralButton("其他选项", null);
        }else {
            if (neutralDialogButtonInfo != null) {
                builder.setNeutralButton(neutralDialogButtonInfo.getText() == null ? "其他选项" : neutralDialogButtonInfo.getText(), neutralDialogButtonInfo.getYier());
            }
        }
        builder.setCancelable(cancelable);
        return builder.create();
    }

    @Override
    protected void onDialogCreated() {
        super.onDialogCreated();
        getDialog().setOnShowListener(d -> {
            if (positiveButtonInfo != null) {
                Button positiveButton = getDialog().getButton(DialogInterface.BUTTON_POSITIVE);
                positiveButton.setText(positiveButtonInfo.getText() == null ? "确定" : positiveButtonInfo.getText());
                positiveButton.setOnClickListener(positiveButtonInfo.getYier());
            }
            if (negativeButtonInfo != null) {
                Button negativeButton = getDialog().getButton(DialogInterface.BUTTON_NEGATIVE);
                negativeButton.setText(negativeButtonInfo.getText() == null ? "取消" : negativeButtonInfo.getText());
                negativeButton.setOnClickListener(negativeButtonInfo.getYier());
            }
            if (neutralButtonInfo != null) {
                Button neutralButton = getDialog().getButton(DialogInterface.BUTTON_NEUTRAL);
                neutralButton.setText(neutralButtonInfo.getText() == null ? "其他选项" : neutralButtonInfo.getText());
                neutralButton.setOnClickListener(neutralButtonInfo.getYier());
            }
        });
    }

    @Override
    protected void onDialogShowed() {
        super.onDialogShowed();
        List<TextView> allTextViews = findAllTextViewsInView(getDialog().getWindow().getDecorView());
        if(title == null) {
            allTextViews.forEach(textView -> {
                if (textView.getText().toString().equals(titleUuid)) {
                    textView.setText(message);
                    textView.setSingleLine(false);
                    textView.setMaxLines(Integer.MAX_VALUE);
                    float textSize = textView.getTextSize();
                    int textLength = textView.getText().length();
                    if (textLength > 15) {
                        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize * 0.81F);
                    }
                }
            });
        }else {
            allTextViews.forEach(textView -> {
                if(textView.getText().toString().equals(messageUuid)){
                    textView.setText(message);
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
                }
            });
        }
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
        positiveDialogButtonInfo = new DialogButtonInfo(null, yier);
        return this;
    }

    public ConfirmDialog setPositiveButton(View.OnClickListener yier){
        positiveButtonInfo = new ButtonInfo(null, yier);
        return this;
    }

    public ConfirmDialog setPositiveButton(){
        positiveDialogButtonInfo = new DialogButtonInfo(null, null);
        return this;
    }

    public ConfirmDialog setNegativeButton(DialogInterface.OnClickListener yier){
        negativeDialogButtonInfo = new DialogButtonInfo(null, yier);
        return this;
    }

    public ConfirmDialog setNegativeButton(View.OnClickListener yier){
        negativeButtonInfo = new ButtonInfo(null, yier);
        return this;
    }

    public ConfirmDialog setNegativeButton(){
        negativeDialogButtonInfo = new DialogButtonInfo(null, null);
        return this;
    }

    public ConfirmDialog setNeutralButton(DialogInterface.OnClickListener yier){
        neutralDialogButtonInfo = new DialogButtonInfo(null, yier);
        return this;
    }

    public ConfirmDialog setNeutralButton(View.OnClickListener yier){
        neutralButtonInfo = new ButtonInfo(null, yier);
        return this;
    }

    public ConfirmDialog setNeutralButton(){
        neutralDialogButtonInfo = new DialogButtonInfo(null, null);
        return this;
    }

    public ConfirmDialog setPositiveButton(String text, DialogInterface.OnClickListener yier){
        positiveDialogButtonInfo = new DialogButtonInfo(text, yier);
        return this;
    }

    public ConfirmDialog setPositiveButton(String text, View.OnClickListener yier){
        positiveButtonInfo = new ButtonInfo(text, yier);
        return this;
    }

    public ConfirmDialog setPositiveButton(String text){
        positiveDialogButtonInfo = new DialogButtonInfo(text, null);
        return this;
    }

    public ConfirmDialog setNegativeButton(String text, DialogInterface.OnClickListener yier){
        negativeDialogButtonInfo = new DialogButtonInfo(text, yier);
        return this;
    }

    public ConfirmDialog setNegativeButton(String text, View.OnClickListener yier){
        negativeButtonInfo = new ButtonInfo(text, yier);
        return this;
    }

    public ConfirmDialog setNegativeButton(String text){
        negativeDialogButtonInfo = new DialogButtonInfo(text, null);
        return this;
    }

    public ConfirmDialog setNeutralButton(String text, DialogInterface.OnClickListener yier){
        neutralDialogButtonInfo = new DialogButtonInfo(text, yier);
        return this;
    }

    public ConfirmDialog setNeutralButton(String text, View.OnClickListener yier){
        neutralButtonInfo = new ButtonInfo(text, yier);
        return this;
    }

    public ConfirmDialog setNeutralButton(String text){
        neutralDialogButtonInfo = new DialogButtonInfo(text, null);
        return this;
    }
}
