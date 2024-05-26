package com.longx.intelligent.android.ichat2.ui.glide;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;
import com.bumptech.glide.signature.ObjectKey;
import com.longx.intelligent.android.ichat2.ui.glide.InputStreamDataFetcher;

import java.io.InputStream;

public class InputStreamModelLoader implements ModelLoader<InputStream, InputStream> {
    @Override
    public LoadData<InputStream> buildLoadData(InputStream model, int width, int height, Options options) {
        return new LoadData<>(new ObjectKey(model), new InputStreamDataFetcher(model));
    }

    @Override
    public boolean handles(InputStream model) {
        return true;
    }

    public static class Factory implements ModelLoaderFactory<InputStream, InputStream> {
        @Override
        public ModelLoader<InputStream, InputStream> build(MultiModelLoaderFactory multiFactory) {
            return new InputStreamModelLoader();
        }

        @Override
        public void teardown() {
            // No teardown necessary
        }
    }
}
