package com.longx.intelligent.android.imessage.util;

import android.content.Context;

import androidx.core.graphics.ColorUtils;

/**
 * Created by LONG on 2024/1/11 at 2:17 PM.
 */
public class ColorUtil {
    public static int getColor(Context context, int color){
        return context.getColor(color);
    }

    public static int getAlphaColor(int color, int alpha){
        return ColorUtils.setAlphaComponent(color, alpha);
    }

    public static int getAlphaAttrColor(Context context, int attrResId, int alpha){
        return getAlphaColor(getAttrColor(context, attrResId), alpha);
    }

    public static int getAttrColor(Context context, int attrResId){
        int colorId = ResourceUtil.getAttr(context, attrResId).resourceId;
        return context.getColor(colorId);
    }

    public static String colorToRGB(int color) {
        int alpha = (color >> 24) & 0xff;
        int red = (color >> 16) & 0xff;
        int green = (color >> 8) & 0xff;
        int blue = color & 0xff;
        if (alpha == 255) {
            return String.format("rgb(%d, %d, %d)", red, green, blue);
        } else {
            return String.format("rgba(%d, %d, %d, %.2f)", red, green, blue, alpha / 255.0);
        }
    }
}
