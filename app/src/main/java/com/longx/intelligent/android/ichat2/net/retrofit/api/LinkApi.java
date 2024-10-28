package com.longx.intelligent.android.ichat2.net.retrofit.api;

import com.longx.intelligent.android.ichat2.data.response.OperationData;
import com.xcheng.retrofit.CompletableCall;

import retrofit2.http.GET;

/**
 * Created by LONG on 2024/10/29 at 上午1:43.
 */
public interface LinkApi {
    @GET("link/ichat_web/home")
    CompletableCall<OperationData> fetchIchatWebHomeUrl();
}
