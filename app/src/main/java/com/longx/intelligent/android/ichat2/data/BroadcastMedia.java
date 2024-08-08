package com.longx.intelligent.android.ichat2.data;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.longx.intelligent.android.ichat2.util.ErrorLogger;

/**
 * Created by LONG on 2024/8/6 at 7:08 PM.
 */
public class BroadcastMedia implements Parcelable {
    public static final int TYPE_IMAGE = 0;
    public static final int TYPE_VIDEO = 1;
    private String mediaId;
    private String broadcastId;
    private byte[] media;
    private int type;
    private String extension;
    private int index;
    private Size size;

    public BroadcastMedia() {
    }

    public BroadcastMedia(String mediaId, String broadcastId, byte[] media, int type, String extension, int index, Size size) {
        this.mediaId = mediaId;
        this.broadcastId = broadcastId;
        this.media = media;
        this.type = type;
        this.extension = extension;
        this.index = index;
        this.size = size;
    }

    public static final Creator<BroadcastMedia> CREATOR = new Creator<BroadcastMedia>() {
        @Override
        public BroadcastMedia createFromParcel(Parcel in) {
            return new BroadcastMedia(in);
        }

        @Override
        public BroadcastMedia[] newArray(int size) {
            return new BroadcastMedia[size];
        }
    };

    public String getMediaId() {
        return mediaId;
    }

    public String getBroadcastId() {
        return broadcastId;
    }

    public byte[] getMedia() {
        return media;
    }

    public int getType() {
        return type;
    }

    public String getExtension() {
        return extension;
    }

    public int getIndex() {
        return index;
    }

    public Size getSize() {
        return size;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(mediaId);
        dest.writeString(broadcastId);
        dest.writeByteArray(media);
        dest.writeInt(type);
        dest.writeString(extension);
        dest.writeInt(index);
        if(size != null) {
            dest.writeInt(size.getWidth());
            dest.writeInt(size.getHeight());
        } else {
            dest.writeInt(-1);
            dest.writeInt(-1);
        }
    }

    protected BroadcastMedia(Parcel in) {
        mediaId = in.readString();
        broadcastId = in.readString();
        media = in.createByteArray();
        type = in.readInt();
        extension = in.readString();
        index = in.readInt();
        int width = in.readInt();
        int height = in.readInt();
        if(width != -1 && height != -1) {
            size = new Size(width, height);
        }
    }
}
