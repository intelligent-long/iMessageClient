package com.longx.intelligent.android.imessage.data;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Created by LONG on 2025/4/15 at 10:49 PM.
 */
@JsonIgnoreProperties({"stability"})
public class GroupChannel implements Parcelable {
    private GroupAvatar groupAvatar;
    private String groupChannelId;
    private String groupChannelIdUser;
    private String owner;
    private String name;
    private String note;
    private Date createTime;
    private List<GroupChannelAssociation> groupChannelAssociations;
    private Region firstRegion;
    private Region secondRegion;
    private Region thirdRegion;
    private String avatarHash;
    private Boolean groupJoinVerification;

    public GroupChannel() {
    }

    public GroupChannel(GroupAvatar groupAvatar, String groupChannelId, String groupChannelIdUser, String owner, String name, String note, Date createTime, List<GroupChannelAssociation> groupChannelAssociations, Region firstRegion, Region secondRegion, Region thirdRegion, String avatarHash, Boolean groupJoinVerification) {
        this.groupAvatar = groupAvatar;
        this.groupChannelId = groupChannelId;
        this.groupChannelIdUser = groupChannelIdUser;
        this.owner = owner;
        this.name = name;
        this.note = note;
        this.createTime = createTime;
        this.groupChannelAssociations = groupChannelAssociations;
        this.firstRegion = firstRegion;
        this.secondRegion = secondRegion;
        this.thirdRegion = thirdRegion;
        this.avatarHash = avatarHash;
        this.groupJoinVerification = groupJoinVerification;
    }

    public GroupChannel(GroupAvatar groupAvatar, String groupChannelId, String groupChannelIdUser, String owner, String name, String note, Date createTime, Region firstRegion, Region secondRegion, Region thirdRegion, String avatarHash, Boolean groupJoinVerification) {
        this(groupAvatar, groupChannelId, groupChannelIdUser, owner, name, note, createTime, new ArrayList<>(), firstRegion, secondRegion, thirdRegion, avatarHash, groupJoinVerification);
    }

    public GroupAvatar getGroupAvatar() {
        return groupAvatar;
    }

    public String getGroupChannelId() {
        return groupChannelId;
    }

    public String getGroupChannelIdUser() {
        return groupChannelIdUser;
    }

    public String getOwner() {
        return owner;
    }

    public String getName() {
        return name;
    }

    public String autoGetName(){
        return note == null ? name : note;
    }

    public String getNote() {
        return note;
    }

    public Date getCreateTime() {
        return createTime;
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

    public String getAvatarHash() {
        return avatarHash;
    }

    public List<GroupChannelAssociation> getGroupChannelAssociations() {
        return groupChannelAssociations;
    }

    public void addGroupChannelAssociation(GroupChannelAssociation groupChannelAssociation){
        groupChannelAssociations.add(groupChannelAssociation);
    }

    public Boolean getGroupJoinVerification() {
        return groupJoinVerification;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupChannel that = (GroupChannel) o;
        return Objects.equals(groupChannelId, that.groupChannelId) && Objects.equals(owner, that.owner) && Objects.equals(name, that.name) && Objects.equals(note, that.note) && Objects.equals(createTime, that.createTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupChannelId, owner, name, note, createTime);
    }

    public static final Creator<GroupChannel> CREATOR = new Creator<GroupChannel>() {
        @Override
        public GroupChannel createFromParcel(Parcel in) {
            return new GroupChannel(in);
        }

        @Override
        public GroupChannel[] newArray(int size) {
            return new GroupChannel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    protected GroupChannel(Parcel in) {
        groupAvatar = in.readParcelable(getClass().getClassLoader());
        groupChannelId = in.readString();
        groupChannelIdUser = in.readString();
        owner = in.readString();
        name = in.readString();
        note = in.readString();
        createTime = (Date) in.readValue(getClass().getClassLoader());
        groupChannelAssociations = in.createTypedArrayList(GroupChannelAssociation.CREATOR);
        firstRegion = in.readParcelable(getClass().getClassLoader());
        secondRegion = in.readParcelable(getClass().getClassLoader());
        thirdRegion = in.readParcelable(getClass().getClassLoader());
        avatarHash = in.readString();
        groupJoinVerification = (Boolean) in.readValue(getClass().getClassLoader());
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeParcelable(groupAvatar, flags);
        dest.writeString(groupChannelId);
        dest.writeString(groupChannelIdUser);
        dest.writeString(owner);
        dest.writeString(name);
        dest.writeString(note);
        dest.writeValue(createTime);
        dest.writeTypedList(groupChannelAssociations);
        dest.writeParcelable(firstRegion, flags);
        dest.writeParcelable(secondRegion, flags);
        dest.writeParcelable(thirdRegion, flags);
        dest.writeString(avatarHash);
        dest.writeValue(groupJoinVerification);
    }

    public String buildRegionDesc(){
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

    @Override
    public String toString() {
        return "GroupChannel{" +
                "groupAvatar=" + groupAvatar +
                ", groupChannelId='" + groupChannelId + '\'' +
                ", groupChannelIdUser='" + groupChannelIdUser + '\'' +
                ", owner='" + owner + '\'' +
                ", name='" + name + '\'' +
                ", getNote='" + note + '\'' +
                ", createTime=" + createTime +
                ", groupChannelAssociations=" + groupChannelAssociations +
                ", firstRegion=" + firstRegion +
                ", secondRegion=" + secondRegion +
                ", thirdRegion=" + thirdRegion +
                ", avatarHash='" + avatarHash + '\'' +
                ", groupJoinVerification=" + groupJoinVerification +
                '}';
    }
}
