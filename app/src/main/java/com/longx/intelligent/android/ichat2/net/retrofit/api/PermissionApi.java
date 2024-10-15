package com.longx.intelligent.android.ichat2.net.retrofit.api;

import com.longx.intelligent.android.ichat2.data.request.ChangeAllowChatMessagePostBody;
import com.longx.intelligent.android.ichat2.data.request.ChangeBroadcastChannelPermissionPostBody;
import com.longx.intelligent.android.ichat2.data.request.ChangeExcludeBroadcastChannelPostBody;
import com.longx.intelligent.android.ichat2.data.request.ChangeUserProfileVisibilityPostBody;
import com.longx.intelligent.android.ichat2.data.request.ChangeWaysToFindMePostBody;
import com.longx.intelligent.android.ichat2.data.response.OperationData;
import com.longx.intelligent.android.ichat2.data.response.OperationStatus;
import com.xcheng.retrofit.CompletableCall;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by LONG on 2024/6/7 at 6:00 PM.
 */
public interface PermissionApi {

    @POST("permission/user/profile_visibility/change")
    CompletableCall<OperationStatus> changeUserProfileVisibility(@Body ChangeUserProfileVisibilityPostBody postBody);

    @POST("permission/user/ways_to_find_me/change")
    CompletableCall<OperationStatus> changeWaysToFindMe(@Body ChangeWaysToFindMePostBody postBody);

    @POST("permission/channel/chat_message/allow_chat_message/change")
    CompletableCall<OperationStatus> changeAllowChatMessage(@Body ChangeAllowChatMessagePostBody postBody);

    @GET("permission/broadcast/channel_permission")
    CompletableCall<OperationData> fetchBroadcastChannelPermission();

    @POST("permission/broadcast/channel_permission/change")
    CompletableCall<OperationStatus> changeChannelPermission(@Body ChangeBroadcastChannelPermissionPostBody postBody);

    @GET("permission/broadcast/exclude_channel")
    CompletableCall<OperationData> fetchExcludeBroadcastChannels();

    @POST("permission/broadcast/exclude_channel/change")
    CompletableCall<OperationStatus> changeExcludeBroadcastChannels(@Body ChangeExcludeBroadcastChannelPostBody postBody);
}
