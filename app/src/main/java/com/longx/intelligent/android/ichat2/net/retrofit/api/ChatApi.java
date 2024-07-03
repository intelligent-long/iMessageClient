package com.longx.intelligent.android.ichat2.net.retrofit.api;

import com.longx.intelligent.android.ichat2.data.request.SendImageChatMessagePostBody;
import com.longx.intelligent.android.ichat2.data.request.SendTextChatMessagePostBody;
import com.longx.intelligent.android.ichat2.data.response.OperationData;
import com.longx.intelligent.android.ichat2.data.response.OperationStatus;
import com.xcheng.retrofit.CompletableCall;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Streaming;

/**
 * Created by LONG on 2024/5/15 at 4:07 AM.
 */
public interface ChatApi {
    @GET("chat/message/new/all")
    CompletableCall<OperationData> fetchAllNewChatMessages();

    @POST("chat/message/new/view/{messageUuid}")
    CompletableCall<OperationData> viewNewMessage(@Path("messageUuid") String messageUuid);

    @POST("chat/message/new/view/all/{other}")
    CompletableCall<OperationStatus> viewAllNewMessage(@Path("other") String other);

    @POST("chat/message/text/send")
    CompletableCall<OperationData> sendTextChatMessage(@Body SendTextChatMessagePostBody postBody);

    @Multipart
    @POST("chat/message/image/send")
    @Headers("LogLevel:HEADERS")
    CompletableCall<OperationData> sendImageChatMessage(@Part MultipartBody.Part image, @Part("metadata") RequestBody metadata);

    @Streaming
    @GET("chat/message/image/new/{imageId}")
    @Headers("LogLevel:HEADERS")
    CompletableCall<ResponseBody> fetchChatMessageImage(@Path("imageId") String imageId);

    @Multipart
    @POST("chat/message/file/send")
    @Headers("LogLevel:HEADERS")
    CompletableCall<OperationData> sendFileChatMessage(@Part MultipartBody.Part file, @Part("metadata") RequestBody metadata);

    @Streaming
    @GET("chat/message/file/new/{fileId}")
    @Headers("LogLevel:HEADERS")
    CompletableCall<ResponseBody> fetchChatMessageFile(@Path("fileId") String fileId);

    @Multipart
    @POST("chat/message/video/send")
    @Headers("LogLevel:HEADERS")
    CompletableCall<OperationData> sendVideoChatMessage(@Part MultipartBody.Part video, @Part("metadata") RequestBody metadata);

    @Streaming
    @GET("chat/message/video/new/{videoId}")
    @Headers("LogLevel:HEADERS")
    CompletableCall<ResponseBody> fetchChatMessageVideo(@Path("videoId") String videoId);

    @Multipart
    @POST("chat/message/voice/send")
    @Headers("LogLevel:HEADERS")
    CompletableCall<OperationData> sendVoiceChatMessage(@Part MultipartBody.Part voice, @Part("metadata") RequestBody metadata);

    @Streaming
    @GET("chat/message/voice/new/{voiceId}")
    @Headers("LogLevel:HEADERS")
    CompletableCall<ResponseBody> fetchChatMessageVoice(@Path("voiceId") String voiceId);
}
