package com.longx.intelligent.android.imessage.net.retrofit.caller;

import androidx.lifecycle.LifecycleOwner;

import com.longx.intelligent.android.imessage.data.response.OperationData;
import com.longx.intelligent.android.imessage.net.retrofit.api.UrlMapApi;
import com.xcheng.retrofit.CompletableCall;

/**
 * Created by LONG on 2024/10/29 at 上午1:45.
 */
public class UrlMapApiCaller extends RetrofitApiCaller{
    public static UrlMapApi getApiImplementation(){
        return getApiImplementation(UrlMapApi.class);
    }

    public static CompletableCall<OperationData> fetchImessageWebHomeUrl(LifecycleOwner lifecycleOwner, BaseYier<OperationData> yier){
        CompletableCall<OperationData> call = getApiImplementation().fetchImessageWebHomeUrl();
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationData> fetchImessageWebUpdatableReleaseDataUrl(LifecycleOwner lifecycleOwner, BaseYier<OperationData> yier){
        CompletableCall<OperationData> call = getApiImplementation().fetchImessageWebUpdatableReleaseDataUrl();
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationData> fetchImessageWebReleaseUrl(LifecycleOwner lifecycleOwner, int versionCode, BaseYier<OperationData> yier){
        CompletableCall<OperationData> call = getApiImplementation().fetchImessageWebReleaseUrl(versionCode);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationData> fetchImessageWebAllDownloadFilesUrl(LifecycleOwner lifecycleOwner, int versionCode, BaseYier<OperationData> yier){
        CompletableCall<OperationData> call = getApiImplementation().fetchImessageWebAllDownloadFilesUrl(versionCode);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationData> fetchImessageWebDownloadFileUrl(LifecycleOwner lifecycleOwner, String fileId, BaseYier<OperationData> yier){
        CompletableCall<OperationData> call = getApiImplementation().fetchImessageWebDownloadFileUrl(fileId);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }
}
