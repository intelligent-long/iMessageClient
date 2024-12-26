package com.longx.intelligent.android.ichat2.net.retrofit.caller;

import androidx.lifecycle.LifecycleOwner;

import com.longx.intelligent.android.ichat2.data.request.ChangeEmailPostBody;
import com.longx.intelligent.android.ichat2.data.request.ChangeIchatIdUserPostBody;
import com.longx.intelligent.android.ichat2.data.request.ChangeRegionPostBody;
import com.longx.intelligent.android.ichat2.data.request.ChangeSexPostBody;
import com.longx.intelligent.android.ichat2.data.request.ChangeUsernamePostBody;
import com.longx.intelligent.android.ichat2.data.response.OperationData;
import com.longx.intelligent.android.ichat2.data.response.OperationStatus;
import com.longx.intelligent.android.ichat2.net.retrofit.api.UserApi;
import com.xcheng.retrofit.CompletableCall;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

/**
 * Created by LONG on 2024/4/1 at 4:07 AM.
 */
public class UserApiCaller extends RetrofitApiCaller{
    public static UserApi getApiImplementation(){
        return getApiImplementation(UserApi.class);
    }

    public static CompletableCall<OperationData> whoAmI(LifecycleOwner lifecycleOwner, BaseYier<OperationData> yier){
        CompletableCall<OperationData> call = getApiImplementation().whoAmI();
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<ResponseBody> fetchAvatar(LifecycleOwner lifecycleOwner, String avatarHash, BaseYier<ResponseBody> yier){
        CompletableCall<ResponseBody> call = getApiImplementation().fetchAvatar(avatarHash);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationStatus> changeAvatar(LifecycleOwner lifecycleOwner, byte[] avatar, BaseYier<OperationStatus> yier){
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), avatar);
        MultipartBody.Part avatarPart = MultipartBody.Part.createFormData("avatar", "avatar", requestBody);
        CompletableCall<OperationStatus> call = getApiImplementation().changeAvatar(avatarPart);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationStatus> removeAvatar(LifecycleOwner lifecycleOwner, BaseYier<OperationStatus> yier){
        CompletableCall<OperationStatus> call = getApiImplementation().removeAvatar();
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationData> ichatIdUserNowCanChange(LifecycleOwner lifecycleOwner, BaseYier<OperationData> yier){
        CompletableCall<OperationData> call = getApiImplementation().ichatIdUserNowCanChange();
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationStatus> changeIchatIdUser(LifecycleOwner lifecycleOwner, ChangeIchatIdUserPostBody postBody, BaseYier<OperationStatus> yier){
        CompletableCall<OperationStatus> call = getApiImplementation().changeIchatIdUser(postBody);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationStatus> changeUsername(LifecycleOwner lifecycleOwner, ChangeUsernamePostBody postBody, BaseYier<OperationStatus> yier){
        CompletableCall<OperationStatus> call = getApiImplementation().changeUsername(postBody);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationStatus> changeEmail(LifecycleOwner lifecycleOwner, ChangeEmailPostBody postBody, BaseYier<OperationStatus> yier){
        CompletableCall<OperationStatus> call = getApiImplementation().changeEmail(postBody);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationStatus> changeSex(LifecycleOwner lifecycleOwner, ChangeSexPostBody postBody, BaseYier<OperationStatus> yier){
        CompletableCall<OperationStatus> call = getApiImplementation().changeSex(postBody);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationData> fetchAllFirstRegions(LifecycleOwner lifecycleOwner, BaseYier<OperationData> yier){
        CompletableCall<OperationData> call = getApiImplementation().fetchAllFirstRegions();
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationData> fetchAllSecondRegions(LifecycleOwner lifecycleOwner, int firstRegionAdcode, BaseYier<OperationData> yier){
        CompletableCall<OperationData> call = getApiImplementation().fetchAllSecondRegions(firstRegionAdcode);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationData> fetchAllThirdRegions(LifecycleOwner lifecycleOwner, int secondRegionAdcode, BaseYier<OperationData> yier){
        CompletableCall<OperationData> call = getApiImplementation().fetchAllThirdRegions(secondRegionAdcode);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationStatus> changeRegion(LifecycleOwner lifecycleOwner, ChangeRegionPostBody postBody, BaseYier<OperationStatus> yier){
        CompletableCall<OperationStatus> call = getApiImplementation().changeRegion(postBody);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }
}
