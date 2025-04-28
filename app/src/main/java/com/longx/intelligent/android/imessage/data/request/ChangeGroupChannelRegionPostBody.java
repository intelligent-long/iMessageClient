package com.longx.intelligent.android.imessage.data.request;

/**
 * Created by LONG on 2024/4/12 at 7:26 PM.
 */
public class ChangeGroupChannelRegionPostBody {
    private String groupChannelId;
    private Integer firstRegionAdcode;
    private Integer secondRegionAdcode;
    private Integer thirdRegionAdcode;

    public ChangeGroupChannelRegionPostBody() {
    }

    public ChangeGroupChannelRegionPostBody(String groupChannelId, Integer firstRegionAdcode, Integer secondRegionAdcode, Integer thirdRegionAdcode) {
        this.groupChannelId = groupChannelId;
        this.firstRegionAdcode = firstRegionAdcode;
        this.secondRegionAdcode = secondRegionAdcode;
        this.thirdRegionAdcode = thirdRegionAdcode;
    }

    public String getGroupChannelId() {
        return groupChannelId;
    }

    public Integer getFirstRegionAdcode() {
        return firstRegionAdcode;
    }

    public Integer getSecondRegionAdcode() {
        return secondRegionAdcode;
    }

    public Integer getThirdRegionAdcode() {
        return thirdRegionAdcode;
    }
}
