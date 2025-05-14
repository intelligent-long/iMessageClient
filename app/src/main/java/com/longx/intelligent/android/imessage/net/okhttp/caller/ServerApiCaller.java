package com.longx.intelligent.android.imessage.net.okhttp.caller;

import com.longx.intelligent.android.imessage.data.GroupChannelActivity;
import com.longx.intelligent.android.imessage.net.ServerLocation;
import com.longx.intelligent.android.imessage.net.ServerValues;

/**
 * Created by LONG on 2024/10/31 at 下午11:35.
 */
public class ServerApiCaller {

    public static void fetchCentralServerLocation(ApiCaller.BaseCallYier<ServerLocation> callYier){
        ApiCaller.callApi(ServerValues.SERVER_FINDER_FIND_IMESSAGE_SERVER_LOCATION_URL, ServerLocation.class, callYier);
    }
}
