package com.longx.intelligent.android.imessage.data.request;

/**
 * Created by LONG on 2024/6/3 at 4:38 PM.
 */
public class AddChannelTagPostBody {
    private String name;

    public AddChannelTagPostBody() {
    }

    public AddChannelTagPostBody(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
