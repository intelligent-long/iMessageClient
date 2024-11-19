package com.longx.intelligent.android.ichat2.net.retrofit.api;

import com.longx.intelligent.android.ichat2.data.response.OperationData;
import com.xcheng.retrofit.CompletableCall;

import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by LONG on 2024/10/29 at 上午1:43.
 */
public interface UrlMapApi {
    @GET("url/ichat_web/home")
    CompletableCall<OperationData> fetchIchatWebHomeUrl();

    @GET("url/ichat_web/release_data/updatable")
    CompletableCall<OperationData> fetchIchatWebUpdatableReleaseDataUrl();

    @GET("url/ichat_web/release/{versionCode}")
    CompletableCall<OperationData> fetchIchatWebReleaseUrl(@Path("versionCode") int versionCode);

    @GET("url/ichat_web/download/file/all/{versionCode}")
    CompletableCall<OperationData> fetchIchatWebAllDownloadFilesUrl(@Path("versionCode") int versionCode);

    @GET("url/ichat_web/download/file/{fileId}")
    CompletableCall<OperationData> fetchIchatWebDownloadFileUrl(@Path("fileId") String fileId);
}
