package com.longx.intelligent.android.imessage.data.request;


import java.util.List;

/**
 * Created by LONG on 2024/9/20 at 1:58 AM.
 */
public class MakeBroadcastReplyCommentsToOldPostBody {
    private List<String> replyCommentIds;

    public MakeBroadcastReplyCommentsToOldPostBody() {
    }

    public MakeBroadcastReplyCommentsToOldPostBody(List<String> replyCommentIds) {
        this.replyCommentIds = replyCommentIds;
    }

    public List<String> getReplyCommentIds() {
        return replyCommentIds;
    }
}
