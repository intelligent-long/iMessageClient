package com.longx.intelligent.android.ichat2.data.request;

/**
 * Created by LONG on 2024/6/20 at 11:20 PM.
 */
public class SendVideoChatMessagePostBody {
    private String toIchatId;
    private String videoFileName;

    public SendVideoChatMessagePostBody() {
    }

    public SendVideoChatMessagePostBody(String toIchatId, String videoFileName) {
        this.toIchatId = toIchatId;
        this.videoFileName = videoFileName;
    }

    public String getToIchatId() {
        return toIchatId;
    }

    public String getVideoFileName() {
        return videoFileName;
    }
}
