package com.longx.intelligent.android.ichat2.da.database.manager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.longx.intelligent.android.ichat2.da.database.helper.ChannelAdditionActivityDatabaseHelper;
import com.longx.intelligent.android.ichat2.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.ichat2.data.ChannelAdditionInfo;
import com.longx.intelligent.android.ichat2.util.DatabaseUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by LONG on 2024/5/3 at 2:54 PM.
 */
public class ChannelAdditionActivityDatabaseManager extends BaseDatabaseManager{
    public ChannelAdditionActivityDatabaseManager(ChannelAdditionActivityDatabaseHelper helper) {
        super(helper);
    }

    private static class InstanceHolder{
        private static ChannelAdditionActivityDatabaseManager instance;
    }

    public static void init(Context context){
        String ichatId = SharedPreferencesAccessor.UserInfoPref.getCurrentUserInfo(context).getIchatId();
        ChannelAdditionActivityDatabaseHelper helper = new ChannelAdditionActivityDatabaseHelper(context, ichatId);
        InstanceHolder.instance = new ChannelAdditionActivityDatabaseManager(helper);
    }

    public static ChannelAdditionActivityDatabaseManager getInstance() {
        return InstanceHolder.instance;
    }

    public boolean insertOrIgnore(List<ChannelAdditionInfo> channelAdditionInfos){
        AtomicBoolean result = new AtomicBoolean(true);
        openDatabaseIfClosed();
        try {
            channelAdditionInfos.forEach(channelAdditionInfo -> {
                ContentValues values = new ContentValues();
                values.put(ChannelAdditionActivityDatabaseHelper.Columns.UUID, channelAdditionInfo.getUuid());
                values.put(ChannelAdditionActivityDatabaseHelper.Columns.MESSAGE, channelAdditionInfo.getMessage());
                values.put(ChannelAdditionActivityDatabaseHelper.Columns.REQUESTER_ICHAT_ID, channelAdditionInfo.getRequesterIchatId());
                values.put(ChannelAdditionActivityDatabaseHelper.Columns.RESPONDER_ICHAT_ID, channelAdditionInfo.getResponderIchatId());
                values.put(ChannelAdditionActivityDatabaseHelper.Columns.REQUEST_TIME, channelAdditionInfo.getRequestTime().getTime());
                values.put(ChannelAdditionActivityDatabaseHelper.Columns.RESPOND_TIME, channelAdditionInfo.getRespondTime().getTime());
                values.put(ChannelAdditionActivityDatabaseHelper.Columns.IS_ACCEPTED, channelAdditionInfo.isAccepted());
                values.put(ChannelAdditionActivityDatabaseHelper.Columns.IS_VIEWED, channelAdditionInfo.isViewed());
                long id = getDatabase().insertWithOnConflict(ChannelAdditionActivityDatabaseHelper.DatabaseInfo.TABLE_NAME,
                        null, values, SQLiteDatabase.CONFLICT_IGNORE);
                if(id == -1){
                    result.set(false);
                }
            });
        }finally {
            releaseDatabaseIfUnused();
        }
        return result.get();
    }

    public List<ChannelAdditionInfo> findAll(){
        openDatabaseIfClosed();
        try(Cursor cursor = getDatabase().query(ChannelAdditionActivityDatabaseHelper.DatabaseInfo.TABLE_NAME, null, null, null, null, null, null)) {
            List<ChannelAdditionInfo> channelAdditionInfos = new ArrayList<>();
            while (cursor.moveToNext()){
                String uuid = DatabaseUtil.getString(cursor, ChannelAdditionActivityDatabaseHelper.Columns.UUID);
                String message = DatabaseUtil.getString(cursor, ChannelAdditionActivityDatabaseHelper.Columns.MESSAGE);
                String requesterIchatId = DatabaseUtil.getString(cursor, ChannelAdditionActivityDatabaseHelper.Columns.REQUESTER_ICHAT_ID);
                String responderIchatId = DatabaseUtil.getString(cursor, ChannelAdditionActivityDatabaseHelper.Columns.RESPONDER_ICHAT_ID);
                Date requestTime = new Date(DatabaseUtil.getLong(cursor, ChannelAdditionActivityDatabaseHelper.Columns.REQUEST_TIME));
                Date respondTime = new Date(DatabaseUtil.getLong(cursor, ChannelAdditionActivityDatabaseHelper.Columns.RESPOND_TIME));
                boolean isAccepted = DatabaseUtil.getBoolean(cursor, ChannelAdditionActivityDatabaseHelper.Columns.IS_ACCEPTED);
                boolean isViewed = DatabaseUtil.getBoolean(cursor, ChannelAdditionActivityDatabaseHelper.Columns.IS_VIEWED);
                channelAdditionInfos.add(new ChannelAdditionInfo(uuid, requesterIchatId, responderIchatId, message, requestTime, respondTime, isAccepted, isViewed));
            }
            return channelAdditionInfos;
        }finally {
            releaseDatabaseIfUnused();
        }
    }

}
