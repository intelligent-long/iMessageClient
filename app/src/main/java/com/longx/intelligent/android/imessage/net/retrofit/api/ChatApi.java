package com.longx.intelligent.android.imessage.net.retrofit.api;

import com.longx.intelligent.android.imessage.data.request.SendTextChatMessagePostBody;
import com.longx.intelligent.android.imessage.data.response.OperationData;
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
    @GET("chat/message/unviewed/all")
    CompletableCall<OperationData> fetchAllUnviewedMessages();

    @POST("chat/message/view/{messageUuid}")
    CompletableCall<OperationData> viewMessage(@Path("messageUuid") String messageUuid);

    @POST("chat/message/text/send")
    CompletableCall<OperationData> sendTextMessage(@Body SendTextChatMessagePostBody postBody);

    @Multipart
    @POST("chat/message/image/send")
    @Headers("LogLevel:HEADERS")
    CompletableCall<OperationData> sendImageMessage(@Part MultipartBody.Part image, @Part("metadata") RequestBody metadata);

    @Streaming
    @GET("chat/message/image/new/{imageId}")
    @Headers("LogLevel:HEADERS")
    CompletableCall<ResponseBody> fetchMessageImage(@Path("imageId") String imageId);

    @Multipart
    @POST("chat/message/file/send")
    @Headers("LogLevel:HEADERS")
    CompletableCall<OperationData> sendFileMessage(@Part MultipartBody.Part file, @Part("metadata") RequestBody metadata);

    @Streaming
    @GET("chat/message/file/new/{fileId}")
    @Headers("LogLevel:HEADERS")
    CompletableCall<ResponseBody> fetchChatMessageFile(@Path("fileId") String fileId);

    @Multipart
    @POST("chat/message/video/send")
    @Headers("LogLevel:HEADERS")
    CompletableCall<OperationData> sendVideoMessage(@Part MultipartBody.Part video, @Part("metadata") RequestBody metadata);

    @Streaming
    @GET("chat/message/video/new/{videoId}")
    @Headers("LogLevel:HEADERS")
    CompletableCall<ResponseBody> fetchMessageVideo(@Path("videoId") String videoId);

    @Multipart
    @POST("chat/message/voice/send")
    @Headers("LogLevel:HEADERS")
    CompletableCall<OperationData> sendVoiceMessage(@Part MultipartBody.Part voice, @Part("metadata") RequestBody metadata);

    @Streaming
    @GET("chat/message/voice/new/{voiceId}")
    @Headers("LogLevel:HEADERS")
    CompletableCall<ResponseBody> fetchMessageVoice(@Path("voiceId") String voiceId);

    @POST("chat/message/unsend/{receiver}/{chatMessageUuid}")
    CompletableCall<OperationData> unsendMessage(@Path("receiver") String receiver, @Path("chatMessageUuid") String chatMessageUuid);
}
