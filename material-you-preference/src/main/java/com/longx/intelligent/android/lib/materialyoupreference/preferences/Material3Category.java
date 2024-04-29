package com.longx.intelligent.android.lib.materialyoupreference.preferences;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceViewHolder;

import com.longx.intelligent.android.lib.materialyoupreference.R;
import com.longx.intelligent.android.lib.materialyoupreference.preferences.divider.DividerHelper;

/**
 * Created by LONG on 2024/1/18 at 3:01 AM.
 */
public class Material3Category extends PreferenceCategory {
    private DividerHelper.DividerAllowRules dividerAllowRules;

    public Material3Category(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initWithAttrs(context, attrs);
    }

    public Material3Category(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initWithAttrs(context, attrs);
    }

    public Material3Category(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initWithAttrs(context, attrs);
    }

    public Material3Category(@NonNull Context context) {
        super(context);
        setLayoutResource(R.layout.material_preference_category);
    }

    private void initWithAttrs(@NonNull Context context, @Nullable AttributeSet attrs) {
        dividerAllowRules = DividerHelper.parseDividerAllowRules(context, attrs);
        setLayoutResource(R.layout.material_preference_category);
    }

    @Override
    public void onBindViewHolder(@NonNull PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        holder.setDividerAllowedAbove(dividerAllowRules.isAllowDividerAbove());
        holder.setDividerAllowedBelow(dividerAllowRules.isAllowDividerBelow());
    }
}
