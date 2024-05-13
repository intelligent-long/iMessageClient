package com.longx.intelligent.android.ichat2.da.database;

import android.content.Context;

import com.longx.intelligent.android.ichat2.da.database.manager.ChannelsDatabaseManager;
import com.longx.intelligent.android.ichat2.da.database.manager.ChatDatabaseManager;

/**
 * Created by LONG on 2024/1/30 at 5:30 AM.
 */
public class DatabaseInitiator {
    public static void initAll(Context context){
        ChannelsDatabaseManager.init(context);
        ChatDatabaseManager.init(context);
    }
}
