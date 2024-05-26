package com.longx.intelligent.android.ichat2.ui.glide;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;
import com.bumptech.glide.signature.ObjectKey;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class ByteBufferModelLoader implements ModelLoader<byte[], InputStream> {

    @Override
    public LoadData<InputStream> buildLoadData(byte[] bytes, int width, int height, Options options) {
        return new LoadData<>(new ObjectKey(bytes), new ByteBufferFetcher(bytes));
    }

    @Override
    public boolean handles(byte[] bytes) {
        return true;
    }

    public static class Factory implements ModelLoaderFactory<byte[], InputStream> {
        @Override
        public ModelLoader<byte[], InputStream> build(MultiModelLoaderFactory multiFactory) {
            return new ByteBufferModelLoader();
        }

        @Override
        public void teardown() {
            // Do nothing.
        }
    }

    private static class ByteBufferFetcher implements DataFetcher<InputStream> {

        private final byte[] model;

        ByteBufferFetcher(byte[] model) {
            this.model = model;
        }

        @Override
        public void loadData(Priority priority, DataCallback<? super InputStream> callback) {
            callback.onDataReady(new ByteArrayInputStream(model));
        }

        @Override
        public void cleanup() {
            // Do nothing.
        }

        @Override
        public void cancel() {
            // Do nothing.
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
}
