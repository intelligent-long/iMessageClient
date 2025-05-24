package com.longx.intelligent.android.imessage.data;

import java.util.Date;

/**
 * Created by LONG on 2025/5/24 at 下午3:17.
 */
public class ChannelQrCode {
    private Integer appVersionCode;
    private String appVersionName;
    private String channelId;
    private Date generateTime;

    public ChannelQrCode() {
    }

    public ChannelQrCode(Integer appVersionCode, String appVersionName, String channelId, Date generateTime) {
        this.appVersionCode = appVersionCode;
        this.appVersionName = appVersionName;
        this.channelId = channelId;
        this.generateTime = generateTime;
    }

    public Integer getAppVersionCode() {
        return appVersionCode;
    }

    public String getAppVersionName() {
        return appVersionName;
    }

    public String getChannelId() {
        return channelId;
    }

    public Date getGenerateTime() {
        return generateTime;
    }
}
