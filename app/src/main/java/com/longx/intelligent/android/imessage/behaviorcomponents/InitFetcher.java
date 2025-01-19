package com.longx.intelligent.android.imessage.behaviorcomponents;

import android.content.Context;

import com.fasterxml.jackson.core.type.TypeReference;
import com.longx.intelligent.android.imessage.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.imessage.data.BroadcastChannelPermission;
import com.longx.intelligent.android.imessage.data.response.OperationData;
import com.longx.intelligent.android.imessage.net.retrofit.caller.PermissionApiCaller;
import com.longx.intelligent.android.imessage.net.retrofit.caller.RetrofitApiCaller;

import java.util.Set;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by LONG on 2024/10/15 at 下午4:17.
 */
public class InitFetcher {
    public static void doAll(Context context){
        checkAndFetchAndStoreBroadcastChannelPermission(context);
        checkAndFetchAndStoreExcludeBroadcastChannels(context);
    }

    private static void checkAndFetchAndStoreBroadcastChannelPermission(Context context){
        BroadcastChannelPermission broadcastChannelPermission = SharedPreferencesAccessor.BroadcastPref.getAppBroadcastChannelPermission(context);
        if(broadcastChannelPermission == null ||
                (broadcastChannelPermission.getPermission() != BroadcastChannelPermission.PUBLIC && broadcastChannelPermission.getPermission() != BroadcastChannelPermission.PRIVATE && broadcastChannelPermission.getPermission() != BroadcastChannelPermission.CONNECTED_CHANNEL_CIRCLE) ||
                broadcastChannelPermission.getExcludeConnectedChannels() == null) {
            PermissionApiCaller.fetchBroadcastChannelPermission(null, new RetrofitApiCaller.BaseCommonYier<OperationData>(context) {
                @Override
                public void ok(OperationData data, Response<OperationData> raw, Call<OperationData> call) {
                    super.ok(data, raw, call);
                    data.commonHandleSuccessResult(() -> {
                        BroadcastChannelPermission broadcastChannelPermissionFetched = data.getData(BroadcastChannelPermission.class);
                        SharedPreferencesAccessor.BroadcastPref.saveAppBroadcastChannelPermission(context, broadcastChannelPermissionFetched);
                        SharedPreferencesAccessor.BroadcastPref.saveServerBroadcastChannelPermission(context, broadcastChannelPermissionFetched);
                    });
                }
            });
        }
    }

    private static void checkAndFetchAndStoreExcludeBroadcastChannels(Context context){
        boolean excludeBroadcastChannelsLoaded = SharedPreferencesAccessor.BroadcastPref.getExcludeBroadcastChannelsLoaded(context);
        if(!excludeBroadcastChannelsLoaded){
            PermissionApiCaller.fetchExcludeBroadcastChannels(null, new RetrofitApiCaller.BaseCommonYier<OperationData>(context){
                @Override
                public void ok(OperationData data, Response<OperationData> raw, Call<OperationData> call) {
                    super.ok(data, raw, call);
                    data.commonHandleSuccessResult(() -> {
                        Set<String> excludeBroadcastChannelIds = data.getData(new TypeReference<Set<String>>() {
                        });
                        SharedPreferencesAccessor.BroadcastPref.saveAppExcludeBroadcastChannels(context, excludeBroadcastChannelIds);
                        SharedPreferencesAccessor.BroadcastPref.saveServerExcludeBroadcastChannels(context, excludeBroadcastChannelIds);
                        SharedPreferencesAccessor.BroadcastPref.saveExcludeBroadcastChannelsLoaded(context);
                    });
                }
            });
        }
    }

}
