package com.longx.intelligent.android.ichat2.data;

/**
 * Created by LONG on 2024/7/17 at 9:35 AM.
 */
public class ChatMessageAllow {
    private boolean allowVoice;
    private boolean allowNotice;

    public ChatMessageAllow() {
    }

    public ChatMessageAllow(boolean allowVoice, boolean allowNotice) {
        this.allowVoice = allowVoice;
        this.allowNotice = allowNotice;
    }

    public boolean isAllowVoice() {
        return allowVoice;
    }

    public boolean isAllowNotice() {
        return allowNotice;
    }
}
