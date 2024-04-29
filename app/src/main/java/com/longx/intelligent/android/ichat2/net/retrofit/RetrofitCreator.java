package com.longx.intelligent.android.ichat2.net.retrofit;

import android.content.Context;

import com.longx.intelligent.android.ichat2.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.ichat2.data.ServerSetting;
import com.longx.intelligent.android.ichat2.net.CookieJar;
import com.longx.intelligent.android.ichat2.net.OkHttpClientCreator;
import com.longx.intelligent.android.ichat2.net.ServerProperties;
import com.xcheng.retrofit.CompletableCallAdapterFactory;
import com.xcheng.retrofit.LogInterceptor;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * Created by LONG on 2024/1/12 at 4:34 PM.
 */
public class RetrofitCreator {
    public static Retrofit retrofit;

    public static void create(Context context){
        String baseUrl = ServerProperties.getBaseUrl(context);
        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(OkHttpClientCreator.client)
                .addCallAdapterFactory(CompletableCallAdapterFactory.INSTANCE)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
    }

    public static Retrofit createTemporary(String baseUrl){
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(OkHttpClientCreator.client)
                .addCallAdapterFactory(CompletableCallAdapterFactory.INSTANCE)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
    }

}
