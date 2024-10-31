package com.longx.intelligent.android.ichat2.net.retrofit;

import android.content.Context;

import com.longx.intelligent.android.ichat2.net.BaseUrlProvider;
import com.longx.intelligent.android.ichat2.net.okhttp.OkHttpClientCreator;
import com.xcheng.retrofit.CompletableCallAdapterFactory;

import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * Created by LONG on 2024/1/12 at 4:34 PM.
 */
public class RetrofitCreator {
    public static Retrofit retrofit;

    public static void create(Context context) {
        String baseUrl = BaseUrlProvider.getHttpBaseUrl(context, true);
        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl == null ? "http://." : baseUrl)
                .client(OkHttpClientCreator.client)
                .addCallAdapterFactory(CompletableCallAdapterFactory.INSTANCE)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
    }

    public static Retrofit customBaseUrl(String baseUrl){
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(OkHttpClientCreator.client)
                .addCallAdapterFactory(CompletableCallAdapterFactory.INSTANCE)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
    }

    public static Retrofit customTimeout(Context context, long connectTimeout, long readTimeout, long writeTimeout, boolean fetchServerLocationAndStoreCentralServerConfig) throws Exception {
        String baseUrl = BaseUrlProvider.getHttpBaseUrl(context, fetchServerLocationAndStoreCentralServerConfig);
        if(baseUrl == null) throw new Exception("无法获取到基础 URL");
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(OkHttpClientCreator.customTimeout(connectTimeout, readTimeout, writeTimeout))
                .addCallAdapterFactory(CompletableCallAdapterFactory.INSTANCE)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
    }

}
