package com.longx.intelligent.android.ichat2.data.request;

/**
 * Created by LONG on 2024/6/27 at 1:59 AM.
 */
public class SendVoiceChatMessagePostBody {
    private String toIchatId;

    public SendVoiceChatMessagePostBody() {
    }

    public SendVoiceChatMessagePostBody(String toIchatId) {
        this.toIchatId = toIchatId;
    }

    public String getToIchatId() {
        return toIchatId;
    }
}
