package com.longx.intelligent.android.imessage.data.request;

import java.util.List;

/**
 * Created by LONG on 2024/9/20 at 1:58 AM.
 */
public class MakeBroadcastCommentsToOldPostBody {
    private List<String> commentIds;

    public MakeBroadcastCommentsToOldPostBody() {
    }

    public MakeBroadcastCommentsToOldPostBody(List<String> commentIds) {
        this.commentIds = commentIds;
    }

    public List<String> getCommentIds() {
        return commentIds;
    }
}
