package com.longx.intelligent.android.ichat2.behavior;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.longx.intelligent.android.ichat2.net.dataurl.NetDataUrls;
import com.longx.intelligent.android.ichat2.ui.glide.GlideApp;
import com.longx.intelligent.android.ichat2.ui.glide.GlideRequest;

import java.io.File;

/**
 * Created by LONG on 2024/5/5 at 1:42 AM.
 */
public class GlideBehaviours {

    public static void loadToImageView(Context context, String url, ImageView imageView){
        loadToImageView(context, url, imageView, false);
    }

    public static void loadToImageView(Context context, String url, ImageView imageView, boolean originalSize){
        GlideRequest<Bitmap> glideRequest = GlideApp
                .with(context)
                .asBitmap();
        if(originalSize){
            glideRequest = glideRequest.override(Target.SIZE_ORIGINAL);
        }
        glideRequest
                .load(url)
                .into(imageView);
    }

    public static void loadToImageView(Context context, Integer id, ImageView imageView){
        loadToImageView(context, id, imageView, false);
    }

    public static void loadToImageView(Context context, Integer id, ImageView imageView, boolean originalSize){
        GlideRequest<Bitmap> glideRequest = GlideApp
                .with(context)
                .asBitmap();
        if(originalSize){
            glideRequest = glideRequest.override(Target.SIZE_ORIGINAL);
        }
        glideRequest
                .load(id)
                .into(imageView);
    }

    public static void loadToBitmap(Context context, String url, CustomTarget<Bitmap> customTarget){
        loadToBitmap(context, url, customTarget, false);
    }

    public static void loadToBitmap(Context context, String url, CustomTarget<Bitmap> customTarget, boolean originalSize){
        GlideRequest<Bitmap> glideRequest = GlideApp
                .with(context)
                .asBitmap();
        if(originalSize){
            glideRequest = glideRequest.override(Target.SIZE_ORIGINAL);
        }
        glideRequest
                .load(url)
                .into(customTarget);
    }

    public static void loadToFile(Context context, String url, CustomTarget<File> customTarget){
        loadToFile(context, url, customTarget, false);
    }

    public static void loadToFile(Context context, String url, CustomTarget<File> customTarget, boolean originalSize){
        GlideRequest<File> glideRequest = GlideApp
                .with(context)
                .downloadOnly();
        if(originalSize){
            glideRequest = glideRequest.override(Target.SIZE_ORIGINAL);
        }
        glideRequest
                .load(url)
                .into(customTarget);
    }
}
