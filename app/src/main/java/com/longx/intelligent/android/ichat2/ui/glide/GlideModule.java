package com.longx.intelligent.android.ichat2.ui.glide;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;
import com.longx.intelligent.android.ichat2.net.CookieJar;
import com.longx.intelligent.android.ichat2.net.OkHttpClientCreator;
import com.xcheng.retrofit.LogInterceptor;

import java.io.InputStream;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by LONG on 2023/6/14 at 12:33 AM.
 */
@com.bumptech.glide.annotation.GlideModule
public class GlideModule extends AppGlideModule {

    @Override
    public void applyOptions(@NonNull Context context, GlideBuilder builder) {
        builder
                .setDefaultRequestOptions(
                        new RequestOptions()
                                .format(DecodeFormat.PREFER_ARGB_8888)
                                .skipMemoryCache(false)
                                .timeout(30 * 1000)
                                .encodeFormat(Bitmap.CompressFormat.PNG)
                                .encodeQuality(100)
                                .diskCacheStrategy(DiskCacheStrategy.ALL))
                .setDiskCache(new InternalCacheDiskCacheFactory(context, "glide", 5L * 1024 * 1024 * 1024));
    }

    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {
        super.registerComponents(context, glide, registry);
        registry.replace(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(OkHttpClientCreator.client));
        registry.append(byte[].class, InputStream.class, new ByteBufferModelLoader.Factory());
        registry.append(InputStream.class, InputStream.class, new InputStreamModelLoader.Factory());
    }

    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }
}