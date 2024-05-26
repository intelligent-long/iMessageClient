package com.longx.intelligent.android.ichat2.media.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.util.Size;

import com.longx.intelligent.android.ichat2.util.ErrorLogger;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by LONG on 2024/1/29 at 11:43 PM.
 */
public class MediaHelper {
    private static final int VIDEO_THUMBNAIL_MAX_LONG = 3000;

    public static Bitmap getVideoThumbnail(String videoPath) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(videoPath);
            int videoWidth = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
            int videoHeight = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
            int targetWidth;
            int targetHeight;
            if(videoWidth > videoHeight){
                targetWidth = VIDEO_THUMBNAIL_MAX_LONG;
                targetHeight = (int)(VIDEO_THUMBNAIL_MAX_LONG * (((double)videoHeight) / videoWidth));
            }else {
                targetHeight = VIDEO_THUMBNAIL_MAX_LONG;
                targetWidth = (int)(VIDEO_THUMBNAIL_MAX_LONG * (((double)videoWidth) / videoHeight));
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                return retriever.getScaledFrameAtTime(-1, MediaMetadataRetriever.OPTION_CLOSEST_SYNC, targetWidth, targetHeight);
            }else {
                return retriever.getFrameAtTime(1);
            }
        } catch (IllegalArgumentException e) {
            ErrorLogger.log(MediaHelper.class, e);
            return null;
        } finally {
            try {
                retriever.release();
            } catch (IOException e) {
                ErrorLogger.log(MediaHelper.class, e);
            }
        }
    }

    public static Bitmap getVideoThumbnail(Context context, Uri videoUri) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(context, videoUri);
            return retriever.getFrameAtTime();
        } catch (IllegalArgumentException e) {
            ErrorLogger.log(MediaHelper.class, e);
            return null;
        } finally {
            try {
                retriever.release();
            } catch (IOException e) {
                ErrorLogger.log(MediaHelper.class, e);
            }
        }
    }

    public static Size getImageSize(byte[] image) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        ByteArrayInputStream bais = new ByteArrayInputStream(image);
        int orientation = getImageOrientation(bais);
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
            case ExifInterface.ORIENTATION_ROTATE_270:
                return new Size(height, width);
            default:
                return new Size(width, height);
        }
    }

    public static int getImageOrientation(InputStream is) {
        try {
            ExifInterface exifInterface = new ExifInterface(is);
            return exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        } catch (IOException e) {
            e.printStackTrace();
            return ExifInterface.ORIENTATION_NORMAL;
        }
    }
}
