package com.longx.intelligent.android.ichat2.net.stomp;

import android.content.Context;

import com.longx.intelligent.android.ichat2.behavior.ServerContentUpdater;

/**
 * Created by LONG on 2024/4/1 at 5:03 AM.
 */
public class ServerMessageServiceStompActions {

    public static void updateCurrentUserInfo(Context context){
        ServerContentUpdater.updateCurrentUserInfo(context);
    }

}
