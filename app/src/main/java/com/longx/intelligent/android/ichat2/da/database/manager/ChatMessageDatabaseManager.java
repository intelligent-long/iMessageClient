package com.longx.intelligent.android.ichat2.da.database.manager;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Size;

import com.longx.intelligent.android.ichat2.da.database.helper.ChatMessageDatabaseHelper;
import com.longx.intelligent.android.ichat2.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.ichat2.data.ChatMessage;
import com.longx.intelligent.android.ichat2.util.DatabaseUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by LONG on 2024/5/13 at 2:28 AM.
 */
public class ChatMessageDatabaseManager extends BaseDatabaseManager{

    private static final Map<String, ChatMessageDatabaseManager> instanceMap = new HashMap<>();
    public ChatMessageDatabaseManager(SQLiteOpenHelper helper) {
        super(helper);
    }

    private synchronized static void initInstance(Context context, String channelIchatId) {
        ChatMessageDatabaseHelper chatMessageDatabaseHelper = new ChatMessageDatabaseHelper(context, channelIchatId, SharedPreferencesAccessor.UserProfilePref.getCurrentUserProfile(context).getIchatId());
        ChatMessageDatabaseManager messageDatabaseManager = new ChatMessageDatabaseManager(chatMessageDatabaseHelper);
        messageDatabaseManager.openDatabaseIfClosed();
        try {
            chatMessageDatabaseHelper.onCreate(messageDatabaseManager.getDatabase());
        }finally {
            messageDatabaseManager.releaseDatabaseIfUnused();
        }
        instanceMap.put(channelIchatId, messageDatabaseManager);
    }

    private static void checkAndInitInstance(Context context, String channelIchatId) {
        synchronized (ChatMessageDatabaseManager.class) {
            if (instanceMap.get(channelIchatId) == null) {
                ChatMessageDatabaseManager.initInstance(context, channelIchatId);
            }
        }
    }

    public static synchronized ChatMessageDatabaseManager getInstanceOrInitAndGet(Context context, String channelIchatId) {
        checkAndInitInstance(context, channelIchatId);
        return instanceMap.get(channelIchatId);
    }

