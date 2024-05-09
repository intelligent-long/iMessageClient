package com.longx.intelligent.android.ichat2.da.database.manager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.longx.intelligent.android.ichat2.da.database.helper.ChannelsDatabaseHelper;
import com.longx.intelligent.android.ichat2.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.ichat2.data.Avatar;
import com.longx.intelligent.android.ichat2.data.ChannelAssociation;
import com.longx.intelligent.android.ichat2.data.Channel;
import com.longx.intelligent.android.ichat2.data.UserInfo;
import com.longx.intelligent.android.ichat2.util.DatabaseUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by LONG on 2024/5/9 at 7:51 PM.
 */
public class ChannelsDatabaseManager extends BaseDatabaseManager{
    public ChannelsDatabaseManager(ChannelsDatabaseHelper helper) {
        super(helper);
    }


    private static class InstanceHolder{
        private static ChannelsDatabaseManager instance;
    }

    public static void init(Context context){
        String ichatId = SharedPreferencesAccessor.UserInfoPref.getCurrentUserInfo(context).getIchatId();
        ChannelsDatabaseHelper helper = new ChannelsDatabaseHelper(context, ichatId);
        InstanceHolder.instance = new ChannelsDatabaseManager(helper);
    }

    public static ChannelsDatabaseManager getInstance() {
        return InstanceHolder.instance;
    }

    public boolean insertOrIgnore(List<ChannelAssociation> channelAssociations){
        AtomicBoolean result = new AtomicBoolean(true);
        openDatabaseIfClosed();
        try {
            channelAssociations.forEach(channelAssociation -> {
                ContentValues values = new ContentValues();
                values.put(ChannelsDatabaseHelper.TableChannelAssociationsColumns.ASSOCIATION_ID, channelAssociation.getAssociationId());
                values.put(ChannelsDatabaseHelper.TableChannelAssociationsColumns.ICHAT_ID, channelAssociation.getIchatId());
                values.put(ChannelsDatabaseHelper.TableChannelAssociationsColumns.CHANNEL_ICHAT_ID, channelAssociation.getChannelIchatId());
                values.put(ChannelsDatabaseHelper.TableChannelAssociationsColumns.IS_REQUESTER, channelAssociation.isRequester());
                values.put(ChannelsDatabaseHelper.TableChannelAssociationsColumns.REQUEST_TIME, channelAssociation.getRequestTime().getTime());
                values.put(ChannelsDatabaseHelper.TableChannelAssociationsColumns.ACCEPT_TIME, channelAssociation.getAcceptTime().getTime());
                values.put(ChannelsDatabaseHelper.TableChannelAssociationsColumns.IS_ACTIVE, channelAssociation.isActive());
                long id = getDatabase().insertWithOnConflict(ChannelsDatabaseHelper.DatabaseInfo.TABLE_NAME_CHANNEL_ASSOCIATIONS, null,
                        values, SQLiteDatabase.CONFLICT_IGNORE);
                if(id == -1){
                    result.set(false);
                }
                ContentValues values1 = new ContentValues();
                values1.put(ChannelsDatabaseHelper.TableChannelsColumns.ICHAT_ID, channelAssociation.getChannel().getIchatId());
                values1.put(ChannelsDatabaseHelper.TableChannelsColumns.ICHAT_ID_USER, channelAssociation.getChannel().getIchatIdUser());
                values1.put(ChannelsDatabaseHelper.TableChannelsColumns.EMAIL, channelAssociation.getChannel().getEmail());
                values1.put(ChannelsDatabaseHelper.TableChannelsColumns.USERNAME, channelAssociation.getChannel().getUsername());
                values1.put(ChannelsDatabaseHelper.TableChannelsColumns.AVATAR_HASH, channelAssociation.getChannel().getAvatar().getHash());
                values1.put(ChannelsDatabaseHelper.TableChannelsColumns.AVATAR_ICHAT_ID, channelAssociation.getChannel().getAvatar().getIchatId());
                values1.put(ChannelsDatabaseHelper.TableChannelsColumns.AVATAR_EXTENSION, channelAssociation.getChannel().getAvatar().getExtension());
                values1.put(ChannelsDatabaseHelper.TableChannelsColumns.AVATAR_TIME, channelAssociation.getChannel().getAvatar().getTime().getTime());
                values1.put(ChannelsDatabaseHelper.TableChannelsColumns.SEX, channelAssociation.getChannel().getSex());
                UserInfo.Region firstRegion = channelAssociation.getChannel().getFirstRegion();
                values1.put(ChannelsDatabaseHelper.TableChannelsColumns.FIRST_REGION_ADCODE, firstRegion == null ? null : firstRegion.getAdcode());
                values1.put(ChannelsDatabaseHelper.TableChannelsColumns.FIRST_REGION_NAME, firstRegion == null ? null : firstRegion.getName());
                UserInfo.Region secondRegion = channelAssociation.getChannel().getSecondRegion();
                values1.put(ChannelsDatabaseHelper.TableChannelsColumns.SECOND_REGION_ADCODE, secondRegion == null ? null : secondRegion.getAdcode());
                values1.put(ChannelsDatabaseHelper.TableChannelsColumns.SECOND_REGION_NAME, secondRegion == null ? null : secondRegion.getName());
                UserInfo.Region thirdRegion = channelAssociation.getChannel().getThirdRegion();
                values1.put(ChannelsDatabaseHelper.TableChannelsColumns.THIRD_REGION_ADCODE, thirdRegion == null ? null : thirdRegion.getAdcode());
                values1.put(ChannelsDatabaseHelper.TableChannelsColumns.THIRD_REGION_NAME, thirdRegion == null ? null : thirdRegion.getName());
                values1.put(ChannelsDatabaseHelper.TableChannelsColumns.ASSOCIATED, channelAssociation.getChannel().isAssociated());
                long id1 = getDatabase().insertWithOnConflict(ChannelsDatabaseHelper.DatabaseInfo.TABLE_NAME_CHANNELS, null,
                        values1, SQLiteDatabase.CONFLICT_IGNORE);
                if(id1 == -1){
                    result.set(false);
                }
            });
        }finally {
            releaseDatabaseIfUnused();
        }
        return result.get();
    }

