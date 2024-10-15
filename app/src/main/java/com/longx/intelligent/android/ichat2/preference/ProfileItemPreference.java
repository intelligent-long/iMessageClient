package com.longx.intelligent.android.ichat2.preference;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.preference.PreferenceViewHolder;

import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.lib.materialyoupreference.preferences.Material3Preference;

/**
 * Created by LONG on 2024/6/7 at 10:06 PM.
 */
public class ProfileItemPreference extends Material3Preference {
    private AppCompatImageView profileVisibilityIcon;
    private Boolean profileVisible;

    public ProfileItemPreference(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setLayoutResource(R.layout.preference_profile_item);
    }

    @Override
    public void onBindViewHolder(@NonNull PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        profileVisibilityIcon = (AppCompatImageView) holder.findViewById(R.id.visibility_icon);
        showProfileVisibilityIcon();
    }

    public void setProfileVisibility(boolean profileVisible){
        this.profileVisible = profileVisible;
        if(profileVisibilityIcon != null){
            showProfileVisibilityIcon();
        }
    }

    private void showProfileVisibilityIcon() {
        if(profileVisible == null) return;
        profileVisibilityIcon.setVisibility(View.VISIBLE);
        if(profileVisible){
            profileVisibilityIcon.setImageResource(R.drawable.visibility_fill_24px);
        }else {
            profileVisibilityIcon.setImageResource(R.drawable.visibility_off_fill_24px);
        }
    }
}
