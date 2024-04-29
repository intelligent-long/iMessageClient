package com.longx.intelligent.android.ichat2.net.retrofit.caller;

import androidx.lifecycle.LifecycleOwner;

import com.longx.intelligent.android.ichat2.data.response.OperationData;
import com.longx.intelligent.android.ichat2.net.retrofit.api.ChannelApi;
import com.xcheng.retrofit.CompletableCall;

/**
 * Created by LONG on 2024/4/28 at 1:10 AM.
 */
public class ChannelApiCaller extends RetrofitApiCaller{
    public static ChannelApi getApiImplementation(){
        return getApiImplementation(ChannelApi.class);
    }

    public static CompletableCall<OperationData> searchChannelByIchatIdUser(LifecycleOwner lifecycleOwner, String ichatIdUser, BaseYier<OperationData> yier){
        CompletableCall<OperationData> call = getApiImplementation().searchChannelByIchatIdUser(ichatIdUser);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationData> searchChannelByEmail(LifecycleOwner lifecycleOwner, String email, BaseYier<OperationData> yier){
        CompletableCall<OperationData> call = getApiImplementation().searchChannelByEmail(email);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }
}
