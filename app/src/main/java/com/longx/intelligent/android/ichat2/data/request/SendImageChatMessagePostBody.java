package com.longx.intelligent.android.ichat2.data.request;

/**
 * Created by LONG on 2024/5/12 at 10:53 PM.
 */
public class SendImageChatMessagePostBody {
    private String toIchatId;
    private String imageFileName;

    public SendImageChatMessagePostBody() {
    }

    public SendImageChatMessagePostBody(String toIchatId, String imageFileName) {
        this.toIchatId = toIchatId;
        this.imageFileName = imageFileName;
    }

    public String getToIchatId() {
        return toIchatId;
    }

    public String getImageFileName() {
        return imageFileName;
    }
}
