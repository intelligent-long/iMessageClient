package com.longx.intelligent.android.imessage.data;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.longx.intelligent.android.imessage.R;

import java.util.Objects;

/**
 * Created by LONG on 2024/4/28 at 9:07 PM.
 */
public abstract class UserInfo {

    public static class UserProfileVisibility{
        private boolean emailVisible;
        private boolean sexVisible;
        private boolean regionVisible;

        public UserProfileVisibility() {
        }

        public UserProfileVisibility(boolean emailVisible, boolean sexVisible, boolean regionVisible) {
            this.emailVisible = emailVisible;
            this.sexVisible = sexVisible;
            this.regionVisible = regionVisible;
        }

        public boolean isEmailVisible() {
            return emailVisible;
        }

        public boolean isSexVisible() {
            return sexVisible;
        }

        public boolean isRegionVisible() {
            return regionVisible;
        }
    }

    public static class WaysToFindMe{
        private boolean byImessageIdUser;
        private boolean byEmail;

        public WaysToFindMe() {
        }

        public WaysToFindMe(boolean byImessageIdUser, boolean byEmail) {
            this.byImessageIdUser = byImessageIdUser;
            this.byEmail = byEmail;
        }

        public boolean isByImessageIdUser() {
            return byImessageIdUser;
        }

        public boolean isByEmail() {
            return byEmail;
        }
    }

    public static String sexValueToString(Context context, Integer sex){
        String[] sexNames = {context.getString(R.string.do_not_set), context.getString(R.string.sex_nv), context.getString(R.string.sex_nan)};
        if(sex == null){
            return sexNames[0];
        }else if(sex == 0){
            return sexNames[1];
        }else if(sex == 1){
            return sexNames[2];
        }else {
            throw new RuntimeException("sex 值非法");
        }
    }

    public static Integer sexStringToValue(Context context, String sex){
        String[] sexNames = {context.getString(R.string.do_not_set), context.getString(R.string.sex_nv), context.getString(R.string.sex_nan)};
        if(Objects.equals(sex, sexNames[0])){
            return null;
        }else if(Objects.equals(sex, sexNames[1])){
            return 0;
        }else if(Objects.equals(sex, sexNames[2])){
            return 1;
        }else {
            throw new RuntimeException("sex 名称非法");
        }
    }

    public abstract Region getFirstRegion();

    public abstract Region getSecondRegion();

    public abstract Region getThirdRegion();

    public String buildRegionDesc(){
        Region firstRegion = getFirstRegion();
        Region secondRegion = getSecondRegion();
        Region thirdRegion = getThirdRegion();
        String firstRegionName = firstRegion == null ? null : firstRegion.getName();
        String secondRegionName = secondRegion == null ? null : secondRegion.getName();
        String thirdRegionName = thirdRegion == null ? null : thirdRegion.getName();
        if(firstRegionName == null && secondRegionName == null && thirdRegionName == null) return null;
        StringBuilder regionDesc = new StringBuilder();
        if(firstRegionName != null) {
            regionDesc.append(firstRegionName);
            if(secondRegionName != null) {
                regionDesc.append(" ").append(secondRegionName);
                if(thirdRegionName != null) regionDesc.append(" ").append(thirdRegionName);
            }
        }
        return regionDesc.toString();
    }
}
