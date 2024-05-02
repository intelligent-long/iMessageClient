package com.longx.intelligent.android.ichat2.net.retrofit.api;

import com.longx.intelligent.android.ichat2.data.request.RequestAddChannelPostBody;
import com.longx.intelligent.android.ichat2.data.response.OperationData;
import com.longx.intelligent.android.ichat2.data.response.OperationStatus;
import com.xcheng.retrofit.CompletableCall;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by LONG on 2024/4/28 at 1:07 AM.
 */
public interface ChannelApi {

    @GET("channel/find/ichat_id_user/{ichatIdUser}")
    CompletableCall<OperationData> findChannelByIchatIdUser(@Path("ichatIdUser") String ichatIdUser);

    @GET("channel/find/email/{email}")
    CompletableCall<OperationData> findChannelByEmail(@Path("email") String email);

    @POST("channel/add/request")
    CompletableCall<OperationStatus> requestAddChannel(@Body RequestAddChannelPostBody postBody);

    @GET("channel/add/activity/get_all")
    CompletableCall<OperationData> getAllAdditionActivities();

    @POST("channel/add/activity/view_all")
    CompletableCall<OperationStatus> viewAllAdditionActivities();
}
