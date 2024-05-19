package com.longx.intelligent.android.ichat2.data;

import android.content.Context;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.longx.intelligent.android.ichat2.da.database.manager.ChatMessageDatabaseManager;
import com.longx.intelligent.android.ichat2.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.ichat2.util.ErrorLogger;
import com.longx.intelligent.android.ichat2.yier.GlobalYiersHolder;
import com.longx.intelligent.android.ichat2.yier.OpenedChatsUpdateYier;

import java.util.Date;
import java.util.Objects;

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
    @JsonIgnore
    private Boolean viewed;

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

    public Boolean isViewed() {
        return viewed;
    }

    public void setViewed(Boolean viewed) {
        this.viewed = viewed;
    }

    public static void determineShowTime(ChatMessage chatMessage, Context context) {
        chatMessage.showTime = SharedPreferencesAccessor.ChatMessageTimeShowing.isShowTime(context, chatMessage.getOther(context), chatMessage.getTime());
    }

    public static void insertToDatabaseAndDetermineShowTime(ChatMessage chatMessage, Context context){
        ChatMessage.determineShowTime(chatMessage, context);
        String other = chatMessage.getOther(context);
        ChatMessageDatabaseManager chatMessageDatabaseManager = ChatMessageDatabaseManager.getInstanceOrInitAndGet(context, other);
        boolean success = chatMessageDatabaseManager.insertOrIgnore(chatMessage);
        if(success && chatMessage.isShowTime()){
            SharedPreferencesAccessor.ChatMessageTimeShowing.saveLastShowingTime(context, other, chatMessage.getTime());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatMessage that = (ChatMessage) o;
        return uuid.equals(that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }
}
