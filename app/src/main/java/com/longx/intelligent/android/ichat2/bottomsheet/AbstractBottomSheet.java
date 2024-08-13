package com.longx.intelligent.android.ichat2.bottomsheet;

import android.app.Activity;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.longx.intelligent.android.ichat2.R;

/**
 * Created by LONG on 2024/2/5 at 4:22 AM.
 */
public abstract class AbstractBottomSheet {
    private final Activity activity;
    private final BottomSheetDialog bottomSheetDialog;
    private View contentView;

    public AbstractBottomSheet(Activity activity) {
        this.activity = activity;
        bottomSheetDialog = new BottomSheetDialog(activity, R.style.BottomSheetDialog);
    }

    public void create() {
        onCreate();
        if (contentView != null) {
            bottomSheetDialog.setContentView(contentView);
        } else {
            throw new IllegalStateException("Content view must be set in onCreate()");
        }
    }

    protected abstract void onCreate();

    public void setContentView(View contentView) {
        this.contentView = contentView;
    }

    public void show(){
        bottomSheetDialog.show();
    }

    public void dismiss(){
        bottomSheetDialog.dismiss();
    }

    public Activity getActivity() {
        return activity;
    }
}
