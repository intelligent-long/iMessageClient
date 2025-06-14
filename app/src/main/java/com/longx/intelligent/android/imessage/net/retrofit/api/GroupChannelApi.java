package com.longx.intelligent.android.imessage.net.retrofit.api;

import com.longx.intelligent.android.imessage.data.request.AcceptAddGroupChannelPostBody;
import com.longx.intelligent.android.imessage.data.request.AddGroupChannelTagPostBody;
import com.longx.intelligent.android.imessage.data.request.AddGroupChannelsToTagPostBody;
import com.longx.intelligent.android.imessage.data.request.ChangeGroupChannelIdUserPostBody;
import com.longx.intelligent.android.imessage.data.request.ChangeGroupChannelJoinVerificationPostBody;
import com.longx.intelligent.android.imessage.data.request.ChangeGroupChannelNamePostBody;
import com.longx.intelligent.android.imessage.data.request.ChangeGroupChannelRegionPostBody;
import com.longx.intelligent.android.imessage.data.request.ChangeGroupChannelTagNamePostBody;
import com.longx.intelligent.android.imessage.data.request.CreateGroupChannelPostBody;
import com.longx.intelligent.android.imessage.data.request.InviteJoinGroupChannelPostBody;
import com.longx.intelligent.android.imessage.data.request.ManageGroupChannelDisconnectPostBody;
import com.longx.intelligent.android.imessage.data.request.RemoveGroupChannelsOfTagPostBody;
import com.longx.intelligent.android.imessage.data.request.RequestAddGroupChannelPostBody;
import com.longx.intelligent.android.imessage.data.request.SetGroupChannelTagsPostBody;
import com.longx.intelligent.android.imessage.data.request.SetNoteToAssociatedGroupChannelPostBody;
import com.longx.intelligent.android.imessage.data.request.SortGroupTagsPostBody;
import com.longx.intelligent.android.imessage.data.response.OperationData;
import com.longx.intelligent.android.imessage.data.response.OperationStatus;
import com.xcheng.retrofit.CompletableCall;

import okhttp3.MultipartBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by LONG on 2025/4/20 at 上午5:27.
 */
public interface GroupChannelApi {

    @POST("group_channel/create")
    CompletableCall<OperationStatus> createGroupChannel(@Body CreateGroupChannelPostBody postBody);

    @GET("group_channel/association/all")
    CompletableCall<OperationData> fetchAllGroupAssociations();

    @POST("group_channel/info/group_name/change")
    CompletableCall<OperationStatus> changeGroupName(@Body ChangeGroupChannelNamePostBody postBody);

    @GET("group_channel/find/group_channel_id/{groupChannelId}")
    CompletableCall<OperationData> findGroupChannelByGroupChannelId(@Path("groupChannelId") String groupChannelId);

    @GET("group_channel/find/group_channel_id/{groupChannelId}")
    CompletableCall<OperationData> findGroupChannelByGroupChannelId(@Path("groupChannelId") String groupChannelId, @Query("queryType") String queryType);

    @GET("group_channel/info/group_channel_id_user/can_change/{groupChannelId}")
    CompletableCall<OperationData> groupChannelIdUserNowCanChange(@Path("groupChannelId") String groupChannelId);

    @POST("group_channel/info/group_channel_id_user/change")
    CompletableCall<OperationStatus> changeGroupChannelIdUser(@Body ChangeGroupChannelIdUserPostBody postBody);

    @POST("group_channel/info/region/change")
    CompletableCall<OperationStatus> changeRegion(@Body ChangeGroupChannelRegionPostBody postBody);

    @Multipart
    @POST("group_channel/info/avatar/change/{groupChannelId}")
    CompletableCall<OperationStatus> changeGroupChannelAvatar(@Part MultipartBody.Part avatarPart, @Path("groupChannelId") String groupChannelId);

    @POST("group_channel/info/avatar/remove/{groupChannelId}")
    CompletableCall<OperationStatus> removeGroupChannelAvatar(@Path("groupChannelId") String groupChannelId);

    @POST("group_channel/association/note/set")
    CompletableCall<OperationStatus> setNoteToAssociatedGroupChannel(@Body SetNoteToAssociatedGroupChannelPostBody postBody);

    @POST("group_channel/association/note/delete/{groupChannelId}")
    CompletableCall<OperationStatus> deleteNoteOfAssociatedGroupChannel(@Path("groupChannelId") String groupChannelId);

    @POST("group_channel/association/tag/add")
    CompletableCall<OperationStatus> addTag(@Body AddGroupChannelTagPostBody postBody);

    @GET("group_channel/association/tag/all")
    CompletableCall<OperationData> fetchAllTags();

    @POST("group_channel/association/tag/sort")
    CompletableCall<OperationStatus> sortGroupChannelTags(@Body SortGroupTagsPostBody postBody);

    @POST("group_channel/association/tag/delete/{tagId}")
    CompletableCall<OperationStatus> deleteGroupChannelTag(@Path("tagId") String tagId);

    @POST("group_channel/association/tag/name/change")
    CompletableCall<OperationStatus> changeGroupChannelTagName(@Body ChangeGroupChannelTagNamePostBody postBody);

    @POST("group_channel/association/tag/channel/add")
    CompletableCall<OperationStatus> addGroupChannelsToTag(@Body AddGroupChannelsToTagPostBody postBody);

    @POST("group_channel/association/tag/channel/remove")
    CompletableCall<OperationStatus> removeGroupChannelsOfTag(@Body RemoveGroupChannelsOfTagPostBody postBody);

    @POST("group_channel/association/tag/channel/set")
    CompletableCall<OperationStatus> setGroupChannelTags(@Body SetGroupChannelTagsPostBody postBody);

    @POST("group_channel_management/group_join_verification/change")
    CompletableCall<OperationStatus> changeGroupJoinVerification(@Body ChangeGroupChannelJoinVerificationPostBody postBody);

    @POST("group_channel/add/request")
    CompletableCall<OperationStatus> requestAddGroupChannel(@Body RequestAddGroupChannelPostBody postBody);

    @GET("group_channel/add/activities/not_viewed_count")
    CompletableCall<OperationData> fetchGroupChannelAdditionNotViewCount();

    @GET("group_channel/add/activity/all")
    CompletableCall<OperationData> fetchAllGroupAdditionActivities();

    @POST("group_channel/add/accept")
    CompletableCall<OperationStatus> acceptAdd(@Body AcceptAddGroupChannelPostBody postBody);

    @POST("group_channel/add/activity/{uuid}/view")
    CompletableCall<OperationStatus> viewOneAdditionActivity(@Path("uuid") String uuid);

    @POST("group_channel/add/invite")
    CompletableCall<OperationStatus> invite(@Body InviteJoinGroupChannelPostBody postBody);

    @POST("group_channel/disconnect/{groupChannelId}")
    CompletableCall<OperationStatus> disconnect(@Path("groupChannelId") String groupChannelId);

    @POST("group_channel/disconnect/manage/{groupChannelId}")
    CompletableCall<OperationStatus> manageGroupChannelDisconnectChannel(@Path("groupChannelId") String groupChannelId, @Body ManageGroupChannelDisconnectPostBody postBody);

    @GET("group_channel/group_channel_notifications")
    CompletableCall<OperationData> fetchGroupChannelNotifications();

}
