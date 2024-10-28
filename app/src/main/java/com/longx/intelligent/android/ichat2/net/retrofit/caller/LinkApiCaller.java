package com.longx.intelligent.android.ichat2.net.retrofit.caller;

import androidx.lifecycle.LifecycleOwner;

import com.longx.intelligent.android.ichat2.data.response.OperationData;
import com.longx.intelligent.android.ichat2.net.retrofit.api.LinkApi;
import com.xcheng.retrofit.CompletableCall;

/**
 * Created by LONG on 2024/10/29 at 上午1:45.
 */
public class LinkApiCaller extends RetrofitApiCaller{
    public static LinkApi getApiImplementation(){
        return getApiImplementation(LinkApi.class);
    }

    public static CompletableCall<OperationData> fetchIchatWebHomeUrl(LifecycleOwner lifecycleOwner, BaseYier<OperationData> yier){
        CompletableCall<OperationData> call = getApiImplementation().fetchIchatWebHomeUrl();
        call.enqueue(lifecycleOwner, yier);
        return call;
    }
}
