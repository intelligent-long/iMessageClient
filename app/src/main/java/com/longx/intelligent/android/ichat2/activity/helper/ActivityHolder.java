package com.longx.intelligent.android.ichat2.activity.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by LONG on 2024/1/9 at 8:42 PM.
 */
public class ActivityHolder {

    private static Map<String, List<HoldableActivity>> activityMap = new ConcurrentHashMap<>();

    public static void holdActivity(HoldableActivity activity) {
        synchronized (activityMap) {
            if(activityMap.containsKey(activity.getClass().getName())){
                List<HoldableActivity> activities = activityMap.get(activity.getClass().getName());
                activities.add(activity);
            }else {
                ArrayList<HoldableActivity> activities = new ArrayList<>();
                activities.add(activity);
                activityMap.put(activity.getClass().getName(), activities);
            }
        }
    }

    public static void removeActivity(HoldableActivity activity) {
        synchronized (activityMap){
            List<HoldableActivity> activities = activityMap.get(activity.getClass().getName());
            if(activities != null){
                activities.remove(activity);
            }
        }
    }

    protected static Map<String, List<HoldableActivity>> getActivityMap() {
        return activityMap;
    }
}
