package com.longx.intelligent.android.ichat2.util;

import android.content.Context;
import android.net.Uri;

import org.apache.commons.codec.binary.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by LONG on 2024/5/26 at 12:49 AM.
 */
public class MediaUtil {

    public static byte[] readUriToBytes(Uri uri, Context context){
        try (InputStream is = context.getContentResolver().openInputStream(uri);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int read;
            while (-1 != (read = is.read(buffer))) {
                baos.write(buffer, 0, read);
            }
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String readUriToBase64(Uri uri, Context context){
        return Base64.encodeBase64String(readUriToBytes(uri, context));
    }
}
