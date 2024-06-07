package com.longx.intelligent.android.ichat2.net.retrofit.caller;

import androidx.lifecycle.LifecycleOwner;

import com.longx.intelligent.android.ichat2.data.request.ChangeUserProfileVisibilityPostBody;
import com.longx.intelligent.android.ichat2.data.response.OperationData;
import com.longx.intelligent.android.ichat2.data.response.OperationStatus;
import com.longx.intelligent.android.ichat2.net.retrofit.api.PrivacyApi;
import com.longx.intelligent.android.ichat2.net.retrofit.api.UserApi;
import com.xcheng.retrofit.CompletableCall;

/**
 * Created by LONG on 2024/6/7 at 6:02 PM.
 */
public class PrivacyApiCaller extends RetrofitApiCaller {
    public static PrivacyApi getApiImplementation(){
        return getApiImplementation(PrivacyApi.class);
    }

    public static CompletableCall<OperationStatus> changeUserProfileVisibility(LifecycleOwner lifecycleOwner, ChangeUserProfileVisibilityPostBody postBody, BaseYier<OperationStatus> yier){
        CompletableCall<OperationStatus> call = getApiImplementation().changeUserProfileVisibility(postBody);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }
}
