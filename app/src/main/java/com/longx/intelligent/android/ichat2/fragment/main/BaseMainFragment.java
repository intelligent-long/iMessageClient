package com.longx.intelligent.android.ichat2.fragment.main;

import android.graphics.drawable.Drawable;

import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;

import com.longx.intelligent.android.ichat2.activity.MainActivity;
import com.longx.intelligent.android.ichat2.fragment.helper.BaseFragment;
import com.longx.intelligent.android.ichat2.yier.ChangeUiYier;

/**
 * Created by LONG on 2024/4/10 at 5:24 AM.
 */
public abstract class BaseMainFragment extends BaseFragment implements ChangeUiYier {

    protected void setupToolbarNavIcon(Toolbar toolbar) {
        FragmentActivity activity = getActivity();
        if(activity instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) getActivity();
            DrawerLayout drawerLayout = mainActivity.getViewBinding().drawerLayout;
            toolbar.setNavigationOnClickListener(v -> {
                if (drawerLayout.isOpen()) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            });
        }
    }

    @Override
    public void changeUi(String id, Object... objects) {
        if(id.equals("hide_nav_icon")){
            Toolbar toolbar = getToolbar();
            if(toolbar != null) {
                Drawable navIcon = (Drawable) objects[0];
                toolbar.setNavigationIcon(navIcon);
            }
        }
    }

    public abstract Toolbar getToolbar();
}
