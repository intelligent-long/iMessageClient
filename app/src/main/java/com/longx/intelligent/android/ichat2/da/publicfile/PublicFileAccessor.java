package com.longx.intelligent.android.ichat2.da.publicfile;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.longx.intelligent.android.ichat2.behavior.GlideBehaviours;
import com.longx.intelligent.android.ichat2.da.DataPaths;
import com.longx.intelligent.android.ichat2.da.FileHelper;
import com.longx.intelligent.android.ichat2.data.Broadcast;
import com.longx.intelligent.android.ichat2.data.ChatMessage;
import com.longx.intelligent.android.ichat2.net.dataurl.NetDataUrls;
import com.longx.intelligent.android.ichat2.util.ErrorLogger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.CountDownLatch;

/**
 * Created by LONG on 2024/5/29 at 3:19 PM.
 */
public class PublicFileAccessor {

    public static class ChatMedia {
        public static String saveImage(ChatMessage chatMessage) throws IOException {
            String savePath = DataPaths.PublicFile.getChatFilePath(chatMessage);
            return FileHelper.save(FileHelper.streamOf(chatMessage.getImageFilePath()), savePath);
        }

        public static String saveVideo(ChatMessage chatMessage) throws IOException {
            String savePath = DataPaths.PublicFile.getChatFilePath(chatMessage);
            return FileHelper.save(FileHelper.streamOf(chatMessage.getVideoFilePath()), savePath);
        }
    }

    public static class CapturedMedia{
        public static File createPhotoFile() throws IOException {
            String filePath = DataPaths.PublicFile.getCapturedMediaFilePath();
            return FileHelper.createFile(filePath);
        }

        public static File createVideoFile() throws IOException {
            String filePath = DataPaths.PublicFile.getCapturedMediaFilePath();
            return FileHelper.createFile(filePath);
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
                    try {
                        savedPath[0] = FileHelper.save(Files.newInputStream(resource.toPath()), savePath);
                        countDownLatch.countDown();
                    } catch (IOException e) {
                        ErrorLogger.log(e);
                        ioException[0] = e;
                        countDownLatch.countDown();
                    }
                }

                @Override
                public void onLoadCleared(@Nullable Drawable placeholder) {
                }
            }, true);
            countDownLatch.await();
            if(ioException[0] != null) throw ioException[0];
            return savedPath[0];
        }
    }
}