    public List<ChannelAssociation> findAll(){
        openDatabaseIfClosed();
        String sql = "SELECT *, " + " ca." + ChannelsDatabaseHelper.TableChannelAssociationsColumns.ICHAT_ID + " AS associationTableIchatId, "
                + " c." + ChannelsDatabaseHelper.TableChannelsColumns.ICHAT_ID + " AS channelTableIchatId "
                + " FROM " + ChannelsDatabaseHelper.DatabaseInfo.TABLE_NAME_CHANNEL_ASSOCIATIONS + " ca "
                + " INNER JOIN " + ChannelsDatabaseHelper.DatabaseInfo.TABLE_NAME_CHANNELS + " c ON " + ChannelsDatabaseHelper.TableChannelAssociationsColumns.CHANNEL_ICHAT_ID
                + " = " + " c." +  ChannelsDatabaseHelper.TableChannelsColumns.ICHAT_ID;
        try(Cursor cursor = getDatabase().rawQuery(sql, null)) {
            List<ChannelAssociation> result = new ArrayList<>();
            while (cursor.moveToNext()){
                String associationId = DatabaseUtil.getString(cursor, ChannelsDatabaseHelper.TableChannelAssociationsColumns.ASSOCIATION_ID);
                String associationTableIchatId = DatabaseUtil.getString(cursor, "associationTableIchatId");
                String channelTableIchatId = DatabaseUtil.getString(cursor, "channelTableIchatId");
                String channelIchatId = DatabaseUtil.getString(cursor, ChannelsDatabaseHelper.TableChannelAssociationsColumns.CHANNEL_ICHAT_ID);
                Boolean isRequester = DatabaseUtil.getBoolean(cursor, ChannelsDatabaseHelper.TableChannelAssociationsColumns.IS_REQUESTER);
                Date requestTime = DatabaseUtil.getTime(cursor, ChannelsDatabaseHelper.TableChannelAssociationsColumns.REQUEST_TIME);
                Date acceptTime = DatabaseUtil.getTime(cursor, ChannelsDatabaseHelper.TableChannelAssociationsColumns.ACCEPT_TIME);
                Boolean isActive = DatabaseUtil.getBoolean(cursor, ChannelsDatabaseHelper.TableChannelAssociationsColumns.IS_ACTIVE);
                String channelTableIchatIdUser = DatabaseUtil.getString(cursor, ChannelsDatabaseHelper.TableChannelsColumns.ICHAT_ID_USER);
                String channelTableEmail = DatabaseUtil.getString(cursor, ChannelsDatabaseHelper.TableChannelsColumns.EMAIL);
                String channelTableUsername = DatabaseUtil.getString(cursor, ChannelsDatabaseHelper.TableChannelsColumns.USERNAME);
                String channelTableAvatarHash = DatabaseUtil.getString(cursor, ChannelsDatabaseHelper.TableChannelsColumns.AVATAR_HASH);
                String channelTableAvatarIchatId = DatabaseUtil.getString(cursor, ChannelsDatabaseHelper.TableChannelsColumns.AVATAR_ICHAT_ID);
                String channelTableAvatarExtension = DatabaseUtil.getString(cursor, ChannelsDatabaseHelper.TableChannelsColumns.AVATAR_EXTENSION);
                Date channelTableAvatarTime = DatabaseUtil.getTime(cursor, ChannelsDatabaseHelper.TableChannelsColumns.AVATAR_TIME);
                Integer channelTableSex = DatabaseUtil.getInteger(cursor, ChannelsDatabaseHelper.TableChannelsColumns.SEX);
                Integer channelTableFirstRegionAdcode = DatabaseUtil.getInteger(cursor, ChannelsDatabaseHelper.TableChannelsColumns.FIRST_REGION_ADCODE);
                String channelTableFirstRegionName = DatabaseUtil.getString(cursor, ChannelsDatabaseHelper.TableChannelsColumns.FIRST_REGION_NAME);
                Integer channelTableSecondRegionAdcode = DatabaseUtil.getInteger(cursor, ChannelsDatabaseHelper.TableChannelsColumns.SECOND_REGION_ADCODE);
                String channelTableSecondRegionName = DatabaseUtil.getString(cursor, ChannelsDatabaseHelper.TableChannelsColumns.SECOND_REGION_NAME);
                Integer channelTableThirdRegionAdcode = DatabaseUtil.getInteger(cursor, ChannelsDatabaseHelper.TableChannelsColumns.THIRD_REGION_ADCODE);
                String channelTableThirdRegionName = DatabaseUtil.getString(cursor, ChannelsDatabaseHelper.TableChannelsColumns.THIRD_REGION_NAME);
                Boolean channelTableAssociated = DatabaseUtil.getBoolean(cursor, ChannelsDatabaseHelper.TableChannelsColumns.ASSOCIATED);
                result.add(new ChannelAssociation(associationId, associationTableIchatId, channelIchatId, Boolean.TRUE.equals(isRequester), requestTime, acceptTime, Boolean.TRUE.equals(isActive),
                        new Channel(channelTableIchatId, channelTableIchatIdUser, channelTableEmail, channelTableUsername, new Avatar(channelTableAvatarHash, channelTableAvatarIchatId, channelTableAvatarExtension, channelTableAvatarTime),
                                channelTableSex,
                                channelTableFirstRegionAdcode == null && channelTableFirstRegionName == null ? null : new UserInfo.Region(channelTableFirstRegionAdcode, channelTableFirstRegionName),
                                channelTableSecondRegionAdcode == null && channelTableSecondRegionName == null ? null : new UserInfo.Region(channelTableSecondRegionAdcode, channelTableSecondRegionName),
                                channelTableThirdRegionAdcode == null && channelTableThirdRegionName == null ? null : new UserInfo.Region(channelTableThirdRegionAdcode, channelTableThirdRegionName),
                                Boolean.TRUE.equals(channelTableAssociated))));
            }
            return result;
        }finally {
            releaseDatabaseIfUnused();
        }
    }

    public void clear(){
        openDatabaseIfClosed();
        try {
            getDatabase().delete(ChannelsDatabaseHelper.DatabaseInfo.TABLE_NAME_CHANNEL_ASSOCIATIONS, "1=1", null);
            getDatabase().delete(ChannelsDatabaseHelper.DatabaseInfo.TABLE_NAME_CHANNELS, "1=1", null);
        }finally {
            releaseDatabaseIfUnused();
        }
    }
}
