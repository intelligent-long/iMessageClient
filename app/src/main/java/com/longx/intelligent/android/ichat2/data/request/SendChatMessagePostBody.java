package com.longx.intelligent.android.ichat2.data.request;

/**
 * Created by LONG on 2024/5/12 at 10:53 PM.
 */
public class SendChatMessagePostBody {
    public static final int TYPE_TEXT = 0;
    public static final int TYPE_NOTICE = 1;
    public static final int TYPE_IMAGE = 2;
    public static final int TYPE_VOICE = 3;

    private int type;

    private String toIchatId;

    private String text;

    public SendChatMessagePostBody() {
    }

    public SendChatMessagePostBody(int type, String toIchatId, String text) {
        this.type = type;
        this.toIchatId = toIchatId;
        this.text = text;
    }

    public int getType() {
        return type;
    }

    public String getToIchatId() {
        return toIchatId;
    }

    public String getText() {
        return text;
    }
}
