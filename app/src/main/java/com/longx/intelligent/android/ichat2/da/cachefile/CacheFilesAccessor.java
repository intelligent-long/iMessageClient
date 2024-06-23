package com.longx.intelligent.android.ichat2.da.cachefile;

import android.content.Context;
import android.graphics.Bitmap;

import com.longx.intelligent.android.ichat2.da.DataPaths;
import com.longx.intelligent.android.ichat2.media.helper.MediaHelper;
import com.longx.intelligent.android.ichat2.util.ErrorLogger;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by LONG on 2024/4/30 at 12:34 AM.
 */
public class CacheFilesAccessor {

    public static class VideoThumbnail{
        private static void cacheVideoThumbnail(Context context, Bitmap bitmap, String videoPath) throws FileNotFoundException {
            String fileName = DigestUtils.md5Hex(videoPath);
            String tempFileName = fileName + "_temp";
            String tempPath = DataPaths.Cache.getVideoThumbnail(context, tempFileName);
            File tempFile = new File(tempPath);
            if(tempFile.exists()){
                tempFile.delete();
            }
            tempFile.getParentFile().mkdirs();
            FileOutputStream fos = new FileOutputStream(tempFile);
            try {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                String path = DataPaths.Cache.getVideoThumbnail(context, fileName);
                boolean success = tempFile.renameTo(new File(path));
                if(!success){
                    tempFile.delete();
                }
            } finally {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public static File getVideoThumbnailFile(Context context, String videoPath){
            String cacheFileName = DigestUtils.md5Hex(videoPath);
            String path = DataPaths.Cache.getVideoThumbnail(context, cacheFileName);
            return new File(path);
        }

        public static File cacheAndGetVideoThumbnail(Context context, String videoPath) {
            Bitmap thumbnailBitmap = MediaHelper.getVideoThumbnail(videoPath);
            if (thumbnailBitmap != null) {
                try {
                    cacheVideoThumbnail(context, thumbnailBitmap, videoPath);
                    return getVideoThumbnailFile(context, videoPath);
                } catch (FileNotFoundException e) {
                    ErrorLogger.log(e);
                }
            }
            return null;
        }
    }
}
