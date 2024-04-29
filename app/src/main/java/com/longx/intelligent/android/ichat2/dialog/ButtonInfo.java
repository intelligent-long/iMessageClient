package com.longx.intelligent.android.ichat2.dialog;

import android.content.DialogInterface;

/**
 * Created by LONG on 2024/4/28 at 2:08 AM.
 */
public class ButtonInfo {
    private final String text;
    private final DialogInterface.OnClickListener yier;

    public ButtonInfo(String text, DialogInterface.OnClickListener yier) {
        this.text = text;
        this.yier = yier;
    }

    public String getText() {
        return text;
    }

    public DialogInterface.OnClickListener getYier() {
        return yier;
    }
}
