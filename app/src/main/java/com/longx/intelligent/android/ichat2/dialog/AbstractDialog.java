package com.longx.intelligent.android.ichat2.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.longx.intelligent.android.ichat2.util.UiUtil;
import com.longx.intelligent.android.ichat2.yier.ResultsYier;

import java.util.concurrent.CountDownLatch;

/**
 * Created by LONG on 2024/1/8 at 9:20 PM.
 */
public abstract class AbstractDialog {
    private final Activity activity;
    private final MaterialAlertDialogBuilder dialogBuilder;
    private AlertDialog dialog;
    private final ContextThemeWrapper dialogContext;

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

    protected View createView(LayoutInflater layoutInflater){
        return null;
    }

    protected abstract AlertDialog create(MaterialAlertDialogBuilder builder);

    public AbstractDialog create(){
        CountDownLatch countDownLatch = new CountDownLatch(1);
        activity.runOnUiThread(() -> {
            View view = createView(activity.getLayoutInflater().cloneInContext(dialogContext));
            if(view != null){
                dialogBuilder.setView(view);
            }
            dialog = create(dialogBuilder);
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
        return this;
    }

    public AbstractDialog show(){
        return show(null);
    }

    public AbstractDialog show(ResultsYier yier) {
        activity.runOnUiThread(() -> {
            try {
                dialog.show();
            }catch (WindowManager.BadTokenException ignore){}
            adjustDialogSize();
            onDialogShowed();
            if(yier != null) yier.onResults();
        });
        return this;
    }

    protected void onDialogCreated() {
    }

    protected void adjustDialogSize() {
    }

    protected void onDialogShowed(){
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
