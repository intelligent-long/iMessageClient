package com.longx.intelligent.android.ichat2.preference;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceViewHolder;

import com.google.android.material.imageview.ShapeableImageView;
import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.lib.materialyoupreference.preferences.Material3Preference;

/**
 * Created by LONG on 2024/4/16 at 9:39 AM.
 */
public class ChangeAvatarPreference extends Material3Preference {
    private Bitmap avatarBitmap;
    private ShapeableImageView avatarView;

    public ChangeAvatarPreference(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setLayoutResource(R.layout.preference_change_avatar);
    }

    @Override
    public void onBindViewHolder(@NonNull PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        avatarView = (ShapeableImageView) holder.findViewById(R.id.avatar);
        avatarView.setImageBitmap(avatarBitmap);
    }

    public void setAvatar(Bitmap avatar){
        avatarBitmap = avatar;
        if(avatarView != null) avatarView.setImageBitmap(avatar);
    }
}
