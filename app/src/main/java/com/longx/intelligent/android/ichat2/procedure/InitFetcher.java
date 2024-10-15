package com.longx.intelligent.android.ichat2.procedure;

import android.content.Context;

import com.longx.intelligent.android.ichat2.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.ichat2.data.BroadcastChannelPermission;
import com.longx.intelligent.android.ichat2.data.response.OperationData;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.PermissionApiCaller;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.RetrofitApiCaller;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by LONG on 2024/10/15 at 下午4:17.
 */
public class InitFetcher {
    public static void doAll(Context context){
        checkAndFetchAndStoreBroadcastChannelPermission(context);
    }

    private static void checkAndFetchAndStoreBroadcastChannelPermission(Context context){
        BroadcastChannelPermission broadcastChannelPermission = SharedPreferencesAccessor.BroadcastPref.getAppBroadcastChannelPermission(context);
        if(broadcastChannelPermission == null) {
            PermissionApiCaller.fetchBroadcastChannelPermission(null, new RetrofitApiCaller.DelayedShowDialogCommonYier<OperationData>(null) {
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

}
