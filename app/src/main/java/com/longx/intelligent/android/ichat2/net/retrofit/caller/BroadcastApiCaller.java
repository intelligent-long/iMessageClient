package com.longx.intelligent.android.ichat2.net.retrofit.caller;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;

import androidx.lifecycle.LifecycleOwner;

import com.longx.intelligent.android.ichat2.da.FileHelper;
import com.longx.intelligent.android.ichat2.data.Broadcast;
import com.longx.intelligent.android.ichat2.data.request.SendBroadcastPostBody;
import com.longx.intelligent.android.ichat2.data.response.OperationData;
import com.longx.intelligent.android.ichat2.data.response.OperationStatus;
import com.longx.intelligent.android.ichat2.data.response.PaginatedOperationData;
import com.longx.intelligent.android.ichat2.net.retrofit.RetrofitCreator;
import com.longx.intelligent.android.ichat2.net.retrofit.api.BroadcastApi;
import com.longx.intelligent.android.ichat2.util.ErrorLogger;
import com.longx.intelligent.android.ichat2.util.FileUtil;
import com.longx.intelligent.android.ichat2.util.JsonUtil;
import com.longx.intelligent.android.ichat2.yier.ProgressYier;
import com.xcheng.retrofit.CompletableCall;
import com.xcheng.retrofit.ProgressRequestBody;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okio.BufferedSink;
import retrofit2.http.Query;

/**
 * Created by LONG on 2024/7/28 at 上午2:49.
 */
public class BroadcastApiCaller extends RetrofitApiCaller{
    private static final long SEND_BROADCAST_CONNECT_TIMEOUT = 60;
    private static final long SEND_BROADCAST_READ_TIMEOUT = 60 * 10;
    private static final long SEND_BROADCAST_WRITE_TIMEOUT = 60;

    public static BroadcastApi getApiImplementation(){
        return getApiImplementation(BroadcastApi.class);
    }

    public static BroadcastApi getApiImplementation(Context context, long connectTimeout, long readTimeout, long writeTimeout){
        return getApiImplementation(RetrofitCreator.customTimeout(context, connectTimeout, readTimeout, writeTimeout), BroadcastApi.class);
    }

    public static CompletableCall<OperationStatus> sendBroadcast(LifecycleOwner lifecycleOwner, Context context,
                                                                 SendBroadcastPostBody postBody, List<Uri> mediaUris,
                                                                 BaseYier<OperationStatus> yier, ProgressYier progressYier){
        RequestBody bodyPart = RequestBody.create(MediaType.parse("application/json"), JsonUtil.toJson(postBody));
        List<MultipartBody.Part> mediaPart = new ArrayList<>();
        ContentResolver contentResolver = context.getContentResolver();
        final int[] index = {0};
        if(mediaUris != null) mediaUris.forEach(mediaUri -> {
            String fileName = FileHelper.getFileNameFromUri(context, mediaUri);
            String mimeType = FileHelper.getMimeType(context, mediaUri);
            RequestBody requestBody = new RequestBody() {
                @Override
                public MediaType contentType() {
                    return MediaType.parse(mimeType);
                }

                @Override
                public long contentLength() throws IOException {
                    return FileUtil.getFileSize(context, mediaUri);
                }

                @Override
                public void writeTo(BufferedSink sink) throws IOException {
                    try (InputStream inputStream = contentResolver.openInputStream(mediaUri)) {
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
                    progressYier.onProgressUpdate(progress, contentLength, index[0]);
                    if(done) index[0]++;
                }
            };
            mediaPart.add(MultipartBody.Part.createFormData("medias", fileName, progressRequestBody));
        });
        CompletableCall<OperationStatus> call = getApiImplementation(context, SEND_BROADCAST_CONNECT_TIMEOUT, SEND_BROADCAST_READ_TIMEOUT, SEND_BROADCAST_WRITE_TIMEOUT).sendBroadcast(bodyPart, mediaPart);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<PaginatedOperationData<Broadcast>> fetchBroadcastsLimit(LifecycleOwner lifecycleOwner, String lastBroadcastId, int ps, boolean desc, BaseYier<PaginatedOperationData<Broadcast>> yier){
        CompletableCall<PaginatedOperationData<Broadcast>> call = getApiImplementation().fetchBroadcastsLimit(lastBroadcastId, ps, desc);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<OperationStatus> deleteBroadcast(LifecycleOwner lifecycleOwner, String broadcastId, BaseYier<OperationStatus> yier){
        CompletableCall<OperationStatus> call = getApiImplementation().deleteBroadcast(broadcastId);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<PaginatedOperationData<Broadcast>> fetchChannelBroadcastsLimit(LifecycleOwner lifecycleOwner, String channelId, String lastBroadcastId, int ps, boolean desc, BaseYier<PaginatedOperationData<Broadcast>> yier){
        CompletableCall<PaginatedOperationData<Broadcast>> call = getApiImplementation().fetchChannelBroadcastsLimit(channelId, lastBroadcastId, ps, desc);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }
}
