package com.longx.intelligent.android.ichat2.da.database.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by LONG on 2024/5/13 at 2:15 AM.
 */
public class ChatMessageDatabaseHelper extends BaseDatabaseHelper{
    private final String channelIchatId;
    public static class DatabaseInfo{
        public static final String DATABASE_NAME = "chat_messages.db";
        public static final int FIRST_VERSION = 1;
        public static final int DATABASE_VERSION = 1;
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
        public static final String SHOW_TIME = "show_time";
        public static final String VIEWED = "viewed";
        public static final String IMAGE_FILE_PATH = "image_file_path";
        public static final String IMAGE_EXTENSION = "image_extension";
        public static final String IMAGE_WIDTH = "image_width";
        public static final String IMAGE_HEIGHT = "image_height";
    }

    public ChatMessageDatabaseHelper(Context context, String channelIchatId, String ichatId) {
        super(context, DatabaseInfo.DATABASE_NAME, null, DatabaseInfo.DATABASE_VERSION, ichatId);
        this.channelIchatId = channelIchatId;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createSql = "CREATE TABLE IF NOT EXISTS " + channelIchatId + "("
                + TableChannelChatMessagesColumns.TYPE + " INTEGER,"
                + TableChannelChatMessagesColumns.UUID + " VARCHAR,"
                + TableChannelChatMessagesColumns.FROM + " VARCHAR,"
                + TableChannelChatMessagesColumns.TO + " VARCHAR,"
                + TableChannelChatMessagesColumns.TEXT + " VARCHAR,"
                + TableChannelChatMessagesColumns.TIME + " DATETIME,"
                + TableChannelChatMessagesColumns.SHOW_TIME + " BOOLEAN,"
                + TableChannelChatMessagesColumns.VIEWED + " BOOLEAN,"
                + TableChannelChatMessagesColumns.IMAGE_FILE_PATH + " VARCHAR,"
                + TableChannelChatMessagesColumns.IMAGE_EXTENSION + " VARCHAR,"
                + TableChannelChatMessagesColumns.IMAGE_WIDTH + " INTEGER,"
                + TableChannelChatMessagesColumns.IMAGE_HEIGHT + " INTEGER,"
                + " CONSTRAINT con_unique1 UNIQUE("
                + TableChannelChatMessagesColumns.TYPE + ","
                + TableChannelChatMessagesColumns.UUID + ","
                + TableChannelChatMessagesColumns.FROM + ","
                + TableChannelChatMessagesColumns.TO
                +")"
                + ");";
        db.execSQL(createSql);
        String createIndexSql = "CREATE INDEX IF NOT EXISTS " + channelIchatId +
                "_index ON " + channelIchatId + "(" + TableChannelChatMessagesColumns.UUID + "," + TableChannelChatMessagesColumns.TIME + ");";
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

    public String getTableName() {
        return channelIchatId;
    }
}
