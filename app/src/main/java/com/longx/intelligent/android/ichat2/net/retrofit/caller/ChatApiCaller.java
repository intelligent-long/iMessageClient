package com.longx.intelligent.android.ichat2.net.retrofit.caller;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;

import androidx.lifecycle.LifecycleOwner;

import com.longx.intelligent.android.ichat2.da.FileHelper;
import com.longx.intelligent.android.ichat2.data.request.SendFileChatMessagePostBody;
import com.longx.intelligent.android.ichat2.data.request.SendImageChatMessagePostBody;
import com.longx.intelligent.android.ichat2.data.request.SendTextChatMessagePostBody;
import com.longx.intelligent.android.ichat2.data.request.SendVideoChatMessagePostBody;
import com.longx.intelligent.android.ichat2.data.request.SendVoiceChatMessagePostBody;
import com.longx.intelligent.android.ichat2.data.response.OperationData;
import com.longx.intelligent.android.ichat2.data.response.OperationStatus;
import com.longx.intelligent.android.ichat2.net.retrofit.api.ChatApi;
import com.longx.intelligent.android.ichat2.util.ErrorLogger;
import com.longx.intelligent.android.ichat2.util.FileUtil;
import com.longx.intelligent.android.ichat2.util.JsonUtil;
import com.longx.intelligent.android.ichat2.yier.MultiProgressYier;
import com.longx.intelligent.android.ichat2.yier.ProgressYier;
import com.xcheng.retrofit.CompletableCall;
import com.xcheng.retrofit.ProgressRequestBody;

import java.io.File;
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

    public static CompletableCall<OperationData> fetchAllNewChatMessages(LifecycleOwner lifecycleOwner, BaseYier<OperationData> yier){
        CompletableCall<OperationData> call = getApiImplementation().fetchAllNewChatMessages();
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationData> viewNewMessage(LifecycleOwner lifecycleOwner, String messageUuid, BaseYier<OperationData> yier){
        CompletableCall<OperationData> call = getApiImplementation().viewNewMessage(messageUuid);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationStatus> viewAllNewMessage(LifecycleOwner lifecycleOwner, String other, BaseYier<OperationStatus> yier){
        CompletableCall<OperationStatus> call = getApiImplementation().viewAllNewMessage(other);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationData> sendTextChatMessage(LifecycleOwner lifecycleOwner, SendTextChatMessagePostBody postBody, BaseCommonYier<OperationData> yier){
        CompletableCall<OperationData> call = getApiImplementation().sendTextChatMessage(postBody);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationData> sendImageChatMessage(LifecycleOwner lifecycleOwner, Context context, Uri imageUri,
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
        CompletableCall<OperationData> call = getApiImplementation().sendImageChatMessage(filePart, metadataRequestBody);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<ResponseBody> fetchChatMessageImage(LifecycleOwner lifecycleOwner, String imageId, BaseYier<ResponseBody> yier){
        CompletableCall<ResponseBody> call = getApiImplementation().fetchChatMessageImage(imageId);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationData> sendFileChatMessage(LifecycleOwner lifecycleOwner, Context context, Uri fileUri,
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
        CompletableCall<OperationData> call = getApiImplementation().sendFileChatMessage(filePart, metadataRequestBody);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<ResponseBody> fetchChatMessageFile(LifecycleOwner lifecycleOwner, String fileId, BaseYier<ResponseBody> yier){
        CompletableCall<ResponseBody> call = getApiImplementation().fetchChatMessageFile(fileId);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationData> sendVideoChatMessage(LifecycleOwner lifecycleOwner, Context context, Uri videoUri,
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
        CompletableCall<OperationData> call = getApiImplementation().sendVideoChatMessage(filePart, metadataRequestBody);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<ResponseBody> fetchChatMessageVideo(LifecycleOwner lifecycleOwner, String videoId, BaseYier<ResponseBody> yier){
        CompletableCall<ResponseBody> call = getApiImplementation().fetchChatMessageVideo(videoId);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationData> sendVoiceChatMessage(LifecycleOwner lifecycleOwner, Context context, Uri voiceUri,
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
        CompletableCall<OperationData> call = getApiImplementation().sendVoiceChatMessage(filePart, metadataRequestBody);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<ResponseBody> fetchChatMessageVoice(LifecycleOwner lifecycleOwner, String voiceId, BaseYier<ResponseBody> yier){
        CompletableCall<ResponseBody> call = getApiImplementation().fetchChatMessageVoice(voiceId);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }
}
