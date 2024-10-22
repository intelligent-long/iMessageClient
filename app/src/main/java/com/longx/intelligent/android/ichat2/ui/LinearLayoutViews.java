package com.longx.intelligent.android.ichat2.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.core.widget.NestedScrollView;

import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.util.ColorUtil;
import com.longx.intelligent.android.ichat2.util.ErrorLogger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LONG on 2024/9/25 at 上午10:20.
 */
public abstract class LinearLayoutViews<T> {
    private final Activity activity;
    private final LinearLayout linearLayout;
    private final NestedScrollView nestedScrollView;
    private final ScrollView scrollView;
    private final List<T> allItems = new ArrayList<>();
    private View footerView;
    private View[] parentViews;

    private int currentHighLightIndex;

    public LinearLayoutViews(Activity activity, LinearLayout linearLayout, NestedScrollView nestedScrollView, View... parentViews) {
        this.activity = activity;
        this.linearLayout = linearLayout;
        this.nestedScrollView = nestedScrollView;
        this.scrollView = null;
        this.parentViews = parentViews;
    }

    public LinearLayoutViews(Activity activity, LinearLayout linearLayout, ScrollView scrollView, View... parentViews) {
        this.activity = activity;
        this.linearLayout = linearLayout;
        this.scrollView = scrollView;
        this.nestedScrollView = null;
        this.parentViews = parentViews;
    }

    public synchronized void addItemsAndShow(List<T> items){
        allItems.addAll(items);
        if(footerView != null){
            linearLayout.removeView(this.footerView);
            items.forEach(item -> linearLayout.addView(getView(item, activity)));
            linearLayout.addView(this.footerView);
        }else {
            items.forEach(item -> linearLayout.addView(getView(item, activity)));
        }
    }

    public synchronized void clear(){
        allItems.clear();
        footerView = null;
        linearLayout.removeAllViews();
    }

    public synchronized void removeView(int position){
        linearLayout.removeViewAt(position);
        allItems.remove(position);
    }

    public synchronized void removeView(T item){
        linearLayout.removeViewAt(allItems.indexOf(item));
        allItems.remove(item);
    }

    public synchronized void updateView(T item){
        int position = allItems.indexOf(item);
        linearLayout.removeViewAt(position);
        linearLayout.addView(getView(item, activity), position);
        allItems.set(position, item);
    }

    public synchronized void updateView(int position, T item){
        linearLayout.removeViewAt(position);
        linearLayout.addView(getView(item, activity), position);
        allItems.set(position, item);
    }

    public abstract View getView(T item, Activity activity);

    public List<T> getAllItems() {
        return allItems;
    }

    public synchronized void setFooter(View footerView){
        if(this.footerView == null){
            linearLayout.addView(footerView);
        }else {
            linearLayout.removeView(this.footerView);
            linearLayout.addView(footerView);
        }
        this.footerView = footerView;
    }

    public synchronized void removeFooter(){
        if(this.footerView != null) {
            linearLayout.removeView(this.footerView);
            this.footerView = null;
        }
    }

    public LinearLayout getLinearLayout() {
        return linearLayout;
    }

    public synchronized void highLight(int index){
        if(currentHighLightIndex != -1){
            getLinearLayout().getChildAt(currentHighLightIndex).setBackgroundColor(Color.TRANSPARENT);
        }
        currentHighLightIndex = index;
        getLinearLayout().getChildAt(index).setBackgroundColor(ColorUtil.getAttrColor(activity, com.google.android.material.R.attr.colorSurfaceContainerLow));
    }

    public synchronized void highLight(T item){
        highLight(allItems.indexOf(item));
    }

    public synchronized void cancelHighLight(int index){
        currentHighLightIndex = -1;
        getLinearLayout().getChildAt(index).setBackgroundColor(Color.TRANSPARENT);
    }

    public synchronized void cancelHighLight(T item){
        cancelHighLight(allItems.indexOf(item));
    }

    public boolean scrollTo(T item, boolean smooth){
        int index = allItems.indexOf(item);
        return scrollTo(index, smooth, false, null);
    }

    public boolean scrollTo(T item, boolean smooth, View.OnTouchListener sourceOnTouchYier){
        int index = allItems.indexOf(item);
        return scrollTo(index, smooth, true, sourceOnTouchYier);
    }

    public boolean scrollTo(int index, boolean smooth){
        return scrollTo(index, smooth, false, null);
    }

    public boolean scrollTo(int index, boolean smooth, View.OnTouchListener sourceOnTouchYier){
        return scrollTo(index, smooth, true, sourceOnTouchYier);
    }

    private int getTopViewsHeight(){
        int topViewsHeight = linearLayout.getTop();
        for (View parentView : parentViews) {
            topViewsHeight += parentView.getTop();
        }
        return topViewsHeight;
    }

    @SuppressLint("ClickableViewAccessibility")
    private boolean scrollTo(int index, boolean smooth, boolean highLight, View.OnTouchListener sourceOnTouchYier){
        if(index < 0) return false;
        View childAt = linearLayout.getChildAt(index);
        if(childAt == null) return false;
        if(scrollView != null) {
            int top = childAt.getTop() + getTopViewsHeight();
            if (smooth) {
                scrollView.smoothScrollTo(0, top);
            } else {
                scrollView.scrollTo(0, top);
            }
            if(highLight) {
                highLight(index);
                scrollView.setOnTouchListener((v, event) -> {
                    cancelHighLight(index);
                    scrollView.setOnTouchListener(sourceOnTouchYier);
                    return true;
                });
            }
        }else if(nestedScrollView != null){
            int top = childAt.getTop() + getTopViewsHeight();
            if (smooth) {
                nestedScrollView.smoothScrollTo(0, top);
            } else {
                nestedScrollView.scrollTo(0, top);
            }
            if(highLight) {
                highLight(index);
                nestedScrollView.setOnTouchListener((v, event) -> {
                    cancelHighLight(index);
                    nestedScrollView.setOnTouchListener(sourceOnTouchYier);
                    return true;
                });
            }
        }
        return true;
    }

    public Activity getActivity() {
        return activity;
    }

    public ScrollView getScrollView() {
        return scrollView;
    }

    public NestedScrollView getNestedScrollView() {
        return nestedScrollView;
    }
}
