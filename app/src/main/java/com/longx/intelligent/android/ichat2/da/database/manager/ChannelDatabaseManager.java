package com.longx.intelligent.android.ichat2.da.database.manager;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.longx.intelligent.android.ichat2.da.database.helper.ChannelDatabaseHelper;
import com.longx.intelligent.android.ichat2.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.ichat2.data.Avatar;
import com.longx.intelligent.android.ichat2.data.ChannelAssociation;
import com.longx.intelligent.android.ichat2.data.Channel;
import com.longx.intelligent.android.ichat2.data.ChannelTag;
import com.longx.intelligent.android.ichat2.data.ChatMessageAllow;
import com.longx.intelligent.android.ichat2.data.RecentBroadcastMedia;
import com.longx.intelligent.android.ichat2.data.UserInfo;
import com.longx.intelligent.android.ichat2.util.DatabaseUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by LONG on 2024/5/9 at 7:51 PM.
 */
public class ChannelDatabaseManager extends BaseDatabaseManager{
    public ChannelDatabaseManager(ChannelDatabaseHelper helper) {
        super(helper);
    }

    private static class InstanceHolder{
        private static ChannelDatabaseManager instance;
    }

    public static void init(Context context){
        String ichatId = SharedPreferencesAccessor.UserProfilePref.getCurrentUserProfile(context).getIchatId();
        ChannelDatabaseHelper helper = new ChannelDatabaseHelper(context, ichatId);
        InstanceHolder.instance = new ChannelDatabaseManager(helper);
    }

    public static ChannelDatabaseManager getInstance() {
        return InstanceHolder.instance;
    }

    public boolean insertAssociationsOrIgnore(List<ChannelAssociation> channelAssociations){
        AtomicBoolean result = new AtomicBoolean(true);
        openDatabaseIfClosed();
        try {
            channelAssociations.forEach(channelAssociation -> {
                ContentValues values = new ContentValues();
                values.put(ChannelDatabaseHelper.TableChannelAssociationsColumns.ASSOCIATION_ID, channelAssociation.getAssociationId());
                values.put(ChannelDatabaseHelper.TableChannelAssociationsColumns.ICHAT_ID, channelAssociation.getIchatId());
                values.put(ChannelDatabaseHelper.TableChannelAssociationsColumns.CHANNEL_ICHAT_ID, channelAssociation.getChannelIchatId());
                values.put(ChannelDatabaseHelper.TableChannelAssociationsColumns.IS_REQUESTER, channelAssociation.isRequester());
                values.put(ChannelDatabaseHelper.TableChannelAssociationsColumns.REQUEST_TIME, channelAssociation.getRequestTime().getTime());
                values.put(ChannelDatabaseHelper.TableChannelAssociationsColumns.ACCEPT_TIME, channelAssociation.getAcceptTime().getTime());
                values.put(ChannelDatabaseHelper.TableChannelAssociationsColumns.IS_ACTIVE, channelAssociation.isActive());
                values.put(ChannelDatabaseHelper.TableChannelAssociationsColumns.ALLOW_VOICE_CHAT_MESSAGE_TO_THEM, channelAssociation.getChatMessageAllowToThem().isAllowVoice());
                values.put(ChannelDatabaseHelper.TableChannelAssociationsColumns.ALLOW_NOTICE_CHAT_MESSAGE_TO_THEM, channelAssociation.getChatMessageAllowToThem().isAllowNotice());
                values.put(ChannelDatabaseHelper.TableChannelAssociationsColumns.ALLOW_VOICE_CHAT_MESSAGE_TO_ME, channelAssociation.getChatMessageAllowToMe().isAllowVoice());
                values.put(ChannelDatabaseHelper.TableChannelAssociationsColumns.ALLOW_NOTICE_CHAT_MESSAGE_TO_ME, channelAssociation.getChatMessageAllowToMe().isAllowNotice());
                long id = getDatabase().insertWithOnConflict(ChannelDatabaseHelper.DatabaseInfo.TABLE_NAME_CHANNEL_ASSOCIATIONS, null,
                        values, SQLiteDatabase.CONFLICT_IGNORE);
                if (id == -1) {
                    result.set(false);
                }
                ContentValues values1 = new ContentValues();
                Channel channel = channelAssociation.getChannel();
                values1.put(ChannelDatabaseHelper.TableChannelsColumns.ICHAT_ID, channel.getIchatId());
                values1.put(ChannelDatabaseHelper.TableChannelsColumns.ICHAT_ID_USER, channel.getIchatIdUser());
                values1.put(ChannelDatabaseHelper.TableChannelsColumns.EMAIL, channel.getEmail());
                values1.put(ChannelDatabaseHelper.TableChannelsColumns.USERNAME, channel.getUsername());
                values1.put(ChannelDatabaseHelper.TableChannelsColumns.NOTE, channel.getNote());
                values1.put(ChannelDatabaseHelper.TableChannelsColumns.AVATAR_HASH, channel.getAvatar() == null ? null : channel.getAvatar().getHash());
                values1.put(ChannelDatabaseHelper.TableChannelsColumns.AVATAR_ICHAT_ID, channel.getAvatar() == null ? null : channel.getAvatar().getIchatId());
                values1.put(ChannelDatabaseHelper.TableChannelsColumns.AVATAR_EXTENSION, channel.getAvatar() == null ? null : channel.getAvatar().getExtension());
                values1.put(ChannelDatabaseHelper.TableChannelsColumns.AVATAR_TIME, channel.getAvatar() == null ? null : channel.getAvatar().getTime().getTime());
                values1.put(ChannelDatabaseHelper.TableChannelsColumns.SEX, channel.getSex());
                UserInfo.Region firstRegion = channel.getFirstRegion();
                values1.put(ChannelDatabaseHelper.TableChannelsColumns.FIRST_REGION_ADCODE, firstRegion == null ? null : firstRegion.getAdcode());
                values1.put(ChannelDatabaseHelper.TableChannelsColumns.FIRST_REGION_NAME, firstRegion == null ? null : firstRegion.getName());
                UserInfo.Region secondRegion = channel.getSecondRegion();
                values1.put(ChannelDatabaseHelper.TableChannelsColumns.SECOND_REGION_ADCODE, secondRegion == null ? null : secondRegion.getAdcode());
                values1.put(ChannelDatabaseHelper.TableChannelsColumns.SECOND_REGION_NAME, secondRegion == null ? null : secondRegion.getName());
                UserInfo.Region thirdRegion = channel.getThirdRegion();
                values1.put(ChannelDatabaseHelper.TableChannelsColumns.THIRD_REGION_ADCODE, thirdRegion == null ? null : thirdRegion.getAdcode());
                values1.put(ChannelDatabaseHelper.TableChannelsColumns.THIRD_REGION_NAME, thirdRegion == null ? null : thirdRegion.getName());
                values1.put(ChannelDatabaseHelper.TableChannelsColumns.ASSOCIATED, channel.isAssociated());
                long id1 = getDatabase().insertWithOnConflict(ChannelDatabaseHelper.DatabaseInfo.TABLE_NAME_CHANNELS, null,
                        values1, SQLiteDatabase.CONFLICT_IGNORE);
                if (id1 == -1) {
                    result.set(false);
                }
            });
        }finally {
            releaseDatabaseIfUnused();
        }
        return result.get();
    }

