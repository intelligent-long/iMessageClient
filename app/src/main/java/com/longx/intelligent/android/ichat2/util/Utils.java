package com.longx.intelligent.android.ichat2.util;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Parcelable;

import com.longx.intelligent.android.ichat2.activity.ExtraKeys;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * Created by LONG on 2024/4/6 at 5:52 PM.
 */
public class Utils {

    public static int getRandomNumberInRange(int min, int max) {
        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }
        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }

    public static byte[] encodeBitmapToBytes(Bitmap bitmap, Bitmap.CompressFormat compressFormat, int quality) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(compressFormat, quality, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public static void copyTextToClipboard(Context context, String label, String text) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(label, text);
        clipboard.setPrimaryClip(clip);
    }

    public static <T> ArrayList<T> parseParcelableArray(Parcelable[] parcelableArray){
        ArrayList<T> uriList = new ArrayList<>();
        for (Parcelable parcelableUri : parcelableArray) {
            uriList.add((T) parcelableUri);
        }
        return uriList;
    }

    public static <T> ArrayList<T> parseParcelableArray(List<Parcelable> parcelableArrayList){
        ArrayList<T> uriList = new ArrayList<>();
        for (Parcelable parcelableUri : parcelableArrayList) {
            uriList.add((T) parcelableUri);
        }
        return uriList;
    }
}
