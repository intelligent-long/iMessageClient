package com.longx.intelligent.android.ichat2.data;

/**
 * Created by LONG on 2024/6/3 at 5:34 PM.
 */
public class ChannelTag {
    private String id;
    private String ichatId;
    private String name;

    public ChannelTag() {
    }

    public ChannelTag(String id, String ichatId, String name) {
        this.id = id;
        this.ichatId = ichatId;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getIchatId() {
        return ichatId;
    }

    public String getName() {
        return name;
    }
}
