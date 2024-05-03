package com.longx.intelligent.android.ichat2.da.database.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by LONG on 2024/5/3 at 2:35 PM.
 */
public class ChannelAdditionActivityDatabaseHelper extends BaseDatabaseHelper{
    public static class DatabaseInfo{
        public static final String DATABASE_NAME = "channel.db";
        public static final int FIRST_VERSION = 1;
        public static final int DATABASE_VERSION = 1;
        public static final String TABLE_NAME = "channel_addition_activity";
    }

    public static class Columns{
        public static final String UUID = "uuid";
        public static final String REQUESTER_ICHAT_ID = "requester_ichat_id";
        public static final String RESPONDER_ICHAT_ID = "responder_ichat_id";
        public static final String MESSAGE = "message";
        public static final String REQUEST_TIME = "request_time";
        public static final String RESPOND_TIME = "respond_time";
        public static final String IS_ACCEPTED = "is_accepted";
        public static final String IS_VIEWED = "is_viewed";
    }
    public ChannelAdditionActivityDatabaseHelper(Context context, String ichatId) {
        super(context, DatabaseInfo.DATABASE_NAME, null, DatabaseInfo.DATABASE_VERSION, ichatId);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createSql = "CREATE TABLE IF NOT EXISTS " + DatabaseInfo.TABLE_NAME + "("
                + Columns.UUID + " VARCHAR PRIMARY KEY,"
                + Columns.REQUESTER_ICHAT_ID + " VARCHAR,"
                + Columns.RESPONDER_ICHAT_ID + " VARCHAR,"
                + Columns.MESSAGE + " VARCHAR,"
                + Columns.REQUEST_TIME + " DATETIME,"
                + Columns.RESPOND_TIME + " DATETIME,"
                + Columns.IS_ACCEPTED + " BOOLEAN,"
                + Columns.IS_VIEWED + " BOOLEAN"
                + ");";
        db.execSQL(createSql);
        onUpgrade(db, DatabaseInfo.FIRST_VERSION, DatabaseInfo.DATABASE_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion >= newVersion){
            return;
        }
        switch(oldVersion) {
            case 1:
            case 2:
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + oldVersion);
        }
    }
}
