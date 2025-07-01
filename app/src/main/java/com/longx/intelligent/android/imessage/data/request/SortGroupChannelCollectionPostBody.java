package com.longx.intelligent.android.imessage.data.request;

import java.util.Map;

/**
 * Created by LONG on 2025/7/2 at 1:35 AM.
 */
public class SortGroupChannelCollectionPostBody {

    private Map<String, Integer> orderMap;

    public SortGroupChannelCollectionPostBody() {
    }

    public SortGroupChannelCollectionPostBody(Map<String, Integer> orderMap) {
        this.orderMap = orderMap;
    }

    public Map<String, Integer> getOrderMap() {
        return orderMap;
    }
}
