package com.longx.intelligent.android.imessage.data;

import java.util.Date;

/**
 * Created by LONG on 2024/3/30 at 6:54 PM.
 */
public class OfflineDetail {
    public static int REASON_NEW_LOGIN_IMESSAGE_ID_PASSWORD = 0;
    public static int REASON_NEW_LOGIN_EMAIL_PASSWORD = 1;
    public static int REASON_NEW_LOGIN_VERIFICATION_CODE = 2;
    public static int REASON_RESET_PASSWORD = 3;
    private int reason;
    private Date time;
    private String ip;
    private String desc;

    public OfflineDetail(){}

    public OfflineDetail(int reason, Date time, String ip) {
        this.reason = reason;
        this.time = time;
        this.ip = ip;
    }

    public int getReason() {
        return reason;
    }

    public Date getTime() {
        return time;
    }

    public String getIp() {
        return ip;
    }

    public String getDesc() {
        return desc;
    }
}
