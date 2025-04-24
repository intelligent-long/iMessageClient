package com.longx.intelligent.android.imessage.data;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Created by LONG on 2025/4/15 at 10:49 PM.
 */
public class GroupChannel implements Parcelable {
    private GroupAvatar groupAvatar;
    private String groupChannelId;
    private String owner;
    private String name;
    private String note;
    private Date createTime;
    private List<GroupChannelAssociation> groupChannelAssociations;
    private UserInfo.Region firstRegion;
    private UserInfo.Region secondRegion;
    private UserInfo.Region thirdRegion;

    public GroupChannel() {
    }

    public GroupChannel(GroupAvatar groupAvatar, String groupChannelId, String owner, String name, String note, Date createTime, List<GroupChannelAssociation> groupChannelAssociations) {
        this.groupAvatar = groupAvatar;
        this.groupChannelId = groupChannelId;
        this.owner = owner;
        this.name = name;
        this.note = note;
        this.createTime = createTime;
        this.groupChannelAssociations = groupChannelAssociations;
    }

    public GroupChannel(GroupAvatar groupAvatar, String groupChannelId, String owner, String name, String note, Date createTime) {
        this.groupAvatar = groupAvatar;
        this.groupChannelId = groupChannelId;
        this.owner = owner;
        this.name = name;
        this.note = note;
        this.createTime = createTime;
        this.groupChannelAssociations = new ArrayList<>();
    }

    public GroupAvatar getGroupAvatar() {
        return groupAvatar;
    }

    public String getGroupChannelId() {
        return groupChannelId;
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

    public UserInfo.Region getFirstRegion() {
        return firstRegion;
    }

    public UserInfo.Region getSecondRegion() {
        return secondRegion;
    }

    public UserInfo.Region getThirdRegion() {
        return thirdRegion;
    }

    public List<GroupChannelAssociation> getGroupChannelAssociations() {
        return groupChannelAssociations;
    }

    public void addGroupChannelAssociation(GroupChannelAssociation groupChannelAssociation){
        groupChannelAssociations.add(groupChannelAssociation);
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
        owner = in.readString();
        name = in.readString();
        note = in.readString();
        createTime = (Date) in.readValue(getClass().getClassLoader());
        groupChannelAssociations = in.createTypedArrayList(GroupChannelAssociation.CREATOR);
        firstRegion = in.readParcelable(getClass().getClassLoader());
        secondRegion = in.readParcelable(getClass().getClassLoader());
        thirdRegion = in.readParcelable(getClass().getClassLoader());
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeParcelable(groupAvatar, flags);
        dest.writeString(groupChannelId);
        dest.writeString(owner);
        dest.writeString(name);
        dest.writeString(note);
        dest.writeValue(createTime);
        dest.writeTypedList(groupChannelAssociations);
        dest.writeParcelable(firstRegion, flags);
        dest.writeParcelable(secondRegion, flags);
        dest.writeParcelable(thirdRegion, flags);
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
}
