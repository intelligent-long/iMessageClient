package com.longx.intelligent.android.imessage.net.retrofit.caller;

import androidx.lifecycle.LifecycleOwner;

import com.longx.intelligent.android.imessage.data.response.OperationData;
import com.longx.intelligent.android.imessage.net.retrofit.api.ImessageWebApi;
import com.xcheng.retrofit.CompletableCall;

/**
 * Created by LONG on 2024/10/30 at 上午2:49.
 */
public class ImessageWebApiCaller extends RetrofitApiCaller{
    public static ImessageWebApi getApiImplementation(){
        return getApiImplementation(ImessageWebApi.class);
    }

    public static CompletableCall<OperationData> fetchUpdatableReleaseData(LifecycleOwner lifecycleOwner, String updatableReleaseUrl, BaseYier<OperationData> yier){
        CompletableCall<OperationData> call = getApiImplementation().fetchUpdatableReleaseData(updatableReleaseUrl);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationData> fetchAllDownloadFiles(LifecycleOwner lifecycleOwner, String allDownloadFilesUrl, BaseYier<OperationData> yier){
        CompletableCall<OperationData> call = getApiImplementation().fetchAllDownloadFiles(allDownloadFilesUrl);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

}
