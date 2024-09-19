package com.longx.intelligent.android.ichat2.data.request;

import java.util.List;

/**
 * Created by LONG on 2024/9/20 at 1:58 AM.
 */
public class MakeBroadcastLikesToOldPostBody {
    private List<String> likeIds;

    public MakeBroadcastLikesToOldPostBody() {
    }

    public MakeBroadcastLikesToOldPostBody(List<String> likeIds) {
        this.likeIds = likeIds;
    }

    public List<String> getLikeIds() {
        return likeIds;
    }
}
