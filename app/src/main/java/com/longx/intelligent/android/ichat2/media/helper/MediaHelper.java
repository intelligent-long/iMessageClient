package com.longx.intelligent.android.ichat2.media.helper;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
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
        return getImageSize(bitmap, image);
    }

    public static Size getImageSize(Bitmap bitmap, byte[] image) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        if(image == null){
            return new Size(width, height);
        }else {
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

    public static Size getVideoSize(String videoPath) {
        int videoWidth = 0;
        int videoHeight = 0;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(videoPath);
            videoWidth = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
            videoHeight = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
        } catch (IllegalArgumentException e) {
            ErrorLogger.log(MediaHelper.class, e);
        } finally {
            try {
                retriever.release();
            } catch (IOException e) {
                ErrorLogger.log(MediaHelper.class, e);
            }
        }
        if(videoWidth == 0 || videoHeight == 0) {
            Bitmap videoThumbnail = getVideoThumbnail(videoPath);
            if(videoThumbnail != null) {
                return getImageSize(videoThumbnail, null);
            }
        }
        return new Size(videoWidth, videoHeight);
    }

    public static Size getVideoSize(Context context, Uri videoUri) {
        int videoWidth = 0;
        int videoHeight = 0;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(context, videoUri);
            videoWidth = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
            videoHeight = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
            return new Size(videoWidth, videoHeight);
        } catch (IllegalArgumentException e) {
            ErrorLogger.log(MediaHelper.class, e);
        } finally {
            try {
                retriever.release();
            } catch (IOException e) {
                ErrorLogger.log(MediaHelper.class, e);
            }
        }
        if(videoWidth == 0 || videoHeight == 0) {
            Bitmap videoThumbnail = getVideoThumbnail(context, videoUri);
            if(videoThumbnail != null) {
                return getImageSize(videoThumbnail, null);
            }
        }
        return new Size(videoWidth, videoHeight);
    }

    public static Bitmap getRotatedBitmap(ContentResolver contentResolver, Uri uri) {
        try {
            InputStream inputStream = contentResolver.openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            ExifInterface exif = new ExifInterface(inputStream);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return rotateBitmap(bitmap, 90);
                case ExifInterface.ORIENTATION_ROTATE_180:
                    return rotateBitmap(bitmap, 180);
                case ExifInterface.ORIENTATION_ROTATE_270:
                    return rotateBitmap(bitmap, 270);
                default:
                    return bitmap;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Bitmap rotateBitmap(Bitmap bitmap, float degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }
}
