package com.longx.intelligent.android.imessage.da.database.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by LONG on 2025/4/20 at 上午8:00.
 */
public class GroupChannelDatabaseHelper extends BaseDatabaseHelper{
    public static class DatabaseInfo{
        public static final String DATABASE_NAME = "group_channel.db";
        public static final int FIRST_VERSION = 1;
        public static final int DATABASE_VERSION = 1;
        public static final String TABLE_NAME_GROUP_CHANNEL_ASSOCIATIONS = "group_channel_associations";
        public static final String TABLE_NAME_GROUP_CHANNELS = "group_channels";
        public static final String TABLE_NAME_GROUP_AVATARS = "group_avatars";
    }

    public static class TableGroupChannelAssociationsColumns {
        public static final String ASSOCIATION_ID = "association_id";
        public static final String GROUP_CHANNEL_ID = "group_channel_id";
        public static final String CHANNEL_IMESSAGE_ID = "channel_imessage_id";
        public static final String INVITE_CHANNEL_IMESSAGE_ID = "invite_channel_imessage_id";
        public static final String INVITE_MESSAGE = "invite_message";
        public static final String INVITE_TIME = "invite_time";
        public static final String ACCEPT_TIME = "accept_time";
    }

    public static class TableGroupChannelsColumns {
        public static final String GROUP_CHANNEL_ID = "group_channel_id";
        public static final String GROUP_CHANNEL_ID_USER = "group_channel_id_user";
        public static final String OWNER = "owner";
        public static final String NAME = "name";
        public static final String NOTE = "note";
        public static final String CREATE_TIME = "create_time";
        public static final String FIRST_REGION_ADCODE = "first_region_adcode";
        public static final String FIRST_REGION_NAME = "first_region_name";
        public static final String SECOND_REGION_ADCODE = "second_region_adcode";
        public static final String SECOND_REGION_NAME = "second_region_name";
        public static final String THIRD_REGION_ADCODE = "third_region_adcode";
        public static final String THIRD_REGION_NAME = "third_region_name";
        public static final String AVATAR_HASH = "avatar_hash";
    }

    public static class TableAvatarsColumns{
        public static final String AVATAR_HASH = "avatar_hash";
        public static final String GROUP_CHANNEL_ID = "group_channel_id";
        public static final String EXTENSION = "extension";
        public static final String TIME = "time";
    }

    public GroupChannelDatabaseHelper(Context context, String imessageId) {
        super(context, DatabaseInfo.DATABASE_NAME, null, DatabaseInfo.DATABASE_VERSION, imessageId);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String create_sql_1 = "CREATE TABLE IF NOT EXISTS " + DatabaseInfo.TABLE_NAME_GROUP_CHANNEL_ASSOCIATIONS + "("
                + TableGroupChannelAssociationsColumns.ASSOCIATION_ID + " VARCHAR,"
                + TableGroupChannelAssociationsColumns.GROUP_CHANNEL_ID + " VARCHAR,"
                + TableGroupChannelAssociationsColumns.CHANNEL_IMESSAGE_ID + " VARCHAR,"
                + TableGroupChannelAssociationsColumns.INVITE_CHANNEL_IMESSAGE_ID + " VARCHAR,"
                + TableGroupChannelAssociationsColumns.INVITE_MESSAGE + " VARCHAR,"
                + TableGroupChannelAssociationsColumns.INVITE_TIME + " DATETIME,"
                + TableGroupChannelAssociationsColumns.ACCEPT_TIME + " DATETIME,"
                + " CONSTRAINT con_unique1 UNIQUE("
                + TableGroupChannelAssociationsColumns.ASSOCIATION_ID
                +")"
                + ");";
        db.execSQL(create_sql_1);
        String create_sql_2 = "CREATE TABLE IF NOT EXISTS " + DatabaseInfo.TABLE_NAME_GROUP_CHANNELS + "("
                + TableGroupChannelsColumns.GROUP_CHANNEL_ID + " VARCHAR,"
                + TableGroupChannelsColumns.GROUP_CHANNEL_ID_USER + " VARCHAR,"
                + TableGroupChannelsColumns.OWNER + " VARCHAR,"
                + TableGroupChannelsColumns.NAME + " VARCHAR,"
                + TableGroupChannelsColumns.NOTE + " VARCHAR,"
                + TableGroupChannelsColumns.CREATE_TIME + " DATETIME,"
                + TableGroupChannelsColumns.FIRST_REGION_ADCODE + " INTEGER,"
                + TableGroupChannelsColumns.FIRST_REGION_NAME + " VARCHAR,"
                + TableGroupChannelsColumns.SECOND_REGION_ADCODE + " INTEGER,"
                + TableGroupChannelsColumns.SECOND_REGION_NAME + " VARCHAR,"
                + TableGroupChannelsColumns.THIRD_REGION_ADCODE + " INTEGER,"
                + TableGroupChannelsColumns.THIRD_REGION_NAME + " VARCHAR,"
                + TableGroupChannelsColumns.AVATAR_HASH + " VARCHAR,"
                + " CONSTRAINT con_unique1 UNIQUE("
                + TableGroupChannelsColumns.GROUP_CHANNEL_ID
                +")"
                + ");";
        db.execSQL(create_sql_2);
        String create_sql_3 = "CREATE TABLE IF NOT EXISTS " + DatabaseInfo.TABLE_NAME_GROUP_AVATARS + "("
                + TableAvatarsColumns.AVATAR_HASH + " VARCHAR,"
                + TableAvatarsColumns.GROUP_CHANNEL_ID + " VARCHAR,"
                + TableAvatarsColumns.EXTENSION + " VARCHAR,"
                + TableAvatarsColumns.TIME + " DATETIME,"
                + " CONSTRAINT con_unique1 UNIQUE("
                + TableAvatarsColumns.AVATAR_HASH
                +")"
                + ");";
        db.execSQL(create_sql_3);
        onUpgrade(db, DatabaseInfo.FIRST_VERSION, DatabaseInfo.DATABASE_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
