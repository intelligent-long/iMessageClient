package com.longx.intelligent.android.imessage.net.retrofit.api;

import com.longx.intelligent.android.imessage.data.request.ChangeEmailPostBody;
import com.longx.intelligent.android.imessage.data.request.ChangeImessageIdUserPostBody;
import com.longx.intelligent.android.imessage.data.request.ChangeRegionPostBody;
import com.longx.intelligent.android.imessage.data.request.ChangeSexPostBody;
import com.longx.intelligent.android.imessage.data.request.ChangeUsernamePostBody;
import com.longx.intelligent.android.imessage.data.response.OperationData;
import com.longx.intelligent.android.imessage.data.response.OperationStatus;
import com.xcheng.retrofit.CompletableCall;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Streaming;

/**
 * Created by LONG on 2024/4/1 at 4:06 AM.
 */
public interface UserApi {
    @GET("user/who_am_i")
    CompletableCall<OperationData> whoAmI();

    @Streaming
    @GET("user/info/avatar/{avatarHash}")
    CompletableCall<ResponseBody> fetchAvatar(@Path("avatarHash") String avatarHash);

    @Multipart
    @POST("user/info/avatar/change")
    CompletableCall<OperationStatus> changeAvatar(@Part MultipartBody.Part avatarPart);

    @POST("user/info/avatar/remove")
    CompletableCall<OperationStatus> removeAvatar();

    @GET("user/info/imessage_id_user/can_change")
    CompletableCall<OperationData> imessageIdUserNowCanChange();

    @POST("user/info/imessage_id_user/change")
    CompletableCall<OperationStatus> changeIchatIdUser(@Body ChangeImessageIdUserPostBody postBody);

    @POST("user/info/username/change")
    CompletableCall<OperationStatus> changeUsername(@Body ChangeUsernamePostBody postBody);

    @POST("user/info/email/change")
    CompletableCall<OperationStatus> changeEmail(@Body ChangeEmailPostBody postBody);

    @POST("user/info/sex/change")
    CompletableCall<OperationStatus> changeSex(@Body ChangeSexPostBody postBody);

    @GET("user/region/first_region/all")
    CompletableCall<OperationData> fetchAllFirstRegions();

    @GET("user/region/second_region/all/{firstRegionAdcode}")
    CompletableCall<OperationData> fetchAllSecondRegions(@Path("firstRegionAdcode") int firstRegionAdcode);

    @GET("user/region/third_region/all/{secondRegionAdcode}")
    CompletableCall<OperationData> fetchAllThirdRegions(@Path("secondRegionAdcode") int secondRegionAdcode);

    @POST("user/info/region/change")
    CompletableCall<OperationStatus> changeRegion(@Body ChangeRegionPostBody postBody);
}
