package com.longx.intelligent.android.ichat2.data;

import android.content.Context;
import android.util.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.longx.intelligent.android.ichat2.da.database.manager.ChatMessageDatabaseManager;
import com.longx.intelligent.android.ichat2.da.privatefile.PrivateFilesAccessor;
import com.longx.intelligent.android.ichat2.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.ichat2.media.helper.MediaHelper;
import com.longx.intelligent.android.ichat2.util.ErrorLogger;
import com.longx.intelligent.android.ichat2.yier.GlobalYiersHolder;
import com.longx.intelligent.android.ichat2.yier.OpenedChatsUpdateYier;

import org.apache.commons.codec.binary.Base64;

import java.util.Date;
import java.util.Objects;

/**
 * Created by LONG on 2024/5/13 at 12:08 AM.
 */
public class ChatMessage {
    public static void determineShowTime(ChatMessage chatMessage, Context context) {
        chatMessage.showTime = SharedPreferencesAccessor.ChatMessageTimeShowing.isShowTime(context, chatMessage.getOther(context), chatMessage.getTime());
    }

    public static void insertToDatabaseAndDetermineShowTime(ChatMessage chatMessage, Context context){
        if(chatMessage.getType() == ChatMessage.TYPE_IMAGE){
            String imageFilePath = PrivateFilesAccessor.ChatImage.save(context, chatMessage);
            chatMessage.setImageFilePath(imageFilePath);
            Size imageSize = MediaHelper.getImageSize(Base64.decodeBase64(chatMessage.getImageBase64()));
            chatMessage.setImageSize(imageSize);
        }
        ChatMessage.determineShowTime(chatMessage, context);
        String other = chatMessage.getOther(context);
        ChatMessageDatabaseManager chatMessageDatabaseManager = ChatMessageDatabaseManager.getInstanceOrInitAndGet(context, other);
        boolean success = chatMessageDatabaseManager.insertOrIgnore(chatMessage);
        if(success && chatMessage.isShowTime()){
            SharedPreferencesAccessor.ChatMessageTimeShowing.saveLastShowingTime(context, other, chatMessage.getTime());
        }
    }

    public static final int TYPE_TEXT = 0;
    public static final int TYPE_VOICE = 1;
    public static final int TYPE_IMAGE = 2;
    public static final int TYPE_VIDEO = 3;
    public static final int TYPE_FILE = 4;
    public static final int TYPE_NOTICE = 5;
    private int type;
    private String uuid;
    private String from;
    private String to;
    private Date time;
    private String text;
    private String imageBase64;
    private String imageExtension;

    @JsonIgnore
    private Boolean showTime;
    @JsonIgnore
    private Boolean viewed;
    @JsonIgnore
    private String imageFilePath;
    @JsonIgnore
    private Size imageSize;

    public ChatMessage() {
    }

    public ChatMessage(int type, String uuid, String from, String to, Date time, String text, String imageBase64, String imageExtension) {
        this.type = type;
        this.uuid = uuid;
        this.from = from;
        this.to = to;
        this.text = text;
        this.time = time;
        this.imageBase64 = imageBase64;
        this.imageExtension = imageExtension;
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

    public String getImageBase64() {
        return imageBase64;
    }

    public String getImageExtension() {
        return imageExtension;
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

    public String getImageFilePath() {
        return imageFilePath;
    }

    public void setImageFilePath(String imageFilePath) {
        this.imageFilePath = imageFilePath;
    }

    public Size getImageSize() {
        return imageSize;
    }

    public void setImageSize(Size imageSize) {
        this.imageSize = imageSize;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatMessage that = (ChatMessage) o;
        return type == that.type && Objects.equals(uuid, that.uuid) && Objects.equals(from, that.from) && Objects.equals(to, that.to) && Objects.equals(time, that.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, uuid, from, to, time);
    }
}
