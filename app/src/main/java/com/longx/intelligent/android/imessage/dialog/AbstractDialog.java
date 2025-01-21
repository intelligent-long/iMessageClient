package com.longx.intelligent.android.imessage.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.content.res.AppCompatResources;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.longx.intelligent.android.imessage.util.UiUtil;
import com.longx.intelligent.android.imessage.yier.ResultsYier;

import java.util.concurrent.CountDownLatch;

/**
 * Created by LONG on 2024/1/8 at 9:20 PM.
 */
public abstract class AbstractDialog<T extends AbstractDialog<T>> {
    private final Activity activity;
    private final MaterialAlertDialogBuilder dialogBuilder;
    private AlertDialog dialog;
    private final ContextThemeWrapper dialogContext;
    private Drawable defaultIcon;
    private Drawable icon;

    public AbstractDialog(Activity activity) {
        this(activity, false);
    }

    public AbstractDialog(Activity activity, boolean centered){
        this.activity = activity;
        if(centered) {
            this.dialogBuilder = new MaterialAlertDialogBuilder(activity, com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered);
        }else {
            this.dialogBuilder = new MaterialAlertDialogBuilder(activity);
        }
        TypedValue outValue = new TypedValue();
        activity.getTheme().resolveAttribute(android.R.attr.dialogTheme, outValue, true);
        this.dialogContext = new ContextThemeWrapper(getActivity(), outValue.resourceId);
    }

    public AbstractDialog(Activity activity, int style){
        this.activity = activity;
        this.dialogBuilder = new MaterialAlertDialogBuilder(activity, style);
        this.dialogContext = new ContextThemeWrapper(getActivity(), style);
    }

    protected View onCreateView(LayoutInflater layoutInflater){
        return null;
    }

    protected void onSetupIcon(MaterialAlertDialogBuilder builder, Drawable defaultIcon, Drawable icon){
        if(icon != null){
            builder.setIcon(icon);
        }else if(defaultIcon != null){
            builder.setIcon(defaultIcon);
        }
    }

    protected abstract AlertDialog onCreate(MaterialAlertDialogBuilder builder);

    public T create(){
        CountDownLatch countDownLatch = new CountDownLatch(1);
        activity.runOnUiThread(() -> {
            View view = onCreateView(activity.getLayoutInflater().cloneInContext(dialogContext));
            if(view != null){
                dialogBuilder.setView(view);
            }
            onSetupIcon(dialogBuilder, defaultIcon, icon);
            dialog = onCreate(dialogBuilder);
            if (view != null) {
                setAutoCancelInput(view);
            }
            onDialogCreated();
            countDownLatch.countDown();
        });
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return (T) this;
    }

    public T show(){
        return show(null);
    }

    public T show(ResultsYier yier) {
        activity.runOnUiThread(() -> {
            try {
                dialog.show();
            }catch (WindowManager.BadTokenException ignore){}
            adjustDialogSize();
            onDialogShowed();
            if(yier != null) yier.onResults();
        });
        return (T) this;
    }

    protected void onDialogCreated() {
    }

    protected void adjustDialogSize() {
    }

    protected void onDialogShowed(){
    }

    public T setDefaultIcon(Drawable defaultIcon){
        this.defaultIcon = defaultIcon;
        return (T) this;
    }

    public T setIcon(Drawable icon){
        this.icon = icon;
        return (T) this;
    }

    public T setDefaultIcon(int defaultIconId){
        this.defaultIcon = AppCompatResources.getDrawable(activity, defaultIconId);
        return (T) this;
    }

    public T setIcon(int iconId){
        this.icon = AppCompatResources.getDrawable(activity, iconId);
        return (T) this;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setAutoCancelInput(View contentView) {
        contentView.setOnTouchListener((view, motionEvent) -> {
            Window window = dialog.getWindow();
            UiUtil.autoCancelInput(activity, window == null ? null : window.getCurrentFocus(), motionEvent);
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                view.performClick();
                return true;
            }
            return false;
        });
    }

    public void hide() {
        activity.runOnUiThread(() -> {
            if (dialog != null) {
                dialog.hide();
            }
        });
    }

    public void dismiss() {
        activity.runOnUiThread(() -> {
            if (dialog != null) {
                try {
                    dialog.dismiss();
                }catch (Exception ignore){}
            }
        });
    }

    public Activity getActivity() {
        return activity;
    }

    public AlertDialog getDialog() {
        return dialog;
    }
}
