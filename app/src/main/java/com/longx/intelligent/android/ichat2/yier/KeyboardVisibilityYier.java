package com.longx.intelligent.android.ichat2.yier;


import android.app.Activity;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;

public class KeyboardVisibilityYier {
    public interface Yier {
        void onKeyboardOpened();
        void onKeyboardClosed();
    }

    private static ViewTreeObserver.OnGlobalLayoutListener globalLayoutListener;

    public static void setYier(Activity activity, final Yier listener) {
        final View rootView = activity.findViewById(android.R.id.content);

        globalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
            private boolean isKeyboardVisible;

            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                rootView.getWindowVisibleDisplayFrame(r);
                int screenHeight = rootView.getRootView().getHeight();
                int keypadHeight = screenHeight - r.bottom;

                boolean isKeyboardNowVisible = keypadHeight > screenHeight * 0.25;

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

    public static void removeYier(Activity activity) {
        final View rootView = activity.findViewById(android.R.id.content);
        if (globalLayoutListener != null) {
            rootView.getViewTreeObserver().removeOnGlobalLayoutListener(globalLayoutListener);
        }
    }

    public static boolean isKeyboardVisible(Activity activity) {
        View rootView = activity.findViewById(android.R.id.content);
        Rect rect = new Rect();
        rootView.getWindowVisibleDisplayFrame(rect);
        int screenHeight = rootView.getRootView().getHeight();
        int keypadHeight = screenHeight - rect.bottom;
        return keypadHeight > screenHeight * 0.25;
    }
}
