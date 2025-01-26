package com.longx.intelligent.android.imessage.net.retrofit.caller;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;

import androidx.lifecycle.LifecycleOwner;

import com.longx.intelligent.android.imessage.da.FileHelper;
import com.longx.intelligent.android.imessage.data.request.SendFileChatMessagePostBody;
import com.longx.intelligent.android.imessage.data.request.SendImageChatMessagePostBody;
import com.longx.intelligent.android.imessage.data.request.SendTextChatMessagePostBody;
import com.longx.intelligent.android.imessage.data.request.SendVideoChatMessagePostBody;
import com.longx.intelligent.android.imessage.data.request.SendVoiceChatMessagePostBody;
import com.longx.intelligent.android.imessage.data.response.OperationData;
import com.longx.intelligent.android.imessage.net.retrofit.api.ChatApi;
import com.longx.intelligent.android.imessage.util.ErrorLogger;
import com.longx.intelligent.android.imessage.util.FileUtil;
import com.longx.intelligent.android.imessage.util.JsonUtil;
import com.longx.intelligent.android.imessage.yier.ProgressYier;
import com.xcheng.retrofit.CompletableCall;
import com.xcheng.retrofit.ProgressRequestBody;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.BufferedSink;

/**
 * Created by LONG on 2024/5/15 at 4:09 AM.
 */
public class ChatApiCaller extends RetrofitApiCaller{
    public static ChatApi getApiImplementation(){
        return getApiImplementation(ChatApi.class);
    }

