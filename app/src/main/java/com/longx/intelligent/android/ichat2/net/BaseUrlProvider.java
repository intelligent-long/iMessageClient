package com.longx.intelligent.android.ichat2.net;

import android.content.Context;

import com.longx.intelligent.android.ichat2.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.ichat2.net.okhttp.caller.ApiCaller;
import com.longx.intelligent.android.ichat2.net.okhttp.caller.ServerApiCaller;

import java.util.concurrent.CountDownLatch;

import okhttp3.Response;

/**
 * Created by LONG on 2024/10/31 at 下午10:59.
 */
public class BaseUrlProvider {

    private static void fetchServerLocationAndStoreCentralServerConfig(Context context){
        CountDownLatch countDownLatch = new CountDownLatch(1);
        ServerApiCaller.fetchCentralServerLocation(new ApiCaller.BaseCallYier<ServerLocation>(context) {
            @Override
            public void failure(Exception e) {
                super.failure(e);
                countDownLatch.countDown();
            }

            @Override
            public void notOk(int code, Response response) {
                super.notOk(code, response);
                countDownLatch.countDown();
            }

            @Override
            public void ok(ServerLocation serverLocation, Response response) {
                ServerConfig serverConfig = new ServerConfig(serverLocation.getHost(), serverLocation.getPort(), serverLocation.getBaseUrl(), ServerValues.CENTRAL_DATA_FOLDER);
                SharedPreferencesAccessor.ServerPref.saveCentralServerConfig(context, serverConfig);
                countDownLatch.countDown();
            }
        });
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getHttpBaseUrl(Context context, boolean fetchServerLocationAndStoreCentralServerConfig){
        if(!SharedPreferencesAccessor.ServerPref.isUseCentral(context)) {
            ServerConfig customServerConfig = SharedPreferencesAccessor.ServerPref.getCustomServerConfig(context);
            return  "http://" + customServerConfig.getHost() + ":" + customServerConfig.getPort() + "/";
        }else {
            if(fetchServerLocationAndStoreCentralServerConfig) {
                fetchServerLocationAndStoreCentralServerConfig(context);
            }
            ServerConfig centralServerConfig = SharedPreferencesAccessor.ServerPref.getCentralServerConfig(context);
            if(centralServerConfig != null){
                if(centralServerConfig.getHost() != null && centralServerConfig.getPort() != -1){
                    return  "http://" + centralServerConfig.getHost() + ":" + centralServerConfig.getPort() + "/";
                }else if(centralServerConfig.getBaseUrl() != null){
                    return "http://" + (centralServerConfig.getBaseUrl().endsWith("/") ? centralServerConfig.getBaseUrl() : (centralServerConfig.getBaseUrl() + "/"));
                }
            }
            return null;
        }
    }

    public static String getWebsocketBaseUrl(Context context, boolean fetchServerLocationAndStoreCentralServerConfig){
        if(!SharedPreferencesAccessor.ServerPref.isUseCentral(context)) {
            ServerConfig customServerConfig = SharedPreferencesAccessor.ServerPref.getCustomServerConfig(context);
            return  "ws://" + customServerConfig.getHost() + ":" + customServerConfig.getPort() + "/";
        }else {
            if(fetchServerLocationAndStoreCentralServerConfig) {
                fetchServerLocationAndStoreCentralServerConfig(context);
            }
            ServerConfig centralServerConfig = SharedPreferencesAccessor.ServerPref.getCentralServerConfig(context);
            if(centralServerConfig != null){
                if(centralServerConfig.getHost() != null && centralServerConfig.getPort() != -1){
                    return  "ws://" + centralServerConfig.getHost() + ":" + centralServerConfig.getPort() + "/";
                }else if(centralServerConfig.getBaseUrl() != null){
                    return "ws://" + (centralServerConfig.getBaseUrl().endsWith("/") ? centralServerConfig.getBaseUrl() : (centralServerConfig.getBaseUrl() + "/"));
                }
            }
            return null;
        }
    }

}
