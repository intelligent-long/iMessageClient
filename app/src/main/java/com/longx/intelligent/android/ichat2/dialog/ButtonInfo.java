package com.longx.intelligent.android.ichat2.dialog;

import android.view.View;

/**
 * Created by LONG on 2024/10/31 at 下午8:31.
 */
public class ButtonInfo {
    private final String text;
    private final View.OnClickListener yier;

    public ButtonInfo(String text, View.OnClickListener yier) {
        this.text = text;
        this.yier = yier;
    }

    public String getText() {
        return text;
    }

    public View.OnClickListener getYier() {
        return yier;
    }
}
