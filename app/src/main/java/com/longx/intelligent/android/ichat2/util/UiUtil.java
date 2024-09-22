package com.longx.intelligent.android.ichat2.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.google.android.material.snackbar.Snackbar;

/**
 * Created by LONG on 2024/1/15 at 2:51 AM.
 */
public class UiUtil {

    public static String getEditTextString(EditText editText){
        Editable editable = editText.getText();
        if(editable == null) return null;
        return editable.toString();
    }

    public static void setSnackbarBottomMargin(Snackbar snackbar, int bottomMargin) {
        View snackBarView = snackbar.getView();
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackBarView.getLayoutParams();
        params.setMargins(params.leftMargin, params.topMargin, params.rightMargin, params.bottomMargin + bottomMargin);
        snackBarView.setLayoutParams(params);
    }

    public static void autoCancelInput(Activity activity, View focusView, MotionEvent motionEvent){
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            if (focusView instanceof EditText) {
                Rect outRect = new Rect();
                focusView.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)motionEvent.getRawX(), (int)motionEvent.getRawY())) {
                    focusView.clearFocus();
                    InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
                }
            }
        }
    }

    public static void autoCancelInput(Activity activity, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            View currentFocusView = activity.getCurrentFocus();
            if (currentFocusView instanceof EditText) {
                if (!isTouchInsideView(motionEvent, currentFocusView)) {
                    ViewGroup viewGroup = (ViewGroup) activity.findViewById(android.R.id.content);
                    if (!isTouchInsideAnyEditText(viewGroup, motionEvent)) {
                        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(currentFocusView.getWindowToken(), 0);
                        currentFocusView.clearFocus();
                    }
                }
            }
        }
    }

    private static boolean isTouchInsideView(MotionEvent motionEvent, View view) {
        Rect outRect = new Rect();
        view.getGlobalVisibleRect(outRect);
        return outRect.contains((int) motionEvent.getRawX(), (int) motionEvent.getRawY());
    }

    private static boolean isTouchInsideAnyEditText(ViewGroup viewGroup, MotionEvent motionEvent) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);
            if (child instanceof ViewGroup) {
                if (isTouchInsideAnyEditText((ViewGroup) child, motionEvent)) {
                    return true;
                }
            } else if (child instanceof EditText) {
                if (isTouchInsideView(motionEvent, child)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void setIconMenuEnabled(MenuItem menuItem, boolean enable){
        menuItem.setEnabled(enable);
        Drawable icon = menuItem.getIcon();
        if(enable){
            if(icon != null){
                icon.setAlpha(255);
            }
        }else {
            if(icon != null){
                icon.setAlpha(66);
            }
        }
    }

    public static void setViewGroupEnabled(ViewGroup viewGroup, boolean enable, boolean changeAlpha){
        viewGroup.setEnabled(enable);
        if(changeAlpha) {
            if (enable) {
                viewGroup.setAlpha(1F);
            } else {
                viewGroup.setAlpha(0.26F);
            }
        }
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);
            setViewEnabled(child, enable, false);
            if (child instanceof ViewGroup) {
                setViewGroupEnabled((ViewGroup) child, enable, false);
            }
        }
    }

    public static void setViewEnabled(View view, boolean enable, boolean changeAlpha) {
        view.setEnabled(enable);
        if(changeAlpha) {
            if (enable) {
                view.setAlpha(1F);
            } else {
                view.setAlpha(0.26F);
            }
        }
    }

    public static void setViewWidth(View view, int width) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params == null) {
            params = new ViewGroup.LayoutParams(width, ViewGroup.LayoutParams.MATCH_PARENT);
        } else {
            params.width = width;
        }
        view.setLayoutParams(params);
    }

    public static void setViewHeight(View view, int height) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params == null) {
            params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
        } else {
            params.height = height;
        }
        view.setLayoutParams(params);
    }

    public static int[] getViewMargin(View view){
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            int leftMargin = params.leftMargin;
            int topMargin = params.topMargin;
            int rightMargin = params.rightMargin;
            int bottomMargin = params.bottomMargin;
            return new int[]{leftMargin, topMargin, rightMargin, bottomMargin};
        }
        return new int[4];
    }

    public static void setViewMargin(View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            params.setMargins(
                    left == -1 ? params.leftMargin : left,
                    top == -1 ? params.topMargin : top,
                    right == -1 ? params.rightMargin : right,
                    bottom == -1 ? params.bottomMargin : bottom);
            view.requestLayout();
        }
    }

    public static int dpToPx(Context context, float dpValue) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * density + 0.5f);
    }

    public static int pxToDp(Context context, float pxValue) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / density + 0.5f);
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void hideKeyboard(EditText editText) {
        InputMethodManager imm = (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        editText.clearFocus();
    }

    public static void openKeyboard(EditText editText) {
        if (editText.requestFocus()) {
            InputMethodManager imm = (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    public static void setViewVisibility(View view, int visibility){
        if(view.getVisibility() != visibility) view.setVisibility(visibility);
    }

    public static boolean isViewCovered(View view) {
        View currentView = view;
        while (currentView.getParent() instanceof ViewGroup) {
            ViewGroup parent = (ViewGroup) currentView.getParent();
            int index = parent.indexOfChild(currentView);

            for (int i = index + 1; i < parent.getChildCount(); i++) {
                View sibling = parent.getChildAt(i);
                Rect viewRect = new Rect();
                Rect siblingRect = new Rect();
                view.getGlobalVisibleRect(viewRect);
                sibling.getGlobalVisibleRect(siblingRect);

                if (Rect.intersects(viewRect, siblingRect)) {
                    return true;
                }
            }
            currentView = parent;
        }
        return false;
    }

    public static boolean isViewOutOfScreen(View view) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);

        int viewLeft = location[0];
        int viewRight = viewLeft + view.getWidth();
        int viewTop = location[1];
        int viewBottom = viewTop + view.getHeight();

        int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;

        return viewRight <= 0 || viewLeft >= screenWidth || viewBottom <= 0 || viewTop >= screenHeight;
    }

    public static boolean isViewVisibleOnScreen(View view){
        return !(isViewCovered(view) || isViewOutOfScreen(view));
    }

}
