package com.longx.intelligent.android.imessage.data;

/**
 * Created by LONG on 2025/5/24 at 下午4:02.
 */
public class QrCodeData {
    public enum Type {CHANNEL, GROUP_CHANNEL}

    private Type type;
    private Object data;

    public QrCodeData() {
    }

    public QrCodeData(Type type, Object data) {
        this.type = type;
        this.data = data;
    }

    public Type getType() {
        return type;
    }

    public Object getData() {
        return data;
    }
}
