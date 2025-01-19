package com.longx.intelligent.android.imessage.data.request;

/**
 * Created by LONG on 2024/5/12 at 10:53 PM.
 */
public class SendTextChatMessagePostBody {
    private String toImessageId;
    private String text;

    public SendTextChatMessagePostBody() {
    }

    public SendTextChatMessagePostBody(String toImessageId, String text) {
        this.toImessageId = toImessageId;
        this.text = text;
    }

    public String getToImessageId() {
        return toImessageId;
    }

    public String getText() {
        return text;
    }
}
