package com.longx.intelligent.android.ichat2.da.database.manager;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;

import com.longx.intelligent.android.ichat2.da.database.helper.ChannelsDatabaseHelper;
import com.longx.intelligent.android.ichat2.da.database.helper.ChatDatabaseHelper;
import com.longx.intelligent.android.ichat2.da.sharedpref.SharedPreferencesAccessor;

/**
 * Created by LONG on 2024/5/13 at 2:28 AM.
 */
public class ChatDatabaseManager extends BaseDatabaseManager{
    public ChatDatabaseManager(SQLiteOpenHelper helper) {
        super(helper);
    }

    private static class InstanceHolder{
        private static ChatDatabaseManager instance;
    }

    public static void init(Context context){
        String ichatId = SharedPreferencesAccessor.UserInfoPref.getCurrentUserInfo(context).getIchatId();
        ChatDatabaseHelper helper = new ChatDatabaseHelper(context, ichatId);
        ChatDatabaseManager.InstanceHolder.instance = new ChatDatabaseManager(helper);
    }

    public static ChatDatabaseManager getInstance() {
        return ChatDatabaseManager.InstanceHolder.instance;
    }


}
