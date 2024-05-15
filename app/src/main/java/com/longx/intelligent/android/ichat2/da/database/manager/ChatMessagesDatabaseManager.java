package com.longx.intelligent.android.ichat2.da.database.manager;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.longx.intelligent.android.ichat2.da.database.helper.ChatMessagesDatabaseHelper;
import com.longx.intelligent.android.ichat2.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.ichat2.data.ChatMessage;
import com.longx.intelligent.android.ichat2.util.DatabaseUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by LONG on 2024/5/13 at 2:28 AM.
 */
public class ChatMessagesDatabaseManager extends BaseDatabaseManager{

    private static final Map<String, ChatMessagesDatabaseManager> instanceMap = new HashMap<>();
    public ChatMessagesDatabaseManager(SQLiteOpenHelper helper) {
        super(helper);
    }

    private synchronized static void initInstance(Context context, String channelIchatId) {
        ChatMessagesDatabaseHelper chatMessagesDatabaseHelper = new ChatMessagesDatabaseHelper(context, channelIchatId, SharedPreferencesAccessor.UserInfoPref.getCurrentUserInfo(context).getIchatId());
        ChatMessagesDatabaseManager messageDatabaseManager = new ChatMessagesDatabaseManager(chatMessagesDatabaseHelper);
        instanceMap.put(channelIchatId, messageDatabaseManager);
    }

    private static void checkAndInitInstance(Context context, String channelIchatId) {
        synchronized (ChatMessagesDatabaseManager.class) {
            if (instanceMap.get(channelIchatId) == null) {
                ChatMessagesDatabaseManager.initInstance(context, channelIchatId);
            }
        }
    }

    public static synchronized ChatMessagesDatabaseManager getInstanceOrInitAndGet(Context context, String channelIchatId) {
        checkAndInitInstance(context, channelIchatId);
        return instanceMap.get(channelIchatId);
    }

    public boolean insertOrIgnore(ChatMessage chatMessage){
        openDatabaseIfClosed();
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(ChatMessagesDatabaseHelper.TableChannelChatMessagesColumns.TYPE, chatMessage.getType());
            contentValues.put(ChatMessagesDatabaseHelper.TableChannelChatMessagesColumns.UUID, chatMessage.getUuid());
            contentValues.put(ChatMessagesDatabaseHelper.TableChannelChatMessagesColumns.FROM, chatMessage.getFrom());
            contentValues.put(ChatMessagesDatabaseHelper.TableChannelChatMessagesColumns.TO, chatMessage.getTo());
            contentValues.put(ChatMessagesDatabaseHelper.TableChannelChatMessagesColumns.TEXT, chatMessage.getText());
            contentValues.put(ChatMessagesDatabaseHelper.TableChannelChatMessagesColumns.TIME, chatMessage.getTime().getTime());
            contentValues.put(ChatMessagesDatabaseHelper.TableChannelChatMessagesColumns.SHOW_TIME, chatMessage.isShowTime());
            long id = getDatabase().insertWithOnConflict(((ChatMessagesDatabaseHelper)getHelper()).getTableName(), null, contentValues, SQLiteDatabase.CONFLICT_IGNORE);
            return id != -1;
        }finally {
            releaseDatabaseIfUnused();
        }
    }

    @SuppressLint("Range")
    public List<ChatMessage> findLimit(int startIndex, int number, boolean desc){
        List<ChatMessage> result = new ArrayList<>();
        openDatabaseIfClosed();
        try(Cursor cursor = getDatabase().query(((ChatMessagesDatabaseHelper)getHelper()).getTableName(), null, null, null, null,
                null, desc ? ChatMessagesDatabaseHelper.TableChannelChatMessagesColumns.TIME + " DESC" : ChatMessagesDatabaseHelper.TableChannelChatMessagesColumns.TIME,
                startIndex + "," + number)) {
            while (cursor.moveToNext()) {
                Integer type = DatabaseUtil.getInteger(cursor, ChatMessagesDatabaseHelper.TableChannelChatMessagesColumns.TYPE);
                String uuid = DatabaseUtil.getString(cursor, ChatMessagesDatabaseHelper.TableChannelChatMessagesColumns.UUID);
                String from = DatabaseUtil.getString(cursor, ChatMessagesDatabaseHelper.TableChannelChatMessagesColumns.REAL_FROM);
                String to = DatabaseUtil.getString(cursor, ChatMessagesDatabaseHelper.TableChannelChatMessagesColumns.REAL_TO);
                String text = DatabaseUtil.getString(cursor, ChatMessagesDatabaseHelper.TableChannelChatMessagesColumns.TEXT);
                Date time = DatabaseUtil.getTime(cursor, ChatMessagesDatabaseHelper.TableChannelChatMessagesColumns.TIME);
                Boolean showTime = DatabaseUtil.getBoolean(cursor, ChatMessagesDatabaseHelper.TableChannelChatMessagesColumns.SHOW_TIME);
                ChatMessage chatMessage = new ChatMessage(type == null ? -1 : type, uuid, from, to, text, time);
                chatMessage.setShowTime(Boolean.TRUE.equals(showTime));
                result.add(chatMessage);
            }
        } finally {
            releaseDatabaseIfUnused();
        }
        return result;
    }

}
