package com.longx.intelligent.android.imessage.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

/**
 * Created by LONG on 2024/2/8 at 1:36 PM.
 */
public class PassThroughCoordinatorLayout extends CoordinatorLayout {

    public PassThroughCoordinatorLayout(Context context) {
        super(context);
    }

    public PassThroughCoordinatorLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PassThroughCoordinatorLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }
}
