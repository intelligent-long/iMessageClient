package com.longx.intelligent.android.ichat2.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.appbar.AppBarLayout;

public class DisableExpandAppBarBehavior extends AppBarLayout.Behavior {

    private boolean isScrollEnabled = true;

    public DisableExpandAppBarBehavior() {
    }

    public DisableExpandAppBarBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setScrollEnabled(boolean isScrollEnabled) {
        this.isScrollEnabled = isScrollEnabled;
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout parent, AppBarLayout child,
                                       View directTargetChild, View target, int axes, int type) {
        return isScrollEnabled && super.onStartNestedScroll(parent, child, directTargetChild, target, axes, type);
    }

    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, AppBarLayout child,
                                  View target, int dx, int dy, int[] consumed, int type) {
        if (isScrollEnabled) {
            super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type);
        }
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, AppBarLayout child,
                               View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type, int[] consumed) {
        if (isScrollEnabled) {
            super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type, consumed);
        }
    }

    @Override
    public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, AppBarLayout abl, View target, int type) {
        if (isScrollEnabled) {
            super.onStopNestedScroll(coordinatorLayout, abl, target, type);
        }
    }
}