    public List<ChannelAssociation> findAllAssociations(){
        openDatabaseIfClosed();
        String sql = "SELECT *, " + " ca." + ChannelDatabaseHelper.TableChannelAssociationsColumns.ICHAT_ID + " AS associationTableIchatId, "
                + " c." + ChannelDatabaseHelper.TableChannelsColumns.ICHAT_ID + " AS channelTableIchatId "
                + " FROM " + ChannelDatabaseHelper.DatabaseInfo.TABLE_NAME_CHANNEL_ASSOCIATIONS + " ca "
                + " INNER JOIN " + ChannelDatabaseHelper.DatabaseInfo.TABLE_NAME_CHANNELS + " c ON " +
                ChannelDatabaseHelper.TableChannelAssociationsColumns.CHANNEL_ICHAT_ID + " = " + " c." +  ChannelDatabaseHelper.TableChannelsColumns.ICHAT_ID;
        try(Cursor cursor = getDatabase().rawQuery(sql, null)) {
            List<ChannelAssociation> result = new ArrayList<>();
            while (cursor.moveToNext()){
                String associationId = DatabaseUtil.getString(cursor, ChannelDatabaseHelper.TableChannelAssociationsColumns.ASSOCIATION_ID);
                String associationTableIchatId = DatabaseUtil.getString(cursor, "associationTableIchatId");
                String channelTableIchatId = DatabaseUtil.getString(cursor, "channelTableIchatId");
                String channelIchatId = DatabaseUtil.getString(cursor, ChannelDatabaseHelper.TableChannelAssociationsColumns.CHANNEL_ICHAT_ID);
                Boolean isRequester = DatabaseUtil.getBoolean(cursor, ChannelDatabaseHelper.TableChannelAssociationsColumns.IS_REQUESTER);
                Date requestTime = DatabaseUtil.getTime(cursor, ChannelDatabaseHelper.TableChannelAssociationsColumns.REQUEST_TIME);
                Date acceptTime = DatabaseUtil.getTime(cursor, ChannelDatabaseHelper.TableChannelAssociationsColumns.ACCEPT_TIME);
                Boolean isActive = DatabaseUtil.getBoolean(cursor, ChannelDatabaseHelper.TableChannelAssociationsColumns.IS_ACTIVE);
                Boolean allowVoiceChatMessageToThem = DatabaseUtil.getBoolean(cursor, ChannelDatabaseHelper.TableChannelAssociationsColumns.ALLOW_VOICE_CHAT_MESSAGE_TO_THEM);
                Boolean allowNoticeChatMessageToThem = DatabaseUtil.getBoolean(cursor, ChannelDatabaseHelper.TableChannelAssociationsColumns.ALLOW_NOTICE_CHAT_MESSAGE_TO_THEM);
                Boolean allowVoiceChatMessageToMe = DatabaseUtil.getBoolean(cursor, ChannelDatabaseHelper.TableChannelAssociationsColumns.ALLOW_VOICE_CHAT_MESSAGE_TO_ME);
                Boolean allowNoticeChatMessageToMe = DatabaseUtil.getBoolean(cursor, ChannelDatabaseHelper.TableChannelAssociationsColumns.ALLOW_NOTICE_CHAT_MESSAGE_TO_ME);
                String channelTableIchatIdUser = DatabaseUtil.getString(cursor, ChannelDatabaseHelper.TableChannelsColumns.ICHAT_ID_USER);
                String channelTableEmail = DatabaseUtil.getString(cursor, ChannelDatabaseHelper.TableChannelsColumns.EMAIL);
                String channelTableUsername = DatabaseUtil.getString(cursor, ChannelDatabaseHelper.TableChannelsColumns.USERNAME);
                String channelTableNote = DatabaseUtil.getString(cursor, ChannelDatabaseHelper.TableChannelsColumns.NOTE);
                String channelTableAvatarHash = DatabaseUtil.getString(cursor, ChannelDatabaseHelper.TableChannelsColumns.AVATAR_HASH);
                String channelTableAvatarIchatId = DatabaseUtil.getString(cursor, ChannelDatabaseHelper.TableChannelsColumns.AVATAR_ICHAT_ID);
                String channelTableAvatarExtension = DatabaseUtil.getString(cursor, ChannelDatabaseHelper.TableChannelsColumns.AVATAR_EXTENSION);
                Date channelTableAvatarTime = DatabaseUtil.getTime(cursor, ChannelDatabaseHelper.TableChannelsColumns.AVATAR_TIME);
                Integer channelTableSex = DatabaseUtil.getInteger(cursor, ChannelDatabaseHelper.TableChannelsColumns.SEX);
                Integer channelTableFirstRegionAdcode = DatabaseUtil.getInteger(cursor, ChannelDatabaseHelper.TableChannelsColumns.FIRST_REGION_ADCODE);
                String channelTableFirstRegionName = DatabaseUtil.getString(cursor, ChannelDatabaseHelper.TableChannelsColumns.FIRST_REGION_NAME);
                Integer channelTableSecondRegionAdcode = DatabaseUtil.getInteger(cursor, ChannelDatabaseHelper.TableChannelsColumns.SECOND_REGION_ADCODE);
                String channelTableSecondRegionName = DatabaseUtil.getString(cursor, ChannelDatabaseHelper.TableChannelsColumns.SECOND_REGION_NAME);
                Integer channelTableThirdRegionAdcode = DatabaseUtil.getInteger(cursor, ChannelDatabaseHelper.TableChannelsColumns.THIRD_REGION_ADCODE);
                String channelTableThirdRegionName = DatabaseUtil.getString(cursor, ChannelDatabaseHelper.TableChannelsColumns.THIRD_REGION_NAME);
                Boolean channelTableAssociated = DatabaseUtil.getBoolean(cursor, ChannelDatabaseHelper.TableChannelsColumns.ASSOCIATED);
                result.add(new ChannelAssociation(associationId, associationTableIchatId, channelIchatId, Boolean.TRUE.equals(isRequester), requestTime, acceptTime, Boolean.TRUE.equals(isActive),
                        new Channel(channelTableIchatId, channelTableIchatIdUser, channelTableEmail, channelTableUsername, channelTableNote, new Avatar(channelTableAvatarHash, channelTableAvatarIchatId, channelTableAvatarExtension, channelTableAvatarTime),
                                channelTableSex,
                                channelTableFirstRegionAdcode == null && channelTableFirstRegionName == null ? null : new UserInfo.Region(channelTableFirstRegionAdcode, channelTableFirstRegionName),
                                channelTableSecondRegionAdcode == null && channelTableSecondRegionName == null ? null : new UserInfo.Region(channelTableSecondRegionAdcode, channelTableSecondRegionName),
                                channelTableThirdRegionAdcode == null && channelTableThirdRegionName == null ? null : new UserInfo.Region(channelTableThirdRegionAdcode, channelTableThirdRegionName),
                                Boolean.TRUE.equals(channelTableAssociated)),
                        new ChatMessageAllow(allowVoiceChatMessageToThem == null || allowVoiceChatMessageToThem, allowNoticeChatMessageToThem == null || allowNoticeChatMessageToThem),
                        new ChatMessageAllow(allowVoiceChatMessageToMe == null || allowVoiceChatMessageToMe, allowNoticeChatMessageToMe == null || allowNoticeChatMessageToMe)));
            }
            return result;
        }finally {
            releaseDatabaseIfUnused();
        }
    }

