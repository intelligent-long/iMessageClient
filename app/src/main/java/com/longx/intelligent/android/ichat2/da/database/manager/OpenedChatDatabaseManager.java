package com.longx.intelligent.android.ichat2.da.database.manager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.longx.intelligent.android.ichat2.da.database.helper.ChannelDatabaseHelper;
import com.longx.intelligent.android.ichat2.da.database.helper.OpenedChatDatabaseHelper;
import com.longx.intelligent.android.ichat2.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.ichat2.data.OpenedChat;
import com.longx.intelligent.android.ichat2.util.DatabaseUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LONG on 2024/5/16 at 7:18 PM.
 */
public class OpenedChatDatabaseManager extends BaseDatabaseManager{

    public OpenedChatDatabaseManager(OpenedChatDatabaseHelper helper) {
        super(helper);
    }

    private static class InstanceHolder{
        private static OpenedChatDatabaseManager instance;
    }

    public static void init(Context context){
        String ichatId = SharedPreferencesAccessor.UserInfoPref.getCurrentUserInfo(context).getIchatId();
        OpenedChatDatabaseHelper helper = new OpenedChatDatabaseHelper(context, ichatId);
        OpenedChatDatabaseManager.InstanceHolder.instance = new OpenedChatDatabaseManager(helper);
    }

    public static OpenedChatDatabaseManager getInstance() {
        return OpenedChatDatabaseManager.InstanceHolder.instance;
    }

    public boolean insertOrUpdate(OpenedChat openedChat){
        openDatabaseIfClosed();
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(OpenedChatDatabaseHelper.Columns.CHANNEL_ICHAT_ID, openedChat.getChannelIchatId());
            contentValues.put(OpenedChatDatabaseHelper.Columns.NOT_VIEWED_COUNT, openedChat.getNotViewedCount());
            contentValues.put(OpenedChatDatabaseHelper.Columns.SHOW, openedChat.isShow());
            long rowId = getDatabase().insertWithOnConflict(OpenedChatDatabaseHelper.DatabaseInfo.TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
            return rowId != -1;
        }finally {
            releaseDatabaseIfUnused();
        }
    }

    public List<OpenedChat> findAllShow(){
        openDatabaseIfClosed();
        try(Cursor cursor = getDatabase().query(OpenedChatDatabaseHelper.DatabaseInfo.TABLE_NAME, null,
                OpenedChatDatabaseHelper.Columns.SHOW + "=?",
                new String[]{"1"}, null, null, null)){
            List<OpenedChat> result = new ArrayList<>();
            while (cursor.moveToNext()){
                String channelIchatId = DatabaseUtil.getString(cursor, OpenedChatDatabaseHelper.Columns.CHANNEL_ICHAT_ID);
                Integer notViewedCount = DatabaseUtil.getInteger(cursor, OpenedChatDatabaseHelper.Columns.NOT_VIEWED_COUNT);
                Boolean show = DatabaseUtil.getBoolean(cursor, OpenedChatDatabaseHelper.Columns.SHOW);
                result.add(new OpenedChat(channelIchatId, notViewedCount == null ? -1 : notViewedCount, Boolean.TRUE.equals(show)));
            }
            return result;
        }finally {
            releaseDatabaseIfUnused();
        }
    }
}
