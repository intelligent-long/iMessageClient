package com.longx.intelligent.android.ichat2.data;

import android.content.Context;
import android.util.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.longx.intelligent.android.ichat2.da.database.manager.ChatMessageDatabaseManager;
import com.longx.intelligent.android.ichat2.da.privatefile.PrivateFilesAccessor;
import com.longx.intelligent.android.ichat2.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.ichat2.media.helper.MediaHelper;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.ChatApiCaller;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.ichat2.yier.ChatMessageUpdateYier;
import com.longx.intelligent.android.ichat2.yier.GlobalYiersHolder;
import com.longx.intelligent.android.ichat2.yier.ResultsYier;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by LONG on 2024/5/13 at 12:08 AM.
 */
public class ChatMessage {
    public static void mainDoOnNewChatMessage(ChatMessage chatMessage, Context context, ResultsYier resultsYier){
        if(chatMessage.getType() == ChatMessage.TYPE_IMAGE){
            ChatApiCaller.fetchChatMessageImage(null, chatMessage.imageId, new RetrofitApiCaller.BaseCommonYier<ResponseBody>(context){
                @Override
                public void ok(ResponseBody data, Response<ResponseBody> row, Call<ResponseBody> call) {
                    super.ok(data, row, call);
                    try {
                        byte[] bytes = data.bytes();
                        chatMessage.setImageBytes(bytes);
                        String imageFilePath = PrivateFilesAccessor.ChatImage.save(context, chatMessage);
                        chatMessage.setImageFilePath(imageFilePath);
                        Size imageSize = MediaHelper.getImageSize(bytes);
                        chatMessage.setImageSize(imageSize);
                        commonDoOfMainDoOnNewChatMessage(chatMessage, context);
                        resultsYier.onResults();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }else {
            commonDoOfMainDoOnNewChatMessage(chatMessage, context);
            resultsYier.onResults();
        }
    }

    private static void commonDoOfMainDoOnNewChatMessage(ChatMessage chatMessage, Context context) {
        ChatMessage.determineShowTime(chatMessage, context);
        String other = chatMessage.getOther(context);
        ChatMessageDatabaseManager chatMessageDatabaseManager = ChatMessageDatabaseManager.getInstanceOrInitAndGet(context, other);
        boolean success = chatMessageDatabaseManager.insertOrIgnore(chatMessage);
        if (success && chatMessage.isShowTime()) {
            SharedPreferencesAccessor.ChatMessageTimeShowing.saveLastShowingTime(context, other, chatMessage.getTime());
        }
    }

    private static void determineShowTime(ChatMessage chatMessage, Context context) {
        chatMessage.showTime = SharedPreferencesAccessor.ChatMessageTimeShowing.isShowTime(context, chatMessage.getOther(context), chatMessage.getTime());
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
    private String imageId;
    private String imageExtension;

    @JsonIgnore
    private Boolean showTime;
    @JsonIgnore
    private Boolean viewed;
    @JsonIgnore
    private String imageFilePath;
    @JsonIgnore
    private Size imageSize;
    @JsonIgnore
    private byte[] imageBytes;

    public ChatMessage() {
    }

    public ChatMessage(int type, String uuid, String from, String to, Date time, String text, String imageId, String imageExtension) {
        this.type = type;
        this.uuid = uuid;
        this.from = from;
        this.to = to;
        this.text = text;
        this.time = time;
        this.imageId = imageId;
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

    public String getImageId() {
        return imageId;
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

    public byte[] getImageBytes() {
        return imageBytes;
    }

    public void setImageBytes(byte[] imageBytes) {
        this.imageBytes = imageBytes;
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
