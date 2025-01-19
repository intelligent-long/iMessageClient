package com.longx.intelligent.android.imessage.util;

import android.content.Context;
import android.media.MediaMetadataRetriever;

import com.longx.intelligent.android.imessage.behaviorcomponents.MessageDisplayer;

import java.io.File;
import java.io.IOException;

/**
 * Created by LONG on 2023/9/27 at 9:52 PM.
 */
public class AudioUtil {

    public static long getDuration(Context context, String path) {
        long millSecond;
        File file = new File(path);
        if (!file.exists()) {
            MessageDisplayer.autoShow(context, "文件不存在", MessageDisplayer.Duration.LONG);
            return -1;
        }
        try (MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever()) {
            mediaMetadataRetriever.setDataSource(path);
            String duration = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            millSecond = Long.parseLong(duration);
        } catch (IOException | NumberFormatException e) {
            ErrorLogger.log(AudioUtil.class, e);
            MessageDisplayer.autoShow(context, "获取音频长度失败", MessageDisplayer.Duration.LONG);
            return -1;
        }
        return millSecond;
    }

}
