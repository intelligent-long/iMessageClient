package com.longx.intelligent.android.ichat2.data.request;

/**
 * Created by LONG on 2024/7/28 at 2:05 AM.
 */
public class SendBroadcastPostBody {

    private String text;

    public SendBroadcastPostBody() {
    }

    public SendBroadcastPostBody(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
