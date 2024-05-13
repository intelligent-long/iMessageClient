package com.longx.intelligent.android.ichat2.da.database.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by LONG on 2024/5/13 at 2:15 AM.
 */
public class ChatDatabaseHelper extends BaseDatabaseHelper{
    public static class DatabaseInfo{
        public static final String DATABASE_NAME = "chat.db";
        public static final int FIRST_VERSION = 1;
        public static final int DATABASE_VERSION = 1;
        public static final String TABLE_NAME_CHAT_MESSAGES = "chat_messages";
    }

    public static class TableChannelChatMessagesColumns {
        public static final String TYPE = "type";
        public static final String UUID = "uuid";
        public static final String FROM = "`from`";
        public static final String REAL_FROM = "from";
        public static final String TO = "`to`";
        public static final String REAL_TO = "to";
        public static final String TEXT = "text";
        public static final String TIME = "time";
    }

    public ChatDatabaseHelper(Context context, String ichatId) {
        super(context, DatabaseInfo.DATABASE_NAME, null, DatabaseInfo.DATABASE_VERSION, ichatId);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createSql = "CREATE TABLE IF NOT EXISTS " + DatabaseInfo.TABLE_NAME_CHAT_MESSAGES + "("
                + TableChannelChatMessagesColumns.TYPE + " INTEGER,"
                + TableChannelChatMessagesColumns.UUID + " VARCHAR,"
                + TableChannelChatMessagesColumns.FROM + " VARCHAR,"
                + TableChannelChatMessagesColumns.TO + " VARCHAR,"
                + TableChannelChatMessagesColumns.TEXT + " VARCHAR,"
                + TableChannelChatMessagesColumns.TIME + " DATETIME,"
                + " CONSTRAINT con_unique1 UNIQUE("
                + TableChannelChatMessagesColumns.TYPE + ","
                + TableChannelChatMessagesColumns.UUID + ","
                + TableChannelChatMessagesColumns.FROM + ","
                + TableChannelChatMessagesColumns.TO
                +")"
                + ");";
        db.execSQL(createSql);
        String createIndexSql = "CREATE INDEX IF NOT EXISTS " + DatabaseInfo.TABLE_NAME_CHAT_MESSAGES +
                "_index ON " + DatabaseInfo.TABLE_NAME_CHAT_MESSAGES + "(" + TableChannelChatMessagesColumns.UUID + "," + TableChannelChatMessagesColumns.TIME + ");";
        db.execSQL(createIndexSql);
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
