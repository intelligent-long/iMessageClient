package com.longx.intelligent.android.ichat2.net.retrofit.caller;

import androidx.lifecycle.LifecycleOwner;

import com.longx.intelligent.android.ichat2.data.request.SendChatMessagePostBody;
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

    public static CompletableCall<OperationData> sendChatMessage(LifecycleOwner lifecycleOwner, SendChatMessagePostBody postBody, BaseCommonYier<OperationData> yier){
        CompletableCall<OperationData> call = getApiImplementation().sendChatMessage(postBody);
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
}
