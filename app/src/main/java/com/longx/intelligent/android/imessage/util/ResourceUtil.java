package com.longx.intelligent.android.imessage.util;

import android.content.Context;
import android.util.TypedValue;

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

}