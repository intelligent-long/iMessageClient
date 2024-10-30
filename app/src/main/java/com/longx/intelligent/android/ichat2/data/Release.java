package com.longx.intelligent.android.ichat2.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.longx.intelligent.android.ichat2.util.TimeUtil;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Created by LONG on 2024/10/24 at 4:37 AM.
 */
public class Release {
    private Integer versionCode;
    private String versionName;
    private Date releaseTime;
    private String notes;
    private List<String> releaseImageIds;

    public Release() {
    }

    public Release(Integer versionCode, String versionName, Date releaseTime, String notes, List<String> releaseImageIds) {
        this.versionCode = versionCode;
        this.versionName = versionName;
        this.releaseTime = releaseTime;
        this.notes = notes;
        this.releaseImageIds = releaseImageIds;
    }

    public Integer getVersionCode() {
        return versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public Date getReleaseTime() {
        return releaseTime;
    }

    public String getNotes() {
        return notes;
    }

    public List<String> getReleaseImageIds() {
        return releaseImageIds;
    }

    public void setReleaseImageIds(List<String> releaseImageIds) {
        this.releaseImageIds = releaseImageIds;
    }

    @JsonIgnore
    public String getFormattedReleaseTime(){
        return TimeUtil.formatRelativeTime(releaseTime);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Release release = (Release) o;
        return Objects.equals(versionCode, release.versionCode) && Objects.equals(versionName, release.versionName) && Objects.equals(releaseTime, release.releaseTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(versionCode, versionName, releaseTime);
    }
}
