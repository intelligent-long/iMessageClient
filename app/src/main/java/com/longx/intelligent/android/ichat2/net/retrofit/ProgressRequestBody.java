package com.longx.intelligent.android.ichat2.net.retrofit;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by LONG on 2024/6/15 at 7:15 PM.
 */
public class ProgressRequestBody extends RequestBody {
    private final InputStream inputStream;
    private final MediaType contentType;
    private final ProgressYier progressYier;
    private final long contentLength;

    public interface ProgressYier {
        void onProgress(long bytesWritten, long contentLength);
    }

    public ProgressRequestBody(InputStream inputStream, MediaType contentType, long contentLength, ProgressYier progressYier) {
        this.inputStream = inputStream;
        this.contentType = contentType;
        this.contentLength = contentLength;
        this.progressYier = progressYier;
    }

    @Override
    public MediaType contentType() {
        return contentType;
    }

    @Override
    public long contentLength() {
        return contentLength;
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        Source source = null;
        try {
            source = Okio.source(inputStream);
            long totalBytesRead = 0;
            long read;
            byte[] buffer = new byte[10240];

            while ((read = inputStream.read(buffer)) != -1) {
                totalBytesRead += read;
                sink.write(buffer, 0, (int) read);
                if (progressYier != null) {
                    progressYier.onProgress(totalBytesRead, contentLength);
                }
            }
        } finally {
            if (source != null) {
                source.close();
            }
        }
    }
}