    public static CompletableCall<OperationData> fetchAllUnviewedMessages(LifecycleOwner lifecycleOwner, BaseYier<OperationData> yier){
        CompletableCall<OperationData> call = getApiImplementation().fetchAllUnviewedMessages();
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationData> viewMessage(LifecycleOwner lifecycleOwner, String messageUuid, BaseYier<OperationData> yier){
        CompletableCall<OperationData> call = getApiImplementation().viewMessage(messageUuid);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationData> sendTextMessage(LifecycleOwner lifecycleOwner, SendTextChatMessagePostBody postBody, BaseCommonYier<OperationData> yier){
        CompletableCall<OperationData> call = getApiImplementation().sendTextMessage(postBody);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationData> sendImageMessage(LifecycleOwner lifecycleOwner, Context context, Uri imageUri,
                                                                  SendImageChatMessagePostBody postBody, String fileName,
                                                                  BaseCommonYier<OperationData> yier, ProgressYier progressYier){
        ContentResolver contentResolver = context.getContentResolver();
        InputStream inputStream;
        try {
            inputStream = contentResolver.openInputStream(imageUri);
            if (inputStream == null) {
                throw new IOException("Unable to open input stream from URI");
            }
        } catch (IOException e) {
            ErrorLogger.log(e);
            return null;
        }
        String mimeType;
        if(fileName == null) {
            fileName = FileHelper.getFileNameFromUri(context, imageUri);
            mimeType = FileHelper.getMimeType(context, imageUri);
        }else {
            mimeType = FileHelper.getMimeType(fileName);
        }
        RequestBody requestBody = new RequestBody() {
            @Override
            public MediaType contentType() {
                return MediaType.parse(mimeType);
            }

            @Override
            public long contentLength() throws IOException {
                return FileUtil.getFileSize(context, imageUri);
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                try (InputStream inputStream = contentResolver.openInputStream(imageUri)) {
                    if (inputStream == null) {
                        throw new IOException("Unable to open input stream from URI");
                    }
                    byte[] buffer = new byte[10240];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        sink.write(buffer, 0, bytesRead);
                    }
                }
            }
        };
        ProgressRequestBody progressRequestBody = new ProgressRequestBody(requestBody) {
            @Override
            protected void onUpload(long progress, long contentLength, boolean done) {
                progressYier.onProgressUpdate(progress, contentLength);
            }
        };
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("image", fileName, progressRequestBody);
        RequestBody metadataRequestBody = RequestBody.create(MediaType.parse("application/json"), JsonUtil.toJson(postBody));
        CompletableCall<OperationData> call = getApiImplementation().sendImageMessage(filePart, metadataRequestBody);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<ResponseBody> fetchMessageImage(LifecycleOwner lifecycleOwner, String imageId, BaseYier<ResponseBody> yier){
        CompletableCall<ResponseBody> call = getApiImplementation().fetchMessageImage(imageId);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationData> sendFileMessage(LifecycleOwner lifecycleOwner, Context context, Uri fileUri,
                                                                 SendFileChatMessagePostBody postBody,
                                                                 BaseCommonYier<OperationData> yier, ProgressYier progressYier) {
        ContentResolver contentResolver = context.getContentResolver();
        InputStream inputStream;
        try {
            inputStream = contentResolver.openInputStream(fileUri);
            if (inputStream == null) {
                throw new IOException("Unable to open input stream from URI");
            }
        } catch (IOException e) {
            ErrorLogger.log(e);
            return null;
        }
        String fileName = FileHelper.getFileNameFromUri(context, fileUri);
        String mimeType = FileHelper.getMimeType(context, fileUri);
        RequestBody requestBody = new RequestBody() {
            @Override
            public MediaType contentType() {
                return MediaType.parse(mimeType);
            }

            @Override
            public long contentLength() throws IOException {
                return FileUtil.getFileSize(context, fileUri);
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                try (InputStream inputStream = contentResolver.openInputStream(fileUri)) {
                    if (inputStream == null) {
                        throw new IOException("Unable to open input stream from URI");
                    }
                    byte[] buffer = new byte[10240];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        sink.write(buffer, 0, bytesRead);
                    }
                }
            }
        };
        ProgressRequestBody progressRequestBody = new ProgressRequestBody(requestBody) {
            @Override
            protected void onUpload(long progress, long contentLength, boolean done) {
                progressYier.onProgressUpdate(progress, contentLength);
            }
        };
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", fileName, progressRequestBody);
        RequestBody metadataRequestBody = RequestBody.create(MediaType.parse("application/json"), JsonUtil.toJson(postBody));
        CompletableCall<OperationData> call = getApiImplementation().sendFileMessage(filePart, metadataRequestBody);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<ResponseBody> fetchMessageFile(LifecycleOwner lifecycleOwner, String fileId, BaseYier<ResponseBody> yier){
        CompletableCall<ResponseBody> call = getApiImplementation().fetchChatMessageFile(fileId);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationData> sendVideoMessage(LifecycleOwner lifecycleOwner, Context context, Uri videoUri,
                                                                  SendVideoChatMessagePostBody postBody,
                                                                  BaseCommonYier<OperationData> yier, ProgressYier progressYier){
        ContentResolver contentResolver = context.getContentResolver();
        InputStream inputStream;
        try {
            inputStream = contentResolver.openInputStream(videoUri);
            if (inputStream == null) {
                throw new IOException("Unable to open input stream from URI");
            }
        } catch (IOException e) {
            ErrorLogger.log(e);
            return null;
        }
        String fileName = FileHelper.getFileNameFromUri(context, videoUri);
        String mimeType = FileHelper.getMimeType(context, videoUri);
        RequestBody requestBody = new RequestBody() {
            @Override
            public MediaType contentType() {
                return MediaType.parse(mimeType);
            }

            @Override
            public long contentLength() throws IOException {
                return FileUtil.getFileSize(context, videoUri);
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                try (InputStream inputStream = contentResolver.openInputStream(videoUri)) {
                    if (inputStream == null) {
                        throw new IOException("Unable to open input stream from URI");
                    }
                    byte[] buffer = new byte[10240];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        sink.write(buffer, 0, bytesRead);
                    }
                }
            }
        };
        ProgressRequestBody progressRequestBody = new ProgressRequestBody(requestBody) {
            @Override
            protected void onUpload(long progress, long contentLength, boolean done) {
                progressYier.onProgressUpdate(progress, contentLength);
            }
        };
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("video", fileName, progressRequestBody);
        RequestBody metadataRequestBody = RequestBody.create(MediaType.parse("application/json"), JsonUtil.toJson(postBody));
        CompletableCall<OperationData> call = getApiImplementation().sendVideoMessage(filePart, metadataRequestBody);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<ResponseBody> fetchMessageVideo(LifecycleOwner lifecycleOwner, String videoId, BaseYier<ResponseBody> yier){
        CompletableCall<ResponseBody> call = getApiImplementation().fetchMessageVideo(videoId);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationData> sendVoiceMessage(LifecycleOwner lifecycleOwner, Context context, Uri voiceUri,
                                                                  SendVoiceChatMessagePostBody postBody,
                                                                  BaseCommonYier<OperationData> yier, ProgressYier progressYier){
        ContentResolver contentResolver = context.getContentResolver();
        InputStream inputStream;
        try {
            inputStream = contentResolver.openInputStream(voiceUri);
            if (inputStream == null) {
                throw new IOException("Unable to open input stream from URI");
            }
        } catch (IOException e) {
            ErrorLogger.log(e);
            return null;
        }
        String fileName = FileHelper.getFileNameFromUri(context, voiceUri);
        String mimeType = FileHelper.getMimeType(context, voiceUri);
        RequestBody requestBody = new RequestBody() {
            @Override
            public MediaType contentType() {
                return MediaType.parse(mimeType);
            }

            @Override
            public long contentLength() throws IOException {
                return FileUtil.getFileSize(context, voiceUri);
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                try (InputStream inputStream = contentResolver.openInputStream(voiceUri)) {
                    if (inputStream == null) {
                        throw new IOException("Unable to open input stream from URI");
                    }
                    byte[] buffer = new byte[10240];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        sink.write(buffer, 0, bytesRead);
                    }
                }
            }
        };
        ProgressRequestBody progressRequestBody = new ProgressRequestBody(requestBody) {
            @Override
            protected void onUpload(long progress, long contentLength, boolean done) {
                progressYier.onProgressUpdate(progress, contentLength);
            }
        };
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("voice", fileName, progressRequestBody);
        RequestBody metadataRequestBody = RequestBody.create(MediaType.parse("application/json"), JsonUtil.toJson(postBody));
        CompletableCall<OperationData> call = getApiImplementation().sendVoiceMessage(filePart, metadataRequestBody);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<ResponseBody> fetchMessageVoice(LifecycleOwner lifecycleOwner, String voiceId, BaseYier<ResponseBody> yier){
        CompletableCall<ResponseBody> call = getApiImplementation().fetchMessageVoice(voiceId);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationData> unsendMessage(LifecycleOwner lifecycleOwner, String receiver, String chatMessageId, BaseYier<OperationData> yier){
        CompletableCall<OperationData> call = getApiImplementation().unsendMessage(receiver, chatMessageId);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }
}
