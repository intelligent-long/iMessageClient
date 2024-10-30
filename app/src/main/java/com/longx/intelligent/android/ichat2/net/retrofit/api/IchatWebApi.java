package com.longx.intelligent.android.ichat2.net.retrofit.api;

import com.longx.intelligent.android.ichat2.data.response.OperationData;
import com.xcheng.retrofit.CompletableCall;

import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by LONG on 2024/10/30 at 上午2:46.
 */
public interface IchatWebApi {

    @GET
    CompletableCall<OperationData> fetchUpdatableReleaseData(@Url String updatableReleaseUrl);

    @GET
    CompletableCall<OperationData> fetchAllDownloadFiles(@Url String allDownloadFilesUrl);

}
