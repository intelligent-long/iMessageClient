package com.longx.intelligent.android.imessage.util;

/**
 * Created by LONG on 2024/3/28 at 5:41 PM.
 */
public class NetworkUtil {

    public static boolean isPortValid(int port) {
        return port >= 0 && port <= 65535;
    }

}
