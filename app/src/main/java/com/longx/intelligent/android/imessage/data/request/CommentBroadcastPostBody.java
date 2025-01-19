package com.longx.intelligent.android.imessage.data.request;

/**
 * Created by LONG on 2024/9/23 at 6:18 AM.
 */
public class CommentBroadcastPostBody {
    private String broadcastId;
    private String text;
    private String toCommentId;

    public CommentBroadcastPostBody() {
    }

    public CommentBroadcastPostBody(String broadcastId, String text, String toCommentId) {
        this.broadcastId = broadcastId;
        this.text = text;
        this.toCommentId = toCommentId;
    }

    public String getBroadcastId() {
        return broadcastId;
    }

    public String getText() {
        return text;
    }

    public String getToCommentId() {
        return toCommentId;
    }
}
