package com.longx.intelligent.android.imessage.net.retrofit.caller;

import androidx.lifecycle.LifecycleOwner;

import com.longx.intelligent.android.imessage.data.request.ChangePasswordPostBody;
import com.longx.intelligent.android.imessage.data.request.EmailLoginPostBody;
import com.longx.intelligent.android.imessage.data.request.ImessageIdUserLoginPostBody;
import com.longx.intelligent.android.imessage.data.request.RegistrationPostBody;
import com.longx.intelligent.android.imessage.data.request.ResetPasswordPostBody;
import com.longx.intelligent.android.imessage.data.request.SendVerifyCodePostBody;
import com.longx.intelligent.android.imessage.data.request.VerifyCodeLoginPostBody;
import com.longx.intelligent.android.imessage.data.response.OperationData;
import com.longx.intelligent.android.imessage.data.response.OperationStatus;
import com.longx.intelligent.android.imessage.net.retrofit.RetrofitCreator;
import com.longx.intelligent.android.imessage.net.retrofit.api.AuthApi;
import com.xcheng.retrofit.CompletableCall;

import retrofit2.Retrofit;

/**
 * Created by LONG on 2024/3/30 at 12:35 PM.
 */
public class AuthApiCaller extends RetrofitApiCaller{
    public static AuthApi getApiImplementation(){
        return getApiImplementation(AuthApi.class);
    }

    public static AuthApi getCustomBaseUrlApiImplementation(String baseUrl){
        Retrofit retrofit = RetrofitCreator.customBaseUrl(baseUrl);
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

    public static CompletableCall<OperationData> imessageIdUserLogin(LifecycleOwner lifecycleOwner, ImessageIdUserLoginPostBody postBody, BaseYier<OperationData> yier){
        CompletableCall<OperationData> call = getApiImplementation().imessageIdUserLogin(postBody);
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
        AuthApi authApi;
        if(baseUrl == null){
            authApi = getApiImplementation();
        }else {
            authApi = getCustomBaseUrlApiImplementation(baseUrl);
        }
        CompletableCall<OperationStatus> call = authApi.logout(cookie);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationData> fetchOfflineDetail(LifecycleOwner lifecycleOwner, BaseYier<OperationData> yier){
        CompletableCall<OperationData> call = getApiImplementation().fetchOfflineDetail();
        call.enqueue(lifecycleOwner, yier);
        return call;
    }
}
