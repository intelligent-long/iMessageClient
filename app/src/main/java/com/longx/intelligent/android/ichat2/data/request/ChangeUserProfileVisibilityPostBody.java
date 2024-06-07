package com.longx.intelligent.android.ichat2.data.request;

/**
 * Created by LONG on 2024/6/7 at 3:25 PM.
 */
public class ChangeUserProfileVisibilityPostBody {
    private boolean emailVisible;
    private boolean sexVisible;
    private boolean regionVisible;

    public ChangeUserProfileVisibilityPostBody() {
    }

    public ChangeUserProfileVisibilityPostBody(boolean emailVisible, boolean sexVisible, boolean regionVisible) {
        this.emailVisible = emailVisible;
        this.sexVisible = sexVisible;
        this.regionVisible = regionVisible;
    }

    public boolean isEmailVisible() {
        return emailVisible;
    }

    public boolean isSexVisible() {
        return sexVisible;
    }

    public boolean isRegionVisible() {
        return regionVisible;
    }
}
