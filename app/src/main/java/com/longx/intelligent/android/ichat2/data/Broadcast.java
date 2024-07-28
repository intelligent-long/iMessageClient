package com.longx.intelligent.android.ichat2.data;

import java.util.Date;

/**
 * Created by LONG on 2024/7/28 at 2:13 PM.
 */
public class Broadcast {
    private String broadcastId;
    private String ichatId;
    private Date time;
    private String text;

    public Broadcast() {
    }

    public Broadcast(String broadcastId, String ichatId, Date time, String text) {
        this.broadcastId = broadcastId;
        this.ichatId = ichatId;
        this.time = time;
        this.text = text;
    }

    public String getBroadcastId() {
        return broadcastId;
    }

    public String getIchatId() {
        return ichatId;
    }

    public Date getTime() {
        return time;
    }

    public String getText() {
        return text;
    }
}
