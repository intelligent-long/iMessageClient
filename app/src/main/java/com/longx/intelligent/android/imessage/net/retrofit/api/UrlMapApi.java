package com.longx.intelligent.android.imessage.net.retrofit.api;

import com.longx.intelligent.android.imessage.data.response.OperationData;
import com.xcheng.retrofit.CompletableCall;

import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by LONG on 2024/10/29 at 上午1:43.
 */
public interface UrlMapApi {
    @GET("url/imessage_web/home")
    CompletableCall<OperationData> fetchImessageWebHomeUrl();

    @GET("url/imessage_web/release_data/updatable")
    CompletableCall<OperationData> fetchIchatWebUpdatableReleaseDataUrl();

    @GET("url/imessage_web/release/{versionCode}")
    CompletableCall<OperationData> fetchIchatWebReleaseUrl(@Path("versionCode") int versionCode);

    @GET("url/imessage_web/download/file/all/{versionCode}")
    CompletableCall<OperationData> fetchIchatWebAllDownloadFilesUrl(@Path("versionCode") int versionCode);

    @GET("url/imessage_web/download/file/{fileId}")
    CompletableCall<OperationData> fetchIchatWebDownloadFileUrl(@Path("fileId") String fileId);
}
