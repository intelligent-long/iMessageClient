package com.longx.intelligent.android.ichat2.ui;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;

import androidx.annotation.NonNull;

import com.longx.intelligent.android.ichat2.util.ErrorLogger;
import com.longx.intelligent.android.ichat2.util.UiUtil;

/**
 * Created by LONG on 2024/6/6 at 2:59 PM.
 */
public abstract class SwipeDownGestureYier extends GestureDetector.SimpleOnGestureListener {
    private Context context;
    private static int SWIPE_THRESHOLD;
    private static int SWIPE_VELOCITY_THRESHOLD;
    private boolean enabled = true;

    public SwipeDownGestureYier(Context context) {
        this.context = context;
        SWIPE_THRESHOLD = UiUtil.dpToPx(context, 80);
        SWIPE_VELOCITY_THRESHOLD = UiUtil.dpToPx(context, 30);
    }

    @Override
    public boolean onFling(MotionEvent e1, @NonNull MotionEvent e2, float velocityX, float velocityY) {
        if(!enabled) return false;
        boolean result = false;
        try {
            float diffY = e2.getY() - e1.getY();
            float diffX = e2.getX() - e1.getX();
            if (Math.abs(diffY) > Math.abs(diffX)) {
                if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0) {
                        onSwipeDown();
                    }
                    result = true;
                }
            }
            result = true;
        } catch (Exception e) {
            ErrorLogger.log(e);
        }
        return result;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public abstract void onSwipeDown();
}
