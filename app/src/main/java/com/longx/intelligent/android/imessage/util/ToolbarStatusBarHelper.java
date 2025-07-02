package com.longx.intelligent.android.imessage.util;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.Window;

import androidx.annotation.ColorInt;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.google.android.material.appbar.AppBarLayout;

public class ToolbarStatusBarHelper {

    private final Activity activity;
    private final AppBarLayout appBarLayout;
    private final int expandedColor;
    private final int collapsedColor;

    public ToolbarStatusBarHelper(Activity activity, AppBarLayout appBarLayout, @ColorInt int expandedColor, @ColorInt int collapsedColor) {
        this.activity = activity;
        this.appBarLayout = appBarLayout;
        this.expandedColor = expandedColor;
        this.collapsedColor = collapsedColor;
        init();
    }

    private void init() {
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                int totalScrollRange = appBarLayout.getTotalScrollRange();
                float fraction = Math.abs((float) verticalOffset / totalScrollRange);
                int blendedColor = blendColors(expandedColor, collapsedColor, fraction);

                Window window = activity.getWindow();
                window.setStatusBarColor(blendedColor);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    View decorView = window.getDecorView();
                    if (isLightColor(blendedColor)) {
                        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                    } else {
                        decorView.setSystemUiVisibility(0);
                    }
                }
            }
        });
    }

    private int blendColors(int from, int to, float ratio) {
        float inverseRatio = 1f - ratio;
        int a = (int) (Color.alpha(from) * inverseRatio + Color.alpha(to) * ratio);
        int r = (int) (Color.red(from) * inverseRatio + Color.red(to) * ratio);
        int g = (int) (Color.green(from) * inverseRatio + Color.green(to) * ratio);
        int b = (int) (Color.blue(from) * inverseRatio + Color.blue(to) * ratio);
        return Color.argb(a, r, g, b);
    }

    private boolean isLightColor(int color) {
        double darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255;
        return darkness < 0.5;
    }
}
