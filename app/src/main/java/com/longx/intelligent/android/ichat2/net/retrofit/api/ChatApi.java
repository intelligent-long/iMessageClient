package com.longx.intelligent.android.ichat2.net.retrofit.api;

import com.longx.intelligent.android.ichat2.data.request.SendImageChatMessagePostBody;
import com.longx.intelligent.android.ichat2.data.request.SendTextChatMessagePostBody;
import com.longx.intelligent.android.ichat2.data.response.OperationData;
import com.longx.intelligent.android.ichat2.data.response.OperationStatus;
import com.xcheng.retrofit.CompletableCall;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by LONG on 2024/5/15 at 4:07 AM.
 */
public interface ChatApi {
    @POST("chat/message/text/send")
    CompletableCall<OperationData> sendTextChatMessage(@Body SendTextChatMessagePostBody postBody);

    @POST("chat/message/image/send")
    CompletableCall<OperationData> sendImageChatMessage(@Body SendImageChatMessagePostBody postBody);

    @GET("chat/message/new/all")
    CompletableCall<OperationData> fetchAllNewChatMessages();

    @POST("chat/message/new/view/{messageUuid}")
    CompletableCall<OperationData> viewNewMessage(@Path("messageUuid") String messageUuid);

    @POST("chat/message/new/view/all/{other}")
    CompletableCall<OperationStatus> viewAllNewMessage(@Path("other") String other);
}
