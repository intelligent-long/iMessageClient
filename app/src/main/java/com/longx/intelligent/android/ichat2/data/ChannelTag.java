package com.longx.intelligent.android.ichat2.data;

/**
 * Created by LONG on 2024/6/3 at 5:34 PM.
 */
public class ChannelTag {
    private String id;
    private String ichatId;
    private String name;
    private int order;

    public ChannelTag() {
    }

    public ChannelTag(String id, String ichatId, String name, int order) {
        this.id = id;
        this.ichatId = ichatId;
        this.name = name;
        this.order = order;
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

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
