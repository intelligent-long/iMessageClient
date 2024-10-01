package com.longx.intelligent.android.ichat2.data.request;

/**
 * Created by LONG on 2024/9/23 at 6:18 AM.
 */
public class CommentBroadcastPostBody {
    private String broadcastId;
    private String text;
    private String toCommentId;
    private String toReplyCommentId;

    public CommentBroadcastPostBody() {
    }

    public CommentBroadcastPostBody(String broadcastId, String text, String toCommentId, String toReplyCommentId) {
        this.broadcastId = broadcastId;
        this.text = text;
        this.toCommentId = toCommentId;
        this.toReplyCommentId = toReplyCommentId;
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

    public String getToReplyCommentId() {
        return toReplyCommentId;
    }
}
