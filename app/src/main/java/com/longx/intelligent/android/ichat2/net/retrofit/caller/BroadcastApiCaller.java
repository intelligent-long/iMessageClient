package com.longx.intelligent.android.ichat2.net.retrofit.caller;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;

import androidx.lifecycle.LifecycleOwner;

import com.longx.intelligent.android.ichat2.da.FileHelper;
import com.longx.intelligent.android.ichat2.data.Broadcast;
import com.longx.intelligent.android.ichat2.data.request.SendBroadcastPostBody;
import com.longx.intelligent.android.ichat2.data.response.OperationStatus;
import com.longx.intelligent.android.ichat2.data.response.PaginatedOperationData;
import com.longx.intelligent.android.ichat2.net.retrofit.api.BroadcastApi;
import com.longx.intelligent.android.ichat2.util.ErrorLogger;
import com.longx.intelligent.android.ichat2.util.FileUtil;
import com.longx.intelligent.android.ichat2.util.JsonUtil;
import com.xcheng.retrofit.CompletableCall;

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
    public static BroadcastApi getApiImplementation(){
        return getApiImplementation(BroadcastApi.class);
    }

    public static CompletableCall<OperationStatus> sendBroadcast(LifecycleOwner lifecycleOwner, Context context,
                                                                 SendBroadcastPostBody postBody, List<Uri> mediaUris,
                                                                 BaseYier<OperationStatus> yier){
        RequestBody bodyPart = RequestBody.create(MediaType.parse("application/json"), JsonUtil.toJson(postBody));
        List<MultipartBody.Part> mediaPart = new ArrayList<>();
        ContentResolver contentResolver = context.getContentResolver();
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
            mediaPart.add(MultipartBody.Part.createFormData("medias", fileName, requestBody));
        });
        CompletableCall<OperationStatus> call = getApiImplementation().sendBroadcast(bodyPart, mediaPart);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }

    public static CompletableCall<PaginatedOperationData<Broadcast>> fetchBroadcastsLimit(LifecycleOwner lifecycleOwner, String lastBroadcastId, int ps, BaseYier<PaginatedOperationData<Broadcast>> yier){
        CompletableCall<PaginatedOperationData<Broadcast>> call = getApiImplementation().fetchBroadcastsLimit(lastBroadcastId, ps);
        call.enqueue(lifecycleOwner, yier);
        return call;
    }
}
