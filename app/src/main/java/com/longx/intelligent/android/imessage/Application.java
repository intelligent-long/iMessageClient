package com.longx.intelligent.android.imessage;

import com.longx.intelligent.android.imessage.behaviorcomponents.InitFetcher;
import com.longx.intelligent.android.imessage.da.database.DatabaseInitiator;
import com.longx.intelligent.android.imessage.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.imessage.net.CookieJar;
import com.longx.intelligent.android.imessage.net.okhttp.OkHttpClientCreator;
import com.longx.intelligent.android.imessage.net.retrofit.RetrofitCreator;
import com.longx.intelligent.android.imessage.service.ServerMessageService;

/**
 * Created by LONG on 2024/3/27 at 7:15 PM.
 */
public class Application extends android.app.Application {
    public static Application application;
    {
        application = this;
    }
    public static boolean foreground;

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    private void init(){
        CookieJar.create(this);
        OkHttpClientCreator.create();
        DatabaseInitiator.initAll(this);
        boolean loginState = SharedPreferencesAccessor.NetPref.getLoginState(this);
        if(loginState) {
            ServerMessageService.work(this);
        }
        new Thread(() -> {
            RetrofitCreator.create(this);
            if(loginState) {
                InitFetcher.doAll(this);
            }
        }).start();
    }
}
