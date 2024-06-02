package com.longx.intelligent.android.ichat2.net;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by LONG on 2024/4/29 at 11:59 PM.
 */
public class OkHttpClientCreator {
    public static OkHttpClient client;
    private static final LogInterceptor logInterceptor = new LogInterceptor();;
    static {
        logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
    }

    public static void create(){
        client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .cookieJar(CookieJar.get())
                .addNetworkInterceptor(logInterceptor)
                .build();
    }
}
