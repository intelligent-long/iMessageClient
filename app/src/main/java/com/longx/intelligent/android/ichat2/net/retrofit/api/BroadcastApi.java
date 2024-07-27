package com.longx.intelligent.android.ichat2.net.retrofit.api;

import com.longx.intelligent.android.ichat2.data.request.SendBroadcastPostBody;
import com.longx.intelligent.android.ichat2.data.response.OperationStatus;
import com.xcheng.retrofit.CompletableCall;

import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by LONG on 2024/7/28 at 上午2:47.
 */
public interface BroadcastApi {

    @POST("broadcast/send")
    CompletableCall<OperationStatus> sendBroadcast(@Body SendBroadcastPostBody postBody);
}
