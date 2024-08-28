package com.longx.intelligent.android.ichat2.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowInsetsController;
import android.view.WindowManager;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by LONG on 2024/3/26 at 7:26 PM.
 */
public class WindowAndSystemUiUtil {

    public static int getStatusBarHeight(final Context context) {
        final Resources resources = context.getResources();
        final int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0)
            return resources.getDimensionPixelSize(resourceId);
        else
            return (int) Math.ceil((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? 24 : 25) * resources.getDisplayMetrics().density);
    }

    public static int getNavigationBarHeight(Context context) {
        Point point = getNavigationBarSize(context);
//        int statusBarHeight = getStatusBarHeight(context);
        int height = point.y;
//        if (isNotchDisplay(statusBarHeight)) {
//            height = height - statusBarHeight;
//        }
        return height;
    }

    private static Point getNavigationBarSize(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Point appUsableSize = getAppUsableScreenSize(context);
            Point realScreenSize = getRealScreenSize(context);
            // navigation bar on the right
            if (appUsableSize.x < realScreenSize.x) {
                return new Point(realScreenSize.x - appUsableSize.x, appUsableSize.y);
            }
            // navigation bar at the bottom
            if (appUsableSize.y < realScreenSize.y) {
                return new Point(appUsableSize.x, realScreenSize.y - appUsableSize.y);
            }
            // navigation bar is not present
            return new Point();
        }
        return new Point();
    }

    private static Point getAppUsableScreenSize(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Point size = new Point();
        if (null != windowManager) {
            Display display = windowManager.getDefaultDisplay();
            display.getSize(size);
        }
        return size;
    }

    public static Point getRealScreenSize(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Point size = new Point();
        if (null != windowManager) {
            Display display = windowManager.getDefaultDisplay();
            if (Build.VERSION.SDK_INT >= 17) {
                display.getRealSize(size);
            } else {
                try {
                    size.x = (Integer) Display.class.getMethod("getRawWidth").invoke(display);
                    size.y = (Integer) Display.class.getMethod("getRawHeight").invoke(display);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }
        }
        return size;
    }

    private static boolean isNotchDisplay(int statusBarHeight) {
        int normalStatusBarHeight = dpToPxForNav(25);
        return statusBarHeight > normalStatusBarHeight;
    }

    private static int dpToPxForNav(float dp) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return Math.round(px);
    }

    public static boolean extendContentUnderSystemBars(Activity activity, View[] toSetStatusInsetsViews, View[] toSetNavigationInsetsViews, int notTranslucentNavigationBarColor){
        boolean translucentNavigation = true;
        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q){
            translucentNavigation = false;
        }else {
            if(!isGestureNavigationUsed(activity)) {
                translucentNavigation = false;
            }else {
                activity.getWindow().setNavigationBarColor(Color.TRANSPARENT);
                activity.getWindow().setDecorFitsSystemWindows(false);
                WindowInsetsController insetsController = activity.getWindow().getInsetsController();
                if (insetsController != null) {
                    insetsController.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
                }
            }
        }
        if(!translucentNavigation){
            activity.getWindow().setNavigationBarColor(notTranslucentNavigationBarColor);
        }
        if(toSetStatusInsetsViews != null){
            for (View toSetStatusInsetsView : toSetStatusInsetsViews) {
                toSetStatusInsetsView.setPadding(toSetStatusInsetsView.getPaddingLeft(),
                        toSetStatusInsetsView.getPaddingTop() + getStatusBarHeight(activity),
                        toSetStatusInsetsView.getPaddingRight(), toSetStatusInsetsView.getPaddingBottom());
            }
        }
        if(toSetNavigationInsetsViews != null && translucentNavigation){
            for (View toSetNavigationInsetsView : toSetNavigationInsetsViews) {
                toSetNavigationInsetsView.setPadding(toSetNavigationInsetsView.getPaddingLeft(),
                        toSetNavigationInsetsView.getPaddingTop(), toSetNavigationInsetsView.getPaddingRight(),
                        toSetNavigationInsetsView.getPaddingBottom() + getNavigationBarHeight(activity));
            }
        }
        return translucentNavigation;
    }

    public static boolean isGestureNavigationUsed(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("config_navBarInteractionMode", "integer", "android");
        if (resourceId > 0) {
            int mode = resources.getInteger(resourceId);
            return mode == 2;
        }
        return false;
    }

    private static void setWindowFlag(Window window, final int flags, boolean on) {
        WindowManager.LayoutParams winParams = window.getAttributes();
        if (on) {
            winParams.flags |= flags;
        } else {
            winParams.flags &= ~flags;
        }
        window.setAttributes(winParams);
    }

    private static void setSystemUiVisibility(Window window, int flags, boolean on){
        int systemUiVisibility = window.getDecorView().getSystemUiVisibility();
        if(on){
            systemUiVisibility |= flags;
        }else {
            systemUiVisibility &= ~flags;
        }
        window.getDecorView().setSystemUiVisibility(systemUiVisibility);
    }

    public static void setStatusBarAppearance(Window window, boolean light){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            setSystemBarsAppearance(window, WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS, light);
        } else {
            setSystemUiVisibility(window, View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR, light);
        }
    }

    private static void setSystemBarsAppearance(Window window, int flag, boolean on){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            final WindowInsetsController controller = window.getInsetsController();
            if (controller != null) {
                int systemBarsAppearance = controller.getSystemBarsAppearance();
                if(on){
                    systemBarsAppearance |= flag;
                }else {
                    systemBarsAppearance &= ~flag;
                }
                controller.setSystemBarsAppearance(systemBarsAppearance, flag);
            }
        }
    }

    public static void setLightStatusBar(Activity activity, boolean light){
        View decorView = activity.getWindow().getDecorView();
        int flag = decorView.getSystemUiVisibility();
        if(light) {
            flag |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        }else {
            flag &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        }
        decorView.setSystemUiVisibility(flag);
    }

    public static int getActionBarHeight(Context context){
        TypedValue tv = new TypedValue();
        if (context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            return TypedValue.complexToDimensionPixelSize(tv.data, context.getResources().getDisplayMetrics());
        }
        return -1;
    }

    public static void setTransparentStatusBar(Activity activity) {
        Window window = activity.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);
    }

    public static void setLayoutExtendedToStatusBar(Activity activity) {
        View decorView = activity.getWindow().getDecorView();
        int flags = decorView.getSystemUiVisibility();
        flags |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        decorView.setSystemUiVisibility(flags);
    }

    public static void setSystemUIShown(Activity activity, boolean show) {
        int showFlag = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        int hideFlag = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        View decorView = activity.getWindow().getDecorView();
        int systemUiVisibility = decorView.getSystemUiVisibility();
        if(show){
            systemUiVisibility &= ~hideFlag;
            systemUiVisibility |= showFlag;
        }else {
            systemUiVisibility |= hideFlag;
            systemUiVisibility &= ~ showFlag;
        }
        if(decorView.getSystemUiVisibility() != systemUiVisibility) {
            decorView.setSystemUiVisibility(systemUiVisibility);
        }
    }

}
