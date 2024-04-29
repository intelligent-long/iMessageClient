package com.longx.intelligent.android.ichat2;

import com.longx.intelligent.android.ichat2.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.ichat2.net.CookieJar;
import com.longx.intelligent.android.ichat2.net.OkHttpClientCreator;
import com.longx.intelligent.android.ichat2.net.retrofit.RetrofitCreator;
import com.longx.intelligent.android.ichat2.service.ServerMessageService;

/**
 * Created by LONG on 2024/3/27 at 7:15 PM.
 */
public class Application extends android.app.Application {
    private static Application application;
    {
        application = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    private void init(){
        CookieJar.create(this);
        OkHttpClientCreator.create();
        RetrofitCreator.create(this);
        boolean loginState = SharedPreferencesAccessor.NetPref.getLoginState(this);
        if(loginState) {
            ServerMessageService.work(this);
        }
    }
}
