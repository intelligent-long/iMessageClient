package com.longx.intelligent.android.ichat2.net.retrofit.caller;

import androidx.lifecycle.LifecycleOwner;

import com.longx.intelligent.android.ichat2.data.request.SendImageChatMessagePostBody;
import com.longx.intelligent.android.ichat2.data.request.SendTextChatMessagePostBody;
import com.longx.intelligent.android.ichat2.data.response.OperationData;
import com.longx.intelligent.android.ichat2.data.response.OperationStatus;
import com.longx.intelligent.android.ichat2.net.retrofit.api.ChatApi;
import com.xcheng.retrofit.CompletableCall;

/**
 * Created by LONG on 2024/5/15 at 4:09 AM.
 */
public class ChatApiCaller extends RetrofitApiCaller{
    public static ChatApi getApiImplementation(){
        return getApiImplementation(ChatApi.class);
    }

    public static CompletableCall<OperationData> sendTextChatMessage(LifecycleOwner lifecycleOwner, SendTextChatMessagePostBody postBody, BaseCommonYier<OperationData> yier){
        CompletableCall<OperationData> call = getApiImplementation().sendTextChatMessage(postBody);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationData> sendImageChatMessage(LifecycleOwner lifecycleOwner, SendImageChatMessagePostBody postBody, BaseCommonYier<OperationData> yier){
        CompletableCall<OperationData> call = getApiImplementation().sendImageChatMessage(postBody);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationData> fetchAllNewChatMessages(LifecycleOwner lifecycleOwner, BaseYier<OperationData> yier){
        CompletableCall<OperationData> call = getApiImplementation().fetchAllNewChatMessages();
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationData> viewNewMessage(LifecycleOwner lifecycleOwner, String messageUuid, BaseYier<OperationData> yier){
        CompletableCall<OperationData> call = getApiImplementation().viewNewMessage(messageUuid);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationStatus> viewAllNewMessage(LifecycleOwner lifecycleOwner, String other, BaseYier<OperationStatus> yier){
        CompletableCall<OperationStatus> call = getApiImplementation().viewAllNewMessage(other);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }
}
