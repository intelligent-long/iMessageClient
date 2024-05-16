package com.longx.intelligent.android.ichat2.data;

import android.content.Context;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.longx.intelligent.android.ichat2.activity.ChatActivity;
import com.longx.intelligent.android.ichat2.da.database.manager.ChatMessagesDatabaseManager;
import com.longx.intelligent.android.ichat2.da.sharedpref.SharedPreferencesAccessor;

import java.util.Date;

/**
 * Created by LONG on 2024/5/13 at 12:08 AM.
 */
public class ChatMessage {
    public static final int TYPE_TEXT = 0;
    public static final int TYPE_NOTICE = 1;
    public static final int TYPE_IMAGE = 2;
    public static final int TYPE_VOICE = 3;
    private int type;
    private String uuid;
    private String from;
    private String to;
    private String text;
    private Date time;

    @JsonIgnore
    private Boolean showTime;

    public ChatMessage() {
    }

    public ChatMessage(int type, String uuid, String from, String to, String text, Date time) {
        this.type = type;
        this.uuid = uuid;
        this.from = from;
        this.to = to;
        this.text = text;
        this.time = time;
    }

    public int getType() {
        return type;
    }

    public String getUuid() {
        return uuid;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getText() {
        return text;
    }

    public Date getTime() {
        return time;
    }

    public boolean isSelfSender(Context context){
        return SharedPreferencesAccessor.UserInfoPref.getCurrentUserInfo(context).getIchatId().equals(from);
    }

    public String getOther(Context context){
        if(isSelfSender(context)){
            return getTo();
        }else {
            return getFrom();
        }
    }

    public boolean isShowTime() {
        if(showTime == null) throw new RuntimeException();
        return showTime;
    }

    public void setShowTime(Boolean showTime) {
        this.showTime = showTime;
    }

    public static void determineShowTime(ChatMessage chatMessage, Context context) {
        chatMessage.showTime = SharedPreferencesAccessor.ChatMessageTimeShowing.isShowTime(context, chatMessage.getOther(context), chatMessage.getTime());
    }

    public static void insertToDatabase(ChatMessage chatMessage, Context context){
        String other = chatMessage.getOther(context);
        ChatMessagesDatabaseManager chatMessagesDatabaseManager = ChatMessagesDatabaseManager.getInstanceOrInitAndGet(context, other);
        boolean success = chatMessagesDatabaseManager.insertOrIgnore(chatMessage);
        if(success && chatMessage.isShowTime()){
            SharedPreferencesAccessor.ChatMessageTimeShowing.saveLastShowingTime(context, other, chatMessage.getTime());
        }
    }
}
