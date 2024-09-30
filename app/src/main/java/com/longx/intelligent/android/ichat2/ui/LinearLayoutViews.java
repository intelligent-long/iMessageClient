package com.longx.intelligent.android.ichat2.ui;

import android.app.Activity;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LONG on 2024/9/25 at 上午10:20.
 */
public abstract class LinearLayoutViews<T> {
    private final Activity activity;
    private final LinearLayout linearLayout;
    private final List<T> allItems = new ArrayList<>();
    private View footerView;

    public LinearLayoutViews(Activity activity, LinearLayout linearLayout) {
        this.activity = activity;
        this.linearLayout = linearLayout;
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
}
