package com.longx.intelligent.android.ichat2.data.request;

import com.longx.intelligent.android.ichat2.data.BroadcastPermission;

import java.util.List;

/**
 * Created by LONG on 2024/7/28 at 2:05 AM.
 */
public class SendBroadcastPostBody {

    private String text;

    private List<Integer> mediaTypes;

    private List<String> mediaExtensions;

    private BroadcastPermission broadcastPermission;

    public SendBroadcastPostBody() {
    }

    public SendBroadcastPostBody(String text, List<Integer> mediaTypes, List<String> mediaExtensions, BroadcastPermission broadcastPermission) {
        this.text = text;
        this.mediaTypes = mediaTypes;
        this.mediaExtensions = mediaExtensions;
        this.broadcastPermission = broadcastPermission;
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

    public BroadcastPermission getBroadcastPermission() {
        return broadcastPermission;
    }
}
