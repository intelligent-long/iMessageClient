package com.longx.intelligent.android.imessage.data;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Objects;

/**
 * Created by LONG on 2025/4/25 at 上午1:58.
 */
@JsonIgnoreProperties({"stability"})
public class Region implements Parcelable {
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
        if (in.readByte() == 0) {
            adcode = null;
        } else {
            adcode = in.readInt();
        }
        name = in.readString();
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        if (adcode == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(adcode);
        }
        dest.writeString(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Region region = (Region) o;
        return Objects.equals(adcode, region.adcode) && Objects.equals(name, region.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(adcode, name);
    }
}
