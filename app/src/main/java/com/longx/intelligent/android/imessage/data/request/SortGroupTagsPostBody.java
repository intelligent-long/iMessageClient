package com.longx.intelligent.android.imessage.data.request;

import java.util.Map;

/**
 * Created by LONG on 2024/6/4 at 10:41 AM.
 */
public class SortGroupTagsPostBody {

    private Map<String, Integer> orderMap;

    public SortGroupTagsPostBody() {
    }

    public SortGroupTagsPostBody(Map<String, Integer> orderMap) {
        this.orderMap = orderMap;
    }

    public Map<String, Integer> getOrderMap() {
        return orderMap;
    }
}
