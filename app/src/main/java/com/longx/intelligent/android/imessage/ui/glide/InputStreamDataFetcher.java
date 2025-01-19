package com.longx.intelligent.android.imessage.ui.glide;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.data.DataFetcher;

import java.io.IOException;
import java.io.InputStream;

public class InputStreamDataFetcher implements DataFetcher<InputStream> {
    private final InputStream inputStream;

    public InputStreamDataFetcher(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public void loadData(Priority priority, DataCallback<? super InputStream> callback) {
        callback.onDataReady(inputStream);
    }

    @Override
    public void cleanup() {
        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void cancel() {
        // No cancellation necessary
    }

    @Override
    public Class<InputStream> getDataClass() {
        return InputStream.class;
    }

    @Override
    public DataSource getDataSource() {
        return DataSource.LOCAL;
    }
}
