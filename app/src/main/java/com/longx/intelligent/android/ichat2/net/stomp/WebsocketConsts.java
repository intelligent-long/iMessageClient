package com.longx.intelligent.android.ichat2.net.stomp;

/**
 * Created by LONG on 2024/3/31 at 8:24 PM.
 */
public class WebsocketConsts {
    public static final String WEBSOCKET_ENDPOINT = "ws";
    public static final int CLOSE_CODE_SERVER_ACTIVE_CLOSE = 4000;
    public static final String CLOSE_REASON_SERVER_ACTIVE_CLOSE = "服务器主动下线";
    public static final int CLOSE_CODE_CLOSE_FOR_CLIENT_NEED_UPDATE = 4001;
    public static final String CLOSE_REASON_CLOSE_FOR_CLIENT_UPDATE = "客户端必须更新新版本";
    public static final int CLOSE_CODE_CLOSE_FOR_CLIENT_VERSION_HIGHER = 4002;
    public static final String CLOSE_REASON_CLOSE_FOR_CLIENT_VERSION_HIGHER = "客户端版本过高";
}
