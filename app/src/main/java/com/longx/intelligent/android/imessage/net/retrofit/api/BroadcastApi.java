package com.longx.intelligent.android.imessage.net.retrofit.api;

import com.longx.intelligent.android.imessage.data.Broadcast;
import com.longx.intelligent.android.imessage.data.BroadcastComment;
import com.longx.intelligent.android.imessage.data.BroadcastLike;
import com.longx.intelligent.android.imessage.data.request.CommentBroadcastPostBody;
import com.longx.intelligent.android.imessage.data.request.MakeBroadcastCommentsToOldPostBody;
import com.longx.intelligent.android.imessage.data.request.MakeBroadcastLikesToOldPostBody;
import com.longx.intelligent.android.imessage.data.request.MakeBroadcastReplyCommentsToOldPostBody;
import com.longx.intelligent.android.imessage.data.response.OperationData;
import com.longx.intelligent.android.imessage.data.response.OperationStatus;
import com.longx.intelligent.android.imessage.data.response.PaginatedOperationData;
import com.xcheng.retrofit.CompletableCall;

import java.util.List;

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
import retrofit2.http.Query;

/**
 * Created by LONG on 2024/7/28 at 上午2:47.
 */
public interface BroadcastApi {

    @POST("broadcast/send")
    @Multipart
    @Headers("LogLevel:HEADERS")
    CompletableCall<OperationData> sendBroadcast(@Part("body") RequestBody postBody, @Part List<MultipartBody.Part> medias);

    @GET("broadcast/limit")
    CompletableCall<PaginatedOperationData<Broadcast>> fetchBroadcastsLimit(@Query("last_broadcast_id") String lastBroadcastId, @Query("ps") int ps, @Query("desc") boolean desc);

    @POST("broadcast/delete")
    CompletableCall<OperationStatus> deleteBroadcast(@Query("broadcast_id") String broadcastId);

    @GET("broadcast/channel/limit")
    CompletableCall<PaginatedOperationData<Broadcast>> fetchChannelBroadcastsLimit(@Query("channel_id") String channelId, @Query("last_broadcast_id") String lastBroadcastId, @Query("ps") int ps, @Query("desc") boolean desc);

    @POST("broadcast/edit")
    @Multipart
    @Headers("LogLevel:HEADERS")
    CompletableCall<OperationData> editBroadcast(@Part("body") RequestBody postBody, @Part List<MultipartBody.Part> addMedias);

    @GET("broadcast/media/data/{mediaId}")
    @Headers("LogLevel:HEADERS")
    CompletableCall<ResponseBody> downloadMediaData(@Path("mediaId") String mediaId);

    @POST("broadcast/like/{broadcastId}")
    CompletableCall<OperationData> likeBroadcast(@Path("broadcastId") String broadcastId);

    @POST("broadcast/like/cancel/{broadcastId}")
    CompletableCall<OperationData> cancelLikeBroadcast(@Path("broadcastId") String broadcastId);

    @GET("broadcast/like/to_self/news_count")
    CompletableCall<OperationData> fetchBroadcastLikeNewsCount();

    @GET("broadcast/like/to_self/limit")
    CompletableCall<PaginatedOperationData<BroadcastLike>> fetchLikesOfSelfBroadcasts(@Query("last_like_id") String lastLikeId, @Query("ps") int ps);

    @GET("broadcast/{broadcastId}")
    CompletableCall<OperationData> fetchBroadcast(@Path("broadcastId") String broadcastId);

    @POST("broadcast/like/to_self/to_old")
    CompletableCall<OperationStatus> makeBroadcastLikesToOld(@Body MakeBroadcastLikesToOldPostBody postBody);

    @GET("broadcast/like/limit")
    CompletableCall<PaginatedOperationData<BroadcastLike>> fetchLikesOfBroadcast(@Query("broadcast_id") String broadcastId, @Query("last_like_id") String lastLikeId, @Query("ps") int ps);

    @POST("broadcast/comment")
    CompletableCall<OperationData> commentBroadcast(@Body CommentBroadcastPostBody postBody);

    @GET("broadcast/comment/limit")
    CompletableCall<PaginatedOperationData<BroadcastComment>> fetchCommentsOfBroadcast(@Query("broadcast_id") String broadcastId, @Query("last_comment_id") String lastCommentId, @Query("ps") int ps);

    @GET("broadcast/comment/to_self/news_count")
    CompletableCall<OperationData> fetchBroadcastCommentNewsCount();

    @GET("broadcast/comment/to_self/limit")
    CompletableCall<PaginatedOperationData<BroadcastComment>> fetchCommentsOfSelfBroadcasts(@Query("last_comment_id") String lastCommentId, @Query("ps") int ps);

    @POST("broadcast/comment/to_self/to_old")
    CompletableCall<OperationStatus> makeBroadcastCommentsToOld(@Body MakeBroadcastCommentsToOldPostBody postBody);

    @POST("broadcast/comment/delete/{commentId}")
    CompletableCall<OperationData> deleteBroadcastComment(@Path("commentId") String commentId);

    @GET("broadcast/reply_comment/to_self/news_count")
    CompletableCall<OperationData> fetchBroadcastReplyCommentNewsCount();

    @GET("broadcast/reply_comment/to_self/limit")
    CompletableCall<PaginatedOperationData<BroadcastComment>> fetchReplyCommentsOfSelfBroadcasts(@Query("last_reply_comment_id") String lastReplyCommentId, @Query("ps") int ps);

    @POST("broadcast/reply_comment/to_self/to_old")
    CompletableCall<OperationStatus> makeBroadcastReplyCommentsToOld(@Body MakeBroadcastReplyCommentsToOldPostBody postBody);
}