    public boolean insertOrIgnore(ChatMessage chatMessage){
        openDatabaseIfClosed();
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(ChatMessageDatabaseHelper.TableChannelChatMessagesColumns.TYPE, chatMessage.getType());
            contentValues.put(ChatMessageDatabaseHelper.TableChannelChatMessagesColumns.UUID, chatMessage.getUuid());
            contentValues.put(ChatMessageDatabaseHelper.TableChannelChatMessagesColumns.FROM, chatMessage.getFrom());
            contentValues.put(ChatMessageDatabaseHelper.TableChannelChatMessagesColumns.TO, chatMessage.getTo());
            contentValues.put(ChatMessageDatabaseHelper.TableChannelChatMessagesColumns.TEXT, chatMessage.getText());
            contentValues.put(ChatMessageDatabaseHelper.TableChannelChatMessagesColumns.TIME, chatMessage.getTime().getTime());
            contentValues.put(ChatMessageDatabaseHelper.TableChannelChatMessagesColumns.SHOW_TIME, chatMessage.isShowTime());
            contentValues.put(ChatMessageDatabaseHelper.TableChannelChatMessagesColumns.VIEWED, chatMessage.isViewed());
            contentValues.put(ChatMessageDatabaseHelper.TableChannelChatMessagesColumns.IMAGE_FILE_PATH, chatMessage.getImageFilePath());
            contentValues.put(ChatMessageDatabaseHelper.TableChannelChatMessagesColumns.IMAGE_EXTENSION, chatMessage.getImageExtension());
            contentValues.put(ChatMessageDatabaseHelper.TableChannelChatMessagesColumns.IMAGE_WIDTH, chatMessage.getImageSize() == null ? null : chatMessage.getImageSize().getWidth());
            contentValues.put(ChatMessageDatabaseHelper.TableChannelChatMessagesColumns.IMAGE_HEIGHT, chatMessage.getImageSize() == null ? null : chatMessage.getImageSize().getHeight());
            long id = getDatabase().insertWithOnConflict(((ChatMessageDatabaseHelper)getHelper()).getTableName(), null, contentValues, SQLiteDatabase.CONFLICT_IGNORE);
            return id != -1;
        }finally {
            releaseDatabaseIfUnused();
        }
    }

    @SuppressLint("Range")
    public List<ChatMessage> findLimit(int startIndex, int number, boolean desc){
        List<ChatMessage> result = new ArrayList<>();
        openDatabaseIfClosed();
        try(Cursor cursor = getDatabase().query(((ChatMessageDatabaseHelper)getHelper()).getTableName(), null, null, null, null,
                null, desc ? ChatMessageDatabaseHelper.TableChannelChatMessagesColumns.TIME + " DESC" : ChatMessageDatabaseHelper.TableChannelChatMessagesColumns.TIME,
                startIndex + "," + number)) {
            while (cursor.moveToNext()) {
                Integer type = DatabaseUtil.getInteger(cursor, ChatMessageDatabaseHelper.TableChannelChatMessagesColumns.TYPE);
                String uuid = DatabaseUtil.getString(cursor, ChatMessageDatabaseHelper.TableChannelChatMessagesColumns.UUID);
                String from = DatabaseUtil.getString(cursor, ChatMessageDatabaseHelper.TableChannelChatMessagesColumns.REAL_FROM);
                String to = DatabaseUtil.getString(cursor, ChatMessageDatabaseHelper.TableChannelChatMessagesColumns.REAL_TO);
                String text = DatabaseUtil.getString(cursor, ChatMessageDatabaseHelper.TableChannelChatMessagesColumns.TEXT);
                Date time = DatabaseUtil.getTime(cursor, ChatMessageDatabaseHelper.TableChannelChatMessagesColumns.TIME);
                Boolean showTime = DatabaseUtil.getBoolean(cursor, ChatMessageDatabaseHelper.TableChannelChatMessagesColumns.SHOW_TIME);
                Boolean viewed = DatabaseUtil.getBoolean(cursor, ChatMessageDatabaseHelper.TableChannelChatMessagesColumns.VIEWED);
                String imageFilePath = DatabaseUtil.getString(cursor, ChatMessageDatabaseHelper.TableChannelChatMessagesColumns.IMAGE_FILE_PATH);
                String imageExtension = DatabaseUtil.getString(cursor, ChatMessageDatabaseHelper.TableChannelChatMessagesColumns.IMAGE_EXTENSION);
                Integer imageWidth = DatabaseUtil.getInteger(cursor, ChatMessageDatabaseHelper.TableChannelChatMessagesColumns.IMAGE_WIDTH);
                Integer imageHeight = DatabaseUtil.getInteger(cursor, ChatMessageDatabaseHelper.TableChannelChatMessagesColumns.IMAGE_HEIGHT);
                ChatMessage chatMessage = new ChatMessage(type == null ? -1 : type, uuid, from, to, time, text, null, imageExtension);
                chatMessage.setImageFilePath(imageFilePath);
                chatMessage.setImageSize(new Size(imageWidth == null ? 0 : imageWidth, imageHeight == null ? 0 : imageHeight));
                chatMessage.setShowTime(Boolean.TRUE.equals(showTime));
                chatMessage.setViewed(viewed);
                result.add(chatMessage);
            }
        } finally {
            releaseDatabaseIfUnused();
        }
        return result;
    }

    public boolean setOneToViewed(String messageUuid){
        openDatabaseIfClosed();
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(ChatMessageDatabaseHelper.TableChannelChatMessagesColumns.VIEWED, true);
            int update = getDatabase().update(((ChatMessageDatabaseHelper) getHelper()).getTableName(), contentValues,
                    ChatMessageDatabaseHelper.TableChannelChatMessagesColumns.UUID + "=?", new String[]{messageUuid});
            return update == 1;
        }finally {
            releaseDatabaseIfUnused();
        }
    }

    public void setAllToViewed(){
        openDatabaseIfClosed();
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(ChatMessageDatabaseHelper.TableChannelChatMessagesColumns.VIEWED, true);
            getDatabase().update(((ChatMessageDatabaseHelper) getHelper()).getTableName(), contentValues,
                    "1=1", null);
        }finally {
            releaseDatabaseIfUnused();
        }
    }

    public int count(){
        openDatabaseIfClosed();
        int count = 0;
        String query = "SELECT COUNT(*) FROM " + ((ChatMessageDatabaseHelper) getHelper()).getTableName();
        try (Cursor cursor = getDatabase().rawQuery(query, null)){
            if (cursor != null && cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
        }finally {
            releaseDatabaseIfUnused();
        }
        return count;
    }

    public boolean existsByUuid(String uuid) {
        openDatabaseIfClosed();
        boolean exists = false;
        String query = "SELECT COUNT(*) FROM " + ((ChatMessageDatabaseHelper) getHelper()).getTableName() + " WHERE " + ChatMessageDatabaseHelper.TableChannelChatMessagesColumns.UUID + " = ?";
        try (Cursor cursor = getDatabase().rawQuery(query, new String[]{uuid})) {
            if (cursor != null && cursor.moveToFirst()) {
                exists = cursor.getInt(0) > 0;
            }
        } finally {
            releaseDatabaseIfUnused();
        }
        return exists;
    }

    public boolean delete(String uuid){
        openDatabaseIfClosed();
        try {
            int row = getDatabase().delete(((ChatMessageDatabaseHelper) getHelper()).getTableName(), ChatMessageDatabaseHelper.TableChannelChatMessagesColumns.UUID + "=?", new String[]{uuid});
            return row > 0;
        }finally {
            releaseDatabaseIfUnused();
        }
    }

    public ChatMessage findNextChatMessage(Date time){
        openDatabaseIfClosed();
        try(Cursor cursor = getDatabase().query(((ChatMessageDatabaseHelper) getHelper()).getTableName(), null,
                ChatMessageDatabaseHelper.TableChannelChatMessagesColumns.TIME + ">" + time.getTime(), null,
                null, null, ChatMessageDatabaseHelper.TableChannelChatMessagesColumns.TIME, 0 + "," + 1)) {
            if (cursor.moveToNext()) {
                Integer type = DatabaseUtil.getInteger(cursor, ChatMessageDatabaseHelper.TableChannelChatMessagesColumns.TYPE);
                String uuid = DatabaseUtil.getString(cursor, ChatMessageDatabaseHelper.TableChannelChatMessagesColumns.UUID);
                String from = DatabaseUtil.getString(cursor, ChatMessageDatabaseHelper.TableChannelChatMessagesColumns.REAL_FROM);
                String to = DatabaseUtil.getString(cursor, ChatMessageDatabaseHelper.TableChannelChatMessagesColumns.REAL_TO);
                String text = DatabaseUtil.getString(cursor, ChatMessageDatabaseHelper.TableChannelChatMessagesColumns.TEXT);
                Date timeFound = DatabaseUtil.getTime(cursor, ChatMessageDatabaseHelper.TableChannelChatMessagesColumns.TIME);
                Boolean showTime = DatabaseUtil.getBoolean(cursor, ChatMessageDatabaseHelper.TableChannelChatMessagesColumns.SHOW_TIME);
                Boolean viewed = DatabaseUtil.getBoolean(cursor, ChatMessageDatabaseHelper.TableChannelChatMessagesColumns.VIEWED);
                String imageFilePath = DatabaseUtil.getString(cursor, ChatMessageDatabaseHelper.TableChannelChatMessagesColumns.IMAGE_FILE_PATH);
                String imageExtension = DatabaseUtil.getString(cursor, ChatMessageDatabaseHelper.TableChannelChatMessagesColumns.IMAGE_EXTENSION);
                Integer imageWidth = DatabaseUtil.getInteger(cursor, ChatMessageDatabaseHelper.TableChannelChatMessagesColumns.IMAGE_WIDTH);
                Integer imageHeight = DatabaseUtil.getInteger(cursor, ChatMessageDatabaseHelper.TableChannelChatMessagesColumns.IMAGE_HEIGHT);
                ChatMessage chatMessage = new ChatMessage(type == null ? -1 : type, uuid, from, to, timeFound, text, null, imageExtension);
                chatMessage.setImageFilePath(imageFilePath);
                chatMessage.setImageSize(new Size(imageWidth == null ? 0 : imageWidth, imageHeight == null ? 0 : imageHeight));
                chatMessage.setShowTime(Boolean.TRUE.equals(showTime));
                chatMessage.setViewed(viewed);
                return chatMessage;
            }
        }finally {
            releaseDatabaseIfUnused();
        }
        return null;
    }

    public boolean updateShowTime(String uuid, boolean showTime){
        openDatabaseIfClosed();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ChatMessageDatabaseHelper.TableChannelChatMessagesColumns.SHOW_TIME, showTime);
        try {
            int updatedNum = getDatabase().update(((ChatMessageDatabaseHelper) getHelper()).getTableName(),
                    contentValues, ChatMessageDatabaseHelper.TableChannelChatMessagesColumns.UUID + "=?", new String[]{uuid});
            return updatedNum > 0;
        }finally {
            releaseDatabaseIfUnused();
        }
    }
}
