package com.longx.intelligent.android.ichat2.net.retrofit.caller;

import androidx.lifecycle.LifecycleOwner;

import com.longx.intelligent.android.ichat2.data.request.ChangeAllowChatMessagePostBody;
import com.longx.intelligent.android.ichat2.data.request.ChangeBroadcastChannelPermissionPostBody;
import com.longx.intelligent.android.ichat2.data.request.ChangeExcludeBroadcastChannelPostBody;
import com.longx.intelligent.android.ichat2.data.request.ChangeUserProfileVisibilityPostBody;
import com.longx.intelligent.android.ichat2.data.request.ChangeWaysToFindMePostBody;
import com.longx.intelligent.android.ichat2.data.response.OperationData;
import com.longx.intelligent.android.ichat2.data.response.OperationStatus;
import com.longx.intelligent.android.ichat2.net.retrofit.api.PermissionApi;
import com.xcheng.retrofit.CompletableCall;

/**
 * Created by LONG on 2024/6/7 at 6:02 PM.
 */
public class PermissionApiCaller extends RetrofitApiCaller {
    public static PermissionApi getApiImplementation(){
        return getApiImplementation(PermissionApi.class);
    }

    public static CompletableCall<OperationStatus> changeUserProfileVisibility(LifecycleOwner lifecycleOwner, ChangeUserProfileVisibilityPostBody postBody, BaseYier<OperationStatus> yier){
        CompletableCall<OperationStatus> call = getApiImplementation().changeUserProfileVisibility(postBody);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationStatus> changeWaysToFindMe(LifecycleOwner lifecycleOwner, ChangeWaysToFindMePostBody postBody, BaseYier<OperationStatus> yier){
        CompletableCall<OperationStatus> call = getApiImplementation().changeWaysToFindMe(postBody);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationStatus> changeAllowChatMessage(LifecycleOwner lifecycleOwner, ChangeAllowChatMessagePostBody postBody, BaseYier<OperationStatus> yier){
        CompletableCall<OperationStatus> call = getApiImplementation().changeAllowChatMessage(postBody);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationData> fetchBroadcastChannelPermission(LifecycleOwner lifecycleOwner, BaseYier<OperationData> yier){
        CompletableCall<OperationData> call = getApiImplementation().fetchBroadcastChannelPermission();
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationStatus> changeBroadcastChannelPermission(LifecycleOwner lifecycleOwner, ChangeBroadcastChannelPermissionPostBody postBody, BaseYier<OperationStatus> yier){
        CompletableCall<OperationStatus> call = getApiImplementation().changeChannelPermission(postBody);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationData> fetchExcludeBroadcastChannels(LifecycleOwner lifecycleOwner, BaseYier<OperationData> yier){
        CompletableCall<OperationData> call = getApiImplementation().fetchExcludeBroadcastChannels();
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationStatus> changeExcludeBroadcastChannels(LifecycleOwner lifecycleOwner, ChangeExcludeBroadcastChannelPostBody postBody, BaseYier<OperationStatus> yier){
        CompletableCall<OperationStatus> call = getApiImplementation().changeExcludeBroadcastChannels(postBody);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }
}