    public ChannelAssociation findOneAssociations(String ichatId){
        openDatabaseIfClosed();
        String sql = "SELECT *, " + " ca." + ChannelDatabaseHelper.TableChannelAssociationsColumns.ICHAT_ID + " AS associationTableIchatId, "
                + " c." + ChannelDatabaseHelper.TableChannelsColumns.ICHAT_ID + " AS channelTableIchatId "
                + " FROM " + ChannelDatabaseHelper.DatabaseInfo.TABLE_NAME_CHANNEL_ASSOCIATIONS + " ca "
                + " INNER JOIN " + ChannelDatabaseHelper.DatabaseInfo.TABLE_NAME_CHANNELS + " c ON "
                + ChannelDatabaseHelper.TableChannelAssociationsColumns.CHANNEL_ICHAT_ID + " = " + " c." +  ChannelDatabaseHelper.TableChannelsColumns.ICHAT_ID
                + " WHERE ca." + ChannelDatabaseHelper.TableChannelAssociationsColumns.CHANNEL_ICHAT_ID + " = \"" + ichatId + "\"";
        try(Cursor cursor = getDatabase().rawQuery(sql, null)) {
            cursor.moveToNext();
            String associationId = DatabaseUtil.getString(cursor, ChannelDatabaseHelper.TableChannelAssociationsColumns.ASSOCIATION_ID);
            String associationTableIchatId = DatabaseUtil.getString(cursor, "associationTableIchatId");
            String channelTableIchatId = DatabaseUtil.getString(cursor, "channelTableIchatId");
            String channelIchatId = DatabaseUtil.getString(cursor, ChannelDatabaseHelper.TableChannelAssociationsColumns.CHANNEL_ICHAT_ID);
            Boolean isRequester = DatabaseUtil.getBoolean(cursor, ChannelDatabaseHelper.TableChannelAssociationsColumns.IS_REQUESTER);
            Date requestTime = DatabaseUtil.getTime(cursor, ChannelDatabaseHelper.TableChannelAssociationsColumns.REQUEST_TIME);
            Date acceptTime = DatabaseUtil.getTime(cursor, ChannelDatabaseHelper.TableChannelAssociationsColumns.ACCEPT_TIME);
            Boolean isActive = DatabaseUtil.getBoolean(cursor, ChannelDatabaseHelper.TableChannelAssociationsColumns.IS_ACTIVE);
            Boolean allowVoiceChatMessageToThem = DatabaseUtil.getBoolean(cursor, ChannelDatabaseHelper.TableChannelAssociationsColumns.ALLOW_VOICE_CHAT_MESSAGE_TO_THEM);
            Boolean allowNoticeChatMessageToThem = DatabaseUtil.getBoolean(cursor, ChannelDatabaseHelper.TableChannelAssociationsColumns.ALLOW_NOTICE_CHAT_MESSAGE_TO_THEM);
            Boolean allowVoiceChatMessageToMe = DatabaseUtil.getBoolean(cursor, ChannelDatabaseHelper.TableChannelAssociationsColumns.ALLOW_VOICE_CHAT_MESSAGE_TO_ME);
            Boolean allowNoticeChatMessageToMe = DatabaseUtil.getBoolean(cursor, ChannelDatabaseHelper.TableChannelAssociationsColumns.ALLOW_NOTICE_CHAT_MESSAGE_TO_ME);
            String channelTableIchatIdUser = DatabaseUtil.getString(cursor, ChannelDatabaseHelper.TableChannelsColumns.ICHAT_ID_USER);
            String channelTableEmail = DatabaseUtil.getString(cursor, ChannelDatabaseHelper.TableChannelsColumns.EMAIL);
            String channelTableUsername = DatabaseUtil.getString(cursor, ChannelDatabaseHelper.TableChannelsColumns.USERNAME);
            String channelTableNote = DatabaseUtil.getString(cursor, ChannelDatabaseHelper.TableChannelsColumns.NOTE);
            String channelTableAvatarHash = DatabaseUtil.getString(cursor, ChannelDatabaseHelper.TableChannelsColumns.AVATAR_HASH);
            String channelTableAvatarIchatId = DatabaseUtil.getString(cursor, ChannelDatabaseHelper.TableChannelsColumns.AVATAR_ICHAT_ID);
            String channelTableAvatarExtension = DatabaseUtil.getString(cursor, ChannelDatabaseHelper.TableChannelsColumns.AVATAR_EXTENSION);
            Date channelTableAvatarTime = DatabaseUtil.getTime(cursor, ChannelDatabaseHelper.TableChannelsColumns.AVATAR_TIME);
            Integer channelTableSex = DatabaseUtil.getInteger(cursor, ChannelDatabaseHelper.TableChannelsColumns.SEX);
            Integer channelTableFirstRegionAdcode = DatabaseUtil.getInteger(cursor, ChannelDatabaseHelper.TableChannelsColumns.FIRST_REGION_ADCODE);
            String channelTableFirstRegionName = DatabaseUtil.getString(cursor, ChannelDatabaseHelper.TableChannelsColumns.FIRST_REGION_NAME);
            Integer channelTableSecondRegionAdcode = DatabaseUtil.getInteger(cursor, ChannelDatabaseHelper.TableChannelsColumns.SECOND_REGION_ADCODE);
            String channelTableSecondRegionName = DatabaseUtil.getString(cursor, ChannelDatabaseHelper.TableChannelsColumns.SECOND_REGION_NAME);
            Integer channelTableThirdRegionAdcode = DatabaseUtil.getInteger(cursor, ChannelDatabaseHelper.TableChannelsColumns.THIRD_REGION_ADCODE);
            String channelTableThirdRegionName = DatabaseUtil.getString(cursor, ChannelDatabaseHelper.TableChannelsColumns.THIRD_REGION_NAME);
            Boolean channelTableAssociated = DatabaseUtil.getBoolean(cursor, ChannelDatabaseHelper.TableChannelsColumns.ASSOCIATED);
            return new ChannelAssociation(associationId, associationTableIchatId, channelIchatId, Boolean.TRUE.equals(isRequester), requestTime, acceptTime, Boolean.TRUE.equals(isActive),
                    new Channel(channelTableIchatId, channelTableIchatIdUser, channelTableEmail, channelTableUsername, channelTableNote, new Avatar(channelTableAvatarHash, channelTableAvatarIchatId, channelTableAvatarExtension, channelTableAvatarTime),
                            channelTableSex,
                            channelTableFirstRegionAdcode == null && channelTableFirstRegionName == null ? null : new UserInfo.Region(channelTableFirstRegionAdcode, channelTableFirstRegionName),
                            channelTableSecondRegionAdcode == null && channelTableSecondRegionName == null ? null : new UserInfo.Region(channelTableSecondRegionAdcode, channelTableSecondRegionName),
                            channelTableThirdRegionAdcode == null && channelTableThirdRegionName == null ? null : new UserInfo.Region(channelTableThirdRegionAdcode, channelTableThirdRegionName),
                            Boolean.TRUE.equals(channelTableAssociated)),
                    new ChatMessageAllow(allowVoiceChatMessageToThem == null || allowVoiceChatMessageToThem, allowNoticeChatMessageToThem == null || allowNoticeChatMessageToThem),
                    new ChatMessageAllow(allowVoiceChatMessageToMe == null || allowVoiceChatMessageToMe, allowNoticeChatMessageToMe == null || allowNoticeChatMessageToMe));
        }finally {
            releaseDatabaseIfUnused();
        }
    }

