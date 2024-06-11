package com.longx.intelligent.android.ichat2.data.request;

/**
 * Created by LONG on 2024/6/10 at 7:12 PM.
 */
public class SendFileChatMessagePostBody {
    private String toIchatId;
    private String fileName;

    public SendFileChatMessagePostBody() {
    }

    public SendFileChatMessagePostBody(String toIchatId, String fileName) {
        this.toIchatId = toIchatId;
        this.fileName = fileName;
    }

    public String getToIchatId() {
        return toIchatId;
    }

    public String getFileName() {
        return fileName;
    }
}
