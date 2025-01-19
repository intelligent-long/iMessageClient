package com.longx.intelligent.android.imessage.yier;

import android.app.Activity;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;

public class KeyboardVisibilityYier {

    public interface Yier {
        void onKeyboardOpened();
        void onKeyboardClosed();
    }

    private ViewTreeObserver.OnGlobalLayoutListener globalLayoutListener;
    private final View rootView;

    public KeyboardVisibilityYier(Activity activity) {
        rootView = activity.findViewById(android.R.id.content);
    }

    public void setYier(final Yier listener) {
        globalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
            private boolean isKeyboardVisible;

            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                rootView.getWindowVisibleDisplayFrame(r);
                int screenHeight = rootView.getRootView().getHeight();
                int keypadHeight = screenHeight - r.bottom;

                boolean isKeyboardNowVisible = keypadHeight > screenHeight * 0.15;

                if (isKeyboardNowVisible != isKeyboardVisible) {
                    isKeyboardVisible = isKeyboardNowVisible;
                    if (isKeyboardVisible) {
                        listener.onKeyboardOpened();
                    } else {
                        listener.onKeyboardClosed();
                    }
                }
            }
        };

        rootView.getViewTreeObserver().addOnGlobalLayoutListener(globalLayoutListener);
    }

    public void removeYier() {
        if (globalLayoutListener != null) {
            rootView.getViewTreeObserver().removeOnGlobalLayoutListener(globalLayoutListener);
        }
    }

    public boolean isKeyboardVisible() {
        Rect rect = new Rect();
        rootView.getWindowVisibleDisplayFrame(rect);
        int screenHeight = rootView.getRootView().getHeight();
        int keypadHeight = screenHeight - rect.bottom;
        return keypadHeight > screenHeight * 0.15;
    }
}
