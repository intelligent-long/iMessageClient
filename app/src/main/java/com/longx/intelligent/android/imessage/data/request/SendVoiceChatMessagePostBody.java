package com.longx.intelligent.android.imessage.data.request;

/**
 * Created by LONG on 2024/6/27 at 1:59 AM.
 */
public class SendVoiceChatMessagePostBody {
    private String toImessageId;

    public SendVoiceChatMessagePostBody() {
    }

    public SendVoiceChatMessagePostBody(String toImessageId) {
        this.toImessageId = toImessageId;
    }

    public String getToImessageId() {
        return toImessageId;
    }
}
