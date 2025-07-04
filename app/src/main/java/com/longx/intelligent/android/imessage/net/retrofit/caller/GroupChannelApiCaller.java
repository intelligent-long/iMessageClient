package com.longx.intelligent.android.imessage.net.retrofit.caller;

import androidx.lifecycle.LifecycleOwner;

import com.longx.intelligent.android.imessage.data.request.AcceptAddGroupChannelPostBody;
import com.longx.intelligent.android.imessage.data.request.AcceptTransferGroupChannelManagerPostBody;
import com.longx.intelligent.android.imessage.data.request.AddChannelCollectionPostBody;
import com.longx.intelligent.android.imessage.data.request.AddGroupChannelCollectionPostBody;
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
import com.longx.intelligent.android.imessage.data.request.RemoveChannelCollectionPostBody;
import com.longx.intelligent.android.imessage.data.request.RemoveGroupChannelCollectionPostBody;
import com.longx.intelligent.android.imessage.data.request.RemoveGroupChannelsOfTagPostBody;
import com.longx.intelligent.android.imessage.data.request.RequestAddGroupChannelPostBody;
import com.longx.intelligent.android.imessage.data.request.SetGroupChannelTagsPostBody;
import com.longx.intelligent.android.imessage.data.request.SetNoteToAssociatedGroupChannelPostBody;
import com.longx.intelligent.android.imessage.data.request.SortGroupTagsPostBody;
import com.longx.intelligent.android.imessage.data.request.TransferGroupChannelManagerPostBody;
import com.longx.intelligent.android.imessage.data.request.ViewGroupChannelNotificationsPostBody;
import com.longx.intelligent.android.imessage.data.response.OperationData;
import com.longx.intelligent.android.imessage.data.response.OperationStatus;
import com.longx.intelligent.android.imessage.net.retrofit.api.GroupChannelApi;
import com.xcheng.retrofit.CompletableCall;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Body;
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

    public static CompletableCall<OperationStatus> changeGroupName(LifecycleOwner lifecycleOwner, ChangeGroupChannelNamePostBody postBody, BaseYier<OperationStatus> yier){
        CompletableCall<OperationStatus> call = getApiImplementation().changeGroupName(postBody);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationData> findGroupChannelByGroupChannelIdUser(LifecycleOwner lifecycleOwner, String groupChannelId, BaseYier<OperationData> yier){
        CompletableCall<OperationData> call = getApiImplementation().findGroupChannelByGroupChannelId(groupChannelId);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationData> findGroupChannelByGroupChannelId(LifecycleOwner lifecycleOwner, String groupChannelId, String queryType, boolean includeInactive, BaseYier<OperationData> yier){
        CompletableCall<OperationData> call = getApiImplementation().findGroupChannelByGroupChannelId(groupChannelId, queryType, includeInactive);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationData> groupChannelIdUserNowCanChange(LifecycleOwner lifecycleOwner, String groupChannelId, BaseYier<OperationData> yier){
        CompletableCall<OperationData> call = getApiImplementation().groupChannelIdUserNowCanChange(groupChannelId);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationStatus> changeGroupChannelIdUser(LifecycleOwner lifecycleOwner, ChangeGroupChannelIdUserPostBody postBody, BaseYier<OperationStatus> yier){
        CompletableCall<OperationStatus> call = getApiImplementation().changeGroupChannelIdUser(postBody);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationStatus> changeGroupChannelRegion(LifecycleOwner lifecycleOwner, ChangeGroupChannelRegionPostBody postBody, BaseYier<OperationStatus> yier){
        CompletableCall<OperationStatus> call = getApiImplementation().changeRegion(postBody);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationStatus> changeGroupChannelAvatar(LifecycleOwner lifecycleOwner, byte[] avatar, String groupChannelId, BaseYier<OperationStatus> yier){
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), avatar);
        MultipartBody.Part avatarPart = MultipartBody.Part.createFormData("avatar", "avatar", requestBody);
        CompletableCall<OperationStatus> call = getApiImplementation().changeGroupChannelAvatar(avatarPart, groupChannelId);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationStatus> removeGroupChannelAvatar(LifecycleOwner lifecycleOwner, String groupChannelId, BaseYier<OperationStatus> yier){
        CompletableCall<OperationStatus> call = getApiImplementation().removeGroupChannelAvatar(groupChannelId);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationStatus> setNoteToAssociatedGroupChannel(LifecycleOwner lifecycleOwner, SetNoteToAssociatedGroupChannelPostBody postBody, BaseYier<OperationStatus> yier){
        CompletableCall<OperationStatus> call = getApiImplementation().setNoteToAssociatedGroupChannel(postBody);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationStatus> deleteNoteOfAssociatedGroupChannel(LifecycleOwner lifecycleOwner, String groupChannelId, BaseYier<OperationStatus> yier){
        CompletableCall<OperationStatus> call = getApiImplementation().deleteNoteOfAssociatedGroupChannel(groupChannelId);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationStatus> addTag(LifecycleOwner lifecycleOwner, AddGroupChannelTagPostBody postBody, BaseYier<OperationStatus> yier){
        CompletableCall<OperationStatus> call = getApiImplementation().addTag(postBody);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationData> fetchAllTags(LifecycleOwner lifecycleOwner, BaseYier<OperationData> yier){
        CompletableCall<OperationData> call = getApiImplementation().fetchAllTags();
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationStatus> sortGroupChannelTags(LifecycleOwner lifecycleOwner, SortGroupTagsPostBody postBody, BaseYier<OperationStatus> yier){
        CompletableCall<OperationStatus> call = getApiImplementation().sortGroupChannelTags(postBody);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationStatus> deleteGroupChannelTag(LifecycleOwner lifecycleOwner, String tagId, BaseYier<OperationStatus> yier){
        CompletableCall<OperationStatus> call = getApiImplementation().deleteGroupChannelTag(tagId);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationStatus> changeGroupChannelTagName(LifecycleOwner lifecycleOwner, ChangeGroupChannelTagNamePostBody postBody, BaseYier<OperationStatus> yier){
        CompletableCall<OperationStatus> call = getApiImplementation().changeGroupChannelTagName(postBody);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationStatus> addGroupChannelsToTag(LifecycleOwner lifecycleOwner, AddGroupChannelsToTagPostBody postBody, BaseYier<OperationStatus> yier){
        CompletableCall<OperationStatus> call = getApiImplementation().addGroupChannelsToTag(postBody);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationStatus> removeGroupChannelsOfTag(LifecycleOwner lifecycleOwner, RemoveGroupChannelsOfTagPostBody postBody, BaseYier<OperationStatus> yier){
        CompletableCall<OperationStatus> call = getApiImplementation().removeGroupChannelsOfTag(postBody);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationStatus> setGroupChannelTags(LifecycleOwner lifecycleOwner, SetGroupChannelTagsPostBody postBody, BaseYier<OperationStatus> yier){
        CompletableCall<OperationStatus> call = getApiImplementation().setGroupChannelTags(postBody);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationStatus> changeGroupJoinVerification(LifecycleOwner lifecycleOwner, ChangeGroupChannelJoinVerificationPostBody postBody, BaseYier<OperationStatus> yier){
        CompletableCall<OperationStatus> call = getApiImplementation().changeGroupJoinVerification(postBody);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationStatus> requestAddGroupChannel(LifecycleOwner lifecycleOwner, RequestAddGroupChannelPostBody postBody, BaseYier<OperationStatus> yier){
        CompletableCall<OperationStatus> call = getApiImplementation().requestAddGroupChannel(postBody);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationData> fetchGroupChannelAdditionUnviewedCount(LifecycleOwner lifecycleOwner, BaseYier<OperationData> yier){
        CompletableCall<OperationData> call = getApiImplementation().fetchGroupChannelAdditionNotViewCount();
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationData> fetchAllGroupAdditionActivities(LifecycleOwner lifecycleOwner, BaseYier<OperationData> yier){
        CompletableCall<OperationData> call = getApiImplementation().fetchAllGroupAdditionActivities();
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationStatus> acceptAdd(LifecycleOwner lifecycleOwner, AcceptAddGroupChannelPostBody postBody, BaseYier<OperationStatus> yier){
        CompletableCall<OperationStatus> call = getApiImplementation().acceptAdd(postBody);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationStatus> viewOneAdditionActivity(LifecycleOwner lifecycleOwner, String uuid, BaseYier<OperationStatus> yier){
        CompletableCall<OperationStatus> call = getApiImplementation().viewOneAdditionActivity(uuid);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationStatus> invite(LifecycleOwner lifecycleOwner, InviteJoinGroupChannelPostBody postBody, BaseYier<OperationStatus> yier){
        CompletableCall<OperationStatus> call = getApiImplementation().invite(postBody);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationStatus> disconnect(LifecycleOwner lifecycleOwner, String groupChannelId, BaseYier<OperationStatus> yier){
        CompletableCall<OperationStatus> call = getApiImplementation().disconnect(groupChannelId);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationStatus> manageGroupChannelDisconnectChannel(LifecycleOwner lifecycleOwner, String groupChannelId, ManageGroupChannelDisconnectPostBody postBody, BaseYier<OperationStatus> yier){
        CompletableCall<OperationStatus> call = getApiImplementation().manageGroupChannelDisconnectChannel(groupChannelId, postBody);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationData> fetchGroupChannelNotifications(LifecycleOwner lifecycleOwner, BaseYier<OperationData> yier){
        CompletableCall<OperationData> call = getApiImplementation().fetchGroupChannelNotifications();
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationStatus> viewGroupChannelNotifications(LifecycleOwner lifecycleOwner, ViewGroupChannelNotificationsPostBody postBody, BaseYier<OperationStatus> yier){
        CompletableCall<OperationStatus> call = getApiImplementation().viewGroupChannelNotifications(postBody);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationStatus> transferGroupChannelManager(LifecycleOwner lifecycleOwner, TransferGroupChannelManagerPostBody postBody, BaseYier<OperationStatus> yier){
        CompletableCall<OperationStatus> call = getApiImplementation().transferGroupChannelManager(postBody);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationStatus> acceptTransferGroupChannelManager(LifecycleOwner lifecycleOwner, AcceptTransferGroupChannelManagerPostBody postBody, BaseYier<OperationStatus> yier){
        CompletableCall<OperationStatus> call = getApiImplementation().acceptTransferGroupChannelManager(postBody);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationStatus> terminateGroupChannel(LifecycleOwner lifecycleOwner, String groupChannelId, BaseYier<OperationStatus> yier){
        CompletableCall<OperationStatus> call = getApiImplementation().terminateGroupChannel(groupChannelId);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationData> fetchAllGroupCollections(LifecycleOwner lifecycleOwner, BaseYier<OperationData> yier){
        CompletableCall<OperationData> call = getApiImplementation().fetchAllGroupCollections();
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationStatus> addGroupCollection(LifecycleOwner lifecycleOwner, AddGroupChannelCollectionPostBody postBody, BaseYier<OperationStatus> yier){
        CompletableCall<OperationStatus> call = getApiImplementation().addGroupCollection(postBody);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationStatus> removeGroupCollection(LifecycleOwner lifecycleOwner, RemoveGroupChannelCollectionPostBody postBody, BaseYier<OperationStatus> yier){
        CompletableCall<OperationStatus> call = getApiImplementation().removeGroupCollection(postBody);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationStatus> sortGroupCollections(LifecycleOwner lifecycleOwner, SortGroupTagsPostBody postBody, BaseYier<OperationStatus> yier){
        CompletableCall<OperationStatus> call = getApiImplementation().sortGroupCollections(postBody);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }
}
