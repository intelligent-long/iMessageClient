package com.longx.intelligent.android.ichat2.net.okhttp.caller;

import android.content.Context;
import android.util.Log;

import com.longx.intelligent.android.ichat2.behaviorcomponents.MessageDisplayer;
import com.longx.intelligent.android.ichat2.util.ErrorLogger;
import com.longx.intelligent.android.ichat2.util.JsonUtil;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

/**
 * Created by LONG on 2024/1/4 at 5:35 PM.
 */
public class ApiCaller {

    public interface CallYier<T>{
        void failure(Exception e);
        void notOk(int code, Response response);
        void ok(T data, Response response);
    }

    public static class BaseCallYier<T> implements CallYier<T>{
        private final Context context;
        private boolean showErrorInfo = true;

        public BaseCallYier(Context context) {
            this.context = context;
        }

        public BaseCallYier(Context context, boolean showErrorInfo) {
            this.context = context;
            this.showErrorInfo = showErrorInfo;
        }

        @Override
        public void failure(Exception e) {
            if(showErrorInfo) {
                if (context != null) {
                    MessageDisplayer.autoShow(context, "无法连接到服务器 > " + e.getClass().getName(), MessageDisplayer.Duration.LONG);
                }
            }
        }

        @Override
        public void notOk(int code, Response response) {
            if(showErrorInfo) {
                if (context != null) {
                    MessageDisplayer.autoShow(context, "HTTP 状态码异常  >  " + code, MessageDisplayer.Duration.LONG);
                }
            }
        }

        @Override
        public void ok(T data, Response response) {

        }
    }

    public static class FailureResponseCodeException extends IOException{
        private final Response response;
        public FailureResponseCodeException(String message, Response response) {
            super(message);
            this.response = response;
        }
    }

    public static <T> void callApi(String url, Class<T> clazz, BaseCallYier<T> yier) {
        new Thread(() -> {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).build();
            try(Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new FailureResponseCodeException("非预期状态码 " + response, response);
                }
                String json = response.body().string();
                Log.d(ApiCaller.class.getName(), ">> START " + url + "\n" + JsonUtil.format(json) + "\n<< END");
                T data = JsonUtil.toObject(json, clazz);
                yier.ok(data, response);
            }catch (FailureResponseCodeException e){
                ErrorLogger.log(e);
                yier.notOk(e.response.code(), e.response);
            }catch (Exception e){
                ErrorLogger.log(e);
                yier.failure(e);
            }
        }).start();
    }

}
