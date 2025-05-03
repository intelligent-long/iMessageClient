package com.longx.intelligent.android.imessage.net.retrofit.api;

import com.longx.intelligent.android.imessage.data.request.AddGroupChannelTagPostBody;
import com.longx.intelligent.android.imessage.data.request.ChangeGroupChannelIdUserPostBody;
import com.longx.intelligent.android.imessage.data.request.ChangeGroupChannelNamePostBody;
import com.longx.intelligent.android.imessage.data.request.ChangeGroupChannelRegionPostBody;
import com.longx.intelligent.android.imessage.data.request.ChangeRegionPostBody;
import com.longx.intelligent.android.imessage.data.request.CreateGroupChannelPostBody;
import com.longx.intelligent.android.imessage.data.request.SetNoteToAssociatedGroupChannelPostBody;
import com.longx.intelligent.android.imessage.data.request.SortGroupTagsPostBody;
import com.longx.intelligent.android.imessage.data.request.SortTagsPostBody;
import com.longx.intelligent.android.imessage.data.response.OperationData;
import com.longx.intelligent.android.imessage.data.response.OperationStatus;
import com.xcheng.retrofit.CompletableCall;

import cn.hutool.http.useragent.OS;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

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
    CompletableCall<OperationData> fetchOneGroupAssociation(@Path("groupChannelId") String groupChannelId);

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

}
