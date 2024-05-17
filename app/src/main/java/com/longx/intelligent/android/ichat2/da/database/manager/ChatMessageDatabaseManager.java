package com.longx.intelligent.android.ichat2.da.database.manager;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
        ChatMessageDatabaseHelper chatMessageDatabaseHelper = new ChatMessageDatabaseHelper(context, channelIchatId, SharedPreferencesAccessor.UserInfoPref.getCurrentUserInfo(context).getIchatId());
        ChatMessageDatabaseManager messageDatabaseManager = new ChatMessageDatabaseManager(chatMessageDatabaseHelper);
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
                ChatMessage chatMessage = new ChatMessage(type == null ? -1 : type, uuid, from, to, text, time);
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

}
