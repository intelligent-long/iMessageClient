package com.longx.intelligent.android.imessage.data;

/**
 * Created by LONG on 2024/9/8 at 下午8:32.
 */
public class RecentBroadcastMedia {
    private String imessageId;
    private String broadcastId;
    private String mediaId;
    private int type;
    private String extension;
    private long videoDuration;
    private int index;

    public RecentBroadcastMedia() {
    }

    public RecentBroadcastMedia(String imessageId, String broadcastId, String mediaId, int type, String extension, long videoDuration, int index) {
        this.imessageId = imessageId;
        this.broadcastId = broadcastId;
        this.mediaId = mediaId;
        this.type = type;
        this.extension = extension;
        this.videoDuration = videoDuration;
        this.index = index;
    }

    public String getImessageId() {
        return imessageId;
    }

    public String getBroadcastId() {
        return broadcastId;
    }

    public String getMediaId() {
        return mediaId;
    }

    public int getType() {
        return type;
    }

    public String getExtension() {
        return extension;
    }

    public long getVideoDuration() {
        return videoDuration;
    }

    public int getIndex() {
        return index;
    }
}
