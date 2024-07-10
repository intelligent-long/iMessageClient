package com.longx.intelligent.android.ichat2.data;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Size;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.longx.intelligent.android.ichat2.da.database.manager.ChatMessageDatabaseManager;
import com.longx.intelligent.android.ichat2.da.privatefile.PrivateFilesAccessor;
import com.longx.intelligent.android.ichat2.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.ichat2.media.helper.MediaHelper;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.ChatApiCaller;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.ichat2.yier.ResultsYier;

import java.io.IOException;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by LONG on 2024/5/13 at 12:08 AM.
 */
public class ChatMessage implements Parcelable {
    private static final ExecutorService executorService = Executors.newFixedThreadPool(9);
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());

    public static void mainDoOnNewChatMessage(ChatMessage chatMessage, Context context, ResultsYier resultsYier){
        switch (chatMessage.getType()){
            case TYPE_IMAGE:{
                ChatApiCaller.fetchChatMessageImage(null, chatMessage.imageId, new RetrofitApiCaller.BaseCommonYier<ResponseBody>(context) {
                    @Override
                    public void ok(ResponseBody data, Response<ResponseBody> row, Call<ResponseBody> call) {
                        super.ok(data, row, call);
                        executorService.execute(() -> {
                            try {
                                byte[] bytes = data.bytes();
                                String imageFilePath = PrivateFilesAccessor.ChatImage.save(context, chatMessage, bytes);
                                chatMessage.setImageFilePath(imageFilePath);
                                Size imageSize = MediaHelper.getImageSize(bytes);
                                chatMessage.setImageSize(imageSize);
                                commonDoOfMainDoOnNewChatMessage(chatMessage, context);
                                mainHandler.post(resultsYier::onResults);
                            } catch (IOException e) {
                                mainHandler.post(() -> {
                                    throw new RuntimeException(e);
                                });
                            }
                        });
                    }
                });
                break;
            }
            case TYPE_FILE:{
                ChatApiCaller.fetchChatMessageFile(null, chatMessage.fileId, new RetrofitApiCaller.BaseCommonYier<ResponseBody>(context){
                    @Override
                    public void ok(ResponseBody data, Response<ResponseBody> row, Call<ResponseBody> call) {
                        super.ok(data, row, call);
                        executorService.execute(() -> {
                            try {
                                byte[] bytes = data.bytes();
                                String chatFileFilePath = PrivateFilesAccessor.ChatFile.save(context, chatMessage, bytes);
                                chatMessage.setFileFilePath(chatFileFilePath);
                                commonDoOfMainDoOnNewChatMessage(chatMessage, context);
                                mainHandler.post(resultsYier::onResults);
                            } catch (IOException e) {
                                mainHandler.post(() -> {
                                    throw new RuntimeException(e);
                                });
                            }
                        });
                    }
                });
                break;
            }
            case TYPE_VIDEO:{
                ChatApiCaller.fetchChatMessageVideo(null, chatMessage.videoId, new RetrofitApiCaller.BaseCommonYier<ResponseBody>(context) {
                    @Override
                    public void ok(ResponseBody data, Response<ResponseBody> row, Call<ResponseBody> call) {
                        super.ok(data, row, call);
                        executorService.execute(() -> {
                            try {
                                byte[] bytes = data.bytes();
                                String chatVideoFilePath = PrivateFilesAccessor.ChatVideo.save(context, chatMessage, bytes);
                                chatMessage.setVideoFilePath(chatVideoFilePath);
                                Size videoSize = MediaHelper.getVideoSize(chatVideoFilePath);
                                chatMessage.setVideoSize(videoSize);
                                commonDoOfMainDoOnNewChatMessage(chatMessage, context);
                                mainHandler.post(resultsYier::onResults);
                            } catch (IOException e) {
                                mainHandler.post(() -> {
                                    throw new RuntimeException(e);
                                });
                            }
                        });
                    }
                });
                break;
            }
            case TYPE_VOICE:{
                chatMessage.setVoiceListened(false);
                ChatApiCaller.fetchChatMessageVoice(null, chatMessage.voiceId, new RetrofitApiCaller.BaseCommonYier<ResponseBody>(context) {
                    @Override
                    public void ok(ResponseBody data, Response<ResponseBody> row, Call<ResponseBody> call) {
                        super.ok(data, row, call);
                        executorService.execute(() -> {
                            try {
                                byte[] bytes = data.bytes();
                                String chatVoiceFilePath = PrivateFilesAccessor.ChatVoice.save(context, chatMessage, bytes);
                                chatMessage.setVoiceFilePath(chatVoiceFilePath);
                                commonDoOfMainDoOnNewChatMessage(chatMessage, context);
                                mainHandler.post(resultsYier::onResults);
                            } catch (IOException e) {
                                mainHandler.post(() -> {
                                    throw new RuntimeException(e);
                                });
                            }
                        });
                    }
                });
                break;
            }
            default:{
                commonDoOfMainDoOnNewChatMessage(chatMessage, context);
                resultsYier.onResults();
                break;
            }
        }
    }

    private static synchronized void commonDoOfMainDoOnNewChatMessage(ChatMessage chatMessage, Context context) {
        String other = chatMessage.getOther(context);
        ChatMessageDatabaseManager chatMessageDatabaseManager = ChatMessageDatabaseManager.getInstanceOrInitAndGet(context, other);
        chatMessageDatabaseManager.insertOrIgnore(chatMessage);
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
    private String fileName;
    private String imageId;
    private String fileId;
    private String videoId;
    private String voiceId;

    @JsonIgnore
    private Boolean showTime;
    @JsonIgnore
    private Boolean viewed;
    @JsonIgnore
    private String imageFilePath;
    @JsonIgnore
    private Size imageSize;
    @JsonIgnore
    private String fileFilePath;
    @JsonIgnore
    private String videoFilePath;
    @JsonIgnore
    private Size videoSize;
    @JsonIgnore
    private String voiceFilePath;
    @JsonIgnore
    private Boolean voiceListened;

    public ChatMessage() {
    }

    public ChatMessage(int type, String uuid, String from, String to, Date time, String text, String fileName,
                       String imageId, String fileId, String videoId, String voiceId) {
        this.type = type;
        this.uuid = uuid;
        this.from = from;
        this.to = to;
        this.time = time;
        this.text = text;
        this.fileName = fileName;
        this.imageId = imageId;
        this.fileId = fileId;
        this.videoId = videoId;
        this.voiceId = voiceId;
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

    public Date getTime() {
        return time;
    }

    public String getText() {
        return text;
    }

    public String getFileName() {
        return fileName;
    }

    public String getImageId() {
        return imageId;
    }

    public String getFileId() {
        return fileId;
    }

    public String getVideoId() {
        return videoId;
    }

    public String getVoiceId() {
        return voiceId;
    }

    public boolean isSelfSender(Context context){
        return SharedPreferencesAccessor.UserProfilePref.getCurrentUserProfile(context).getIchatId().equals(from);
    }

    public String getOther(Context context){
        if(isSelfSender(context)){
            return getTo();
        }else {
            return getFrom();
        }
    }

    public Boolean isShowTime() {
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

    public String getFileFilePath() {
        return fileFilePath;
    }

    public void setFileFilePath(String fileFilePath) {
        this.fileFilePath = fileFilePath;
    }

    public Size getImageSize() {
        return imageSize;
    }

    public void setImageSize(Size imageSize) {
        this.imageSize = imageSize;
    }

    public Size getVideoSize() {
        return videoSize;
    }

    public void setVideoSize(Size videoSize) {
        this.videoSize = videoSize;
    }

    public String getVideoFilePath() {
        return videoFilePath;
    }

    public void setVideoFilePath(String videoFilePath) {
        this.videoFilePath = videoFilePath;
    }

    public String getVoiceFilePath() {
        return voiceFilePath;
    }

    public void setVoiceFilePath(String voiceFilePath) {
        this.voiceFilePath = voiceFilePath;
    }

    public Boolean isVoiceListened() {
        return voiceListened;
    }

    public void setVoiceListened(Boolean voiceListened) {
        this.voiceListened = voiceListened;
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

    protected ChatMessage(Parcel in) {
        type = in.readInt();
        uuid = in.readString();
        from = in.readString();
        to = in.readString();
        time = new Date(in.readLong());
        text = in.readString();
        fileName = in.readString();
        imageId = in.readString();
        fileId = in.readString();
        videoId = in.readString();
        voiceId = in.readString();
        byte tmpShowTime = in.readByte();
        showTime = tmpShowTime == 0 ? null : tmpShowTime == 1;
        byte tmpViewed = in.readByte();
        viewed = tmpViewed == 0 ? null : tmpViewed == 1;
        if(type == TYPE_IMAGE) {
            imageFilePath = in.readString();
            imageSize = in.readSize();
        }else if(type == TYPE_FILE){
            fileFilePath = in.readString();
        }else if(type == TYPE_VIDEO){
            videoFilePath = in.readString();
        }else if(type == TYPE_VOICE){
            byte tmpVoiceListened = in.readByte();
            voiceListened = tmpVoiceListened == 0 ? null : tmpVoiceListened == 1;
        }
    }

    public static final Creator<ChatMessage> CREATOR = new Creator<ChatMessage>() {
        @Override
        public ChatMessage createFromParcel(Parcel in) {
            return new ChatMessage(in);
        }

        @Override
        public ChatMessage[] newArray(int size) {
            return new ChatMessage[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(type);
        dest.writeString(uuid);
        dest.writeString(from);
        dest.writeString(to);
        dest.writeLong(time.getTime());
        dest.writeString(text);
        dest.writeString(fileName);
        dest.writeString(imageId);
        dest.writeString(fileId);
        dest.writeString(videoId);
        dest.writeString(voiceId);
        dest.writeByte((byte) (showTime == null ? 0 : showTime ? 1 : 2));
        dest.writeByte((byte) (viewed == null ? 0 : viewed ? 1 : 2));
        if(type == TYPE_IMAGE) {
            dest.writeString(imageFilePath);
            dest.writeSize(imageSize);
        }else if(type == TYPE_FILE) {
            dest.writeString(fileFilePath);
        }else if(type == TYPE_VIDEO){
            dest.writeString(videoFilePath);
        }else if(type == TYPE_VOICE){
            dest.writeByte((byte) (voiceListened == null ? 0 : voiceListened ? 1 : 2));
        }
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "type=" + type +
                ", uuid='" + uuid + '\'' +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", time=" + time +
                ", text='" + text + '\'' +
                ", fileName='" + fileName + '\'' +
                ", imageId='" + imageId + '\'' +
                ", fileId='" + fileId + '\'' +
                ", videoId='" + videoId + '\'' +
                ", voiceId='" + voiceId + '\'' +
                ", showTime=" + showTime +
                ", viewed=" + viewed +
                ", imageFilePath='" + imageFilePath + '\'' +
                ", imageSize=" + imageSize +
                ", fileFilePath='" + fileFilePath + '\'' +
                ", videoFilePath='" + videoFilePath + '\'' +
                ", videoSize=" + videoSize +
                ", voiceFilePath='" + voiceFilePath + '\'' +
                ", voiceListened=" + voiceListened +
                '}';
    }
}
