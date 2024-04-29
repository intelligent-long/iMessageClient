package com.longx.intelligent.android.ichat2.net.retrofit.api;

import com.longx.intelligent.android.ichat2.data.response.OperationData;
import com.xcheng.retrofit.CompletableCall;

import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by LONG on 2024/4/28 at 1:07 AM.
 */
public interface ChannelApi {

    @GET("channel/search/ichat_id_user/{ichatIdUser}")
    CompletableCall<OperationData> searchChannelByIchatIdUser(@Path("ichatIdUser") String ichatIdUser);

    @GET("channel/search/email/{email}")
    CompletableCall<OperationData> searchChannelByEmail(@Path("email") String email);
}
