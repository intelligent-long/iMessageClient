package com.longx.intelligent.android.ichat2.net.okhttp;

import com.longx.intelligent.android.ichat2.util.JsonUtil;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

/**
 * Created by LONG on 2024/1/4 at 5:35 PM.
 */
public class ApiCaller {

    public static <T> T callApi(String url, Class<T> clazz) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        Response response;
        T result;
        try {
            response = client.newCall(request).execute();
            String json = response.body().string();
            result = JsonUtil.toObject(json, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

}
