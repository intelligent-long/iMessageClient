package com.longx.intelligent.android.lib.materialyoupreference.preferences;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceViewHolder;

import com.longx.intelligent.android.lib.materialyoupreference.R;
import com.longx.intelligent.android.lib.materialyoupreference.preferences.divider.DividerHelper;

public class Material3ListPreference extends ListPreference {
    private DividerHelper.DividerAllowRules dividerAllowRules;
    public Material3ListPreference(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public Material3ListPreference(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    private void init(@NonNull Context context, @Nullable AttributeSet attrs) {
        dividerAllowRules = DividerHelper.parseDividerAllowRules(context, attrs);
        setLayoutResource(R.layout.material_preference_list);
    }

    @Override
    protected void onClick() {
        super.onClick();
    }

    @Override
    public void onBindViewHolder(@NonNull PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        holder.setDividerAllowedAbove(dividerAllowRules.isAllowDividerAbove());
        holder.setDividerAllowedBelow(dividerAllowRules.isAllowDividerBelow());
    }
}
