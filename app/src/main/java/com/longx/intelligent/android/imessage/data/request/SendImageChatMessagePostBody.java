package com.longx.intelligent.android.imessage.data.request;

/**
 * Created by LONG on 2024/5/12 at 10:53 PM.
 */
public class SendImageChatMessagePostBody {
    private String toImessageId;
    private String imageFileName;

    public SendImageChatMessagePostBody() {
    }

    public SendImageChatMessagePostBody(String toImessageId, String imageFileName) {
        this.toImessageId = toImessageId;
        this.imageFileName = imageFileName;
    }

    public String getToImessageId() {
        return toImessageId;
    }

    public String getImageFileName() {
        return imageFileName;
    }
}
