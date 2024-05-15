package com.longx.intelligent.android.ichat2.yier;


import android.app.Activity;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;

public class SoftKeyBoardYier {
    private View rootView;
    private int rootViewVisibleHeight;
    private OnSoftKeyBoardChangeListener onSoftKeyBoardChangeListener;
    private SoftKeyBoardYier(Activity activity){
        init(activity);
    }
    private void init(Activity activity) {
        rootView = activity.getWindow().getDecorView();
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            Rect r = new Rect();
            rootView.getWindowVisibleDisplayFrame(r);
            int visibleHeight = r.height();

            if (rootViewVisibleHeight == 0) {
                rootViewVisibleHeight = visibleHeight;
                return;
            }
            if (rootViewVisibleHeight == visibleHeight) {
                return;
            }

            if (rootViewVisibleHeight - visibleHeight > 200) {
                if (onSoftKeyBoardChangeListener != null) {
                    onSoftKeyBoardChangeListener.keyBoardShow();
                }
                rootViewVisibleHeight = visibleHeight;
                return;
            }

            if (visibleHeight - rootViewVisibleHeight > 200) {
                if (onSoftKeyBoardChangeListener != null) {
                    onSoftKeyBoardChangeListener.keyBoardHide();
                }
                rootViewVisibleHeight = visibleHeight;
                return;
            }

        });
    }

    private void setOnSoftKeyBoardChangeListener(OnSoftKeyBoardChangeListener onSoftKeyBoardChangeListener) {
        this.onSoftKeyBoardChangeListener = onSoftKeyBoardChangeListener;
    }


    public static void setListener(Activity activity, OnSoftKeyBoardChangeListener onSoftKeyBoardChangeListener) {
        SoftKeyBoardYier softKeyBoardYier = new SoftKeyBoardYier(activity);
        softKeyBoardYier.setOnSoftKeyBoardChangeListener(onSoftKeyBoardChangeListener);
    }

    public interface OnSoftKeyBoardChangeListener {
        void keyBoardShow();
        void keyBoardHide();
    }
}
