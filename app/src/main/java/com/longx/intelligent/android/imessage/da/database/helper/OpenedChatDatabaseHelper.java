package com.longx.intelligent.android.imessage.da.database.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by LONG on 2024/5/16 at 7:10 PM.
 */
public class OpenedChatDatabaseHelper extends BaseDatabaseHelper{
    public static class DatabaseInfo{
        public static final String DATABASE_NAME = "opened_chat.db";
        public static final int FIRST_VERSION = 1;
        public static final int DATABASE_VERSION = 1;
        public static final String TABLE_NAME = "opened_chats";
    }

    public static class Columns {
        public static final String CHANNEL_ICHAT_ID = "channel_ichat_id";
        public static final String NOT_VIEWED_COUNT = "not_viewed_count";
        public static final String SHOW = "show";
    }

    public OpenedChatDatabaseHelper(Context context, String ichatId) {
        super(context, DatabaseInfo.DATABASE_NAME, null, DatabaseInfo.DATABASE_VERSION, ichatId);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createSql = "CREATE TABLE IF NOT EXISTS " + DatabaseInfo.TABLE_NAME + "("
                + Columns.CHANNEL_ICHAT_ID + " VARCHAR,"
                + Columns.NOT_VIEWED_COUNT + " INTEGER,"
                + Columns.SHOW + " BOOLEAN,"
                + " CONSTRAINT con_unique1 UNIQUE("
                + Columns.CHANNEL_ICHAT_ID
                +")"
                + ");";
        db.execSQL(createSql);
        onUpgrade(db, DatabaseInfo.FIRST_VERSION, DatabaseInfo.DATABASE_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
