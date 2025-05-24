package com.longx.intelligent.android.imessage.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;

import androidx.core.content.FileProvider;

import com.longx.intelligent.android.imessage.da.DataPaths;
import com.longx.intelligent.android.imessage.value.Constants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by LONG on 2025/5/24 at 下午9:19.
 */
public class ShareUtil {
    public static void shareString(Context context, String string, String title){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, string);
        sendIntent.putExtra(Intent.EXTRA_TITLE, title);
        sendIntent.setType("text/plain");
        Intent shareIntent = Intent.createChooser(sendIntent, null);
        context.startActivity(shareIntent);
    }

    public static void shareBitmap(Context context, Bitmap bitmap, String id) {
        File tempFile = new File(DataPaths.Cache.qrCodeFilePath(context, id, "png"));
        File parentDir = tempFile.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }
        try (FileOutputStream out = new FileOutputStream(tempFile)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (IOException e) {
            ErrorLogger.log(e);
        }
        Uri contentUri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", tempFile);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(Intent.createChooser(shareIntent, null));
    }

}
