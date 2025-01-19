package com.longx.intelligent.android.imessage.data;

import java.util.List;

public class AmapDistrict {
    private Integer adcode;
    private String name;
    private String level;
    private List<AmapDistrict> districts;

    public AmapDistrict(){}

    public AmapDistrict(Integer adcode, String name, String level, List<AmapDistrict> districts) {
        this.adcode = adcode;
        this.name = name;
        this.level = level;
        this.districts = districts;
    }

    public Integer getAdcode() {
        return adcode;
    }

    public String getName() {
        return name;
    }

    public String getLevel() {
        return level;
    }

    public List<AmapDistrict> getDistricts() {
        return districts;
    }
}
