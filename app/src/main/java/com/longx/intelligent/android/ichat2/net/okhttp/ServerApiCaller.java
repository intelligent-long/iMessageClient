package com.longx.intelligent.android.ichat2.net.okhttp;

import androidx.lifecycle.LifecycleOwner;

import com.longx.intelligent.android.ichat2.net.ServerLocation;
import com.longx.intelligent.android.ichat2.net.ServerValues;
import com.longx.intelligent.android.ichat2.net.retrofit.RetrofitCreator;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.RetrofitApiCaller;
import com.xcheng.retrofit.CompletableCall;

/**
 * Created by LONG on 2024/10/31 at 下午11:35.
 */
public class ServerApiCaller {

    public static ServerLocation fetchCentralServerLocation(){
        return ApiCaller.callApi(ServerValues.SERVER_FINDER_FIND_ICHAT_SERVER_LOCATION_URL, ServerLocation.class);
    }
}
