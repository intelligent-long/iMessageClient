package com.longx.intelligent.android.lib.materialyoupreference.preferences.divider;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.longx.intelligent.android.lib.materialyoupreference.R;

/**
 * Created by LONG on 2024/4/1 at 10:23 PM.
 */
public class DividerHelper {

    public static class DividerAllowRules {
        private final boolean allowDividerAbove;
        private final boolean allowDividerBelow = true;

        public DividerAllowRules(boolean allowDivider) {
            this.allowDividerAbove = allowDivider;
        }

        public boolean isAllowDividerAbove() {
            return allowDividerAbove;
        }

        public boolean isAllowDividerBelow() {
            return allowDividerBelow;
        }
    }

    public static DividerAllowRules parseDividerAllowRules(@NonNull Context context, @Nullable AttributeSet attrs) {
        DividerAllowRules dividerAllowRules;
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.Material3Preference, 0, 0);
        try {
            dividerAllowRules = new DividerAllowRules(a.getBoolean(R.styleable.Material3Preference_showDivider, false));
        } finally {
            a.recycle();
        }
        return dividerAllowRules;
    }
}
