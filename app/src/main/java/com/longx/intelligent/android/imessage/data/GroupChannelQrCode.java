package com.longx.intelligent.android.imessage.data;

import java.util.Date;

/**
 * Created by LONG on 2025/5/24 at 下午3:17.
 */
public class GroupChannelQrCode {
    private Integer appVersionCode;
    private String appVersionName;
    private String groupChannelId;
    private Date generateTime;

    public GroupChannelQrCode() {
    }

    public GroupChannelQrCode(Integer appVersionCode, String appVersionName, String groupChannelId, Date generateTime) {
        this.appVersionCode = appVersionCode;
        this.appVersionName = appVersionName;
        this.groupChannelId = groupChannelId;
        this.generateTime = generateTime;
    }

    public Integer getAppVersionCode() {
        return appVersionCode;
    }

    public String getAppVersionName() {
        return appVersionName;
    }

    public String getGroupChannelId() {
        return groupChannelId;
    }

    public Date getGenerateTime() {
        return generateTime;
    }
}
