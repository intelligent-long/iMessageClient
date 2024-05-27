package com.longx.intelligent.android.ichat2.net;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import okhttp3.logging.HttpLoggingInterceptor.Logger;

/**
 * Created by LONG on 2024/5/26 at 10:05 PM.
 */
public class LogInterceptor implements Interceptor {
    private static final int JSON_INDENT = 2;
    private static final String LOG_LEVEL = "LogLevel";
    private final Logger logger;
    private volatile Level level = Level.NONE;

    public LogInterceptor() {
        this(Logger.DEFAULT);
    }

    public LogInterceptor(Logger logger) {
        this.logger = logger;
    }

    /**
     * Change the level at which this interceptor logs.
     */
    public void setLevel(Level level) {
        if (level == null) throw new NullPointerException("level == null. Use Level.NONE instead.");
        this.level = level;
    }

    public Level getLevel() {
        return level;
    }

    @NonNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        Level level = findLevel(chain.request());
        if (level == Level.NONE)
            return chain.proceed(chain.request());

        final StringBuilder builder = new StringBuilder();
        HttpLoggingInterceptor httpInterceptor = new HttpLoggingInterceptor(new Logger() {
            @Override
            public void log(@NonNull String message) {
                append(builder, message);
            }
        });
        //可以单独为某个请求设置日志的级别，避免全局设置的局限性
        httpInterceptor.setLevel(level);
        Response response = httpInterceptor.intercept(chain);
        logger.log(builder.toString());
        return response;
    }

    @NonNull
    private Level findLevel(Request request) {
        //可以单独为某个请求设置日志的级别，避免全局设置的局限性
        String logLevel = request.header(LOG_LEVEL);
        if (logLevel != null) {
            if (logLevel.equalsIgnoreCase("NONE")) {
                return Level.NONE;
            } else if (logLevel.equalsIgnoreCase("BASIC")) {
                return Level.BASIC;
            } else if (logLevel.equalsIgnoreCase("HEADERS")) {
                return Level.HEADERS;
            } else if (logLevel.equalsIgnoreCase("BODY")) {
                return Level.BODY;
            }
        }
        return level;
    }

    private static void append(StringBuilder builder, String message) {
        if (TextUtils.isEmpty(message)) {
            return;
        }
        try {
            // 以{}或者[]形式的说明是响应结果的json数据，需要进行格式化
            if (message.startsWith("{") && message.endsWith("}")) {
                JSONObject jsonObject = new JSONObject(message);
                message = jsonObject.toString(JSON_INDENT);
            } else if (message.startsWith("[") && message.endsWith("]")) {
                JSONArray jsonArray = new JSONArray(message);
                message = jsonArray.toString(JSON_INDENT);
            }
        } catch (JSONException ignored) {
        }
        builder.append(message).append('\n');
    }
}
