package com.longx.intelligent.android.imessage.ui.glide;

import android.content.Context;

import com.bumptech.glide.Glide;

/**
 * Created by LONG on 2024/4/30 at 3:25 AM.
 */
public class GlideHelper {
    public static void clearAllCache(Context context){
        Glide.get(context).clearMemory();
        new Thread(() -> Glide.get(context).clearDiskCache()).start();
    }
}
