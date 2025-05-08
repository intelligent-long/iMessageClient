package com.longx.intelligent.android.imessage.data;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Size;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.longx.intelligent.android.imessage.behaviorcomponents.MessageDisplayer;
import com.longx.intelligent.android.imessage.da.database.manager.ChatMessageDatabaseManager;
import com.longx.intelligent.android.imessage.da.privatefile.PrivateFilesAccessor;
import com.longx.intelligent.android.imessage.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.imessage.media.helper.MediaHelper;
import com.longx.intelligent.android.imessage.net.retrofit.caller.ChatApiCaller;
import com.longx.intelligent.android.imessage.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.imessage.util.ErrorLogger;
import com.longx.intelligent.android.imessage.yier.ChatMessagesUpdateYier;
import com.longx.intelligent.android.imessage.yier.GlobalYiersHolder;
import com.longx.intelligent.android.imessage.yier.ResultsYier;

import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by LONG on 2024/5/13 at 12:08 AM.
 */
public class ChatMessage implements Parcelable {
    private static final ExecutorService executorService = Executors.newCachedThreadPool();
    private static final Set<String> FULL_CONTENT_GETTING_MESSAGE_UUID_SET = new HashSet<>();

    public static void mainDoOnNewMessage(ChatMessage newMessage, Context context, ResultsYier resultsYier){
        newMessage.setViewed(false);
        newMessage.setFullContentGot(false);
        ChatMessageDatabaseManager chatMessageDatabaseManager = ChatMessageDatabaseManager.getInstanceOrInitAndGet(context, newMessage.getOther(context));
        chatMessageDatabaseManager.insertOrIgnore(newMessage);
        FULL_CONTENT_GETTING_MESSAGE_UUID_SET.add(newMessage.uuid);
        GlobalYiersHolder.getYiers(ChatMessagesUpdateYier.class).ifPresent(chatMessageUpdateYiers -> {
            chatMessageUpdateYiers.forEach(chatMessagesUpdateYier -> {
                chatMessagesUpdateYier.onNewChatMessages(List.of(newMessage));
            });
        });
        resultsYier.onResults();
        fillMessageContent(newMessage, context);
    }

