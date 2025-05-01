package com.longx.intelligent.android.imessage.preference;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceViewHolder;

import com.google.android.material.imageview.ShapeableImageView;
import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.imessage.behaviorcomponents.GlideBehaviours;
import com.longx.intelligent.android.imessage.net.dataurl.NetDataUrls;
import com.longx.intelligent.android.lib.materialyoupreference.preferences.Material3Preference;

import java.io.File;

/**
 * Created by LONG on 2024/4/16 at 9:39 AM.
 */
public class ChangeGroupAvatarPreference extends Material3Preference {
    private String avatarHash;
    private File avatarFile;
    private ShapeableImageView avatarView;

    public ChangeGroupAvatarPreference(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setLayoutResource(R.layout.preference_change_avatar);
    }

    @Override
    public void onBindViewHolder(@NonNull PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        avatarView = (ShapeableImageView) holder.findViewById(R.id.avatar);
        showAvatar();
    }

    public void setAvatar(String avatarHash){
        this.avatarHash = avatarHash;
        if(avatarView != null){
            showAvatar();
        }
    }

    private void showAvatar() {
        if(avatarHash == null){
            GlideBehaviours.loadToImageView(getContext(), R.drawable.group_channel_default_avatar, avatarView);
        }else {
            GlideBehaviours.loadToImageView(getContext(), NetDataUrls.getGroupAvatarUrl(getContext(), avatarHash), avatarView);
        }
    }
}
