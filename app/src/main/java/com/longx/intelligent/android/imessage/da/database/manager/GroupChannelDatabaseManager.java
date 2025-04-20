package com.longx.intelligent.android.imessage.da.database.manager;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.longx.intelligent.android.imessage.da.database.helper.GroupChannelDatabaseHelper;
import com.longx.intelligent.android.imessage.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.imessage.data.GroupChannel;
import com.longx.intelligent.android.imessage.data.GroupChannelAssociation;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by LONG on 2025/4/20 at 上午8:31.
 */
public class GroupChannelDatabaseManager extends BaseDatabaseManager{
    public GroupChannelDatabaseManager(GroupChannelDatabaseHelper helper) {
        super(helper);
    }

    private static class InstanceHolder{
        private static GroupChannelDatabaseManager instance;
    }

    public static void init(Context context){
        String imessageId = SharedPreferencesAccessor.UserProfilePref.getCurrentUserProfile(context).getImessageId();
        GroupChannelDatabaseHelper helper = new GroupChannelDatabaseHelper(context, imessageId);
        InstanceHolder.instance = new GroupChannelDatabaseManager(helper);
    }

    public static GroupChannelDatabaseManager getInstance() {
        return InstanceHolder.instance;
    }

    public void clearGroupChannels(){
        openDatabaseIfClosed();
        try {
            getDatabase().delete(GroupChannelDatabaseHelper.DatabaseInfo.TABLE_NAME_GROUP_CHANNEL_ASSOCIATIONS, "1=1", null);
            getDatabase().delete(GroupChannelDatabaseHelper.DatabaseInfo.TABLE_NAME_GROUP_CHANNELS, "1=1", null);
        }finally {
            releaseDatabaseIfUnused();
        }
    }

    public boolean insertAssociationsOrIgnore(List<GroupChannel> groupChannels) {
        AtomicBoolean result = new AtomicBoolean(true);
        openDatabaseIfClosed();
        try {
            groupChannels.forEach(groupChannel -> {
                ContentValues values = new ContentValues();
                values.put(GroupChannelDatabaseHelper.TableGroupChannelsColumns.GROUP_CHANNEL_ID, groupChannel.getGroupChannelId());
                values.put(GroupChannelDatabaseHelper.TableGroupChannelsColumns.OWNER, groupChannel.getOwner());
                values.put(GroupChannelDatabaseHelper.TableGroupChannelsColumns.NAME, groupChannel.getName());
                values.put(GroupChannelDatabaseHelper.TableGroupChannelsColumns.NOTE, groupChannel.getName());
                values.put(GroupChannelDatabaseHelper.TableGroupChannelsColumns.CREATE_TIME, groupChannel.getCreateTime().getTime());
                long id = getDatabase().insertWithOnConflict(GroupChannelDatabaseHelper.DatabaseInfo.TABLE_NAME_GROUP_CHANNELS, null,
                        values, SQLiteDatabase.CONFLICT_IGNORE);
                if (id == -1) {
                    result.set(false);
                }
                List<GroupChannelAssociation> groupChannelAssociations = groupChannel.getGroupChannelAssociations();
                groupChannelAssociations.forEach(groupChannelAssociation -> {
                    ContentValues values1 = new ContentValues();
                    values1.put(GroupChannelDatabaseHelper.TableGroupChannelAssociationsColumns.GROUP_CHANNEL_ID, groupChannelAssociation.getGroupChannelId());
                    values1.put(GroupChannelDatabaseHelper.TableGroupChannelAssociationsColumns.ASSOCIATION_ID, groupChannelAssociation.getAssociationId());
                    values1.put(GroupChannelDatabaseHelper.TableGroupChannelAssociationsColumns.CHANNEL_IMESSAGE_ID, groupChannelAssociation.getChannelImessageId());
                    values1.put(GroupChannelDatabaseHelper.TableGroupChannelAssociationsColumns.INVITE_CHANNEL_IMESSAGE_ID, groupChannelAssociation.getInviteChannelImessageId());
                    values1.put(GroupChannelDatabaseHelper.TableGroupChannelAssociationsColumns.INVITE_MESSAGE, groupChannelAssociation.getInviteMessage());
                    values1.put(GroupChannelDatabaseHelper.TableGroupChannelAssociationsColumns.INVITE_TIME, groupChannelAssociation.getInviteTime().getTime());
                    values1.put(GroupChannelDatabaseHelper.TableGroupChannelAssociationsColumns.ACCEPT_TIME, groupChannelAssociation.getAcceptTime().getTime());
                    long id1 = getDatabase().insertWithOnConflict(GroupChannelDatabaseHelper.DatabaseInfo.TABLE_NAME_GROUP_CHANNEL_ASSOCIATIONS, null,
                            values1, SQLiteDatabase.CONFLICT_IGNORE);
                    if (id1 == -1) {
                        result.set(false);
                    }
                });
            });
        }finally {
            releaseDatabaseIfUnused();
        }
        return result.get();
    }
}
