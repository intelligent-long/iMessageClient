package com.longx.intelligent.android.ichat2.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.longx.intelligent.android.ichat2.Application;
import com.longx.intelligent.android.ichat2.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.ichat2.service.ServerMessageService;

/**
 * Created by LONG on 2024/4/22 at 12:09 AM.
 */
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(action == null){
            return;
        }
        if (action.equals("android.intent.action.BOOT_COMPLETED")) {
            boolean loginState = SharedPreferencesAccessor.NetPref.getLoginState(context);
            if(loginState) {
                ServerMessageService.work((Application) context.getApplicationContext());
            }
        }
    }
}
