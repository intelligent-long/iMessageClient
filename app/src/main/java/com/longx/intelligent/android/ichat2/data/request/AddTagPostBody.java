package com.longx.intelligent.android.ichat2.data.request;

/**
 * Created by LONG on 2024/6/3 at 4:38 PM.
 */
public class AddTagPostBody {
    private String name;

    public AddTagPostBody() {
    }

    public AddTagPostBody(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
