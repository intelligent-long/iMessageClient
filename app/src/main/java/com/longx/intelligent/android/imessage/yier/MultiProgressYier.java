package com.longx.intelligent.android.imessage.yier;

/**
 * Created by LONG on 2024/6/15 at 7:20 PM.
 */
public interface MultiProgressYier {
    void onProgressUpdate(long current, long total, int index, int count);
}
