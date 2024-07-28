package com.longx.intelligent.android.ichat2.net.retrofit.caller;

import androidx.lifecycle.LifecycleOwner;

import com.longx.intelligent.android.ichat2.data.Broadcast;
import com.longx.intelligent.android.ichat2.data.request.SendBroadcastPostBody;
import com.longx.intelligent.android.ichat2.data.response.OperationStatus;
import com.longx.intelligent.android.ichat2.data.response.PaginatedOperationData;
import com.longx.intelligent.android.ichat2.net.retrofit.api.BroadcastApi;
import com.xcheng.retrofit.CompletableCall;

import retrofit2.http.Query;

/**
 * Created by LONG on 2024/7/28 at 上午2:49.
 */
public class BroadcastApiCaller extends RetrofitApiCaller{
    public static BroadcastApi getApiImplementation(){
        return getApiImplementation(BroadcastApi.class);
    }

    public static CompletableCall<OperationStatus> sendBroadcast(LifecycleOwner lifecycleOwner, SendBroadcastPostBody postBody, BaseYier<OperationStatus> yier){
        CompletableCall<OperationStatus> call = getApiImplementation().sendBroadcast(postBody);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<PaginatedOperationData<Broadcast>> fetchBroadcastsLimit(LifecycleOwner lifecycleOwner, int pn, int ps, BaseYier<PaginatedOperationData<Broadcast>> yier){
        CompletableCall<PaginatedOperationData<Broadcast>> call = getApiImplementation().fetchBroadcastsLimit(pn, ps);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }
}
