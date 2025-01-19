package com.longx.intelligent.android.imessage.fragment.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by LONG on 2024/1/10 at 6:22 PM.
 */
public class FragmentHolder {

    private static Map<String, List<HoldableFragment>> fragmentMap = new ConcurrentHashMap<>();

    public static void holdFragment(HoldableFragment fragment) {
        synchronized (fragmentMap) {
            if (fragmentMap.containsKey(fragment.getClass().getName())) {
                List<HoldableFragment> fragments = fragmentMap.get(fragment.getClass().getName());
                fragments.add(fragment);
            } else {
                ArrayList<HoldableFragment> fragments = new ArrayList<>();
                fragments.add(fragment);
                fragmentMap.put(fragment.getClass().getName(), fragments);
            }
        }
    }

    public static void removeFragment(HoldableFragment fragment) {
        synchronized (fragmentMap) {
            List<HoldableFragment> fragments = fragmentMap.get(fragment.getClass().getName());
            if (fragments != null) {
                fragments.remove(fragment);
            }
        }
    }

}
