package com.longx.intelligent.android.ichat2.net;

import android.content.Context;

import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;

import java.util.Objects;

import okhttp3.HttpUrl;

/**
 * Created by LONG on 2024/1/23 at 7:44 PM.
 */
public class CookieJar {
    private static PersistentCookieJar cookieJar;

    public static synchronized void create(Context context){
        if(cookieJar == null) {
            cookieJar = new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(context));
        }
    }

    public static synchronized PersistentCookieJar get(){
        return cookieJar;
    }

    public static synchronized void clear(){
        cookieJar.clear();
    }

    public static synchronized String getCookieString(String url){
        StringBuilder stringBuilder = new StringBuilder();
        cookieJar.loadForRequest(Objects.requireNonNull(HttpUrl.parse(url))).forEach(cookie -> {
            stringBuilder
                    .append(cookie.name())
                    .append("=")
                    .append(cookie.value())
                    .append("; ");
        });
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        return stringBuilder.toString();
    }
}
