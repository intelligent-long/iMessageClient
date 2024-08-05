package com.longx.intelligent.android.ichat2.data.request;

import java.util.List;

/**
 * Created by LONG on 2024/7/28 at 2:05 AM.
 */
public class SendBroadcastPostBody {
    public static final int MEDIA_TYPE_IMAGE = 0;
    public static final int MEDIA_TYPE_VIDEO = 1;

    private String text;
    private List<Integer> mediaTypes;

    public SendBroadcastPostBody() {
    }

    public SendBroadcastPostBody(String text, List<Integer> mediaTypes) {
        this.text = text;
        this.mediaTypes = mediaTypes;
    }

    public String getText() {
        return text;
    }

    public List<Integer> getMediaTypes() {
        return mediaTypes;
    }
}
