package com.longx.intelligent.android.imessage.preference;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.preference.PreferenceViewHolder;

import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.lib.materialyoupreference.preferences.Material3Preference;

/**
 * Created by LONG on 2024/4/21 at 3:27 PM.
 */
public class PermissionPreference extends Material3Preference {
    private boolean checked;
    private AppCompatCheckBox checkBox;

    public PermissionPreference(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setLayoutResource(R.layout.preference_permission);
    }

    @Override
    public void onBindViewHolder(@NonNull PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        checkBox = (AppCompatCheckBox) holder.findViewById(R.id.check_box);
        checkBox.setChecked(checked);
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
        if(checkBox != null) checkBox.setChecked(checked);
    }
}
