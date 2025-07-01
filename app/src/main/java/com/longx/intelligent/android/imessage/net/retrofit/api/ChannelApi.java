package com.longx.intelligent.android.imessage.net.retrofit.api;

import com.longx.intelligent.android.imessage.data.request.AcceptAddChannelPostBody;
import com.longx.intelligent.android.imessage.data.request.AddChannelCollectionPostBody;
import com.longx.intelligent.android.imessage.data.request.AddChannelTagPostBody;
import com.longx.intelligent.android.imessage.data.request.AddChannelsToTagPostBody;
import com.longx.intelligent.android.imessage.data.request.ChangeChannelTagNamePostBody;
import com.longx.intelligent.android.imessage.data.request.DeleteChannelAssociationPostBody;
import com.longx.intelligent.android.imessage.data.request.RemoveChannelCollectionPostBody;
import com.longx.intelligent.android.imessage.data.request.RemoveChannelsOfTagPostBody;
import com.longx.intelligent.android.imessage.data.request.RequestAddChannelPostBody;
import com.longx.intelligent.android.imessage.data.request.SetChannelTagsPostBody;
import com.longx.intelligent.android.imessage.data.request.SetNoteToAssociatedChannelPostBody;
import com.longx.intelligent.android.imessage.data.request.SortGroupTagsPostBody;
import com.longx.intelligent.android.imessage.data.request.SortTagsPostBody;
import com.longx.intelligent.android.imessage.data.response.OperationData;
import com.longx.intelligent.android.imessage.data.response.OperationStatus;
import com.xcheng.retrofit.CompletableCall;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by LONG on 2024/4/28 at 1:07 AM.
 */
public interface ChannelApi {

    @GET("channel/find/imessage_id/{imessageId}")
    CompletableCall<OperationData> findChannelByImessageId(@Path("imessageId") String imessageId);

    @GET("channel/find/imessage_id_user/{imessageIdUser}")
    CompletableCall<OperationData> findChannelByImessageIdUser(@Path("imessageIdUser") String imessageIdUser);

    @GET("channel/find/email/{email}")
    CompletableCall<OperationData> findChannelByEmail(@Path("email") String email);

    @POST("channel/add/request")
    CompletableCall<OperationStatus> requestAddChannel(@Body RequestAddChannelPostBody postBody);

    @POST("channel/add/accept")
    CompletableCall<OperationStatus> acceptAddChannel(@Body AcceptAddChannelPostBody postBody);

    @GET("channel/add/activities/not_viewed_count")
    CompletableCall<OperationData> fetchChannelAdditionNotViewCount();

    @GET("channel/add/activity/all")
    CompletableCall<OperationData> fetchAllAdditionActivities();

    @POST("channel/add/activity/{uuid}/view")
    CompletableCall<OperationStatus> viewOneAdditionActivity(@Path("uuid") String uuid);

    @GET("channel/association/all")
    CompletableCall<OperationData> fetchAllAssociations();

    @POST("channel/association/delete")
    CompletableCall<OperationStatus> deleteAssociatedChannel(@Body DeleteChannelAssociationPostBody postBody);

    @POST("channel/association/note/set")
    CompletableCall<OperationStatus> setNoteToAssociatedChannel(@Body SetNoteToAssociatedChannelPostBody postBody);

    @POST("channel/association/note/delete/{channelImessageId}")
    CompletableCall<OperationStatus> deleteNoteOfAssociatedChannel(@Path("channelImessageId") String channelImessageId);

    @POST("channel/association/tag/add")
    CompletableCall<OperationStatus> addTag(@Body AddChannelTagPostBody postBody);

    @GET("channel/association/tag/all")
    CompletableCall<OperationData> fetchAllTags();

    @POST("channel/association/tag/name/change")
    CompletableCall<OperationStatus> changeTagName(@Body ChangeChannelTagNamePostBody postBody);

    @POST("channel/association/tag/sort")
    CompletableCall<OperationStatus> sortChannelTags(@Body SortTagsPostBody postBody);

    @POST("channel/association/tag/channel/add")
    CompletableCall<OperationStatus> addChannelsToTag(@Body AddChannelsToTagPostBody postBody);

    @POST("channel/association/tag/channel/remove")
    CompletableCall<OperationStatus> removeChannelsOfTag(@Body RemoveChannelsOfTagPostBody postBody);

    @POST("channel/association/tag/delete/{tagId}")
    CompletableCall<OperationStatus> deleteChannelTag(@Path("tagId") String tagId);

    @POST("channel/association/tag/channel/set")
    CompletableCall<OperationStatus> setChannelTags(@Body SetChannelTagsPostBody postBody);

    @GET("channel/collection")
    CompletableCall<OperationData> fetchAllCollections();

    @POST("channel/collection/add")
    CompletableCall<OperationStatus> addCollection(@Body AddChannelCollectionPostBody postBody);

    @POST("channel/collection/remove")
    CompletableCall<OperationStatus> removeCollection(@Body RemoveChannelCollectionPostBody postBody);

    @POST("channel/collection/sort")
    CompletableCall<OperationStatus> sortCollections(@Body SortTagsPostBody postBody);
}