    public Channel findOneChannel(String ichatId){
        openDatabaseIfClosed();
        try (Cursor cursor = getDatabase().query(ChannelDatabaseHelper.DatabaseInfo.TABLE_NAME_CHANNELS, null, ChannelDatabaseHelper.TableChannelsColumns.ICHAT_ID + " = \"" + ichatId + "\"", null, null, null, null)){
            cursor.moveToNext();
            String channelTableIchatId = DatabaseUtil.getString(cursor, ChannelDatabaseHelper.TableChannelsColumns.ICHAT_ID);
            String channelTableIchatIdUser = DatabaseUtil.getString(cursor, ChannelDatabaseHelper.TableChannelsColumns.ICHAT_ID_USER);
            String channelTableEmail = DatabaseUtil.getString(cursor, ChannelDatabaseHelper.TableChannelsColumns.EMAIL);
            String channelTableUsername = DatabaseUtil.getString(cursor, ChannelDatabaseHelper.TableChannelsColumns.USERNAME);
            String channelTableNote = DatabaseUtil.getString(cursor, ChannelDatabaseHelper.TableChannelsColumns.NOTE);
            String channelTableAvatarHash = DatabaseUtil.getString(cursor, ChannelDatabaseHelper.TableChannelsColumns.AVATAR_HASH);
            String channelTableAvatarIchatId = DatabaseUtil.getString(cursor, ChannelDatabaseHelper.TableChannelsColumns.AVATAR_ICHAT_ID);
            String channelTableAvatarExtension = DatabaseUtil.getString(cursor, ChannelDatabaseHelper.TableChannelsColumns.AVATAR_EXTENSION);
            Date channelTableAvatarTime = DatabaseUtil.getTime(cursor, ChannelDatabaseHelper.TableChannelsColumns.AVATAR_TIME);
            Integer channelTableSex = DatabaseUtil.getInteger(cursor, ChannelDatabaseHelper.TableChannelsColumns.SEX);
            Integer channelTableFirstRegionAdcode = DatabaseUtil.getInteger(cursor, ChannelDatabaseHelper.TableChannelsColumns.FIRST_REGION_ADCODE);
            String channelTableFirstRegionName = DatabaseUtil.getString(cursor, ChannelDatabaseHelper.TableChannelsColumns.FIRST_REGION_NAME);
            Integer channelTableSecondRegionAdcode = DatabaseUtil.getInteger(cursor, ChannelDatabaseHelper.TableChannelsColumns.SECOND_REGION_ADCODE);
            String channelTableSecondRegionName = DatabaseUtil.getString(cursor, ChannelDatabaseHelper.TableChannelsColumns.SECOND_REGION_NAME);
            Integer channelTableThirdRegionAdcode = DatabaseUtil.getInteger(cursor, ChannelDatabaseHelper.TableChannelsColumns.THIRD_REGION_ADCODE);
            String channelTableThirdRegionName = DatabaseUtil.getString(cursor, ChannelDatabaseHelper.TableChannelsColumns.THIRD_REGION_NAME);
            Boolean channelTableAssociated = DatabaseUtil.getBoolean(cursor, ChannelDatabaseHelper.TableChannelsColumns.ASSOCIATED);
            return new Channel(channelTableIchatId, channelTableIchatIdUser, channelTableEmail, channelTableUsername, channelTableNote, new Avatar(channelTableAvatarHash, channelTableAvatarIchatId, channelTableAvatarExtension, channelTableAvatarTime),
                    channelTableSex,
                    channelTableFirstRegionAdcode == null && channelTableFirstRegionName == null ? null : new UserInfo.Region(channelTableFirstRegionAdcode, channelTableFirstRegionName),
                    channelTableSecondRegionAdcode == null && channelTableSecondRegionName == null ? null : new UserInfo.Region(channelTableSecondRegionAdcode, channelTableSecondRegionName),
                    channelTableThirdRegionAdcode == null && channelTableThirdRegionName == null ? null : new UserInfo.Region(channelTableThirdRegionAdcode, channelTableThirdRegionName),
                    Boolean.TRUE.equals(channelTableAssociated));
        }catch (Exception e){
            return null;
        } finally {
            releaseDatabaseIfUnused();
        }
    }

