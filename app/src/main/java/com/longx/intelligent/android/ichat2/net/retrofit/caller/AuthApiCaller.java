package com.longx.intelligent.android.ichat2.net.retrofit.caller;

import androidx.lifecycle.LifecycleOwner;

import com.longx.intelligent.android.ichat2.data.request.ChangePasswordPostBody;
import com.longx.intelligent.android.ichat2.data.request.EmailLoginPostBody;
import com.longx.intelligent.android.ichat2.data.request.IchatIdUserLoginPostBody;
import com.longx.intelligent.android.ichat2.data.request.RegistrationPostBody;
import com.longx.intelligent.android.ichat2.data.request.ResetPasswordPostBody;
import com.longx.intelligent.android.ichat2.data.request.SendVerifyCodePostBody;
import com.longx.intelligent.android.ichat2.data.request.VerifyCodeLoginPostBody;
import com.longx.intelligent.android.ichat2.data.response.OperationData;
import com.longx.intelligent.android.ichat2.data.response.OperationStatus;
import com.longx.intelligent.android.ichat2.net.retrofit.RetrofitCreator;
import com.longx.intelligent.android.ichat2.net.retrofit.api.AuthApi;
import com.xcheng.retrofit.CompletableCall;

import retrofit2.Retrofit;

/**
 * Created by LONG on 2024/3/30 at 12:35 PM.
 */
public class AuthApiCaller extends RetrofitApiCaller{
    public static AuthApi getApiImplementation(){
        return getApiImplementation(AuthApi.class);
    }

    public static AuthApi getApiImplementation(String baseUrl){
        if(baseUrl == null){
            return getApiImplementation();
        }
        Retrofit retrofit = RetrofitCreator.createTemporary(baseUrl);
        return getApiImplementation(retrofit, AuthApi.class);
    }

    public static CompletableCall<OperationData> register(LifecycleOwner lifecycleOwner, RegistrationPostBody postBody, BaseYier<OperationData> yier){
        CompletableCall<OperationData> call = getApiImplementation().register(postBody);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationStatus> sendVerifyCode(LifecycleOwner lifecycleOwner, SendVerifyCodePostBody postBody, BaseYier<OperationStatus> yier){
        CompletableCall<OperationStatus> call = getApiImplementation().sendVerifyCode(postBody);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationData> ichatIdUserLogin(LifecycleOwner lifecycleOwner, IchatIdUserLoginPostBody postBody, BaseYier<OperationData> yier){
        CompletableCall<OperationData> call = getApiImplementation().ichatIdUserLogin(postBody);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationData> emailLogin(LifecycleOwner lifecycleOwner, EmailLoginPostBody postBody, BaseYier<OperationData> yier){
        CompletableCall<OperationData> call = getApiImplementation().emailLogin(postBody);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationData> verifyCodeLogin(LifecycleOwner lifecycleOwner, VerifyCodeLoginPostBody postBody, BaseYier<OperationData> yier){
        CompletableCall<OperationData> call = getApiImplementation().verifyCodeLogin(postBody);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationStatus> resetPassword(LifecycleOwner lifecycleOwner, ResetPasswordPostBody postBody, BaseYier<OperationStatus> yier){
        CompletableCall<OperationStatus> call = getApiImplementation().resetPassword(postBody);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationStatus> changePassword(LifecycleOwner lifecycleOwner, ChangePasswordPostBody postBody, BaseYier<OperationStatus> yier){
        CompletableCall<OperationStatus> call = getApiImplementation().changePassword(postBody);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationStatus> logout(LifecycleOwner lifecycleOwner, String baseUrl, String cookie, BaseYier<OperationStatus> yier){
        CompletableCall<OperationStatus> call = getApiImplementation(baseUrl).logout(cookie);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }
}