    public static void fillMessageContent(ChatMessage newMessage, Context context) {
        ChatMessageDatabaseManager chatMessageDatabaseManager = ChatMessageDatabaseManager.getInstanceOrInitAndGet(context, newMessage.getOther(context));
        switch (newMessage.getType()){
            case TYPE_IMAGE:{
                ChatApiCaller.fetchMessageImage(null, newMessage.imageId, new RetrofitApiCaller.BaseCommonYier<ResponseBody>(context) {
                    private boolean got;
                    private final CountDownLatch countDownLatch = new CountDownLatch(1);

                    @Override
                    public void ok(ResponseBody data, Response<ResponseBody> raw, Call<ResponseBody> call) {
                        super.ok(data, raw, call); //方
                        executorService.execute(() -> {
                            try {
                                byte[] bytes = data.bytes();
                                String imageFilePath = PrivateFilesAccessor.ChatImage.save(context, newMessage, bytes);
                                newMessage.setImageFilePath(imageFilePath);
                                Size imageSize = MediaHelper.getImageSize(bytes);
                                newMessage.setImageSize(imageSize);
                                got = true;
                            } catch (IOException e) {
                                MessageDisplayer.autoShow(context, "获取聊天消息内容出错", MessageDisplayer.Duration.LONG);
                                ErrorLogger.log(e);
                            }
                            countDownLatch.countDown();
                        });
                        try {
                            countDownLatch.await();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    @Override
                    public void complete(Call<ResponseBody> call) {
                        super.complete(call);
                        FULL_CONTENT_GETTING_MESSAGE_UUID_SET.remove(newMessage.uuid);
                        onMessageFullContentGot(got, newMessage, context, chatMessageDatabaseManager);
                    }
                });
                break;
            }
            case TYPE_FILE:{
                ChatApiCaller.fetchMessageFile(null, newMessage.fileId, new RetrofitApiCaller.BaseCommonYier<ResponseBody>(context){
                    private boolean got;
                    private final CountDownLatch countDownLatch = new CountDownLatch(1);

                    @Override
                    public void ok(ResponseBody data, Response<ResponseBody> raw, Call<ResponseBody> call) {
                        super.ok(data, raw, call);
                        executorService.execute(() -> {
                            try {
                                byte[] bytes = data.bytes();
                                String chatFileFilePath = PrivateFilesAccessor.ChatFile.save(context, newMessage, bytes);
                                newMessage.setFileFilePath(chatFileFilePath);
                                got = true;
                            } catch (IOException e) {
                                MessageDisplayer.autoShow(context, "获取聊天消息内容出错", MessageDisplayer.Duration.LONG);
                                ErrorLogger.log(e);
                            }
                            countDownLatch.countDown();
                        });
                        try {
                            countDownLatch.await();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    @Override
                    public void complete(Call<ResponseBody> call) {
                        super.complete(call);
                        FULL_CONTENT_GETTING_MESSAGE_UUID_SET.remove(newMessage.uuid);
                        onMessageFullContentGot(got, newMessage, context, chatMessageDatabaseManager);
                    }
                });
                break;
            }
            case TYPE_VIDEO:{
                ChatApiCaller.fetchMessageVideo(null, newMessage.videoId, new RetrofitApiCaller.BaseCommonYier<ResponseBody>(context) {
                    private boolean got;
                    private final CountDownLatch countDownLatch = new CountDownLatch(1);

                    @Override
                    public void ok(ResponseBody data, Response<ResponseBody> raw, Call<ResponseBody> call) {
                        super.ok(data, raw, call);
                        executorService.execute(() -> {
                            try {
                                byte[] bytes = data.bytes();
                                String chatVideoFilePath = PrivateFilesAccessor.ChatVideo.save(context, newMessage, bytes);
                                newMessage.setVideoFilePath(chatVideoFilePath);
                                Size videoSize = MediaHelper.getVideoSize(chatVideoFilePath);
                                newMessage.setVideoSize(videoSize);
                                long videoDuration = MediaHelper.getVideoDuration(chatVideoFilePath);
                                newMessage.setVideoDuration(videoDuration);
                                got = true;
                            } catch (IOException e) {
                                MessageDisplayer.autoShow(context, "获取聊天消息内容出错", MessageDisplayer.Duration.LONG);
                                ErrorLogger.log(e);
                            }
                            countDownLatch.countDown();
                        });
                        try {
                            countDownLatch.await();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    @Override
                    public void complete(Call<ResponseBody> call) {
                        super.complete(call);
                        FULL_CONTENT_GETTING_MESSAGE_UUID_SET.remove(newMessage.uuid);
                        onMessageFullContentGot(got, newMessage, context, chatMessageDatabaseManager);
                    }
                });
                break;
            }
            case TYPE_VOICE:{
                newMessage.setVoiceListened(false);
                ChatApiCaller.fetchMessageVoice(null, newMessage.voiceId, new RetrofitApiCaller.BaseCommonYier<ResponseBody>(context) {
                    private boolean got;
                    private final CountDownLatch countDownLatch = new CountDownLatch(1);

                    @Override
                    public void ok(ResponseBody data, Response<ResponseBody> raw, Call<ResponseBody> call) {
                        super.ok(data, raw, call);
                        executorService.execute(() -> {
                            try {
                                byte[] bytes = data.bytes();
                                String chatVoiceFilePath = PrivateFilesAccessor.ChatVoice.save(context, newMessage, bytes);
                                newMessage.setVoiceFilePath(chatVoiceFilePath);
                                got = true;
                            } catch (IOException e) {
                                MessageDisplayer.autoShow(context, "获取聊天消息内容出错", MessageDisplayer.Duration.LONG);
                                throw new RuntimeException(e);
                            }
                            countDownLatch.countDown();
                        });
                        try {
                            countDownLatch.await();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    @Override
                    public void complete(Call<ResponseBody> call) {
                        super.complete(call);
                        FULL_CONTENT_GETTING_MESSAGE_UUID_SET.remove(newMessage.uuid);
                        onMessageFullContentGot(got, newMessage, context, chatMessageDatabaseManager);
                    }
                });
                break;
            }
            case TYPE_UNSEND: {
                executorService.execute(() -> {
                    ChatMessage toUnsendMessage = chatMessageDatabaseManager.findOne(newMessage.unsendMessageUuid);
                    chatMessageDatabaseManager.delete(newMessage.unsendMessageUuid);
                    GlobalYiersHolder.getYiers(ChatMessagesUpdateYier.class).ifPresent(chatMessageUpdateYiers -> {
                        chatMessageUpdateYiers.forEach(chatMessagesUpdateYier -> {
                            chatMessagesUpdateYier.onUnsendChatMessages(List.of(newMessage), List.of(toUnsendMessage));
                        });
                    });
                    FULL_CONTENT_GETTING_MESSAGE_UUID_SET.remove(newMessage.uuid);
                    onMessageFullContentGot(true, newMessage, context, chatMessageDatabaseManager);
                });
                break;
            }
            default:{
                executorService.execute(() -> {
                    FULL_CONTENT_GETTING_MESSAGE_UUID_SET.remove(newMessage.uuid);
                    onMessageFullContentGot(true, newMessage, context, chatMessageDatabaseManager);
                });
                break;
            }
        }
    }

    private static synchronized void onMessageFullContentGot(boolean got, ChatMessage message, Context context, ChatMessageDatabaseManager chatMessageDatabaseManager) {
        message.setFullContentGot(got);
        chatMessageDatabaseManager.update(message);
        GlobalYiersHolder.getYiers(ChatMessagesUpdateYier.class).ifPresent(chatMessageUpdateYiers -> {
            chatMessageUpdateYiers.forEach(chatMessagesUpdateYier -> {
                chatMessagesUpdateYier.onChatMessagesUpdated(List.of(message));
            });
        });
    }

    public static boolean isInGetting(String uuid){
        return FULL_CONTENT_GETTING_MESSAGE_UUID_SET.contains(uuid);
    }

    public static final int TYPE_TEXT = 0;
    public static final int TYPE_VOICE = 1;
    public static final int TYPE_IMAGE = 2;
    public static final int TYPE_VIDEO = 3;
    public static final int TYPE_FILE = 4;
    public static final int TYPE_NOTICE = 5;
    public static final int TYPE_UNSEND = 6;
    public static final int TYPE_MESSAGE_EXPIRED = 7;
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
    private String unsendMessageUuid;
    private Integer expiredMessageCount;

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
    private Long videoDuration;
    @JsonIgnore
    private String voiceFilePath;
    @JsonIgnore
    private Boolean voiceListened;
    @JsonIgnore
    private Boolean fullContentGot;

    public ChatMessage() {
    }

    public ChatMessage(int type, String uuid, String from, String to, Date time, String text, String fileName, String imageId,
                       String fileId, String videoId, String voiceId, String unsendMessageUuid, Integer expiredMessageCount) {
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
        this.unsendMessageUuid = unsendMessageUuid;
        this.expiredMessageCount = expiredMessageCount;
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

    public String getUnsendMessageUuid() {
        return unsendMessageUuid;
    }

    public Integer getExpiredMessageCount() {
        return expiredMessageCount;
    }

    public boolean isSelfSender(Context context){
        return SharedPreferencesAccessor.UserProfilePref.getCurrentUserProfile(context).getImessageId().equals(from);
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

    public Long getVideoDuration() {
        return videoDuration;
    }

    public void setVideoDuration(Long videoDuration) {
        this.videoDuration = videoDuration;
    }

    public Boolean isFullContentGot() {
        return fullContentGot;
    }

    public void setFullContentGot(Boolean fullContentGot) {
        this.fullContentGot = fullContentGot;
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
        byte tmpFullContentGot = in.readByte();
        fullContentGot = tmpFullContentGot == 0 ? null : tmpFullContentGot == 1;
        if(type == TYPE_IMAGE) {
            imageFilePath = in.readString();
            imageSize = in.readSize();
        }else if(type == TYPE_FILE){
            fileFilePath = in.readString();
        }else if(type == TYPE_VIDEO){
            videoFilePath = in.readString();
            videoSize = in.readSize();
            videoDuration = in.readLong();
        }else if(type == TYPE_VOICE){
            byte tmpVoiceListened = in.readByte();
            voiceListened = tmpVoiceListened == 0 ? null : tmpVoiceListened == 1;
            voiceFilePath = in.readString();
        }else if(type == TYPE_UNSEND){
            unsendMessageUuid = in.readString();
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
        dest.writeByte((byte) (fullContentGot == null ? 0 : fullContentGot ? 1 : 2));
        if(type == TYPE_IMAGE) {
            dest.writeString(imageFilePath);
            dest.writeSize(imageSize);
        }else if(type == TYPE_FILE) {
            dest.writeString(fileFilePath);
        }else if(type == TYPE_VIDEO){
            dest.writeString(videoFilePath);
            dest.writeSize(videoSize);
            dest.writeLong(videoDuration);
        }else if(type == TYPE_VOICE){
            dest.writeByte((byte) (voiceListened == null ? 0 : voiceListened ? 1 : 2));
            dest.writeString(voiceFilePath);
        }else if(type == TYPE_UNSEND){
            dest.writeString(unsendMessageUuid);
        }
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "type=" + type +
                ", getUuid='" + uuid + '\'' +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", time=" + time +
                ", text='" + text + '\'' +
                ", fileName='" + fileName + '\'' +
                ", imageId='" + imageId + '\'' +
                ", fileId='" + fileId + '\'' +
                ", videoId='" + videoId + '\'' +
                ", voiceId='" + voiceId + '\'' +
                ", unsendMessageUuid='" + unsendMessageUuid + '\'' +
                ", expiredMessageCount=" + expiredMessageCount +
                ", showTime=" + showTime +
                ", viewed=" + viewed +
                ", imageFilePath='" + imageFilePath + '\'' +
                ", imageSize=" + imageSize +
                ", fileFilePath='" + fileFilePath + '\'' +
                ", videoFilePath='" + videoFilePath + '\'' +
                ", videoSize=" + videoSize +
                ", videoDuration=" + videoDuration +
                ", voiceFilePath='" + voiceFilePath + '\'' +
                ", voiceListened=" + voiceListened +
                ", fullContentGot=" + fullContentGot +
                '}';
    }
}
