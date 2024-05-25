package com.longx.intelligent.android.ichat2.media.data;

/**
 * Created by LONG on 2024/1/30 at 5:05 AM.
 */
public class DirectoryInfo {
    private final String path;
    private final int mediaCount;
    private final long mediaEarliestAddedTime;
    private final long mediaLatestAddedTime;
    private final MediaInfo coverMediaInfo;

    public DirectoryInfo(String path, int mediaCount, long mediaEarliestAddedTime, long mediaLatestAddedTime, MediaInfo coverMediaInfo) {
        this.path = path;
        this.mediaCount = mediaCount;
        this.mediaEarliestAddedTime = mediaEarliestAddedTime;
        this.mediaLatestAddedTime = mediaLatestAddedTime;
        this.coverMediaInfo = coverMediaInfo;
    }

    public String getPath() {
        return path;
    }

    public int getMediaCount() {
        return mediaCount;
    }

    public long getMediaEarliestAddedTime() {
        return mediaEarliestAddedTime;
    }

    public long getMediaLatestAddedTime() {
        return mediaLatestAddedTime;
    }

    public MediaInfo getCoverMediaInfo() {
        return coverMediaInfo;
    }
}