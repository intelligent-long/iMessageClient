package com.longx.intelligent.android.imessage.data.request;

/**
 * Created by LONG on 2024/6/10 at 7:12 PM.
 */
public class SendFileChatMessagePostBody {
    private String toImessageId;
    private String fileName;

    public SendFileChatMessagePostBody() {
    }

    public SendFileChatMessagePostBody(String toImessageId, String fileName) {
        this.toImessageId = toImessageId;
        this.fileName = fileName;
    }

    public String getToImessageId() {
        return toImessageId;
    }

    public String getFileName() {
        return fileName;
    }
}
