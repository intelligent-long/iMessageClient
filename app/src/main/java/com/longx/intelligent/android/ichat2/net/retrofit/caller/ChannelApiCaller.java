package com.longx.intelligent.android.ichat2.net.retrofit.caller;

import androidx.lifecycle.LifecycleOwner;

import com.longx.intelligent.android.ichat2.data.request.AcceptAddChannelPostBody;
import com.longx.intelligent.android.ichat2.data.request.RequestAddChannelPostBody;
import com.longx.intelligent.android.ichat2.data.response.OperationData;
import com.longx.intelligent.android.ichat2.data.response.OperationStatus;
import com.longx.intelligent.android.ichat2.net.retrofit.api.ChannelApi;
import com.xcheng.retrofit.CompletableCall;

/**
 * Created by LONG on 2024/4/28 at 1:10 AM.
 */
public class ChannelApiCaller extends RetrofitApiCaller{
    public static ChannelApi getApiImplementation(){
        return getApiImplementation(ChannelApi.class);
    }

    public static CompletableCall<OperationData> findChannelByIchatId(LifecycleOwner lifecycleOwner, String ichatId, BaseYier<OperationData> yier){
        CompletableCall<OperationData> call = getApiImplementation().findChannelByIchatId(ichatId);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationData> findChannelByIchatIdUser(LifecycleOwner lifecycleOwner, String ichatIdUser, BaseYier<OperationData> yier){
        CompletableCall<OperationData> call = getApiImplementation().findChannelByIchatIdUser(ichatIdUser);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationData> findChannelByEmail(LifecycleOwner lifecycleOwner, String email, BaseYier<OperationData> yier){
        CompletableCall<OperationData> call = getApiImplementation().findChannelByEmail(email);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationStatus> requestAddChannel(LifecycleOwner lifecycleOwner, RequestAddChannelPostBody postBody, BaseYier<OperationStatus> yier){
        CompletableCall<OperationStatus> call = getApiImplementation().requestAddChannel(postBody);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationStatus> acceptAddChannel(LifecycleOwner lifecycleOwner, AcceptAddChannelPostBody postBody, BaseYier<OperationStatus> yier){
        CompletableCall<OperationStatus> call = getApiImplementation().acceptAddChannel(postBody);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationData> fetchChannelAdditionNotViewCount(LifecycleOwner lifecycleOwner, BaseYier<OperationData> yier){
        CompletableCall<OperationData> call = getApiImplementation().fetchChannelAdditionNotViewCount();
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationData> fetchAllAdditionActivities(LifecycleOwner lifecycleOwner, BaseYier<OperationData> yier){
        CompletableCall<OperationData> call = getApiImplementation().fetchAllAdditionActivities();
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationStatus> viewAllAdditionActivities(LifecycleOwner lifecycleOwner, BaseYier<OperationStatus> yier){
        CompletableCall<OperationStatus> call = getApiImplementation().viewAllAdditionActivities();
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationStatus> viewOneAdditionActivity(LifecycleOwner lifecycleOwner, String uuid, BaseYier<OperationStatus> yier){
        CompletableCall<OperationStatus> call = getApiImplementation().viewOneAdditionActivity(uuid);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }
}
