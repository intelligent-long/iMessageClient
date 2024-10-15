package com.longx.intelligent.android.ichat2.yier;

import android.content.Context;
import android.view.View;

import com.longx.intelligent.android.ichat2.procedure.MessageDisplayer;
import com.longx.intelligent.android.ichat2.util.Utils;

/**
 * Created by LONG on 2024/5/1 at 2:52 PM.
 */
public class CopyTextOnLongClickYier implements View.OnLongClickListener {
    private final Context context;
    private final String text;

    public CopyTextOnLongClickYier(Context context, String text) {
        this.context = context;
        this.text = text;
    }

    @Override
    public boolean onLongClick(View v) {
        Utils.copyTextToClipboard(context, text, text);
        MessageDisplayer.autoShow(context, "已复制", MessageDisplayer.Duration.SHORT);
        return true;
    }
}
