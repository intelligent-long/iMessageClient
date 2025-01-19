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

    public static CompletableCall<OperationData> fetchIchatWebHomeUrl(LifecycleOwner lifecycleOwner, BaseYier<OperationData> yier){
        CompletableCall<OperationData> call = getApiImplementation().fetchImessageWebHomeUrl();
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationData> fetchIchatWebUpdatableReleaseDataUrl(LifecycleOwner lifecycleOwner, BaseYier<OperationData> yier){
        CompletableCall<OperationData> call = getApiImplementation().fetchIchatWebUpdatableReleaseDataUrl();
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationData> fetchIchatWebReleaseUrl(LifecycleOwner lifecycleOwner, int versionCode, BaseYier<OperationData> yier){
        CompletableCall<OperationData> call = getApiImplementation().fetchIchatWebReleaseUrl(versionCode);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationData> fetchIchatWebAllDownloadFilesUrl(LifecycleOwner lifecycleOwner, int versionCode, BaseYier<OperationData> yier){
        CompletableCall<OperationData> call = getApiImplementation().fetchIchatWebAllDownloadFilesUrl(versionCode);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationData> fetchIchatWebDownloadFileUrl(LifecycleOwner lifecycleOwner, String fileId, BaseYier<OperationData> yier){
        CompletableCall<OperationData> call = getApiImplementation().fetchIchatWebDownloadFileUrl(fileId);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }
}
