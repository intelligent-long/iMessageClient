package com.longx.intelligent.android.ichat2.value;

/**
 * Created by LONG on 2024/5/3 at 8:15 PM.
 */
public class Variables {
    private static final String MESSAGE = "你好，我是{NAME}。";
    public static String getMessage(String name){
        return MESSAGE.replace("{NAME}", name);
    }
}
