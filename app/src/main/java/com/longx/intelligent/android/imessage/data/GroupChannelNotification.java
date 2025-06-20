package com.longx.intelligent.android.imessage.data;

import androidx.appcompat.app.AppCompatActivity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.longx.intelligent.android.imessage.da.database.manager.ChannelDatabaseManager;
import com.longx.intelligent.android.imessage.da.database.manager.GroupChannelDatabaseManager;
import com.longx.intelligent.android.imessage.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.imessage.data.response.OperationData;
import com.longx.intelligent.android.imessage.net.retrofit.caller.ChannelApiCaller;
import com.longx.intelligent.android.imessage.net.retrofit.caller.GroupChannelApiCaller;
import com.longx.intelligent.android.imessage.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.imessage.yier.ResultsYier;

import java.util.Date;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by LONG on 2025/6/14 at 7:09 AM.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GroupChannelNotification {
    public enum Type{ACTIVE_DISCONNECT, PASSIVE_DISCONNECT, INVITE_TRANSFER_MANAGER}
    private String uuid;
    private Type type;
    private String groupChannelId;
    private String channelId;
    private Boolean passive;
    private String byWhom;
    private Date time;
    @JsonProperty("viewed")
    private boolean isViewed;

    private GroupChannel groupChannel;
    private Channel channel;
    private Channel byChannel;

    public GroupChannelNotification() {
    }

    public GroupChannelNotification(String uuid, Type type, String groupChannelId, String channelId, Boolean passive, String byWhom, Date time, boolean isViewed) {
        this.uuid = uuid;
        this.type = type;
        this.groupChannelId = groupChannelId;
        this.channelId = channelId;
        this.passive = passive;
        this.byWhom = byWhom;
        this.time = time;
        this.isViewed = isViewed;
    }

    public String getUuid() {
        return uuid;
    }

    public Type getType() {
        return type;
    }

    public String getGroupChannelId() {
        return groupChannelId;
    }

    public String getChannelId() {
        return channelId;
    }

    public Boolean isPassive() {
        return passive;
    }

    public String getByWhom() {
        return byWhom;
    }

    public Date getTime() {
        return time;
    }

    public boolean isViewed() {
        return isViewed;
    }

    public void getChannel(AppCompatActivity activity, ResultsYier resultsYier) {
        if(channel == null){
            channel = ChannelDatabaseManager.getInstance().findOneChannel(channelId);
        }
        if(channel == null && channelId.equals(SharedPreferencesAccessor.UserProfilePref.getCurrentUserProfile(activity).getImessageId())){
            channel = SharedPreferencesAccessor.UserProfilePref.getCurrentUserProfile(activity).toChannel();
        }
        if(channel == null) {
            ChannelApiCaller.findChannelByImessageId(activity, channelId, new RetrofitApiCaller.BaseCommonYier<OperationData>(activity){
                @Override
                public void ok(OperationData data, Response<OperationData> raw, Call<OperationData> call) {
                    super.ok(data, raw, call);
                    data.commonHandleSuccessResult(() -> {
                        channel = data.getData(Channel.class);
                        resultsYier.onResults(channel);
                    });
                }
            });
        }else {
            resultsYier.onResults(channel);
        }
    }

    public void getGroupChannel(AppCompatActivity activity, ResultsYier resultsYier) {
        if(groupChannel == null){
            groupChannel = GroupChannelDatabaseManager.getInstance().findOneAssociation(groupChannelId);
        }
        if(groupChannel == null){
            GroupChannelApiCaller.findGroupChannelByGroupChannelId(activity, groupChannelId, "id", new RetrofitApiCaller.BaseCommonYier<OperationData>(activity){
                @Override
                public void ok(OperationData data, Response<OperationData> raw, Call<OperationData> call) {
                    super.ok(data, raw, call);
                    data.commonHandleSuccessResult(() -> {
                        groupChannel = data.getData(GroupChannel.class);
                        resultsYier.onResults(groupChannel);
                    });
                }
            });
        }else {
            resultsYier.onResults(groupChannel);
        }
    }

    public void getByChannel(AppCompatActivity activity, ResultsYier resultsYier) {
        if(byChannel == null){
            byChannel = ChannelDatabaseManager.getInstance().findOneChannel(byWhom);
        }
        if(byChannel == null && byWhom.equals(SharedPreferencesAccessor.UserProfilePref.getCurrentUserProfile(activity).getImessageId())){
            byChannel = SharedPreferencesAccessor.UserProfilePref.getCurrentUserProfile(activity).toChannel();
        }
        if(byChannel == null){
            ChannelApiCaller.findChannelByImessageId(activity, byWhom, new RetrofitApiCaller.BaseCommonYier<OperationData>(activity){
                @Override
                public void ok(OperationData data, Response<OperationData> raw, Call<OperationData> call) {
                    super.ok(data, raw, call);
                    data.commonHandleSuccessResult(() -> {
                        byChannel = data.getData(Channel.class);
                        resultsYier.onResults(byChannel);
                    });
                }
            });
        }else {
            resultsYier.onResults(byChannel);
        }
    }

    public Channel getChannel() {
        return channel;
    }

    public GroupChannel getGroupChannel() {
        return groupChannel;
    }

    public Channel getByChannel() {
        return byChannel;
    }
}
