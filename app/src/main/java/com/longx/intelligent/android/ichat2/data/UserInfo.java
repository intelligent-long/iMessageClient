package com.longx.intelligent.android.ichat2.data;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.longx.intelligent.android.ichat2.R;

import java.util.Objects;

/**
 * Created by LONG on 2024/4/28 at 9:07 PM.
 */
public abstract class UserInfo {

    public static class Region implements Parcelable {
        private final Integer adcode;
        private final String name;

        public Region() {
            this(null, null);
        }

        public Region(Integer adcode, String name) {
            this.adcode = adcode;
            this.name = name;
        }

        public Integer getAdcode() {
            return adcode;
        }

        public String getName() {
            return name;
        }

        public static final Creator<Region> CREATOR = new Creator<Region>() {
            @Override
            public Region createFromParcel(Parcel in) {
                return new Region(in);
            }

            @Override
            public Region[] newArray(int size) {
                return new Region[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        protected Region(Parcel in) {
            adcode = in.readInt();
            name = in.readString();
        }

        @Override
        public void writeToParcel(@NonNull Parcel dest, int flags) {
            dest.writeInt(adcode);
            dest.writeString(name);
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
