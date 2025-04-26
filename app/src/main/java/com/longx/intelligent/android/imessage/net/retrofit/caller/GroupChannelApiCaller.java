package com.longx.intelligent.android.imessage.net.retrofit.caller;

import androidx.lifecycle.LifecycleOwner;

import com.longx.intelligent.android.imessage.data.request.ChangeGroupNamePostBody;
import com.longx.intelligent.android.imessage.data.request.CreateGroupChannelPostBody;
import com.longx.intelligent.android.imessage.data.response.OperationData;
import com.longx.intelligent.android.imessage.data.response.OperationStatus;
import com.longx.intelligent.android.imessage.net.retrofit.api.GroupChannelApi;
import com.xcheng.retrofit.CompletableCall;

import retrofit2.http.Path;

/**
 * Created by LONG on 2025/4/20 at 上午5:29.
 */
public class GroupChannelApiCaller extends RetrofitApiCaller {
    public static GroupChannelApi getApiImplementation() {
        return getApiImplementation(GroupChannelApi.class);
    }

    public static CompletableCall<OperationStatus> createGroupChannel(LifecycleOwner lifecycleOwner, CreateGroupChannelPostBody postBody, BaseYier<OperationStatus> yier){
        CompletableCall<OperationStatus> call = getApiImplementation().createGroupChannel(postBody);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationData> fetchAllGroupAssociations(LifecycleOwner lifecycleOwner, BaseYier<OperationData> yier){
        CompletableCall<OperationData> call = getApiImplementation().fetchAllGroupAssociations();
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationStatus> changeGroupName(LifecycleOwner lifecycleOwner, ChangeGroupNamePostBody postBody, BaseYier<OperationStatus> yier){
        CompletableCall<OperationStatus> call = getApiImplementation().changeGroupName(postBody);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationData> fetchOneGroupAssociation(LifecycleOwner lifecycleOwner, @Path("groupChannelId") String groupChannelId, BaseYier<OperationData> yier){
        CompletableCall<OperationData> call = getApiImplementation().fetchOneGroupAssociation(groupChannelId);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }
}
