package com.longx.intelligent.android.ichat2.behaviorcomponents;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.ichat2.util.ColorUtil;
import com.longx.intelligent.android.ichat2.util.UiUtil;

/**
 * Created by LONG on 2024/1/30 at 6:26 AM.
 */
public class MessageDisplayer {
    public enum Duration{SHORT, LONG}

    public static void showToast(Context context, String message, int duration) {
        Toast.makeText(context, message, duration).show();
    }

    public static Snackbar showSnackbar(Activity activity, String message, int duration){
        return showSnackbar(activity, activity.getWindow().getDecorView(), message, duration, true);
    }

    public static Snackbar showSnackbar(Activity activity, View view, String message, int duration){
        return showSnackbar(activity, view, message, duration, false);
    }

    public static Snackbar showSnackbar(Activity activity, View view, String message, int duration, boolean setBottomMargin){
        try {
            Snackbar snackbar = Snackbar.make(view, message, duration);
            int snackbarAppearance = SharedPreferencesAccessor.DefaultPref.getSnackbarAppearance(activity);
            switch (snackbarAppearance){
                case 0:
                    break;
                case 1:
                    snackbar.setBackgroundTint(ColorUtil.getAttrColor(activity, com.google.android.material.R.attr.colorOnSurfaceInverse));
                    snackbar.setTextColor(ColorUtil.getAttrColor(activity, com.google.android.material.R.attr.colorOnBackground));
                    snackbar.setActionTextColor(ColorUtil.getAttrColor(activity, com.google.android.material.R.attr.colorPrimary));
                    break;
                case 2:
                    snackbar.setBackgroundTint(ColorUtil.getAttrColor(activity, com.google.android.material.R.attr.colorTertiaryContainer));
                    snackbar.setTextColor(ColorUtil.getAttrColor(activity, com.google.android.material.R.attr.colorOnBackground));
                    snackbar.setActionTextColor(ColorUtil.getAttrColor(activity, com.google.android.material.R.attr.colorAccent));
                    break;
            }
            TextView snackbarTextView = snackbar.getView().findViewById(com.google.android.material.R.id.snackbar_text);
            snackbarTextView.setMaxLines(Integer.MAX_VALUE);
            if(setBottomMargin){
                UiUtil.setSnackbarBottomMargin(snackbar, 210);
            }
            snackbar.show();
            return snackbar;
        }catch (Exception e){
            Log.e(Application.class.getName(), "showSnackBar() 出错", e);
            return null;
        }
    }

    public static void autoShow(Context context, String message, Duration duration){
        if(context instanceof Activity){
            int snackbarDuration;
            if(duration.equals(Duration.LONG)){
                snackbarDuration = Snackbar.LENGTH_LONG;
            }else {
                snackbarDuration = Snackbar.LENGTH_SHORT;
            }
            ((Activity)context).runOnUiThread(() ->  showSnackbar((Activity) context, message, snackbarDuration));
        }else {
            int toastDuration;
            if(duration.equals(Duration.LONG)){
                toastDuration = Toast.LENGTH_LONG;
            }else {
                toastDuration = Toast.LENGTH_SHORT;
            }
            new Handler(Looper.getMainLooper()).post(() -> showToast(context, message, toastDuration));
        }
    }

    public static Snackbar showSnackbar(Activity activity, View customView, int duration){
        return showSnackbar(activity.getWindow().getDecorView(), customView, duration, true);
    }

    public static Snackbar showSnackbar(View view, View customView, int duration){
        return showSnackbar(view, customView, duration, false);
    }

    public static Snackbar showSnackbar(View view, View customView, int duration, boolean setBottomMargin){
        try {
            Snackbar snackbar = Snackbar.make(view, "", duration);
            @SuppressLint("RestrictedApi") Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();
            snackbarLayout.removeAllViews();
            snackbarLayout.addView(customView);
            if(setBottomMargin){
                UiUtil.setSnackbarBottomMargin(snackbar, 210);
            }
            snackbar.show();
            return snackbar;
        }catch (Exception e){
            Log.e(Application.class.getName(), "showSnackBar() 出错", e);
            return null;
        }
    }
}
