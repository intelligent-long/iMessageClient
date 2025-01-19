package com.longx.intelligent.android.imessage.data.request;

/**
 * Created by LONG on 2024/6/20 at 11:20 PM.
 */
public class SendVideoChatMessagePostBody {
    private String toImessageId;
    private String videoFileName;

    public SendVideoChatMessagePostBody() {
    }

    public SendVideoChatMessagePostBody(String toImessageId, String videoFileName) {
        this.toImessageId = toImessageId;
        this.videoFileName = videoFileName;
    }

    public String getToImessageId() {
        return toImessageId;
    }

    public String getVideoFileName() {
        return videoFileName;
    }
}
