package com.longx.intelligent.android.imessage.net.retrofit.caller;


import androidx.lifecycle.LifecycleOwner;

import com.longx.intelligent.android.imessage.data.response.OperationData;
import com.longx.intelligent.android.imessage.net.retrofit.api.RegionApi;
import com.xcheng.retrofit.CompletableCall;

/**
 * Created by LONG on 2025/4/28 at 上午9:56.
 */
public class RegionApiCaller extends RetrofitApiCaller{
    public static RegionApi getApiImplementation(){
        return getApiImplementation(RegionApi.class);
    }

    public static CompletableCall<OperationData> fetchAllFirstRegions(LifecycleOwner lifecycleOwner, BaseYier<OperationData> yier){
        CompletableCall<OperationData> call = getApiImplementation().fetchAllFirstRegions();
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationData> fetchAllSecondRegions(LifecycleOwner lifecycleOwner, int firstRegionAdcode, BaseYier<OperationData> yier){
        CompletableCall<OperationData> call = getApiImplementation().fetchAllSecondRegions(firstRegionAdcode);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationData> fetchAllThirdRegions(LifecycleOwner lifecycleOwner, int secondRegionAdcode, BaseYier<OperationData> yier){
        CompletableCall<OperationData> call = getApiImplementation().fetchAllThirdRegions(secondRegionAdcode);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }
}
