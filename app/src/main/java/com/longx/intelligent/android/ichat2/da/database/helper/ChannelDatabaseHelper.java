package com.longx.intelligent.android.ichat2.da.database.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by LONG on 2024/5/9 at 7:43 PM.
 */
public class ChannelDatabaseHelper extends BaseDatabaseHelper{
    public static class DatabaseInfo{
        public static final String DATABASE_NAME = "channel.db";
        public static final int FIRST_VERSION = 1;
        public static final int DATABASE_VERSION = 1;
        public static final String TABLE_NAME_CHANNEL_ASSOCIATIONS = "channel_associations";
        public static final String TABLE_NAME_CHANNELS = "channels";
        public static final String TABLE_NAME_TAGS = "tags";
    }

    public static class TableChannelAssociationsColumns {
        public static final String ASSOCIATION_ID = "association_id";
        public static final String ICHAT_ID = "ichat_id";
        public static final String CHANNEL_ICHAT_ID = "channel_ichat_id";
        public static final String IS_REQUESTER = "is_requester";
        public static final String REQUEST_TIME = "request_time";
        public static final String ACCEPT_TIME = "accept_time";
        public static final String IS_ACTIVE = "is_active";
    }

    public static class TableChannelsColumns {
        public static final String ICHAT_ID = "ichat_id";
        public static final String ICHAT_ID_USER = "ichat_id_user";
        public static final String EMAIL = "email";
        public static final String USERNAME = "username";
        public static final String NOTE = "note";
        public static final String AVATAR_HASH = "avatar_hash";
        public static final String AVATAR_ICHAT_ID = "avatar_ichat_id";
        public static final String AVATAR_EXTENSION = "avatar_extension";
        public static final String AVATAR_TIME = "avatar_time";
        public static final String SEX = "sex";
        public static final String FIRST_REGION_ADCODE = "first_region_adcode";
        public static final String FIRST_REGION_NAME = "first_region_name";
        public static final String SECOND_REGION_ADCODE = "second_region_adcode";
        public static final String SECOND_REGION_NAME = "second_region_name";
        public static final String THIRD_REGION_ADCODE = "third_region_adcode";
        public static final String THIRD_REGION_NAME = "third_region_name";
        public static final String ASSOCIATED = "associated";
    }

    public static class TableTagColumns {
        public static final String ID = "id";
        public static final String ICHAT_ID = "ichat_id";
        public static final String NAME = "name";
        public static final String ORDER = "`order`";
        public static final String RAW_ORDER = "order";
    }

    public ChannelDatabaseHelper(Context context, String ichatId) {
        super(context, DatabaseInfo.DATABASE_NAME, null, DatabaseInfo.DATABASE_VERSION, ichatId);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String create_sql_1 = "CREATE TABLE IF NOT EXISTS " + DatabaseInfo.TABLE_NAME_CHANNEL_ASSOCIATIONS + "("
                + TableChannelAssociationsColumns.ASSOCIATION_ID + " VARCHAR,"
                + TableChannelAssociationsColumns.ICHAT_ID + " VARCHAR,"
                + TableChannelAssociationsColumns.CHANNEL_ICHAT_ID + " VARCHAR,"
                + TableChannelAssociationsColumns.IS_REQUESTER + " BOOLEAN,"
                + TableChannelAssociationsColumns.REQUEST_TIME + " DATETIME,"
                + TableChannelAssociationsColumns.ACCEPT_TIME + " DATETIME,"
                + TableChannelAssociationsColumns.IS_ACTIVE + " BOOLEAN,"
                + " CONSTRAINT con_unique1 UNIQUE("
                + TableChannelAssociationsColumns.ASSOCIATION_ID
                +")"
                + ");";
        db.execSQL(create_sql_1);
        String create_sql_2 = "CREATE TABLE IF NOT EXISTS " + DatabaseInfo.TABLE_NAME_CHANNELS + "("
                + TableChannelsColumns.ICHAT_ID + " VARCHAR,"
                + TableChannelsColumns.ICHAT_ID_USER + " VARCHAR,"
                + TableChannelsColumns.EMAIL + " VARCHAR,"
                + TableChannelsColumns.USERNAME + " VARCHAR,"
                + TableChannelsColumns.NOTE + " VARCHAR,"
                + TableChannelsColumns.AVATAR_HASH + " VARCHAR,"
                + TableChannelsColumns.AVATAR_ICHAT_ID + " VARCHAR,"
                + TableChannelsColumns.AVATAR_EXTENSION + " VARCHAR,"
                + TableChannelsColumns.AVATAR_TIME + " DATETIME,"
                + TableChannelsColumns.SEX + " INTEGER,"
                + TableChannelsColumns.FIRST_REGION_ADCODE + " INTEGER,"
                + TableChannelsColumns.FIRST_REGION_NAME + " VARCHAR,"
                + TableChannelsColumns.SECOND_REGION_ADCODE + " INTEGER,"
                + TableChannelsColumns.SECOND_REGION_NAME + " VARCHAR,"
                + TableChannelsColumns.THIRD_REGION_ADCODE + " INTEGER,"
                + TableChannelsColumns.THIRD_REGION_NAME + " VARCHAR,"
                + TableChannelsColumns.ASSOCIATED + " BOOLEAN,"
                + " CONSTRAINT con_unique1 UNIQUE("
                + TableChannelAssociationsColumns.ICHAT_ID
                +")"
                + ");";
        db.execSQL(create_sql_2);
        String create_sql_3 = "CREATE TABLE IF NOT EXISTS " + DatabaseInfo.TABLE_NAME_TAGS + "("
                + TableTagColumns.ID + " VARCHAR,"
                + TableTagColumns.ICHAT_ID + " VARCHAR,"
                + TableTagColumns.NAME + " VARCHAR,"
                + TableTagColumns.ORDER + " INTEGER,"
                + " CONSTRAINT con_unique1 UNIQUE("
                + TableTagColumns.ID
                +")"
                + ");";
        db.execSQL(create_sql_3);
        onUpgrade(db, DatabaseInfo.FIRST_VERSION, DatabaseInfo.DATABASE_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
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
