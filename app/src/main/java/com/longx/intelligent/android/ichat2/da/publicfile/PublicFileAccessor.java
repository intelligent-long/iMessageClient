package com.longx.intelligent.android.ichat2.da.publicfile;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.longx.intelligent.android.ichat2.behaviorcomponents.GlideBehaviours;
import com.longx.intelligent.android.ichat2.da.DataPaths;
import com.longx.intelligent.android.ichat2.da.FileHelper;
import com.longx.intelligent.android.ichat2.data.Broadcast;
import com.longx.intelligent.android.ichat2.data.ChatMessage;
import com.longx.intelligent.android.ichat2.media.helper.MediaStoreHelper;
import com.longx.intelligent.android.ichat2.net.dataurl.NetDataUrls;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.BroadcastApiCaller;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.ichat2.util.ErrorLogger;
import com.longx.intelligent.android.ichat2.yier.ResultsYier;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.concurrent.CountDownLatch;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by LONG on 2024/5/29 at 3:19 PM.
 */
public class PublicFileAccessor {

    public static class ChatMedia {
        public static String saveImage(Context context, ChatMessage chatMessage) throws IOException {
            String savePath = DataPaths.PublicFile.getChatFilePath(chatMessage);
            String saved = FileHelper.save(FileHelper.streamOf(chatMessage.getImageFilePath()), savePath);
            MediaStoreHelper.notifyMediaStore(context, savePath);
            return saved;
        }

        public static String saveVideo(Context context, ChatMessage chatMessage) throws IOException {
            String savePath = DataPaths.PublicFile.getChatFilePath(chatMessage);
            String saved = FileHelper.save(FileHelper.streamOf(chatMessage.getVideoFilePath()), savePath);
            MediaStoreHelper.notifyMediaStore(context, savePath);
            return saved;
        }
    }

    public static class CapturedMedia{
        public static File createPhotoFile(Context context) throws IOException {
            String filePath = DataPaths.PublicFile.getCapturedMediaFilePath();
            File file = FileHelper.createFile(filePath);
            MediaStoreHelper.notifyMediaStore(context, filePath);
            return file;
        }

        public static File createVideoFile(Context context) throws IOException {
            String filePath = DataPaths.PublicFile.getCapturedMediaFilePath();
            File file = FileHelper.createFile(filePath);
            MediaStoreHelper.notifyMediaStore(context, filePath);
            return file;
        }
    }

    public static class BroadcastMedia{
        public static String saveImage(Context context, Broadcast broadcast, int mediaIndex) throws IOException, InterruptedException {
            CountDownLatch countDownLatch = new CountDownLatch(1);
            String savePath = DataPaths.PublicFile.getBroadcastFilePath(broadcast, mediaIndex);
            final String[] savedPath = new String[1];
            final IOException[] ioException = new IOException[1];
            GlideBehaviours.loadToFile(context, NetDataUrls.getBroadcastMediaDataUrl(context, broadcast.getBroadcastMedias().get(mediaIndex).getMediaId()), new CustomTarget<File>() {
                @Override
                public void onResourceReady(@NonNull File resource, @Nullable Transition<? super File> transition) {
                    InputStream inputStream = null;
                    try {
                        inputStream = Files.newInputStream(resource.toPath());
                        savedPath[0] = FileHelper.save(inputStream, savePath);
                        countDownLatch.countDown();
                    } catch (IOException e) {
                        ErrorLogger.log(e);
                        ioException[0] = e;
                        countDownLatch.countDown();
                    }finally {
                        try {
                            if (inputStream != null) {
                                inputStream.close();
                            }
                        } catch (IOException e) {
                            ErrorLogger.log(e);
                            ioException[0] = e;
                        }
                    }
                }

                @Override
                public void onLoadCleared(@Nullable Drawable placeholder) {
                }
            }, true);
            countDownLatch.await();
            if(ioException[0] != null) throw ioException[0];
            MediaStoreHelper.notifyMediaStore(context, savedPath[0]);
            return savedPath[0];
        }

        public static String saveVideo(AppCompatActivity activity, Broadcast broadcast, int mediaIndex) throws InterruptedException, IOException {
            CountDownLatch countDownLatch = new CountDownLatch(1);
            String savePath = DataPaths.PublicFile.getBroadcastFilePath(broadcast, mediaIndex);
            final IOException[] ioException = new IOException[1];
            BroadcastApiCaller.downloadMediaData(activity, broadcast.getBroadcastMedias().get(mediaIndex).getMediaId(),
                    new RetrofitApiCaller.DownloadCommonYier(activity, savePath, true, results -> {
                        Boolean success = (Boolean) results[0];
                        String saveTo = (String) results[1];
                        if(success != null && !success.equals(Boolean.FALSE)) {
                            MediaStoreHelper.notifyMediaStore(activity, saveTo);
                        }else {
                            ioException[0] = new IOException("保存广播视频失败");
                        }
                        countDownLatch.countDown();
                    }));
            countDownLatch.await();
            if(ioException[0] != null) throw ioException[0];
            return savePath;
        }
    }
}
