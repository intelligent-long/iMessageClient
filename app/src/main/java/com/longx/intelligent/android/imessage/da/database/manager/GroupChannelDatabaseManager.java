package com.longx.intelligent.android.imessage.da.database.manager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.longx.intelligent.android.imessage.da.database.helper.GroupChannelDatabaseHelper;
import com.longx.intelligent.android.imessage.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.imessage.data.Channel;
import com.longx.intelligent.android.imessage.data.GroupAvatar;
import com.longx.intelligent.android.imessage.data.GroupChannel;
import com.longx.intelligent.android.imessage.data.GroupChannelAssociation;
import com.longx.intelligent.android.imessage.data.GroupChannelNotification;
import com.longx.intelligent.android.imessage.data.GroupChannelTag;
import com.longx.intelligent.android.imessage.data.Region;
import com.longx.intelligent.android.imessage.util.DatabaseUtil;
import com.longx.intelligent.android.imessage.util.ErrorLogger;

import java.util.ArrayList;
import java.util.Date;
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
                values.put(GroupChannelDatabaseHelper.TableGroupChannelsColumns.GROUP_CHANNEL_ID_USER, groupChannel.getGroupChannelIdUser());
                values.put(GroupChannelDatabaseHelper.TableGroupChannelsColumns.OWNER, groupChannel.getOwner());
                values.put(GroupChannelDatabaseHelper.TableGroupChannelsColumns.NAME, groupChannel.getName());
                values.put(GroupChannelDatabaseHelper.TableGroupChannelsColumns.NOTE, groupChannel.getNote());
                values.put(GroupChannelDatabaseHelper.TableGroupChannelsColumns.CREATE_TIME, groupChannel.getCreateTime().getTime());
                Region firstRegion = groupChannel.getFirstRegion();
                values.put(GroupChannelDatabaseHelper.TableGroupChannelsColumns.FIRST_REGION_ADCODE, firstRegion == null ? null : firstRegion.getAdcode());
                values.put(GroupChannelDatabaseHelper.TableGroupChannelsColumns.FIRST_REGION_NAME, firstRegion == null ? null : firstRegion.getName());
                Region secondRegion = groupChannel.getSecondRegion();
                values.put(GroupChannelDatabaseHelper.TableGroupChannelsColumns.SECOND_REGION_ADCODE, secondRegion == null ? null : secondRegion.getAdcode());
                values.put(GroupChannelDatabaseHelper.TableGroupChannelsColumns.SECOND_REGION_NAME, secondRegion == null ? null : secondRegion.getName());
                Region thirdRegion = groupChannel.getThirdRegion();
                values.put(GroupChannelDatabaseHelper.TableGroupChannelsColumns.THIRD_REGION_ADCODE, thirdRegion == null ? null : thirdRegion.getAdcode());
                values.put(GroupChannelDatabaseHelper.TableGroupChannelsColumns.THIRD_REGION_NAME, thirdRegion == null ? null : thirdRegion.getName());
                values.put(GroupChannelDatabaseHelper.TableGroupChannelsColumns.AVATAR_HASH, groupChannel.getAvatarHash());
                values.put(GroupChannelDatabaseHelper.TableGroupChannelsColumns.GROUP_JOIN_VERIFICATION, groupChannel.getGroupJoinVerification());
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
                    values1.put(GroupChannelDatabaseHelper.TableGroupChannelAssociationsColumns.CHANNEL_IMESSAGE_ID, groupChannelAssociation.getOwner());
                    values1.put(GroupChannelDatabaseHelper.TableGroupChannelAssociationsColumns.REQUESTER_CHANNEL_IMESSAGE_ID, groupChannelAssociation.getRequester().getImessageId());
                    values1.put(GroupChannelDatabaseHelper.TableGroupChannelAssociationsColumns.REQUESTER_MESSAGE, groupChannelAssociation.getRequestMessage());
                    values1.put(GroupChannelDatabaseHelper.TableGroupChannelAssociationsColumns.REQUESTER__TIME, groupChannelAssociation.getRequestTime().getTime());
                    values1.put(GroupChannelDatabaseHelper.TableGroupChannelAssociationsColumns.ACCEPT_TIME, groupChannelAssociation.getAcceptTime().getTime());
                    values1.put(GroupChannelDatabaseHelper.TableGroupChannelAssociationsColumns.INVITE_UUID, groupChannelAssociation.getInviteUuid());
                    long id1 = getDatabase().insertWithOnConflict(GroupChannelDatabaseHelper.DatabaseInfo.TABLE_NAME_GROUP_CHANNEL_ASSOCIATIONS, null,
                            values1, SQLiteDatabase.CONFLICT_IGNORE);
                    boolean success = ChannelDatabaseManager.getInstance().insertChannelOrIgnore(groupChannelAssociation.getRequester());
                    if (id1 == -1 || !success) {
                        result.set(false);
                    }
                });
                GroupAvatar groupAvatar = groupChannel.getGroupAvatar();
                ContentValues contentValues = new ContentValues();
                contentValues.put(GroupChannelDatabaseHelper.TableAvatarsColumns.AVATAR_HASH, groupAvatar.getHash());
                contentValues.put(GroupChannelDatabaseHelper.TableAvatarsColumns.GROUP_CHANNEL_ID, groupAvatar.getGroupChannelId());
                contentValues.put(GroupChannelDatabaseHelper.TableAvatarsColumns.EXTENSION, groupAvatar.getExtension());
                contentValues.put(GroupChannelDatabaseHelper.TableAvatarsColumns.TIME, (groupAvatar.getTime() == null) ? (null) : groupAvatar.getTime().getTime());
                long id1 = getDatabase().insertWithOnConflict(GroupChannelDatabaseHelper.DatabaseInfo.TABLE_NAME_GROUP_AVATARS, null,
                        contentValues, SQLiteDatabase.CONFLICT_IGNORE);
                if (id1 == -1) {
                    result.set(false);
                }
            });
        }finally {
            releaseDatabaseIfUnused();
        }
        return result.get();
    }

    public boolean insertOrUpdate(GroupChannel groupChannel){
        AtomicBoolean result = new AtomicBoolean(true);
        openDatabaseIfClosed();
        try {
            ContentValues values = new ContentValues();
            values.put(GroupChannelDatabaseHelper.TableGroupChannelsColumns.GROUP_CHANNEL_ID, groupChannel.getGroupChannelId());
            values.put(GroupChannelDatabaseHelper.TableGroupChannelsColumns.GROUP_CHANNEL_ID_USER, groupChannel.getGroupChannelIdUser());
            values.put(GroupChannelDatabaseHelper.TableGroupChannelsColumns.OWNER, groupChannel.getOwner());
            values.put(GroupChannelDatabaseHelper.TableGroupChannelsColumns.NAME, groupChannel.getName());
            values.put(GroupChannelDatabaseHelper.TableGroupChannelsColumns.NOTE, groupChannel.getNote());
            values.put(GroupChannelDatabaseHelper.TableGroupChannelsColumns.CREATE_TIME, groupChannel.getCreateTime().getTime());
            Region firstRegion = groupChannel.getFirstRegion();
            values.put(GroupChannelDatabaseHelper.TableGroupChannelsColumns.FIRST_REGION_ADCODE, firstRegion == null ? null : firstRegion.getAdcode());
            values.put(GroupChannelDatabaseHelper.TableGroupChannelsColumns.FIRST_REGION_NAME, firstRegion == null ? null : firstRegion.getName());
            Region secondRegion = groupChannel.getSecondRegion();
            values.put(GroupChannelDatabaseHelper.TableGroupChannelsColumns.SECOND_REGION_ADCODE, secondRegion == null ? null : secondRegion.getAdcode());
            values.put(GroupChannelDatabaseHelper.TableGroupChannelsColumns.SECOND_REGION_NAME, secondRegion == null ? null : secondRegion.getName());
            Region thirdRegion = groupChannel.getThirdRegion();
            values.put(GroupChannelDatabaseHelper.TableGroupChannelsColumns.THIRD_REGION_ADCODE, thirdRegion == null ? null : thirdRegion.getAdcode());
            values.put(GroupChannelDatabaseHelper.TableGroupChannelsColumns.THIRD_REGION_NAME, thirdRegion == null ? null : thirdRegion.getName());
            values.put(GroupChannelDatabaseHelper.TableGroupChannelsColumns.AVATAR_HASH, groupChannel.getAvatarHash());
            values.put(GroupChannelDatabaseHelper.TableGroupChannelsColumns.GROUP_JOIN_VERIFICATION, groupChannel.getGroupJoinVerification());
            long id = getDatabase().insertWithOnConflict(GroupChannelDatabaseHelper.DatabaseInfo.TABLE_NAME_GROUP_CHANNELS, null,
                    values, SQLiteDatabase.CONFLICT_REPLACE);
            if (id == -1) {
                result.set(false);
            }
            List<GroupChannelAssociation> groupChannelAssociations = groupChannel.getGroupChannelAssociations();
            groupChannelAssociations.forEach(groupChannelAssociation -> {
                ContentValues values1 = new ContentValues();
                values1.put(GroupChannelDatabaseHelper.TableGroupChannelAssociationsColumns.GROUP_CHANNEL_ID, groupChannelAssociation.getGroupChannelId());
                values1.put(GroupChannelDatabaseHelper.TableGroupChannelAssociationsColumns.ASSOCIATION_ID, groupChannelAssociation.getAssociationId());
                values1.put(GroupChannelDatabaseHelper.TableGroupChannelAssociationsColumns.CHANNEL_IMESSAGE_ID, groupChannelAssociation.getOwner());
                values1.put(GroupChannelDatabaseHelper.TableGroupChannelAssociationsColumns.REQUESTER_MESSAGE, groupChannelAssociation.getRequestMessage());
                values1.put(GroupChannelDatabaseHelper.TableGroupChannelAssociationsColumns.REQUESTER__TIME, groupChannelAssociation.getRequestTime().getTime());
                values1.put(GroupChannelDatabaseHelper.TableGroupChannelAssociationsColumns.ACCEPT_TIME, groupChannelAssociation.getAcceptTime().getTime());
                values1.put(GroupChannelDatabaseHelper.TableGroupChannelAssociationsColumns.INVITE_UUID, groupChannelAssociation.getInviteUuid());
                values1.put(GroupChannelDatabaseHelper.TableGroupChannelAssociationsColumns.REQUESTER_CHANNEL_IMESSAGE_ID, groupChannelAssociation.getRequester().getImessageId());
                long id1 = getDatabase().insertWithOnConflict(GroupChannelDatabaseHelper.DatabaseInfo.TABLE_NAME_GROUP_CHANNEL_ASSOCIATIONS, null,
                        values1, SQLiteDatabase.CONFLICT_REPLACE);
                boolean success = ChannelDatabaseManager.getInstance().insertChannelOrIgnore(groupChannelAssociation.getRequester());
                if (id1 == -1 || !success) {
                    result.set(false);
                }
            });
            GroupAvatar groupAvatar = groupChannel.getGroupAvatar();
            ContentValues contentValues = new ContentValues();
            contentValues.put(GroupChannelDatabaseHelper.TableAvatarsColumns.AVATAR_HASH, groupAvatar.getHash());
            contentValues.put(GroupChannelDatabaseHelper.TableAvatarsColumns.GROUP_CHANNEL_ID, groupAvatar.getGroupChannelId());
            contentValues.put(GroupChannelDatabaseHelper.TableAvatarsColumns.EXTENSION, groupAvatar.getExtension());
            contentValues.put(GroupChannelDatabaseHelper.TableAvatarsColumns.TIME, (groupAvatar.getTime() == null) ? (null) : groupAvatar.getTime().getTime());
            long id1 = getDatabase().insertWithOnConflict(GroupChannelDatabaseHelper.DatabaseInfo.TABLE_NAME_GROUP_AVATARS, null,
                    contentValues, SQLiteDatabase.CONFLICT_IGNORE);
            if (id1 == -1) {
                result.set(false);
            }
        }finally {
            releaseDatabaseIfUnused();
        }
        return result.get();
    }

    public List<GroupChannel> findAllAssociations(){
        openDatabaseIfClosed();
        String sql = "SELECT *, ca." +  GroupChannelDatabaseHelper.TableGroupChannelsColumns.GROUP_CHANNEL_ID + ", ca." +  GroupChannelDatabaseHelper.TableGroupChannelsColumns.GROUP_CHANNEL_ID_USER
                + " FROM " + GroupChannelDatabaseHelper.DatabaseInfo.TABLE_NAME_GROUP_CHANNELS + " ca "
                + " LEFT JOIN " + GroupChannelDatabaseHelper.DatabaseInfo.TABLE_NAME_GROUP_CHANNEL_ASSOCIATIONS + " gca ON "
                + "ca." + GroupChannelDatabaseHelper.TableGroupChannelsColumns.GROUP_CHANNEL_ID + " = " + "gca." + GroupChannelDatabaseHelper.TableGroupChannelAssociationsColumns.GROUP_CHANNEL_ID
                + " LEFT JOIN " + GroupChannelDatabaseHelper.DatabaseInfo.TABLE_NAME_GROUP_AVATARS + " ga ON "
                + "ca." + GroupChannelDatabaseHelper.TableGroupChannelsColumns.AVATAR_HASH + " = " + "ga." + GroupChannelDatabaseHelper.TableAvatarsColumns.AVATAR_HASH;
        try(Cursor cursor = getDatabase().rawQuery(sql, null)) {
            List<GroupChannel> result = new ArrayList<>();
            while (cursor.moveToNext()){
                String groupChannelId = DatabaseUtil.getString(cursor, GroupChannelDatabaseHelper.TableGroupChannelsColumns.GROUP_CHANNEL_ID);
                String groupChannelIdUser = DatabaseUtil.getString(cursor, GroupChannelDatabaseHelper.TableGroupChannelsColumns.GROUP_CHANNEL_ID_USER);
                String owner = DatabaseUtil.getString(cursor, GroupChannelDatabaseHelper.TableGroupChannelsColumns.OWNER);
                String name = DatabaseUtil.getString(cursor, GroupChannelDatabaseHelper.TableGroupChannelsColumns.NAME);
                String note = DatabaseUtil.getString(cursor, GroupChannelDatabaseHelper.TableGroupChannelsColumns.NOTE);
                Long createTime = DatabaseUtil.getLong(cursor, GroupChannelDatabaseHelper.TableGroupChannelsColumns.CREATE_TIME);
                Integer firstRegionAdcode = DatabaseUtil.getInteger(cursor, GroupChannelDatabaseHelper.TableGroupChannelsColumns.FIRST_REGION_ADCODE);
                String firstRegionName = DatabaseUtil.getString(cursor, GroupChannelDatabaseHelper.TableGroupChannelsColumns.FIRST_REGION_NAME);
                Integer secondRegionAdcode = DatabaseUtil.getInteger(cursor, GroupChannelDatabaseHelper.TableGroupChannelsColumns.SECOND_REGION_ADCODE);
                String secondRegionName = DatabaseUtil.getString(cursor, GroupChannelDatabaseHelper.TableGroupChannelsColumns.SECOND_REGION_NAME);
                Integer thirdRegionAdcode = DatabaseUtil.getInteger(cursor, GroupChannelDatabaseHelper.TableGroupChannelsColumns.THIRD_REGION_ADCODE);
                String thirdRegionName = DatabaseUtil.getString(cursor, GroupChannelDatabaseHelper.TableGroupChannelsColumns.THIRD_REGION_NAME);
                String avatarHash = DatabaseUtil.getString(cursor, GroupChannelDatabaseHelper.TableGroupChannelsColumns.AVATAR_HASH);
                Boolean joinVerification = DatabaseUtil.getBoolean(cursor, GroupChannelDatabaseHelper.TableGroupChannelsColumns.GROUP_JOIN_VERIFICATION);
                String avatarHash1 = DatabaseUtil.getString(cursor, GroupChannelDatabaseHelper.TableAvatarsColumns.AVATAR_HASH);
                String groupChannelId1 = DatabaseUtil.getString(cursor, GroupChannelDatabaseHelper.TableAvatarsColumns.GROUP_CHANNEL_ID);
                String avatarExtension = DatabaseUtil.getString(cursor, GroupChannelDatabaseHelper.TableAvatarsColumns.EXTENSION);
                Long avatarTime = DatabaseUtil.getLong(cursor, GroupChannelDatabaseHelper.TableAvatarsColumns.TIME);
                GroupChannel groupChannel = new GroupChannel(new GroupAvatar(avatarHash1, groupChannelId1, avatarExtension, avatarTime == null ? null : new Date(avatarTime)), groupChannelId, groupChannelIdUser, owner, name, note, createTime == null ? null : new Date(createTime), new Region(firstRegionAdcode, firstRegionName), new Region(secondRegionAdcode, secondRegionName), new Region(thirdRegionAdcode, thirdRegionName), avatarHash, joinVerification);
                String associationId = DatabaseUtil.getString(cursor, GroupChannelDatabaseHelper.TableGroupChannelAssociationsColumns.ASSOCIATION_ID);
                String channelImessageId = DatabaseUtil.getString(cursor, GroupChannelDatabaseHelper.TableGroupChannelAssociationsColumns.CHANNEL_IMESSAGE_ID);
                String requester = DatabaseUtil.getString(cursor, GroupChannelDatabaseHelper.TableGroupChannelAssociationsColumns.REQUESTER_CHANNEL_IMESSAGE_ID);
                String requesterMessage = DatabaseUtil.getString(cursor, GroupChannelDatabaseHelper.TableGroupChannelAssociationsColumns.REQUESTER_MESSAGE);
                Long requesterTime = DatabaseUtil.getLong(cursor, GroupChannelDatabaseHelper.TableGroupChannelAssociationsColumns.REQUESTER__TIME);
                Long acceptTime = DatabaseUtil.getLong(cursor, GroupChannelDatabaseHelper.TableGroupChannelAssociationsColumns.ACCEPT_TIME);
                String inviteUuid = DatabaseUtil.getString(cursor, GroupChannelDatabaseHelper.TableGroupChannelAssociationsColumns.INVITE_UUID);
                Channel requesterChannel = ChannelDatabaseManager.getInstance().findOneChannel(requester);
                GroupChannelAssociation groupChannelAssociation = new GroupChannelAssociation(associationId, groupChannelId, channelImessageId, requesterChannel, requesterMessage, requesterTime == null ? null : new Date(requesterTime), acceptTime == null ? null : new Date(acceptTime), inviteUuid);
                int index = result.indexOf(groupChannel);
                if(index >= 0){
                    result.get(index).addGroupChannelAssociation(groupChannelAssociation);
                }else {
                    groupChannel.addGroupChannelAssociation(groupChannelAssociation);
                    result.add(groupChannel);
                }
            }
            return result;
        }finally {
            releaseDatabaseIfUnused();
        }
    }

    public GroupChannel findOneAssociation(String groupChannelId){
        openDatabaseIfClosed();
        String sql = "SELECT *, ca." +  GroupChannelDatabaseHelper.TableGroupChannelsColumns.GROUP_CHANNEL_ID + ", ca." +  GroupChannelDatabaseHelper.TableGroupChannelsColumns.GROUP_CHANNEL_ID_USER
                + " FROM " + GroupChannelDatabaseHelper.DatabaseInfo.TABLE_NAME_GROUP_CHANNELS + " ca "
                + " LEFT JOIN " + GroupChannelDatabaseHelper.DatabaseInfo.TABLE_NAME_GROUP_CHANNEL_ASSOCIATIONS + " gca ON "
                + "ca." + GroupChannelDatabaseHelper.TableGroupChannelsColumns.GROUP_CHANNEL_ID + " = " + "gca." + GroupChannelDatabaseHelper.TableGroupChannelAssociationsColumns.GROUP_CHANNEL_ID
                + " LEFT JOIN " + GroupChannelDatabaseHelper.DatabaseInfo.TABLE_NAME_GROUP_AVATARS + " ga ON "
                + "ca." + GroupChannelDatabaseHelper.TableGroupChannelsColumns.AVATAR_HASH + " = " + "ga." + GroupChannelDatabaseHelper.TableAvatarsColumns.AVATAR_HASH
                + " WHERE ca." + GroupChannelDatabaseHelper.TableGroupChannelsColumns.GROUP_CHANNEL_ID + " = ?";
        try(Cursor cursor = getDatabase().rawQuery(sql, new String[]{groupChannelId})) {
            List<GroupChannel> result = new ArrayList<>();
            while (cursor.moveToNext()){
                String groupChannelIdFind = DatabaseUtil.getString(cursor, GroupChannelDatabaseHelper.TableGroupChannelsColumns.GROUP_CHANNEL_ID);
                String groupChannelIdUser = DatabaseUtil.getString(cursor, GroupChannelDatabaseHelper.TableGroupChannelsColumns.GROUP_CHANNEL_ID_USER);
                String owner = DatabaseUtil.getString(cursor, GroupChannelDatabaseHelper.TableGroupChannelsColumns.OWNER);
                String name = DatabaseUtil.getString(cursor, GroupChannelDatabaseHelper.TableGroupChannelsColumns.NAME);
                String note = DatabaseUtil.getString(cursor, GroupChannelDatabaseHelper.TableGroupChannelsColumns.NOTE);
                Long createTime = DatabaseUtil.getLong(cursor, GroupChannelDatabaseHelper.TableGroupChannelsColumns.CREATE_TIME);
                Integer firstRegionAdcode = DatabaseUtil.getInteger(cursor, GroupChannelDatabaseHelper.TableGroupChannelsColumns.FIRST_REGION_ADCODE);
                String firstRegionName = DatabaseUtil.getString(cursor, GroupChannelDatabaseHelper.TableGroupChannelsColumns.FIRST_REGION_NAME);
                Integer secondRegionAdcode = DatabaseUtil.getInteger(cursor, GroupChannelDatabaseHelper.TableGroupChannelsColumns.SECOND_REGION_ADCODE);
                String secondRegionName = DatabaseUtil.getString(cursor, GroupChannelDatabaseHelper.TableGroupChannelsColumns.SECOND_REGION_NAME);
                Integer thirdRegionAdcode = DatabaseUtil.getInteger(cursor, GroupChannelDatabaseHelper.TableGroupChannelsColumns.THIRD_REGION_ADCODE);
                String thirdRegionName = DatabaseUtil.getString(cursor, GroupChannelDatabaseHelper.TableGroupChannelsColumns.THIRD_REGION_NAME);
                String avatarHash = DatabaseUtil.getString(cursor, GroupChannelDatabaseHelper.TableGroupChannelsColumns.AVATAR_HASH);
                Boolean joinVerification = DatabaseUtil.getBoolean(cursor, GroupChannelDatabaseHelper.TableGroupChannelsColumns.GROUP_JOIN_VERIFICATION);
                String avatarHash1 = DatabaseUtil.getString(cursor, GroupChannelDatabaseHelper.TableAvatarsColumns.AVATAR_HASH);
                String groupChannelId1 = DatabaseUtil.getString(cursor, GroupChannelDatabaseHelper.TableAvatarsColumns.GROUP_CHANNEL_ID);
                String avatarExtension = DatabaseUtil.getString(cursor, GroupChannelDatabaseHelper.TableAvatarsColumns.EXTENSION);
                Long avatarTime = DatabaseUtil.getLong(cursor, GroupChannelDatabaseHelper.TableAvatarsColumns.TIME);
                GroupChannel groupChannel = new GroupChannel(new GroupAvatar(avatarHash1, groupChannelId1, avatarExtension, avatarTime == null ? null : new Date(avatarTime)), groupChannelId, groupChannelIdUser, owner, name, note, createTime == null ? null : new Date(createTime), new Region(firstRegionAdcode, firstRegionName), new Region(secondRegionAdcode, secondRegionName), new Region(thirdRegionAdcode, thirdRegionName), avatarHash, joinVerification);
                String associationId = DatabaseUtil.getString(cursor, GroupChannelDatabaseHelper.TableGroupChannelAssociationsColumns.ASSOCIATION_ID);
                String channelImessageId = DatabaseUtil.getString(cursor, GroupChannelDatabaseHelper.TableGroupChannelAssociationsColumns.CHANNEL_IMESSAGE_ID);
                String requester = DatabaseUtil.getString(cursor, GroupChannelDatabaseHelper.TableGroupChannelAssociationsColumns.REQUESTER_CHANNEL_IMESSAGE_ID);
                String requesterMessage = DatabaseUtil.getString(cursor, GroupChannelDatabaseHelper.TableGroupChannelAssociationsColumns.REQUESTER_MESSAGE);
                Long requesterTime = DatabaseUtil.getLong(cursor, GroupChannelDatabaseHelper.TableGroupChannelAssociationsColumns.REQUESTER__TIME);
                Long acceptTime = DatabaseUtil.getLong(cursor, GroupChannelDatabaseHelper.TableGroupChannelAssociationsColumns.ACCEPT_TIME);
                String inviteUuid = DatabaseUtil.getString(cursor, GroupChannelDatabaseHelper.TableGroupChannelAssociationsColumns.INVITE_UUID);
                Channel requesterChannel = ChannelDatabaseManager.getInstance().findOneChannel(requester);
                GroupChannelAssociation groupChannelAssociation = new GroupChannelAssociation(associationId, groupChannelIdFind, channelImessageId, requesterChannel, requesterMessage, requesterTime == null ? null : new Date(requesterTime), acceptTime == null ? null : new Date(acceptTime), inviteUuid);
                int index = result.indexOf(groupChannel);
                if(index >= 0){
                    result.get(index).addGroupChannelAssociation(groupChannelAssociation);
                }else {
                    groupChannel.addGroupChannelAssociation(groupChannelAssociation);
                    result.add(groupChannel);
                }
            }
            return result.get(0);
        }finally {
            releaseDatabaseIfUnused();
        }
    }

    public List<GroupChannelTag> findAllGroupChannelTags(){
        openDatabaseIfClosed();
        try(Cursor cursor = getDatabase().rawQuery("SELECT * FROM " + GroupChannelDatabaseHelper.DatabaseInfo.TABLE_NAME_TAGS + " t"
                + " LEFT JOIN " + GroupChannelDatabaseHelper.DatabaseInfo.TABLE_NAME_TAG_CHANNELS + " tc"
                + " ON t." + GroupChannelDatabaseHelper.TableTagsColumns.ID + " = tc." + GroupChannelDatabaseHelper.TableTagChannelsColumns.TAG_ID, null)){
            List<GroupChannelTag> result = new ArrayList<>();
            String currentTagId = null;
            GroupChannelTag channelTag = null;
            List<String> channelIds = null;
            while (cursor.moveToNext()){
                String tagId = DatabaseUtil.getString(cursor, GroupChannelDatabaseHelper.TableTagsColumns.ID);
                String imessageId = DatabaseUtil.getString(cursor, GroupChannelDatabaseHelper.TableTagsColumns.IMESSAGE_ID);
                String name = DatabaseUtil.getString(cursor, GroupChannelDatabaseHelper.TableTagsColumns.NAME);
                Integer order = DatabaseUtil.getInteger(cursor, GroupChannelDatabaseHelper.TableTagsColumns.RAW_ORDER);
                String channelImessageId = DatabaseUtil.getString(cursor, GroupChannelDatabaseHelper.TableTagChannelsColumns.IMESSAGE_ID);
                if (currentTagId == null) {
                    currentTagId = tagId;
                    channelIds = new ArrayList<>();
                    if(channelImessageId != null) channelIds.add(channelImessageId);
                    channelTag = new GroupChannelTag(tagId, imessageId, name, order == null ? -1 : order, null);
                } else {
                    if (currentTagId.equals(tagId)) {
                        if(channelImessageId != null) channelIds.add(channelImessageId);
                    } else {
                        channelTag.setGroupChannelIdList(new ArrayList<>(channelIds));
                        result.add(channelTag);
                        channelIds = new ArrayList<>();
                        currentTagId = tagId;
                        if(channelImessageId != null) channelIds.add(channelImessageId);
                        channelTag = new GroupChannelTag(tagId, imessageId, name, order == null ? -1 : order, null);
                    }
                }
            }
            if(channelTag != null) {
                channelTag.setGroupChannelIdList(new ArrayList<>(channelIds));
                result.add(channelTag);
            }
            return result;
        }finally {
            releaseDatabaseIfUnused();
        }
    }

    public GroupChannelTag findOneGroupChannelTag(String tagId){
        openDatabaseIfClosed();
        try(Cursor cursor = getDatabase().rawQuery("SELECT * FROM " + GroupChannelDatabaseHelper.DatabaseInfo.TABLE_NAME_TAGS + " t"
                + " LEFT JOIN " + GroupChannelDatabaseHelper.DatabaseInfo.TABLE_NAME_TAG_CHANNELS + " tc"
                + " ON t." + GroupChannelDatabaseHelper.TableTagsColumns.ID + " = tc." + GroupChannelDatabaseHelper.TableTagChannelsColumns.TAG_ID
                + " WHERE t." + GroupChannelDatabaseHelper.TableTagsColumns.ID + " = \"" + tagId + "\"", null)){
            List<GroupChannelTag> result = new ArrayList<>();
            String currentTagId = null;
            GroupChannelTag channelTag = null;
            List<String> channelIds = null;
            while (cursor.moveToNext()){
                String tagIdFound = DatabaseUtil.getString(cursor, GroupChannelDatabaseHelper.TableTagsColumns.ID);
                String imessageId = DatabaseUtil.getString(cursor, GroupChannelDatabaseHelper.TableTagsColumns.IMESSAGE_ID);
                String name = DatabaseUtil.getString(cursor, GroupChannelDatabaseHelper.TableTagsColumns.NAME);
                Integer order = DatabaseUtil.getInteger(cursor, GroupChannelDatabaseHelper.TableTagsColumns.RAW_ORDER);
                String channelImessageId = DatabaseUtil.getString(cursor, GroupChannelDatabaseHelper.TableTagChannelsColumns.IMESSAGE_ID);
                if (currentTagId == null) {
                    currentTagId = tagIdFound;
                    channelIds = new ArrayList<>();
                    if(channelImessageId != null) channelIds.add(channelImessageId);
                    channelTag = new GroupChannelTag(tagIdFound, imessageId, name, order == null ? -1 : order, null);
                } else {
                    if (currentTagId.equals(tagIdFound)) {
                        if(channelImessageId != null) channelIds.add(channelImessageId);
                    } else {
                        channelTag.setGroupChannelIdList(new ArrayList<>(channelIds));
                        result.add(channelTag);
                        channelIds = new ArrayList<>();
                        currentTagId = tagIdFound;
                        if(channelImessageId != null) channelIds.add(channelImessageId);
                        channelTag = new GroupChannelTag(tagIdFound, imessageId, name, order == null ? -1 : order, null);
                    }
                }
            }
            if(channelTag != null) {
                channelTag.setGroupChannelIdList(new ArrayList<>(channelIds));
                result.add(channelTag);
            }
            return result.get(0);
        }finally {
            releaseDatabaseIfUnused();
        }
    }

    public void clearChannelTags(){
        openDatabaseIfClosed();
        try {
            getDatabase().delete(GroupChannelDatabaseHelper.DatabaseInfo.TABLE_NAME_TAGS, "1=1", null);
            getDatabase().delete(GroupChannelDatabaseHelper.DatabaseInfo.TABLE_NAME_TAG_CHANNELS, "1=1", null);
        }finally {
            releaseDatabaseIfUnused();
        }
    }

    public boolean insertTagsOrIgnore(List<GroupChannelTag> groupChannelTags){
        AtomicBoolean result = new AtomicBoolean(true);
        openDatabaseIfClosed();
        getDatabase().beginTransaction();
        try {
            OUTER: for (GroupChannelTag groupChannelTag : groupChannelTags) {
                ContentValues values = new ContentValues();
                values.put(GroupChannelDatabaseHelper.TableTagsColumns.ID, groupChannelTag.getTagId());
                values.put(GroupChannelDatabaseHelper.TableTagsColumns.IMESSAGE_ID, groupChannelTag.getImessageId());
                values.put(GroupChannelDatabaseHelper.TableTagsColumns.NAME, groupChannelTag.getName());
                values.put(GroupChannelDatabaseHelper.TableTagsColumns.ORDER, groupChannelTag.getOrder());
                long rowId = getDatabase().insertWithOnConflict(GroupChannelDatabaseHelper.DatabaseInfo.TABLE_NAME_TAGS, null,
                        values, SQLiteDatabase.CONFLICT_REPLACE);
                if (rowId == -1) {
                    result.set(false);
                    break;
                }
                for (String groupChannelId : groupChannelTag.getGroupChannelIdList()) {
                    ContentValues values1 = new ContentValues();
                    values1.put(GroupChannelDatabaseHelper.TableTagChannelsColumns.TAG_ID, groupChannelTag.getTagId());
                    values1.put(GroupChannelDatabaseHelper.TableTagChannelsColumns.IMESSAGE_ID, groupChannelId);
                    long rowId1 = getDatabase().insertWithOnConflict(GroupChannelDatabaseHelper.DatabaseInfo.TABLE_NAME_TAG_CHANNELS, null,
                            values1, SQLiteDatabase.CONFLICT_REPLACE);
                    if (rowId1 == -1) {
                        result.set(false);
                        break OUTER;
                    }
                };
            };
            if(result.get()) getDatabase().setTransactionSuccessful();
            return result.get();
        }finally {
            getDatabase().endTransaction();
            releaseDatabaseIfUnused();
        }
    }

    public boolean insertGroupChannelNotificationsOrUpdate(List<GroupChannelNotification> groupChannelNotifications){
        openDatabaseIfClosed();
        boolean success = false;
        try {
            for (GroupChannelNotification groupChannelNotification : groupChannelNotifications) {
                ContentValues values = new ContentValues();
                values.put(GroupChannelDatabaseHelper.TableGroupChannelNotificationsColumns.UUID, groupChannelNotification.getUuid());
                values.put(GroupChannelDatabaseHelper.TableGroupChannelNotificationsColumns.TYPE, groupChannelNotification.getType().name());
                values.put(GroupChannelDatabaseHelper.TableGroupChannelNotificationsColumns.GROUP_CHANNEL_ID, groupChannelNotification.getGroupChannelId());
                values.put(GroupChannelDatabaseHelper.TableGroupChannelNotificationsColumns.CHANNEL_ID, groupChannelNotification.getChannelId());
                values.put(GroupChannelDatabaseHelper.TableGroupChannelNotificationsColumns.PASSIVE, groupChannelNotification.isPassive());
                values.put(GroupChannelDatabaseHelper.TableGroupChannelNotificationsColumns.BY_WHOM, groupChannelNotification.getByWhom());
                values.put(GroupChannelDatabaseHelper.TableGroupChannelNotificationsColumns.TIME, groupChannelNotification.getTime().getTime());
                values.put(GroupChannelDatabaseHelper.TableGroupChannelNotificationsColumns.IS_VIEWED, groupChannelNotification.isViewed());
                long rowId = getDatabase().insertWithOnConflict(GroupChannelDatabaseHelper.DatabaseInfo.TABLE_NAME_GROUP_CHANNEL_NOTIFICATIONS, null,
                        values, SQLiteDatabase.CONFLICT_REPLACE);
                if(rowId >= 0) success = true;
            }
        }finally {
            releaseDatabaseIfUnused();
        }
        return success;
    }

    public List<GroupChannelNotification> findAllGroupChannelNotifications(){
        openDatabaseIfClosed();
        List<GroupChannelNotification> result = new ArrayList<>();
        try(Cursor cursor = getDatabase().rawQuery("SELECT * FROM " + GroupChannelDatabaseHelper.DatabaseInfo.TABLE_NAME_GROUP_CHANNEL_NOTIFICATIONS, null)){
            while (cursor.moveToNext()){
                String uuid = DatabaseUtil.getString(cursor, GroupChannelDatabaseHelper.TableGroupChannelNotificationsColumns.UUID);
                GroupChannelNotification.Type type = GroupChannelNotification.Type.valueOf(DatabaseUtil.getString(cursor, GroupChannelDatabaseHelper.TableGroupChannelNotificationsColumns.TYPE));
                String groupChannelId = DatabaseUtil.getString(cursor, GroupChannelDatabaseHelper.TableGroupChannelNotificationsColumns.GROUP_CHANNEL_ID);
                String channelId = DatabaseUtil.getString(cursor, GroupChannelDatabaseHelper.TableGroupChannelNotificationsColumns.CHANNEL_ID);
                Boolean passive = DatabaseUtil.getBoolean(cursor, GroupChannelDatabaseHelper.TableGroupChannelNotificationsColumns.PASSIVE);
                String byWhom = DatabaseUtil.getString(cursor, GroupChannelDatabaseHelper.TableGroupChannelNotificationsColumns.BY_WHOM);
                Date time = new Date(DatabaseUtil.getLong(cursor, GroupChannelDatabaseHelper.TableGroupChannelNotificationsColumns.TIME));
                Boolean isViewed = DatabaseUtil.getBoolean(cursor, GroupChannelDatabaseHelper.TableGroupChannelNotificationsColumns.IS_VIEWED);
                result.add(new GroupChannelNotification(uuid, type, groupChannelId, channelId, passive, byWhom, time, isViewed));
            }
        }finally {
            releaseDatabaseIfUnused();
        }
        return result;
    }
}
