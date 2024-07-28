package com.longx.intelligent.android.ichat2.net.retrofit.api;

import com.longx.intelligent.android.ichat2.data.Broadcast;
import com.longx.intelligent.android.ichat2.data.request.SendBroadcastPostBody;
import com.longx.intelligent.android.ichat2.data.response.OperationStatus;
import com.longx.intelligent.android.ichat2.data.response.PaginatedOperationData;
import com.xcheng.retrofit.CompletableCall;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by LONG on 2024/7/28 at 上午2:47.
 */
public interface BroadcastApi {

    @POST("broadcast/send")
    CompletableCall<OperationStatus> sendBroadcast(@Body SendBroadcastPostBody postBody);

    @GET("broadcast/limit")
    CompletableCall<PaginatedOperationData<Broadcast>> fetchBroadcastsLimit(@Query("pn") int pn, @Query("ps") int ps);
}
