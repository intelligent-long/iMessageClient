package com.longx.intelligent.android.ichat2.util;

import java.sql.Connection;
import java.util.Collection;

/**
 * Created by LONG on 2024/8/30 at 上午2:33.
 */
public class CollectionUtil {
    public static <T> boolean isEmpty(Collection<T> collection){
        return collection == null || collection.isEmpty();
    }

    public static <T> boolean equals(Collection<T> col1, Collection<T> col2) {
        if (col1 == null || col2 == null) {
            return col1 == col2;
        }
        if (col1.size() != col2.size()) {
            return false;
        }

        return col1.equals(col2);
    }
}