    public void clearChannels(){
        openDatabaseIfClosed();
        try {
            getDatabase().delete(ChannelDatabaseHelper.DatabaseInfo.TABLE_NAME_CHANNEL_ASSOCIATIONS, "1=1", null);
            getDatabase().delete(ChannelDatabaseHelper.DatabaseInfo.TABLE_NAME_CHANNELS, "1=1", null);
        }finally {
            releaseDatabaseIfUnused();
        }
    }

    public void clearChannelTags(){
        openDatabaseIfClosed();
        try {
            getDatabase().delete(ChannelDatabaseHelper.DatabaseInfo.TABLE_NAME_TAGS, "1=1", null);
            getDatabase().delete(ChannelDatabaseHelper.DatabaseInfo.TABLE_NAME_TAG_CHANNELS, "1=1", null);
        }finally {
            releaseDatabaseIfUnused();
        }
    }

    public boolean insertTagsOrIgnore(List<ChannelTag> channelTags){
        AtomicBoolean result = new AtomicBoolean(true);
        openDatabaseIfClosed();
        getDatabase().beginTransaction();
        try {
            OUTER: for (ChannelTag channelTag : channelTags) {
                ContentValues values = new ContentValues();
                values.put(ChannelDatabaseHelper.TableTagsColumns.ID, channelTag.getId());
                values.put(ChannelDatabaseHelper.TableTagsColumns.ICHAT_ID, channelTag.getIchatId());
                values.put(ChannelDatabaseHelper.TableTagsColumns.NAME, channelTag.getName());
                values.put(ChannelDatabaseHelper.TableTagsColumns.ORDER, channelTag.getOrder());
                long rowId = getDatabase().insertWithOnConflict(ChannelDatabaseHelper.DatabaseInfo.TABLE_NAME_TAGS, null,
                        values, SQLiteDatabase.CONFLICT_REPLACE);
                if (rowId == -1) {
                    result.set(false);
                    break;
                }
                for (String channelIchatId : channelTag.getChannelIchatIdList()) {
                    ContentValues values1 = new ContentValues();
                    values1.put(ChannelDatabaseHelper.TableTagChannelsColumns.TAG_ID, channelTag.getId());
                    values1.put(ChannelDatabaseHelper.TableTagChannelsColumns.ICHAT_ID, channelIchatId);
                    long rowId1 = getDatabase().insertWithOnConflict(ChannelDatabaseHelper.DatabaseInfo.TABLE_NAME_TAG_CHANNELS, null,
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

    public List<ChannelTag> findAllChannelTags(){
        openDatabaseIfClosed();
        try(Cursor cursor = getDatabase().rawQuery("SELECT * FROM " + ChannelDatabaseHelper.DatabaseInfo.TABLE_NAME_TAGS + " t"
                + " LEFT JOIN " + ChannelDatabaseHelper.DatabaseInfo.TABLE_NAME_TAG_CHANNELS + " tc"
                + " ON t." + ChannelDatabaseHelper.TableTagsColumns.ID + " = tc." + ChannelDatabaseHelper.TableTagChannelsColumns.TAG_ID, null)){
            List<ChannelTag> result = new ArrayList<>();
            String currentTagId = null;
            ChannelTag channelTag = null;
            List<String> channelIds = null;
            while (cursor.moveToNext()){
                String tagId = DatabaseUtil.getString(cursor, ChannelDatabaseHelper.TableTagsColumns.ID);
                String ichatId = DatabaseUtil.getString(cursor, ChannelDatabaseHelper.TableTagsColumns.ICHAT_ID);
                String name = DatabaseUtil.getString(cursor, ChannelDatabaseHelper.TableTagsColumns.NAME);
                Integer order = DatabaseUtil.getInteger(cursor, ChannelDatabaseHelper.TableTagsColumns.RAW_ORDER);
                String channelIchatId = DatabaseUtil.getString(cursor, ChannelDatabaseHelper.TableTagChannelsColumns.ICHAT_ID);
                if (currentTagId == null) {
                    currentTagId = tagId;
                    channelIds = new ArrayList<>();
                    if(channelIchatId != null) channelIds.add(channelIchatId);
                    channelTag = new ChannelTag(tagId, ichatId, name, order == null ? -1 : order, null);
                } else {
                    if (currentTagId.equals(tagId)) {
                        if(channelIchatId != null) channelIds.add(channelIchatId);
                    } else {
                        channelTag.setChannelIchatIdList(new ArrayList<>(channelIds));
                        result.add(channelTag);
                        channelIds = new ArrayList<>();
                        currentTagId = tagId;
                        if(channelIchatId != null) channelIds.add(channelIchatId);
                        channelTag = new ChannelTag(tagId, ichatId, name, order == null ? -1 : order, null);
                    }
                }
            }
            if(channelTag != null) {
                channelTag.setChannelIchatIdList(new ArrayList<>(channelIds));
                result.add(channelTag);
            }
            return result;
        }finally {
            releaseDatabaseIfUnused();
        }
    }

    public ChannelTag findOneChannelTags(String tagId){
        openDatabaseIfClosed();
        try(Cursor cursor = getDatabase().rawQuery("SELECT * FROM " + ChannelDatabaseHelper.DatabaseInfo.TABLE_NAME_TAGS + " t"
                + " LEFT JOIN " + ChannelDatabaseHelper.DatabaseInfo.TABLE_NAME_TAG_CHANNELS + " tc"
                + " ON t." + ChannelDatabaseHelper.TableTagsColumns.ID + " = tc." + ChannelDatabaseHelper.TableTagChannelsColumns.TAG_ID
                + " WHERE t." + ChannelDatabaseHelper.TableTagsColumns.ID + " = \"" + tagId + "\"", null)){
            List<ChannelTag> result = new ArrayList<>();
            String currentTagId = null;
            ChannelTag channelTag = null;
            List<String> channelIds = null;
            while (cursor.moveToNext()){
                String tagIdFound = DatabaseUtil.getString(cursor, ChannelDatabaseHelper.TableTagsColumns.ID);
                String ichatId = DatabaseUtil.getString(cursor, ChannelDatabaseHelper.TableTagsColumns.ICHAT_ID);
                String name = DatabaseUtil.getString(cursor, ChannelDatabaseHelper.TableTagsColumns.NAME);
                Integer order = DatabaseUtil.getInteger(cursor, ChannelDatabaseHelper.TableTagsColumns.RAW_ORDER);
                String channelIchatId = DatabaseUtil.getString(cursor, ChannelDatabaseHelper.TableTagChannelsColumns.ICHAT_ID);
                if (currentTagId == null) {
                    currentTagId = tagIdFound;
                    channelIds = new ArrayList<>();
                    if(channelIchatId != null) channelIds.add(channelIchatId);
                    channelTag = new ChannelTag(tagIdFound, ichatId, name, order == null ? -1 : order, null);
                } else {
                    if (currentTagId.equals(tagIdFound)) {
                        if(channelIchatId != null) channelIds.add(channelIchatId);
                    } else {
                        channelTag.setChannelIchatIdList(new ArrayList<>(channelIds));
                        result.add(channelTag);
                        channelIds = new ArrayList<>();
                        currentTagId = tagIdFound;
                        if(channelIchatId != null) channelIds.add(channelIchatId);
                        channelTag = new ChannelTag(tagIdFound, ichatId, name, order == null ? -1 : order, null);
                    }
                }
            }
            if(channelTag != null) {
                channelTag.setChannelIchatIdList(new ArrayList<>(channelIds));
                result.add(channelTag);
            }
            return result.get(0);
        }finally {
            releaseDatabaseIfUnused();
        }
    }

    public boolean updateRecentBroadcastMedias(List<RecentBroadcastMedia> recentBroadcastMedias){
        AtomicBoolean result = new AtomicBoolean(true);
        openDatabaseIfClosed();
        getDatabase().delete(ChannelDatabaseHelper.DatabaseInfo.TABLE_NAME_RECENT_BROADCAST_MEDIAS,
                ChannelDatabaseHelper.TableRecentBroadcastMedias.ICHAT_ID + " = ?",
                new String[]{recentBroadcastMedias.get(0).getIchatId()});

        try{
            recentBroadcastMedias.forEach(recentBroadcastMedia -> {
                ContentValues values = new ContentValues();
                values.put(ChannelDatabaseHelper.TableRecentBroadcastMedias.ICHAT_ID, recentBroadcastMedia.getIchatId());
                values.put(ChannelDatabaseHelper.TableRecentBroadcastMedias.BROADCAST_ID, recentBroadcastMedia.getBroadcastId());
                values.put(ChannelDatabaseHelper.TableRecentBroadcastMedias.MEDIA_ID, recentBroadcastMedia.getMediaId());
                values.put(ChannelDatabaseHelper.TableRecentBroadcastMedias.TYPE, recentBroadcastMedia.getType());
                values.put(ChannelDatabaseHelper.TableRecentBroadcastMedias.EXTENSION, recentBroadcastMedia.getExtension());
                values.put(ChannelDatabaseHelper.TableRecentBroadcastMedias.VIDEO_DURATION, recentBroadcastMedia.getVideoDuration());
                values.put(ChannelDatabaseHelper.TableRecentBroadcastMedias.INDEX, recentBroadcastMedia.getIndex());
                long id = getDatabase().insertWithOnConflict(ChannelDatabaseHelper.DatabaseInfo.TABLE_NAME_RECENT_BROADCAST_MEDIAS, null,
                        values, SQLiteDatabase.CONFLICT_IGNORE);
                if (id == -1) {
                    result.set(false);
                }
            });
        }finally {
            releaseDatabaseIfUnused();
        }
        return result.get();
    }

    @SuppressLint("Range")
    public List<RecentBroadcastMedia> findRecentBroadcastMedias(String ichatId) {
        List<RecentBroadcastMedia> recentBroadcastMedias = new ArrayList<>();
        openDatabaseIfClosed();
        try(Cursor cursor = getDatabase().query(
                ChannelDatabaseHelper.DatabaseInfo.TABLE_NAME_RECENT_BROADCAST_MEDIAS,
                null,
                ChannelDatabaseHelper.TableRecentBroadcastMedias.ICHAT_ID + " = ?",
                new String[]{ichatId},
                null,
                null,
                ChannelDatabaseHelper.TableRecentBroadcastMedias.INDEX
        )) {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String ichatId1 = cursor.getString(cursor.getColumnIndex(ChannelDatabaseHelper.TableRecentBroadcastMedias.ICHAT_ID));
                    String broadcastId = cursor.getString(cursor.getColumnIndex(ChannelDatabaseHelper.TableRecentBroadcastMedias.BROADCAST_ID));
                    String mediaId = cursor.getString(cursor.getColumnIndex(ChannelDatabaseHelper.TableRecentBroadcastMedias.MEDIA_ID));
                    int type = cursor.getInt(cursor.getColumnIndex(ChannelDatabaseHelper.TableRecentBroadcastMedias.TYPE));
                    String extension = cursor.getString(cursor.getColumnIndex(ChannelDatabaseHelper.TableRecentBroadcastMedias.EXTENSION));
                    long videoDuration = cursor.getLong(cursor.getColumnIndex(ChannelDatabaseHelper.TableRecentBroadcastMedias.VIDEO_DURATION));
                    int index = cursor.getInt(cursor.getColumnIndex(ChannelDatabaseHelper.TableRecentBroadcastMedias.RAW_INDEX));
                    recentBroadcastMedias.add(new RecentBroadcastMedia(ichatId1, broadcastId, mediaId, type, extension, videoDuration, index));
                } while (cursor.moveToNext());
            }
        } finally {
            releaseDatabaseIfUnused();
        }
        recentBroadcastMedias.sort(Comparator.comparingInt(RecentBroadcastMedia::getIndex));
        return recentBroadcastMedias;
    }

}
