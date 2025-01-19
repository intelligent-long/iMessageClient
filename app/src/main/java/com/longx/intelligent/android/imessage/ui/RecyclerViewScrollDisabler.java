package com.longx.intelligent.android.imessage.ui;

import android.view.MotionEvent;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by LONG on 2024/5/17 at 11:16 PM.
 */
public class RecyclerViewScrollDisabler implements RecyclerView.OnItemTouchListener {
    private boolean isScrollingDisabled = false;

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        // Intercept touch events only if scrolling is disabled
        return isScrollingDisabled;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        // No additional action needed
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        // No additional action needed
    }

    public void setScrollingDisabled(boolean disabled) {
        isScrollingDisabled = disabled;
    }
}

