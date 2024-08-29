package com.longx.intelligent.android.ichat2.util;

import java.util.Collection;

/**
 * Created by LONG on 2024/8/30 at 上午2:33.
 */
public class CollectionUtil {
    public static <T> boolean hasData(Collection<T> collection){
        return collection != null && !collection.isEmpty();
    }
}
