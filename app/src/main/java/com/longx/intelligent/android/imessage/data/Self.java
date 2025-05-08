package com.longx.intelligent.android.imessage.data;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.util.Date;

/**
 * Created by LONG on 2024/3/30 at 12:56 PM.
 */
@JsonAutoDetect(getterVisibility = NONE, fieldVisibility = ANY)
public class Self extends UserInfo{
    private final String imessageId;
    private final String imessageIdUser;
    private final String email;
    private final Date registerTime;
    private final String username;
    private final Avatar avatar;
    private final Integer sex;
    private final Region firstRegion;
    private final Region secondRegion;
    private final Region thirdRegion;
    private final UserProfileVisibility userProfileVisibility;
    private final WaysToFindMe waysToFindMe;

    public Self() {
        this(null, null, null, null, null, null, null, null, null, null, null, null);
    }

    public Self(String imessageId, String imessageIdUser, String email, Date registerTime, String username, Avatar avatar, Integer sex, Region firstRegion, Region secondRegion, Region thirdRegion, UserProfileVisibility userProfileVisibility, WaysToFindMe waysToFindMe) {
        this.imessageId = imessageId;
        this.imessageIdUser = imessageIdUser;
        this.email = email;
        this.registerTime = registerTime;
        this.username = username;
        this.avatar = avatar;
        this.sex = sex;
        this.firstRegion = firstRegion;
        this.secondRegion = secondRegion;
        this.thirdRegion = thirdRegion;
        this.userProfileVisibility = userProfileVisibility;
        this.waysToFindMe = waysToFindMe;
    }

    public String getImessageId() {
        return imessageId;
    }

    public String getImessageIdUser() {
        return imessageIdUser;
    }

    public String getEmail() {
        return email;
    }

    public Date getRegisterTime() {
        return registerTime;
    }

    public String getUsername() {
        return username;
    }

    public Avatar getAvatar() {
        return avatar;
    }

    public Integer getSex() {
        return sex;
    }

    public Region getFirstRegion() {
        return firstRegion;
    }

    public Region getSecondRegion() {
        return secondRegion;
    }

    public Region getThirdRegion() {
        return thirdRegion;
    }

    public UserProfileVisibility getUserProfileVisibility() {
        return userProfileVisibility;
    }

    public WaysToFindMe getWaysToFindMe() {
        return waysToFindMe;
    }

    public Channel toChannel(){
        return new Channel(imessageId, imessageIdUser, email, username, null, avatar, sex, firstRegion, secondRegion, thirdRegion, true);
    }

    @Override
    public String toString() {
        return "Self{" +
                "imessageId='" + imessageId + '\'' +
                ", imessageIdUser='" + imessageIdUser + '\'' +
                ", email='" + email + '\'' +
                ", registerTime=" + registerTime +
                ", username='" + username + '\'' +
                ", avatar=" + avatar +
                ", sex=" + sex +
                ", firstRegion=" + firstRegion +
                ", secondRegion=" + secondRegion +
                ", thirdRegion=" + thirdRegion +
                ", userProfileVisibility=" + userProfileVisibility +
                ", waysToFindMe=" + waysToFindMe +
                '}';
    }
}
