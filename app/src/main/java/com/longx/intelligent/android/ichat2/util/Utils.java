package com.longx.intelligent.android.ichat2.util;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
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
}
