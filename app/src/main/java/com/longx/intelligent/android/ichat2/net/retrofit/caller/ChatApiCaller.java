package com.longx.intelligent.android.ichat2.net.retrofit.caller;

import androidx.lifecycle.LifecycleOwner;

import com.longx.intelligent.android.ichat2.data.request.SendImageChatMessagePostBody;
import com.longx.intelligent.android.ichat2.data.request.SendTextChatMessagePostBody;
import com.longx.intelligent.android.ichat2.data.response.OperationData;
import com.longx.intelligent.android.ichat2.data.response.OperationStatus;
import com.longx.intelligent.android.ichat2.net.retrofit.api.ChatApi;
import com.longx.intelligent.android.ichat2.util.JsonUtil;
import com.xcheng.retrofit.CompletableCall;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Part;

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

    public static CompletableCall<OperationData> sendImageChatMessage(LifecycleOwner lifecycleOwner, byte[] imageBytes, SendImageChatMessagePostBody postBody, BaseCommonYier<OperationData> yier){
        RequestBody imageRequestBody = RequestBody.create(MediaType.parse("image/*"), imageBytes);
        MultipartBody.Part imagePart = MultipartBody.Part.createFormData("image", "." + postBody.getImageExtension(), imageRequestBody);
        RequestBody metadataRequestBody = RequestBody.create(MediaType.parse("application/json"), JsonUtil.toJson(postBody));
        CompletableCall<OperationData> call = getApiImplementation().sendImageChatMessage(imagePart, metadataRequestBody);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationData> fetchAllNewChatMessages(LifecycleOwner lifecycleOwner, BaseYier<OperationData> yier){
        CompletableCall<OperationData> call = getApiImplementation().fetchAllNewChatMessages();
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<ResponseBody> fetchChatMessageImage(LifecycleOwner lifecycleOwner, String imageId, BaseYier<ResponseBody> yier){
        CompletableCall<ResponseBody> call = getApiImplementation().fetchChatMessageImage(imageId);
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
