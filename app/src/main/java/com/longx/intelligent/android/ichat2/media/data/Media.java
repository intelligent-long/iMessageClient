package com.longx.intelligent.android.ichat2.media.data;

import android.net.Uri;

import com.longx.intelligent.android.ichat2.media.MediaType;

/**
 * Created by LONG on 2024/8/6 at 上午12:35.
 */
public class Media {
    private final MediaType mediaType;
    private final Uri uri;

    public Media(MediaType mediaType, Uri uri) {
        this.mediaType = mediaType;
        this.uri = uri;
    }

    public MediaType getMediaType() {
        return mediaType;
    }

    public Uri getUri() {
        return uri;
    }
}
