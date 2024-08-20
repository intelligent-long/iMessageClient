package com.longx.intelligent.android.ichat2.util;

import android.util.Log;

/**
 * Created by LONG on 2024/1/12 at 6:15 PM.
 */
public class ErrorLogger {

    public static void log(Object o){
        log(null, o, null);
    }

    public static void log(Throwable t){
        log(null, "", t);
    }

    public static void log(Class<?> clazz, Object o){
        log(clazz, o, null);
    }

    public static void log(Class<?> clazz, Throwable t){
        log(clazz, "", t);
    }

    public static void log(Class<?> clazz, Object o, Throwable t){
        String text = o == null ? "null" : o.toString();
        String tag = clazz == null ? "" : clazz.getName();
        if(t != null) {
            Log.e(tag, text, t);
        }else {
            Log.e(tag, text);
        }
    }
}
