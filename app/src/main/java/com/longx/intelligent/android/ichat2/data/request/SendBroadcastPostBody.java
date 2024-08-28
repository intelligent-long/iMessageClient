package com.longx.intelligent.android.ichat2.data.request;

import java.util.List;

/**
 * Created by LONG on 2024/7/28 at 2:05 AM.
 */
public class SendBroadcastPostBody {

    private String text;

    private List<Integer> mediaTypes;

    private List<String> mediaExtensions;

    public SendBroadcastPostBody() {
    }

    public SendBroadcastPostBody(String text, List<Integer> mediaTypes, List<String> mediaExtensions) {
        this.text = text;
        this.mediaTypes = mediaTypes;
        this.mediaExtensions = mediaExtensions;
    }

    public String getText() {
        return text;
    }

    public List<Integer> getMediaTypes() {
        return mediaTypes;
    }

    public List<String> getMediaExtensions() {
        return mediaExtensions;
    }
}
