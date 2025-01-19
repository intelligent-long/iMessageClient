package com.longx.intelligent.android.imessage.activity.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by LONG on 2024/1/9 at 8:42 PM.
 */
public class ActivityHolder {
    private static final ConcurrentHashMap<String, List<HoldableActivity>> activityMap = new ConcurrentHashMap<>();
    private static List<HoldableActivity> activityList = new ArrayList<>();

    protected static void holdActivity(HoldableActivity activity) {
        synchronized (activityMap) {
            String key = activity.getClass().getName();
            if(activityMap.containsKey(key)){
                List<HoldableActivity> activities = activityMap.get(key);
                activities.add(activity);
            }else {
                ArrayList<HoldableActivity> activities = new ArrayList<>();
                activities.add(activity);
                activityMap.put(key, activities);
            }
        }
    }

    protected static void removeActivity(HoldableActivity activity) {
        synchronized (activityMap){
            String key = activity.getClass().getName();
            List<HoldableActivity> activities = activityMap.get(key);
            if(activities != null){
                activities.remove(activity);
                if(activities.size() == 0){
                    activityMap.remove(key);
                }
            }
        }
    }

    protected static ConcurrentHashMap<String, List<HoldableActivity>> getActivityMap() {
        return activityMap;
    }

    protected static void addToList(HoldableActivity activity){
        activityList.add(activity);
    }

    protected static void removeFromList(HoldableActivity activity){
        activityList.remove(activity);
    }

    protected static List<HoldableActivity> getActivityList() {
        return activityList;
    }
}
