package com.longx.intelligent.android.imessage.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.TypedValue;

import androidx.core.content.ContextCompat;

import com.longx.intelligent.android.imessage.R;

import java.lang.reflect.Field;

/**
 * Created by LONG on 2023/6/13 at 7:32 PM.
 */
public class ResourceUtil {
    public static int getResId(String variableName, Class<?> c) {
        try {
            Field idField = c.getDeclaredField(variableName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static TypedValue getAttr(Context context, int resId){
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(resId, typedValue, true);
        return typedValue;
    }

    public static Bitmap getMipMapBitmapSquare(Context context, int mipmapId, float foregroundScale){
        Bitmap bitmap = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Drawable drawable = ContextCompat.getDrawable(context, mipmapId);
            if (drawable instanceof AdaptiveIconDrawable) {
                AdaptiveIconDrawable adaptiveIconDrawable = (AdaptiveIconDrawable) drawable;
                Drawable foregroundDrawable = adaptiveIconDrawable.getForeground();
                Drawable backgroundDrawable = adaptiveIconDrawable.getBackground();
                int size = Math.max(foregroundDrawable.getIntrinsicWidth(), foregroundDrawable.getIntrinsicHeight());
                bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                backgroundDrawable.setBounds(0, 0, size, size);
                backgroundDrawable.draw(canvas);
                int foregroundWidth = foregroundDrawable.getIntrinsicWidth();
                int foregroundHeight = foregroundDrawable.getIntrinsicHeight();
                int scaledWidth = (int) (foregroundWidth * foregroundScale);
                int scaledHeight = (int) (foregroundHeight * foregroundScale);
                int left = (size - scaledWidth) / 2;
                int top = (size - scaledHeight) / 2;
                foregroundDrawable.setBounds(left, top, left + scaledWidth, top + scaledHeight);
                foregroundDrawable.draw(canvas);
            } else {
                bitmap = ((BitmapDrawable) drawable).getBitmap();
            }
        }
        return bitmap;
    }

    public static Bitmap getMipMapBitmapSource(Context context, int mipmapId){
        Bitmap bitmap = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Drawable drawable = ContextCompat.getDrawable(context, mipmapId); // 获取Drawable资源

            if (drawable instanceof AdaptiveIconDrawable) {
                // 如果是 AdaptiveIconDrawable 类型
                AdaptiveIconDrawable adaptiveIconDrawable = (AdaptiveIconDrawable) drawable;

                // 创建一个 Bitmap，大小与图标一致
                bitmap = Bitmap.createBitmap(adaptiveIconDrawable.getIntrinsicWidth(),
                        adaptiveIconDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

                // 渲染到 Bitmap 上
                Canvas canvas = new Canvas(bitmap);
                adaptiveIconDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                adaptiveIconDrawable.draw(canvas);

                // 现在你可以使用这个 Bitmap（比如作为二维码的 Logo）
                // Bitmap appLogo = bitmap; // 用作二维码的 logo
            } else {
                // 如果不是 AdaptiveIconDrawable，直接获取 Bitmap
                bitmap = ((BitmapDrawable) drawable).getBitmap();
                // Bitmap appLogo = bitmap; // 用作二维码的 logo
            }
        }
        return bitmap;
    }

}