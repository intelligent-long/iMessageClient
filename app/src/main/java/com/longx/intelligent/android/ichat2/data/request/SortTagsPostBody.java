package com.longx.intelligent.android.ichat2.data.request;

import java.util.Map;

/**
 * Created by LONG on 2024/6/4 at 10:41 AM.
 */
public class SortTagsPostBody {

    private Map<String, Integer> orderMap;

    public SortTagsPostBody() {
    }

    public SortTagsPostBody(Map<String, Integer> orderMap) {
        this.orderMap = orderMap;
    }

    public Map<String, Integer> getOrderMap() {
        return orderMap;
    }
}
