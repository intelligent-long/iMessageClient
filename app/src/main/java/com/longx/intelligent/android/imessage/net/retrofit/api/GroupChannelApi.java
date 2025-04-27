package com.longx.intelligent.android.imessage.net.retrofit.api;

import com.longx.intelligent.android.imessage.data.request.ChangeGroupChannelIdUserPostBody;
import com.longx.intelligent.android.imessage.data.request.ChangeGroupNamePostBody;
import com.longx.intelligent.android.imessage.data.request.CreateGroupChannelPostBody;
import com.longx.intelligent.android.imessage.data.response.OperationData;
import com.longx.intelligent.android.imessage.data.response.OperationStatus;
import com.xcheng.retrofit.CompletableCall;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
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
    CompletableCall<OperationStatus> changeGroupName(@Body ChangeGroupNamePostBody postBody);

    @GET("group_channel/find/group_channel_id/{groupChannelId}")
    CompletableCall<OperationData> fetchOneGroupAssociation(@Path("groupChannelId") String groupChannelId);

    @GET("group_channel/info/group_channel_id_user/can_change/{groupChannelId}")
    CompletableCall<OperationData> groupChannelIdUserNowCanChange(@Path("groupChannelId") String groupChannelId);

    @POST("group_channel/info/group_channel_id_user/change")
    CompletableCall<OperationStatus> changeGroupChannelIdUser(@Body ChangeGroupChannelIdUserPostBody postBody);

}
