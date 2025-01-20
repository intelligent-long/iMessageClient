package com.longx.intelligent.android.imessage.net.retrofit.caller;

import androidx.lifecycle.LifecycleOwner;

import com.longx.intelligent.android.imessage.data.request.AcceptAddChannelPostBody;
import com.longx.intelligent.android.imessage.data.request.AddChannelTagPostBody;
import com.longx.intelligent.android.imessage.data.request.AddChannelsToTagPostBody;
import com.longx.intelligent.android.imessage.data.request.ChangeChannelTagNamePostBody;
import com.longx.intelligent.android.imessage.data.request.DeleteChannelAssociationPostBody;
import com.longx.intelligent.android.imessage.data.request.RemoveChannelsOfTagPostBody;
import com.longx.intelligent.android.imessage.data.request.RequestAddChannelPostBody;
import com.longx.intelligent.android.imessage.data.request.SetChannelTagsPostBody;
import com.longx.intelligent.android.imessage.data.request.SetNoteToAssociatedChannelPostBody;
import com.longx.intelligent.android.imessage.data.request.SortTagsPostBody;
import com.longx.intelligent.android.imessage.data.response.OperationData;
import com.longx.intelligent.android.imessage.data.response.OperationStatus;
import com.longx.intelligent.android.imessage.net.retrofit.api.ChannelApi;
import com.xcheng.retrofit.CompletableCall;

/**
 * Created by LONG on 2024/4/28 at 1:10 AM.
 */
public class ChannelApiCaller extends RetrofitApiCaller{
    public static ChannelApi getApiImplementation(){
        return getApiImplementation(ChannelApi.class);
    }

    public static CompletableCall<OperationData> findChannelByImessageId(LifecycleOwner lifecycleOwner, String imessageId, BaseYier<OperationData> yier){
        CompletableCall<OperationData> call = getApiImplementation().findChannelByImessageId(imessageId);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationData> findChannelByImessageIdUser(LifecycleOwner lifecycleOwner, String imessageIdUser, BaseYier<OperationData> yier){
        CompletableCall<OperationData> call = getApiImplementation().findChannelByImessageIdUser(imessageIdUser);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationData> findChannelByEmail(LifecycleOwner lifecycleOwner, String email, BaseYier<OperationData> yier){
        CompletableCall<OperationData> call = getApiImplementation().findChannelByEmail(email);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationStatus> requestAddChannel(LifecycleOwner lifecycleOwner, RequestAddChannelPostBody postBody, BaseYier<OperationStatus> yier){
        CompletableCall<OperationStatus> call = getApiImplementation().requestAddChannel(postBody);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationStatus> acceptAddChannel(LifecycleOwner lifecycleOwner, AcceptAddChannelPostBody postBody, BaseYier<OperationStatus> yier){
        CompletableCall<OperationStatus> call = getApiImplementation().acceptAddChannel(postBody);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationData> fetchChannelAdditionUnviewedCount(LifecycleOwner lifecycleOwner, BaseYier<OperationData> yier){
        CompletableCall<OperationData> call = getApiImplementation().fetchChannelAdditionNotViewCount();
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationData> fetchAllAdditionActivities(LifecycleOwner lifecycleOwner, BaseYier<OperationData> yier){
        CompletableCall<OperationData> call = getApiImplementation().fetchAllAdditionActivities();
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationStatus> viewOneAdditionActivity(LifecycleOwner lifecycleOwner, String uuid, BaseYier<OperationStatus> yier){
        CompletableCall<OperationStatus> call = getApiImplementation().viewOneAdditionActivity(uuid);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationData> fetchAllAssociations(LifecycleOwner lifecycleOwner, BaseYier<OperationData> yier){
        CompletableCall<OperationData> call = getApiImplementation().fetchAllAssociations();
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationStatus> deleteAssociatedChannel(LifecycleOwner lifecycleOwner, DeleteChannelAssociationPostBody postBody, BaseYier<OperationStatus> yier){
        CompletableCall<OperationStatus> call = getApiImplementation().deleteAssociatedChannel(postBody);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationStatus> setNoteToAssociatedChannel(LifecycleOwner lifecycleOwner, SetNoteToAssociatedChannelPostBody postBody, BaseYier<OperationStatus> yier){
        CompletableCall<OperationStatus> call = getApiImplementation().setNoteToAssociatedChannel(postBody);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationStatus> deleteNoteOfAssociatedChannel(LifecycleOwner lifecycleOwner, String channelImessageId, BaseYier<OperationStatus> yier){
        CompletableCall<OperationStatus> call = getApiImplementation().deleteNoteOfAssociatedChannel(channelImessageId);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationStatus> addTag(LifecycleOwner lifecycleOwner, AddChannelTagPostBody postBody, BaseYier<OperationStatus> yier){
        CompletableCall<OperationStatus> call = getApiImplementation().addTag(postBody);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationData> fetchAllTags(LifecycleOwner lifecycleOwner, BaseYier<OperationData> yier){
        CompletableCall<OperationData> call = getApiImplementation().fetchAllTags();
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationStatus> changeTagName(LifecycleOwner lifecycleOwner, ChangeChannelTagNamePostBody postBody, BaseYier<OperationStatus> yier){
        CompletableCall<OperationStatus> call = getApiImplementation().changeTagName(postBody);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationStatus> sortChannelTags(LifecycleOwner lifecycleOwner, SortTagsPostBody postBody, BaseYier<OperationStatus> yier){
        CompletableCall<OperationStatus> call = getApiImplementation().sortChannelTags(postBody);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationStatus> addChannelsToTag(LifecycleOwner lifecycleOwner, AddChannelsToTagPostBody postBody, BaseYier<OperationStatus> yier){
        CompletableCall<OperationStatus> call = getApiImplementation().addChannelsToTag(postBody);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationStatus> removeChannelsOfTag(LifecycleOwner lifecycleOwner, RemoveChannelsOfTagPostBody postBody, BaseYier<OperationStatus> yier){
        CompletableCall<OperationStatus> call = getApiImplementation().removeChannelsOfTag(postBody);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationStatus> deleteChannelTag(LifecycleOwner lifecycleOwner, String tagId, BaseYier<OperationStatus> yier){
        CompletableCall<OperationStatus> call = getApiImplementation().deleteChannelTag(tagId);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationStatus> setChannelTags(LifecycleOwner lifecycleOwner, SetChannelTagsPostBody postBody, BaseYier<OperationStatus> yier){
        CompletableCall<OperationStatus> call = getApiImplementation().setChannelTags(postBody);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }
}
