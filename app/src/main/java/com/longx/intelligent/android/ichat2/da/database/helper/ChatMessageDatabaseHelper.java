package com.longx.intelligent.android.ichat2.da.database.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

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
        public static final String TIME = "time";
        public static final String VIEWED = "viewed";
        public static final String TEXT = "text";
        public static final String FILE_NAME = "file_name";
        public static final String UNSEND_MESSAGE_UUID = "unsend_message_uuid";
        public static final String IMAGE_FILE_PATH = "image_file_path";
        public static final String IMAGE_WIDTH = "image_width";
        public static final String IMAGE_HEIGHT = "image_height";
        public static final String FILE_FILE_PATH = "file_file_path";
        public static final String VIDEO_FILE_PATH = "video_file_path";
        public static final String VIDEO_WIDTH = "video_width";
        public static final String VIDEO_HEIGHT = "video_height";
        public static final String VIDEO_DURATION = "video_duration";
        public static final String VOICE_FILE_PATH = "voice_file_path";
        public static final String VOICE_LISTENED = "voice_listened";
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
                + TableChannelChatMessagesColumns.VIEWED + " BOOLEAN,"
                + TableChannelChatMessagesColumns.IMAGE_FILE_PATH + " VARCHAR,"
                + TableChannelChatMessagesColumns.FILE_NAME + " VARCHAR,"
                + TableChannelChatMessagesColumns.UNSEND_MESSAGE_UUID + " VARCHAR,"
                + TableChannelChatMessagesColumns.IMAGE_WIDTH + " INTEGER,"
                + TableChannelChatMessagesColumns.IMAGE_HEIGHT + " INTEGER,"
                + TableChannelChatMessagesColumns.FILE_FILE_PATH + " VARCHAR,"
                + TableChannelChatMessagesColumns.VIDEO_FILE_PATH + " VARCHAR,"
                + TableChannelChatMessagesColumns.VIDEO_WIDTH + " INTEGER,"
                + TableChannelChatMessagesColumns.VIDEO_HEIGHT + " INTEGER,"
                + TableChannelChatMessagesColumns.VIDEO_DURATION + " LONG,"
                + TableChannelChatMessagesColumns.VOICE_FILE_PATH + " VARCHAR,"
                + TableChannelChatMessagesColumns.VOICE_LISTENED + " BOOLEAN,"
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
