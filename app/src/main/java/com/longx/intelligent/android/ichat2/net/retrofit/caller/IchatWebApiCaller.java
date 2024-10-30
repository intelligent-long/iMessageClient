package com.longx.intelligent.android.ichat2.net.retrofit.caller;

import androidx.lifecycle.LifecycleOwner;

import com.longx.intelligent.android.ichat2.data.Release;
import com.longx.intelligent.android.ichat2.data.response.OperationData;
import com.longx.intelligent.android.ichat2.net.retrofit.api.IchatWebApi;
import com.xcheng.retrofit.CompletableCall;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Url;

/**
 * Created by LONG on 2024/10/30 at 上午2:49.
 */
public class IchatWebApiCaller extends RetrofitApiCaller{
    public static IchatWebApi getApiImplementation(){
        return getApiImplementation(IchatWebApi.class);
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
