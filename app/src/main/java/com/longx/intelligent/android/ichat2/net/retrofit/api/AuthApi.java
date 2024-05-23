package com.longx.intelligent.android.ichat2.net.retrofit.api;

import com.longx.intelligent.android.ichat2.data.request.ChangePasswordPostBody;
import com.longx.intelligent.android.ichat2.data.request.EmailLoginPostBody;
import com.longx.intelligent.android.ichat2.data.request.IchatIdUserLoginPostBody;
import com.longx.intelligent.android.ichat2.data.request.RegistrationPostBody;
import com.longx.intelligent.android.ichat2.data.request.ResetPasswordPostBody;
import com.longx.intelligent.android.ichat2.data.request.SendVerifyCodePostBody;
import com.longx.intelligent.android.ichat2.data.request.VerifyCodeLoginPostBody;
import com.longx.intelligent.android.ichat2.data.response.OperationData;
import com.longx.intelligent.android.ichat2.data.response.OperationStatus;
import com.xcheng.retrofit.CompletableCall;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by LONG on 2024/3/30 at 12:12 PM.
 */
public interface AuthApi {

    @POST("auth/register")
    CompletableCall<OperationData> register(@Body RegistrationPostBody postBody);

    @POST("auth/verify_code/send")
    CompletableCall<OperationStatus> sendVerifyCode(@Body SendVerifyCodePostBody postBody);

    @POST("auth/login/ichat_id_user")
    CompletableCall<OperationData> ichatIdUserLogin(@Body IchatIdUserLoginPostBody postBody);

    @POST("auth/login/email")
    CompletableCall<OperationData> emailLogin(@Body EmailLoginPostBody postBody);

    @POST("auth/login/verify_code")
    CompletableCall<OperationData> verifyCodeLogin(@Body VerifyCodeLoginPostBody postBody);

    @POST("auth/password/reset")
    CompletableCall<OperationStatus> resetPassword(@Body ResetPasswordPostBody postBody);

    @POST("auth/password/change")
    CompletableCall<OperationStatus> changePassword(@Body ChangePasswordPostBody postBody);

    @POST("auth/logout")
    CompletableCall<OperationStatus> logout(@Header("Cookie") String cookie);

    @GET("auth/offline_detail")
    CompletableCall<OperationData> fetchOfflineDetail();

}
