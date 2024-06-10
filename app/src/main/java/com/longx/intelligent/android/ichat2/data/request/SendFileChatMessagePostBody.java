package com.longx.intelligent.android.ichat2.data.request;

/**
 * Created by LONG on 2024/6/10 at 7:12 PM.
 */
public class SendFileChatMessagePostBody {
    private String toIchatId;
    private String fileExtension;

    public SendFileChatMessagePostBody() {
    }

    public SendFileChatMessagePostBody(String toIchatId, String fileExtension) {
        this.toIchatId = toIchatId;
        this.fileExtension = fileExtension;
    }

    public String getToIchatId() {
        return toIchatId;
    }

    public String getFileExtension() {
        return fileExtension;
    }
}
