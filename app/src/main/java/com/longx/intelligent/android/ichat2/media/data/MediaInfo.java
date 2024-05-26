package com.longx.intelligent.android.ichat2.media.data;

import android.content.Context;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.exifinterface.media.ExifInterface;

import com.longx.intelligent.android.ichat2.media.MediaType;
import com.longx.intelligent.android.ichat2.permission.PermissionOperator;
import com.longx.intelligent.android.ichat2.permission.PermissionUtil;
import com.longx.intelligent.android.ichat2.util.ErrorLogger;

import java.io.IOException;
import java.util.Objects;

/**
 * Created by LONG on 2024/2/1 at 12:18 AM.
 */
public class MediaInfo implements Parcelable {
    private final Uri uri;
    private final String path;
    private final MediaType mediaType;
    private final long addedTime;
    private final long modifiedTime;
    private final long photoTakenTime;
    private final long videoDuration;
    private final int imageWidth;
    private final int imageHeight;
    private final int videoWidth;
    private final int videoHeight;
    private ExifInterface exifInterface;

    public MediaInfo(Uri uri, String path, MediaType mediaType, long addedTime, long modifiedTime, long photoTakenTime, long videoDuration, int imageWidth, int imageHeight, int videoWidth, int videoHeight) {
        this.uri = uri;
        this.path = path;
        this.mediaType = mediaType;
        this.addedTime = addedTime;
        this.modifiedTime = modifiedTime;
        this.photoTakenTime = photoTakenTime;
        this.videoDuration = videoDuration;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.videoWidth = videoWidth;
        this.videoHeight = videoHeight;
    }

    protected MediaInfo(Parcel in) {
        uri = in.readParcelable(Uri.class.getClassLoader());
        path = in.readString();
        mediaType = MediaType.valueOf(in.readString());
        addedTime = in.readLong();
        modifiedTime = in.readLong();
        photoTakenTime = in.readLong();
        videoDuration = in.readLong();
        imageWidth = in.readInt();
        imageHeight = in.readInt();
        videoWidth = in.readInt();
        videoHeight = in.readInt();
    }

    public Uri getUri() {
        return uri;
    }

    public String getPath() {
        return path;
    }

    public MediaType getMediaType() {
        return mediaType;
    }

    public long getAddedTime() {
        return addedTime;
    }

    public long getModifiedTime() {
        return modifiedTime;
    }

    public long getPhotoTakenTime() {
        return photoTakenTime;
    }

    public long getVideoDuration() {
        return videoDuration;
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public int getVideoWidth() {
        return videoWidth;
    }

    public int getVideoHeight() {
        return videoHeight;
    }

    @Override
    public String toString() {
        return "MediaInfo{" +
                "uri=" + uri +
                ", path='" + path + '\'' +
                ", mediaType=" + mediaType +
                ", addedTime=" + addedTime +
                ", modifiedTime=" + modifiedTime +
                ", photoTakenTime=" + photoTakenTime +
                ", videoDuration=" + videoDuration +
                ", imageWidth=" + imageWidth +
                ", imageHeight=" + imageHeight +
                ", videoWidth=" + videoWidth +
                ", videoHeight=" + videoHeight +
                ", exifInterface=" + exifInterface +
                '}';
    }

    public ExifInterface readExif(Context context) {
        try {
            if (exifInterface == null) {
                Uri original = uri;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                    original = MediaStore.setRequireOriginal(uri);
                }
                exifInterface = new ExifInterface(Objects.requireNonNull(context.getContentResolver().openInputStream(original)));
            }
        }catch (IOException e){
            ErrorLogger.log(getClass(), e);
        }
        return exifInterface;
    }

    public static final Creator<MediaInfo> CREATOR = new Creator<MediaInfo>() {
        @Override
        public MediaInfo createFromParcel(Parcel in) {
            return new MediaInfo(in);
        }

        @Override
        public MediaInfo[] newArray(int size) {
            return new MediaInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeParcelable(uri, flags);
        dest.writeString(path);
        dest.writeString(mediaType.toString());
        dest.writeLong(addedTime);
        dest.writeLong(modifiedTime);
        dest.writeLong(photoTakenTime);
        dest.writeLong(videoDuration);
        dest.writeInt(imageWidth);
        dest.writeInt(imageHeight);
        dest.writeInt(videoWidth);
        dest.writeInt(videoHeight);
    }
}
