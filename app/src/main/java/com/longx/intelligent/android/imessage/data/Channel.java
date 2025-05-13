package com.longx.intelligent.android.imessage.data;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Objects;

/**
 * Created by LONG on 2024/4/26 at 8:19 PM.
 */
@JsonIgnoreProperties({"stability"})
public class Channel extends UserInfo implements Parcelable {
    private final String imessageId;
    private final String imessageIdUser;
    private final String email;
    private final String username;
    private final String note;
    private final Avatar avatar;
    private final Integer sex;
    private final Region firstRegion;
    private final Region secondRegion;
    private final Region thirdRegion;
    private final boolean associated;

    public Channel() {
        this(null, null, null, null, null, null, null, null, null, null, false);
    }

    public Channel(String imessageId, String imessageIdUser, String email, String username, String note, Avatar avatar, Integer sex, Region firstRegion, Region secondRegion, Region thirdRegion, boolean associated) {
        this.imessageId = imessageId;
        this.imessageIdUser = imessageIdUser;
        this.email = email;
        this.username = username;
        this.note = note;
        this.avatar = avatar;
        this.sex = sex;
        this.firstRegion = firstRegion;
        this.secondRegion = secondRegion;
        this.thirdRegion = thirdRegion;
        this.associated = associated;
    }

    public String getImessageId() {
        return imessageId;
    }

    public String getUsername() {
        return username;
    }

    public String getNote() {
        return note;
    }

    public String autoGetName(){
        return note == null ? username : note;
    }

    public String getImessageIdUser() {
        return imessageIdUser;
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

    public String getEmail() {
        return email;
    }

    public Avatar getAvatar() {
        return avatar;
    }

    public boolean isAssociated() {
        return associated;
    }

    public static final Creator<Channel> CREATOR = new Creator<Channel>() {
        @Override
        public Channel createFromParcel(Parcel in) {
            return new Channel(in);
        }

        @Override
        public Channel[] newArray(int size) {
            return new Channel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    protected Channel(Parcel in) {
        imessageId = in.readString();
        imessageIdUser = in.readString();
        email = in.readString();
        username = in.readString();
        note = in.readString();
        avatar = in.readParcelable(getClass().getClassLoader());
        sex = (Integer) in.readValue(getClass().getClassLoader());
        firstRegion = in.readParcelable(getClass().getClassLoader());
        secondRegion = in.readParcelable(getClass().getClassLoader());
        thirdRegion = in.readParcelable(getClass().getClassLoader());
        associated = in.readInt() == 1;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(imessageId);
        dest.writeString(imessageIdUser);
        dest.writeString(email);
        dest.writeString(username);
        dest.writeString(note);
        dest.writeParcelable(avatar, flags);
        dest.writeValue(sex);
        dest.writeParcelable(firstRegion, flags);
        dest.writeParcelable(secondRegion, flags);
        dest.writeParcelable(thirdRegion, flags);
        dest.writeInt(associated ? 1 : 0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Channel channel = (Channel) o;
        return associated == channel.associated && Objects.equals(imessageId, channel.imessageId) && Objects.equals(imessageIdUser, channel.imessageIdUser) && Objects.equals(email, channel.email) && Objects.equals(username, channel.username) && Objects.equals(note, channel.note) && Objects.equals(avatar, channel.avatar) && Objects.equals(sex, channel.sex) && Objects.equals(firstRegion, channel.firstRegion) && Objects.equals(secondRegion, channel.secondRegion) && Objects.equals(thirdRegion, channel.thirdRegion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(imessageId, imessageIdUser, email, username, note, avatar, sex, firstRegion, secondRegion, thirdRegion, associated);
    }

    @Override
    public String toString() {
        return "Channel{" +
                "imessageId='" + imessageId + '\'' +
                ", imessageIdUser='" + imessageIdUser + '\'' +
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", getNote='" + note + '\'' +
                ", avatar=" + avatar +
                ", sex=" + sex +
                ", firstRegion=" + firstRegion +
                ", secondRegion=" + secondRegion +
                ", thirdRegion=" + thirdRegion +
                ", associated=" + associated +
                '}';
    }
}
