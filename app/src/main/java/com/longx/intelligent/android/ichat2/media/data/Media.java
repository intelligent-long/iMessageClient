package com.longx.intelligent.android.ichat2.media.data;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.longx.intelligent.android.ichat2.media.MediaType;

import java.util.Objects;

/**
 * Created by LONG on 2024/8/6 at 上午12:35.
 */
public class Media implements Parcelable {
    private final MediaType mediaType;
    private final Uri uri;

    public Media(MediaType mediaType, Uri uri) {
        this.mediaType = mediaType;
        this.uri = uri;
    }

    public static final Creator<Media> CREATOR = new Creator<Media>() {
        @Override
        public Media createFromParcel(Parcel in) {
            return new Media(in);
        }

        @Override
        public Media[] newArray(int size) {
            return new Media[size];
        }
    };

    public MediaType getMediaType() {
        return mediaType;
    }

    public Uri getUri() {
        return uri;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(mediaType.ordinal());
        dest.writeParcelable(uri, flags);
    }

    protected Media(Parcel in) {
        mediaType = MediaType.values()[in.readInt()];
        uri = in.readParcelable(Uri.class.getClassLoader());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Media media = (Media) o;
        return mediaType == media.mediaType && Objects.equals(uri, media.uri);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mediaType, uri);
    }
}
