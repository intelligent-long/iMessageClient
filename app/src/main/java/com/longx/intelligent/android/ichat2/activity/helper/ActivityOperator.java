package com.longx.intelligent.android.ichat2.activity.helper;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;

import com.longx.intelligent.android.ichat2.activity.AuthActivity;
import com.longx.intelligent.android.ichat2.activity.MainActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LONG on 2024/1/16 at 5:38 PM.
 */
public class ActivityOperator extends ActivityHolder{
    public static void finishAll() {
        for (List<HoldableActivity> activities : getActivityMap().values()) {
            activities.forEach(activity -> {
                if (!activity.isFinishing()) {
                    activity.finish();
                }
            });
        }
    }

    @SafeVarargs
    public static void recreateAll(Class<? extends HoldableActivity>... exceptHoldableActivity){
        for (List<HoldableActivity> activities : getActivityMap().values()) {
            activities.forEach(holdableActivity -> {
                boolean isExceptInstance = false;
                for (Class<? extends HoldableActivity> clazz : exceptHoldableActivity) {
                    if(clazz.isInstance(holdableActivity)){
                        isExceptInstance = true;
                    }
                }
                if(!isExceptInstance){
                    holdableActivity.recreate();
                }
            });
        }
    }

    public static void switchToAuth(Context context){
        switchTo(context, new Intent(context, AuthActivity.class), true);
    }

    public static void switchToMain(Context context){
        switchTo(context, new Intent(context, MainActivity.class), false);
    }

    public static void switchTo(Context context, Intent intent, boolean newTask){
        finishAll();
        if(newTask || context instanceof Service){
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    public static <T> List<T> getActivitiesOf(Class<T> clazz){
        List<T> results = new ArrayList<>();
        for (List<HoldableActivity> activities : getActivityMap().values()) {
            for (HoldableActivity activity : activities) {
                if(clazz.isInstance(activity)) {
                    results.add((T) activity);
                }
            }
        }
        return results;
    }

    public static List<HoldableActivity> getActivityList(){
        return ActivityHolder.getActivityList();
    }

    public static HoldableActivity getTopActivity(){
        return getActivityList().get(getActivityList().size() - 1);
    }
}
