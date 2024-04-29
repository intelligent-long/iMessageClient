package com.longx.intelligent.android.ichat2.data;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;

import android.content.Context;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.longx.intelligent.android.ichat2.da.privatefile.PrivateFilesAccessor;

import java.io.File;
import java.util.Date;

/**
 * Created by LONG on 2024/3/30 at 12:56 PM.
 */
@JsonAutoDetect(getterVisibility = NONE, fieldVisibility = ANY)
public class SelfInfo extends UserInfo{
    private final String ichatId;
    private final String ichatIdUser;
    private final String email;
    private final Date registerTime;
    private final String username;
    private final String avatarHash;
    private final Integer sex;
    private final Region firstRegion;
    private final Region secondRegion;
    private final Region thirdRegion;
    @JsonIgnore
    private final String avatarExtension;

    public SelfInfo() {
        this(null, null, null, null, null, null, null, null, null, null, null);
    }

    public SelfInfo(String ichatId, String ichatIdUser, String email, Date registerTime, String username, String avatarHash, Integer sex, Region firstRegion, Region secondRegion, Region thirdRegion, String avatarExtension) {
        this.ichatId = ichatId;
        this.ichatIdUser = ichatIdUser;
        this.email = email;
        this.registerTime = registerTime;
        this.username = username;
        this.avatarHash = avatarHash;
        this.sex = sex;
        this.firstRegion = firstRegion;
        this.secondRegion = secondRegion;
        this.thirdRegion = thirdRegion;
        this.avatarExtension = avatarExtension;
    }

    public String getIchatId() {
        return ichatId;
    }

    public String getIchatIdUser() {
        return ichatIdUser;
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

    public String getAvatarHash() {
        return avatarHash;
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

    public String getAvatarExtension() {
        return avatarExtension;
    }

    public SelfInfo setAvatarExtension(String avatarExtension){
        return new SelfInfo(ichatId, ichatIdUser, email, registerTime, username, avatarHash, sex, firstRegion, secondRegion, thirdRegion, avatarExtension);
    }

    public File getAvatarFile(Context context) {
        if(avatarExtension == null) return null;
        return PrivateFilesAccessor.getAvatarFile(context, ichatId, avatarExtension);
    }
}
