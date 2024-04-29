package com.longx.intelligent.android.ichat2.data.request;

/**
 * Created by LONG on 2024/4/12 at 7:26 PM.
 */
public class ChangeRegionPostBody {
    private Integer firstRegionAdcode;
    private Integer secondRegionAdcode;
    private Integer thirdRegionAdcode;

    public ChangeRegionPostBody() {
    }

    public ChangeRegionPostBody(Integer firstRegionAdcode, Integer secondRegionAdcode, Integer thirdRegionAdcode) {
        this.firstRegionAdcode = firstRegionAdcode;
        this.secondRegionAdcode = secondRegionAdcode;
        this.thirdRegionAdcode = thirdRegionAdcode;
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
