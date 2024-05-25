package com.longx.intelligent.android.ichat2.data.request;

/**
 * Created by LONG on 2024/5/12 at 10:53 PM.
 */
public class SendImageChatMessagePostBody {
    private String toIchatId;
    private String imageBase64;
    private String imageExtension;

    public SendImageChatMessagePostBody() {
    }

    public SendImageChatMessagePostBody(String toIchatId, String imageBase64, String imageExtension) {
        this.toIchatId = toIchatId;
        this.imageBase64 = imageBase64;
        this.imageExtension = imageExtension;
    }

    public String getToIchatId() {
        return toIchatId;
    }

    public String getImageBase64() {
        return imageBase64;
    }

    public String getImageExtension() {
        return imageExtension;
    }
}
