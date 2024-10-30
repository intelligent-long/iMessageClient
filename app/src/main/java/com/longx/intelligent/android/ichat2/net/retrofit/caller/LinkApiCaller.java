package com.longx.intelligent.android.ichat2.net.retrofit.caller;

import androidx.lifecycle.LifecycleOwner;

import com.longx.intelligent.android.ichat2.data.response.OperationData;
import com.longx.intelligent.android.ichat2.net.retrofit.api.LinkApi;
import com.xcheng.retrofit.CompletableCall;

import retrofit2.http.Path;

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
