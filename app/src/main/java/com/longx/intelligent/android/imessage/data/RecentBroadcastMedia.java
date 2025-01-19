package com.longx.intelligent.android.imessage.data;

/**
 * Created by LONG on 2024/9/8 at 下午8:32.
 */
public class RecentBroadcastMedia {
    private String ichatId;
    private String broadcastId;
    private String mediaId;
    private int type;
    private String extension;
    private long videoDuration;
    private int index;

    public RecentBroadcastMedia() {
    }

    public RecentBroadcastMedia(String ichatId, String broadcastId, String mediaId, int type, String extension, long videoDuration, int index) {
        this.ichatId = ichatId;
        this.broadcastId = broadcastId;
        this.mediaId = mediaId;
        this.type = type;
        this.extension = extension;
        this.videoDuration = videoDuration;
        this.index = index;
    }

    public String getIchatId() {
        return ichatId;
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
